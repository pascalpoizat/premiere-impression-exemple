# F<n> : <nom de la feature>

| | |
|---|---|
| Version | v0.1.0 / v0.2.0 / v1.0.0 |
| Responsable | Prénom NOM (`@compte-github`) |
| Algorithme(s) | <nom de l'algorithme> |

## 1. Idéation

### Besoin
<!-- Quel problème de l'utilisateur ? Pourquoi est-ce difficile à résoudre « à la main » ? -->

### Scénarios d'usage
- *En tant que …*, je … **afin de …**

### Modèle
<!-- Entités et objets valeur ; invariants (règles toujours vraies) et où ils sont vérifiés. Lien avec l'entité pivot. -->

### Algorithme
<!-- Principe, pseudo-code ou déroulé sur un petit exemple, complexité en temps et en mémoire,
     alternatives écartées et pourquoi, structures de données choisies. -->

### Impact sur les couches
- **BD** : migration `V<n>__<nom>.sql` (tables, contraintes)
- **Back** : module `<module>` (domain, application, adapters)
- **API** : `MÉTHODE /api/v1/...` (codes de retour)
- **Front** : <écran>

### Critères d'acceptation
- [ ] <comportement observable et testable>
- [ ] <cas d'erreur>

### Limites connues
<!-- Ce qui n'est pas traité, les simplifications assumées, les pistes pour plus tard. -->

## 2. Bilan (à compléter en fin de feature)

### Tests
<!-- Nombre de tests par type, propriétés vérifiées, couverture du module. -->

### Part IA / part humaine
<!-- Valeurs déclarées pour cette feature (mêmes axes que doc/part-ia.json) et explication des dépassements de plafond. -->

| Compétence | Part IA | Plafond |
|---|---|---|
| Idéation | % | 30 % |
| Conception | % | 30 % |
| Spécification | % | 40 % |
| Algorithmique | % | 30 % |
| Validation | % | 20 % |
| Code | % | 70 % |
| Tests | % | 60 % |
| CI/CD | % | 80 % |
| Rédaction | % | 70 % |
| Débogage | % | 70 % |
