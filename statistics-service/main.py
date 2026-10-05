"""Statistics Service - FastAPI (port 8085).

Recupere les livres (Book Service) et les emprunts (Borrowing Service)
via HTTP REST en passant par Eureka (decouverte par nom de service),
puis calcule des statistiques simples.
"""
import os
from collections import Counter
from contextlib import asynccontextmanager

import py_eureka_client.eureka_client as eureka_client
import uvicorn
from fastapi import FastAPI, HTTPException
from py_eureka_client.http_client import HTTPError

# Le port declare a Eureka doit etre le meme que celui d'Uvicorn
PORT = int(os.getenv("PORT", "8085"))
EUREKA_SERVER = os.getenv("EUREKA_SERVER", "http://localhost:8761/eureka")

# Noms des services dans Eureka (spring.application.name en majuscules)
BOOK_SERVICE = "BOOK-SERVICE"
BORROWING_SERVICE = "BORROWING-SERVICE"


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Inscription dans Eureka au demarrage, desinscription a l'arret."""
    try:
        await eureka_client.init_async(
            eureka_server=EUREKA_SERVER,
            app_name="STATISTICS-SERVICE",
            instance_id=f"statistics-service:{PORT}",
            instance_host="localhost",
            instance_port=PORT,
            health_check_url=f"http://localhost:{PORT}/health",
            status_page_url=f"http://localhost:{PORT}/health",
        )
        print(f"Statistics Service enregistre dans Eureka ({EUREKA_SERVER})")
    except Exception as exc:
        print(f"Eureka indisponible au demarrage : {exc}")
    yield
    try:
        await eureka_client.stop_async()
    except Exception:
        pass


app = FastAPI(
    title="Statistics Service",
    description="Statistiques simples sur les livres et les emprunts de la bibliotheque.",
    version="1.0.0",
    lifespan=lifespan,
)


async def fetch_list(app_name: str, path: str, service_name: str) -> list:
    """Appel GET vers un autre microservice via Eureka (par son nom)."""
    try:
        return await eureka_client.do_service_async(app_name, path, return_type="json")
    except HTTPError as exc:
        raise HTTPException(
            status_code=502,
            detail=f"{service_name} a repondu avec une erreur HTTP {getattr(exc, 'code', '?')}",
        )
    except Exception:
        raise HTTPException(
            status_code=503,
            detail=f"{service_name} est indisponible (introuvable dans Eureka)",
        )


def compute_book_stats(books: list) -> dict:
    available = sum(1 for b in books if b.get("available"))
    categories = Counter((b.get("category") or "Sans categorie") for b in books)
    return {
        "totalBooks": len(books),
        "availableBooks": available,
        "borrowedBooks": len(books) - available,
        "booksByCategory": dict(categories),
    }


def compute_borrowing_stats(borrowings: list) -> dict:
    ongoing = sum(1 for b in borrowings if b.get("status") == "BORROWED")
    return {
        "totalBorrowings": len(borrowings),
        "ongoingBorrowings": ongoing,
        "returnedBorrowings": len(borrowings) - ongoing,
    }


@app.get("/health", tags=["Health"])
async def health():
    return {"status": "UP", "service": "statistics-service"}


@app.get("/api/statistics/books", tags=["Statistics"])
async def book_statistics():
    books = await fetch_list(BOOK_SERVICE, "/api/books", "Book Service")
    return compute_book_stats(books)


@app.get("/api/statistics/borrowings", tags=["Statistics"])
async def borrowing_statistics():
    borrowings = await fetch_list(BORROWING_SERVICE, "/api/borrowings", "Borrowing Service")
    return compute_borrowing_stats(borrowings)


@app.get("/api/statistics", tags=["Statistics"])
async def global_statistics():
    books = await fetch_list(BOOK_SERVICE, "/api/books", "Book Service")
    borrowings = await fetch_list(BORROWING_SERVICE, "/api/borrowings", "Borrowing Service")
    book_stats = compute_book_stats(books)
    borrowing_stats = compute_borrowing_stats(borrowings)
    return {
        "totalBooks": book_stats["totalBooks"],
        "availableBooks": book_stats["availableBooks"],
        "totalBorrowings": borrowing_stats["totalBorrowings"],
        "ongoingBorrowings": borrowing_stats["ongoingBorrowings"],
    }


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=PORT, reload=False)