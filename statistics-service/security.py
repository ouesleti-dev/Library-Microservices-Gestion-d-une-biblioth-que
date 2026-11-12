"""Validation du JWT Keycloak et controle des roles (equivalent du SecurityConfig Java)."""
import os

import jwt
from fastapi import Depends, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from jwt import PyJWKClient

ISSUER = os.getenv("KEYCLOAK_ISSUER", "http://localhost:8180/realms/library")
JWKS_URL = os.getenv("KEYCLOAK_JWKS_URL", ISSUER + "/protocol/openid-connect/certs")

# Cles publiques de Keycloak, recuperees a la premiere requete puis mises en cache
_jwks_client = PyJWKClient(JWKS_URL, cache_keys=True)

# Declare le schema "Bearer" : FastAPI affiche alors le bouton Authorize dans Swagger
bearer_scheme = HTTPBearer(auto_error=False)


def require_roles(*allowed_roles: str):
    def checker(credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme)) -> dict:
        if credentials is None:
            raise HTTPException(status_code=401, detail="Token manquant",
                                headers={"WWW-Authenticate": "Bearer"})
        token = credentials.credentials
        try:
            signing_key = _jwks_client.get_signing_key_from_jwt(token)
            claims = jwt.decode(
                token,
                signing_key.key,
                algorithms=["RS256"],
                issuer=ISSUER,
                options={"verify_aud": False},  # Keycloak met aud="account" par defaut
            )
        except jwt.PyJWTError:
            raise HTTPException(status_code=401, detail="Token invalide ou expire",
                                headers={"WWW-Authenticate": "Bearer"})
        roles = (claims.get("realm_access") or {}).get("roles", [])
        if not any(role in roles for role in allowed_roles):
            raise HTTPException(status_code=403, detail="Role insuffisant")
        return claims

    return checker