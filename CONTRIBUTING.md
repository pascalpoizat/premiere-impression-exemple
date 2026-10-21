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

- `main` : versions livrées et taguées (`v0.1.0`, `v0.1.1`, …) ; on n'y pousse jamais directement
- `develop` : intégration
- `feature/<issue>-<nom-court>` : une branche par feature, partie de `develop`
- `amelioration/`, `bugfix/`, `chore/`, `doc/<issue>-<nom-court>` : une branche par issue des autres types (voir « Issues et labels »), partie de `develop` elle aussi

Le circuit, de la feature à la release, passe par **deux pull requests**, chacune relue par un autre membre :

1. **Issue** au format « Feature » (fiche, algorithme, critères d'acceptation), ou au format de son type pour les autres travaux.
2. **Branche** `feature/<issue>-<nom-court>` (ou `amelioration/`, `bugfix/`, `chore/`, `doc/`) créée depuis `develop` à jour, **avant toute modification** ; un commit par étape de la démarche, avec `Refs #<issue>`.
3. **PR de feature**, de la branche vers `develop`, au format du modèle de PR (checklist, « Usage de l'IA », part IA). Elle contient `Closes #<issue>` et n'est fusionnée qu'une fois la CI verte.
4. **PR de release**, de `develop` vers `main`, quand une version est prête (voir « Livraisons »).
5. **Tag annoté** sur `main`, après la fusion : il déclenche la publication de la release et le redéploiement de la page de projet.

## Issues et labels

Tout travail part d'une issue, créée avec le modèle de son type. Chaque issue porte **deux labels**, un par axe :

| Axe | Labels |
|---|---|
| Type (pose automatique par le modèle d'issue) | `type: feature`, `type: amélioration`, `type: bugfix`, `type: chore`, `type: doc` |
| Priorité (à choisir à la création) | `priorité: important`, `priorité: moyen`, `priorité: faible` |

Seule `type: feature` compte comme la feature d'un membre pour une itération. Les autres labels (`~duplicate`, `~invalid`, `~wontfix`, `~question`) commencent par `~`.

« Use this template » ne copie pas les labels : une fois par dépôt, un membre les crée avec la CLI GitHub connectée. Le script est idempotent.

```bash
.github/labels.sh
```

**Fermeture des issues.** GitHub ne ferme une issue automatiquement que si une PR fusionnée dans la **branche par défaut** (`main`) contient un mot-clé **en anglais** : `Closes`, `Fixes` ou `Resolves #<issue>` (« Ferme » n'est pas reconnu). La PR de feature, qui vise `develop`, ne ferme donc rien : c'est la **PR de release** qui doit reprendre une ligne `Closes #<issue>` pour chaque feature qu'elle livre.

Avant ton premier commit, vérifie ton identité, sinon ta contribution risque de ne pas t'être attribuée :

```bash
git config user.name  "Prénom Nom"
git config user.email "email-associé-à-ton-compte-github"
```

## Migrations de BD

- Une migration poussée sur `develop` ou `main` n'est **jamais modifiée** : on en ajoute une nouvelle.
- Numéros : se coordonner dans l'issue pour éviter que deux personnes prennent le même `V<n>`.

## Livraisons

À la fin de chaque séance, une **pré-livraison** : un tag sur `develop`, que la CI publie comme pré-version.

```bash
git tag -a v0.1.0-SNAPSHOT -m "Pré-livraison fin de séance"
git push origin v0.1.0-SNAPSHOT
```

Pour une **livraison** (exemple : `v0.1.1`) :

1. Sur `develop`, mettre à jour le dossier, `doc/part-ia/` et la page de projet (`site/`), et la version par défaut dans `build.gradle` ; vérifier `./gradlew build`, `./gradlew dossier` et, en local, `./gradlew testInterface`.
2. Ouvrir la **PR de release** de `develop` vers `main` (contenu de la version, vérifications faites, une ligne `Closes #<issue>` par feature livrée), la faire relire et la fusionner.
3. Poser le **tag annoté** sur `main` à jour, et le pousser :

   ```bash
   git switch main && git pull
   git tag -a v0.1.1 -m "Livraison v0.1.1"
   git push origin v0.1.1
   ```

4. Vérifier que la release contient le jar et `dossier.pdf`, et que la page de projet affiche la nouvelle version.

La CI construit le jar et le dossier à partir du tag : la version publiée est celle du tag, sans le « v ».

**Place d'une version dans le dossier.**

| Type de version | Exemple | Numéro | Dans le dossier |
|---|---|---|---|
| Feature | nouvel algorithme, nouvelle table | version mineure (v0.2.0) | sa section, avec sa fiche et son araignée |
| Mineure fonctionnelle | amélioration de saisie ou de visualisation, optimisation d'un algorithme existant, refactoring | patch (v0.1.4) | sa section, avec son araignée, comme une feature |
| Très mineure | documentation, outillage | patch (v0.1.2) | une ligne dans l'annexe « Historique des versions » |

Chaque version, quel que soit son type, a sa ligne dans l'historique des versions.

## Usage de l'IA

Voir le protocole dans `doc/dossier.md`. Chaque pull request le renseigne dans sa section « Usage de l'IA ».
