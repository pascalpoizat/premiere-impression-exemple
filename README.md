# PremièreImpression

[![CI](https://github.com/pascalpoizat/premiere-impression-exemple/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/pascalpoizat/premiere-impression-exemple/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/pascalpoizat/premiere-impression-exemple?include_prereleases)](https://github.com/pascalpoizat/premiere-impression-exemple/releases)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

> **Projet d'exemple, équipe fictive.** Réalisé par l'enseignant avec une IA (Claude Code) à partir du [dépôt modèle](https://github.com/pascalpoizat/template-projet-m1-miage), pour montrer à quoi ressemble un projet complet : dossier, features, tests, CI, release et page de projet. Codecov et SonarCloud n'y sont pas branchés.

PremièreImpression aide à faire bonne impression en entretien : analyse d'annonce, tenue, préparation, ponctualité. Projet M1 MIAGE Apprentissage.

- **Page de projet** : https://pascalpoizat.github.io/premiere-impression-exemple/
- **Dossier de projet** : [`doc/dossier.pdf`](doc/dossier.pdf) (source [`doc/dossier.md`](doc/dossier.md)), également joint à chaque [release](https://github.com/pascalpoizat/premiere-impression-exemple/releases)

## Architecture

Front → **API REST** → **Back** → **BD**

| Couche | Technologie |
|--------|-------------|
| Front | application web : HTML, CSS, JavaScript natif (sans build) |
| API REST | Spring Boot 4 (Spring MVC), documentée par OpenAPI / Swagger UI |
| Back | Java 21, Spring Data JPA, Bean Validation |
| BD | PostgreSQL 17, schéma versionné par Flyway (H2 pour les tests) |
| Qualité | JUnit 5, AssertJ, ArchUnit, JaCoCo, Codecov, SonarCloud, GitHub Actions |

Le back suit une **architecture hexagonale** (ports et adaptateurs), organisée par domaine fonctionnel :

```
src/main/java/fr/miage/premiereimpression/
├── candidature/                     F1 : profils, candidatures, analyse d'annonce
├── tenue/                           F2 : mensurations, vêtements, ajustement morphologique
└── <module>/                        même structure pour chaque module :
    ├── domain/                      Java pur : modèle, règles métier, algorithmes
    │   ├── model/
    │   ├── algo/
    │   └── exception/
    ├── application/                 cas d'utilisation, sans framework
    │   ├── port/in/                 interfaces appelées par les adaptateurs entrants
    │   ├── port/out/                interfaces implémentées par les adaptateurs sortants
    │   └── service/                 implémentation des cas d'utilisation
    ├── adapter/
    │   ├── in/web/                  contrôleurs REST, DTO, gestion des erreurs
    │   └── out/persistence/         entités et dépôts JPA (out/profil : lien vers un autre module)
    └── config/                      assemblage Spring de l'hexagone
src/main/resources/db/migration/     migrations Flyway V1__..., V2__...
front/                               client web, ne parle qu'à l'API (api.js)
```

Les dépendances vont toujours vers le domaine. Un test ArchUnit (`ArchitectureHexagonaleTest`) fait échouer le build si cette règle est violée.

## Features

| Version | Feature | Algorithmes | Fiche |
|---|---|---|---|
| v0.1.0 | Analyse d'annonce et correspondance avec le profil | TF-IDF, similarité cosinus, racinisation | [F1](doc/features/v0.1.0-F1-analyse-annonce.md) |
| v0.1.0 | Ajustement morphologique | distance pondérée asymétrique, top-k par tas borné | [F2](doc/features/v0.1.0-F2-ajustement-morphologique.md) |
| v0.1.0 (rattrapage, en cours) | Planning de révision jusqu'au jour J | placement glouton par stratégie (CTA, MKW) avec file de priorité, coupure au plus long préfixe | [F3](doc/features/v0.1.0-F3-planning-revision.md) |

## Prérequis

- JDK 21
- Docker (pour PostgreSQL), facultatif grâce au profil `h2`

## Lancer

```bash
docker compose up -d     # PostgreSQL sur localhost:5432
./gradlew bootRun
```

Sans Docker : `./gradlew bootRun --args='--spring.profiles.active=h2'`

Avec des données de démonstration (profils, annonces réelles, vêtements) : ajouter le profil `demo`, par exemple `--spring.profiles.active=h2,demo`.

Front : `python3 -m http.server 8000 -d front`, puis ouvrir http://localhost:8000 (voir [front/README.md](front/README.md)).

- API : http://localhost:8080/api/v1/profils, http://localhost:8080/api/v1/candidatures/1/analyse…
- Swagger UI : http://localhost:8080/swagger-ui.html
- Santé : http://localhost:8080/actuator/health

## Tester

```bash
./gradlew build          # compile, tests unitaires + intégration, rapport de couverture
open build/reports/jacoco/test/html/index.html
```

Conventions : `*Test` pour les tests unitaires (sans Spring), `*IT` pour les tests d'intégration (`@SpringBootTest`).

## Dossier PDF

```bash
make -C doc              # pandoc + WeasyPrint (pip install weasyprint) ; mise en page dans doc/style.css
make -C doc site         # aperçu local de la page de projet dans build/site/
```

## Livrer une version

Pousser un tag suffit : la CI construit l'application et la documentation, puis publie la release avec le jar et `dossier.pdf`.

```bash
git tag -a v0.1.0 -m "Livraison v0.1.0" && git push origin v0.1.0
```

## Contribuer

Voir [CONTRIBUTING.md](CONTRIBUTING.md).

## Licence

[Apache 2.0](LICENSE)
