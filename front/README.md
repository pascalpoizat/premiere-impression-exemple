# Front PremièreImpression

Client web statique (HTML, CSS, JavaScript natif en modules), sans étape de build.
Il ne communique qu'avec l'API REST : toutes les URL sont dans `api.js`.

| Fichier | Rôle |
|---|---|
| `app.js` | point d'entrée, choix du profil courant |
| `onglets.js` | navigation par onglets, onglet ouvert retenu dans l'URL (`#candidatures`, `#tenue`, `#revision`) |
| `candidature.js` | F1 : candidatures et analyse d'annonce |
| `tenue.js` | F2 : mensurations, prêt de vêtements, vêtements compatibles |
| `revision.js` | F3 : concepts requis et planning de révision jusqu'à la veille de l'entretien |
| `disponibilites.js` | F3 : saisie des minutes par jour (jours de la semaine façon réveil, calendrier mensuel) |
| `commun.js` | utilitaires partagés (échappement anti-XSS, erreurs) |

```bash
# 1. lancer le back avec les données de démonstration (depuis la racine du dépôt)
./gradlew bootRun --args='--spring.profiles.active=h2,demo'
# 2. servir le front sur le port 8000 (origine autorisée par la configuration CORS du back)
python3 -m http.server 8000 -d front
```

Puis ouvrir http://localhost:8000 (en `http`, et avec `localhost` : c'est une origine autorisée par CORS, contrairement à l'adresse `http://[::]:8000/` affichée par Python).

## Problème connu : menus déroulants vides

Si la page s'affiche mais que les menus (profil, type de vêtement, candidature) restent vides, les appels à l'API échouent :

- **Safari en mode « HTTPS uniquement »** : Safari laisse charger la page après un avertissement, mais bloque sans rien dire les appels en `http` vers l'API (sur `127.0.0.1`, il refuse même la page, avec l'erreur `WebKitErrorDomain: 305`). Utiliser Firefox ou Chrome, ou désactiver ce réglage dans Safari.
- **Mauvaise adresse** : ouvrir `http://localhost:8000` ou `http://127.0.0.1:8000`, ni `https://` ni `http://[::]:8000/`, sinon le back refuse les appels (CORS, voir `app.cors.origines-autorisees` dans `application.properties`).

La console du navigateur (outils de développement) indique la cause exacte.
