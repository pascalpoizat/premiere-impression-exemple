# Front PremièreImpression

Client web statique (HTML, CSS, JavaScript natif en modules), sans étape de build.
Il ne communique qu'avec l'API REST : toutes les URL sont dans `api.js`.

| Fichier | Rôle |
|---|---|
| `app.js` | point d'entrée, choix du profil courant |
| `candidature.js` | F1 : candidatures et analyse d'annonce |
| `tenue.js` | F2 : mensurations, prêt de vêtements, vêtements compatibles |
| `revision.js` | F3 : concepts requis et planning de révision jusqu'à la veille de l'entretien |
| `commun.js` | utilitaires partagés (échappement anti-XSS, erreurs) |

```bash
# 1. lancer le back avec les données de démonstration (depuis la racine du dépôt)
./gradlew bootRun --args='--spring.profiles.active=h2,demo'
# 2. servir le front sur le port 8000 (origine autorisée par la configuration CORS du back)
python3 -m http.server 8000 -d front
```

Puis ouvrir http://localhost:8000.
