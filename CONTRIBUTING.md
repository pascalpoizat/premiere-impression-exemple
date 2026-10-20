# Contribuer

## Une feature, une personne, une démarche

Chaque membre porte **une feature par itération**. Une feature a un effet sur **le schéma de BD, le back, l'API REST et le front**.
On respecte toujours cet ordre :

1. **Idéation** : ouvrir une issue avec le modèle « Feature », et rédiger la fiche `doc/features/<version>-F<n>-<nom>.md` (besoin, algorithme et complexité, impact sur le schéma, critères d'acceptation). Exemples : [F1](doc/features/v0.1.0-F1-analyse-annonce.md), [F2](doc/features/v0.1.0-F2-ajustement-morphologique.md).
2. **Back**, en architecture hexagonale :
   - migration Flyway `V<n>__<feature>.sql` ;
   - `domain` : modèle, règles métier et **algorithme**, en Java pur ;
   - `application` : ports `in` (cas d'utilisation) et `out` (persistance…), services ;
   - `adapter/out` : implémentation des ports de sortie (JPA) ;
   - `config` : assemblage Spring.
3. **Tests** :
   - unitaires du domaine et de l'algorithme (cas limites, invariants, propriétés) ;
   - services testés avec des faux adaptateurs en mémoire ;
   - intégration de la persistance et de l'API (`*IT`).
4. **API REST** : `adapter/in/web` (contrôleurs, DTO, erreurs `problem+json`) sous `/api/v1/...`, vérifiée dans Swagger UI.
5. **Front** : seulement à la fin, en consommant uniquement l'API.

Règles d'architecture, vérifiées par ArchUnit : `domain` ne dépend de rien d'autre que Java ; `application` ne dépend ni des adaptateurs ni de Spring.

Entre modules : un module n'utilise jamais le domaine ni les tables d'un autre. Il déclare un port de sortie, implémenté par un adaptateur qui appelle le port d'entrée de l'autre module (exemple : `tenue/adapter/out/profil/ProfilsConnusAdapter`).

## Branches et commits

- `main` : versions livrées et taguées (`v0.1.0-SNAPSHOT`, `v0.1.0`, ...)
- `develop` : intégration
- `feature/<issue>-<nom-court>` : une branche par feature, fusionnée dans `develop` par pull request relue par un autre membre

Avant ton premier commit, vérifie ton identité, sinon ta contribution risque de ne pas t'être attribuée :

```bash
git config user.name  "Prénom Nom"
git config user.email "email-associé-à-ton-compte-github"
```

## Migrations de BD

- Une migration poussée sur `develop` ou `main` n'est **jamais modifiée** : on en ajoute une nouvelle.
- Numéros : se coordonner dans l'issue pour éviter que deux personnes prennent le même `V<n>`.

## Livraisons

```bash
git tag -a v0.1.0-SNAPSHOT -m "Pré-livraison fin de séance"
git push origin v0.1.0-SNAPSHOT
```

À la livraison : mettre à jour `doc/part-ia.json`, le dossier et la page de projet (`site/`), vérifier `./gradlew build` et `make -C doc`, puis pousser le tag `v0.1.0`. La CI publie la release, avec le jar et `dossier.pdf`, et sa version est celle du tag.

## Usage de l'IA

Voir le protocole dans `doc/dossier.md`. Chaque pull request le renseigne dans sa section « Usage de l'IA ».
