# Library Frontend (Angular)

Front Angular pour le projet *Library Microservices*. Toutes les requêtes passent par l'**API Gateway** (`http://localhost:8090`) ;
l'authentification se fait auprès de **Keycloak** (`http://localhost:8180`, realm `library`, client `library-client`).

## Lancer
1. Démarrer le backend : `docker compose up --build` (à la racine du projet microservices).
2. Ici : `npm install` puis `npm start` → http://localhost:4200
3. Se connecter avec `nadim` / `191929` (rôles ADMIN + USER).

(Le CORS de la gateway n'autorise que `http://localhost:4200`.)

## Configuration
URLs dans `src/app/core/config.ts`.

## Pages
Tableau de bord (statistiques), Livres (CRUD, recherche, détail + avis), Membres (CRUD), Emprunts (créer, retourner, modifier l'échéance, supprimer),
Réservations, Amendes, Notifications. Les actions de suppression/création de livres et membres sont réservées aux ADMIN (comme côté backend).
