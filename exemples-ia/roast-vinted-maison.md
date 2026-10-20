# Exemple d'échange avec l'IA : faire critiquer son idée de projet

> Témoin d'usage de l'IA en phase d'idéation. On soumet une idée en demandant une critique sans complaisance au regard des attentes du projet. On attend en retour une analyse précise et argumentée, puis des pistes d'amélioration concrètes.

## La question

> J'ai une idée, roast-la au regard des demandes du prof :
> c'est un Vinted maison où on peut se prêter des vêtements entre étudiants pour des entretiens. Je pense qu'avec ça on va tirer 12 features car on est 4.

## La réponse de l'IA

### Le roast 🔥

**1. « Un Vinted maison »**
Tu présentes toi-même le projet comme une copie d'une application existante. Pour la rubrique « caractère innovant » du dossier, c'est mal parti.

**2. « On peut se prêter »**
Le prêt entre étudiants fait partie des sujets que le prof a déjà jugés « bof ». Remplacer la perceuse par une veste ne change pas le problème.

**3. C'est une surcouche sur une BD**
Écris honnêtement la liste des features : publier un vêtement, rechercher, réserver, rendre, noter le prêteur, messagerie, favoris, filtres par taille… Ce sont 12 formulaires. Aucune ne contient un algorithme : on fait une requête avec un `WHERE taille = 'M'` et c'est terminé. C'est exactement le cas « une seule méthode de 3 lignes » que le prof refuse.

**4. « On est 4, donc 12 features »**
C'est une multiplication, pas une justification. Le nombre de features se déduit du sujet, pas de la taille de l'équipe. En soutenance, à la question « où est l'algorithme dans ta feature ? », il n'y aura rien à répondre.

**5. Le cas d'usage est étroit**
Combien de fois par an un étudiant a-t-il un entretien ? Avec quelques dizaines de vêtements dans le catalogue, il n'y a ni volume ni problème à optimiser.

**Verdict : en l'état, refusé.** On est sur du CRUD, du déjà-vu, et sur la catégorie « prêt » que le prof a explicitement écartée.

### Ce qui est sauvable

L'intuition de départ est bonne et concrète : l'apprenti qui n'a pas la tenue pour son entretien. Le problème, c'est que vous avez choisi l'angle catalogue. Il faut changer d'angle et rendre le système responsable de **décisions difficiles à calculer**. Par exemple :

| Feature | Algorithme |
|---|---|
| Ajustement morphologique : quel vêtement va à qui, à partir des mensurations et pas seulement de la taille de l'étiquette | distance pondérée entre mensurations, **k plus proches voisins** |
| Composer une tenue complète et cohérente (couleurs, style imposé par le type d'entretien) | **satisfaction de contraintes** avec backtracking |
| Répartir les vêtements quand plusieurs entretiens tombent la même semaine | **couplage biparti** pondéré (algorithme hongrois) |
| Planifier les prêts dans le temps, avec les délais de nettoyage et de remise | **ordonnancement** sur des intervalles, détection de conflits |
| Lieu de remise équitable pour le prêteur et l'emprunteur | plus court chemin dans un graphe, **médiane géométrique** |
| Fiabilité des prêteurs avec peu d'avis | moyenne **bayésienne** |
| Recommandation de vêtements | **filtrage collaboratif** |

Avec ce recentrage, ça ne devient plus « Vinted » mais un système d'appariement et de planification sous contraintes, dont le prêt de vêtements n'est que le prétexte. C'est défendable.

**Avis honnête :** c'est récupérable, mais l'étiquette « prêt » colle à la peau du sujet, et vous partez avec un a priori négatif du prof. Si vous y tenez, présentez-le dans le dossier par son angle algorithmique (« composition et appariement de tenues sous contraintes »), pas par « Vinted ». Faites aussi l'exercice suivant : écrivez les 12 features en face de leur algorithme. Si vous n'arrivez pas à remplir la colonne algorithme pour au moins 10 d'entre elles, changez de sujet.
