"""Consumer RabbitMQ : ecoute les evenements de book-service
et maintient un etat local des livres."""
import asyncio
import json
import os

import aio_pika

RABBIT_URL = os.getenv("RABBITMQ_URL", "amqp://guest:guest@localhost:5672/")
QUEUE_NAME = "statistics.book.queue"

# Etat local : bookId -> {"category": ..., "available": ...}
books_state: dict = {}


def apply_event(event: dict) -> None:
    book_id = event.get("bookId")
    if event.get("eventType") == "BookDeleted":
        books_state.pop(book_id, None)
    else:  # BookCreated ou BookUpdated
        books_state[book_id] = {
            "category": event.get("category") or "Sans categorie",
            "available": bool(event.get("available")),
        }


async def handle_message(message: aio_pika.IncomingMessage) -> None:
    # ack automatique si tout se passe bien
    async with message.process():
        event = json.loads(message.body)
        apply_event(event)
        print(f"Evenement recu : {event.get('eventType')} "
              f"(bookId={event.get('bookId')}) - {len(books_state)} livre(s) en etat local")


async def run_consumer() -> None:
    """Se connecte a RabbitMQ et consomme la file ; reessaie si RabbitMQ est absent."""
    while True:
        try:
            connection = await aio_pika.connect_robust(RABBIT_URL)
            async with connection:
                channel = await connection.channel()
                await channel.set_qos(prefetch_count=10)
                queue = await channel.declare_queue(QUEUE_NAME, durable=True)
                await queue.consume(handle_message)
                print(f"Consumer RabbitMQ connecte, ecoute de '{QUEUE_NAME}'")
                await asyncio.Future()  # attend indefiniment
        except asyncio.CancelledError:
            raise
        except Exception as exc:
            print(f"RabbitMQ indisponible ({exc}), nouvel essai dans 5 s")
            await asyncio.sleep(5)

def load_initial(books: list) -> None:
    """Charge l'etat de depart (livres crees avant le consumer)."""
    for b in books:
        books_state.setdefault(b.get("id"), {
            "category": b.get("category") or "Sans categorie",
            "available": bool(b.get("available")),
        })
    print(f"Etat initial charge : {len(books_state)} livre(s)")

