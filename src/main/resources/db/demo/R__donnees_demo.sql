-- Jeu de démonstration de PremièreImpression : profils, candidatures, mensurations, vêtements.
-- Chargé uniquement avec le profil « demo » (voir application-demo.properties).
-- Migration « répétable » (R__) et idempotente : relancer l'application ne duplique rien.

-- Profils (F1)
INSERT INTO profil (pseudo, competences)
SELECT 'alex', 'Java, Spring Boot, API REST, SQL, PostgreSQL, Git, tests unitaires JUnit, méthode agile Scrum'
WHERE NOT EXISTS (SELECT 1 FROM profil WHERE pseudo = 'alex');
INSERT INTO profil (pseudo, competences)
SELECT 'sam', 'Python, SQL, analyse de données, Excel, Power BI, statistiques, visualisation'
WHERE NOT EXISTS (SELECT 1 FROM profil WHERE pseudo = 'sam');
INSERT INTO profil (pseudo, competences)
SELECT 'camille', 'Gestion de projet, ERP, SAP, modélisation des processus métier, UML, SQL, conduite du changement'
WHERE NOT EXISTS (SELECT 1 FROM profil WHERE pseudo = 'camille');
INSERT INTO profil (pseudo, competences)
SELECT 'noa', 'Communication, anglais courant, travail en équipe'
WHERE NOT EXISTS (SELECT 1 FROM profil WHERE pseudo = 'noa');

-- Candidatures (F1)
INSERT INTO candidature (profil_id, entreprise, poste, texte_annonce, date_heure_entretien, adresse_entretien)
SELECT p.id, 'Société Générale', 'Développeur Java / Spring en alternance',
       'Au sein de la DSI, vous rejoindrez une équipe agile (Scrum) de 8 personnes en charge d''une plateforme de paiement.
Vos missions : concevoir et développer des API REST en Java 21 avec Spring Boot ; écrire des tests unitaires et d''intégration (JUnit, Mockito) ;
participer aux revues de code ; contribuer à l''intégration continue (GitLab CI, Docker, Kubernetes) ; surveiller la qualité avec SonarQube.
Profil : formation Bac+4/5 en informatique (MIAGE appréciée), bonnes bases en Java et SQL, curiosité pour Docker et Kubernetes, anglais technique.',
       TIMESTAMP '2026-11-11 10:00:00', '29 boulevard Haussmann, 75009 Paris'
FROM profil p WHERE p.pseudo = 'alex'
  AND NOT EXISTS (SELECT 1 FROM candidature c WHERE c.profil_id = p.id AND c.entreprise = 'Société Générale' AND c.poste = 'Développeur Java / Spring en alternance');
INSERT INTO candidature (profil_id, entreprise, poste, texte_annonce, date_heure_entretien, adresse_entretien)
SELECT p.id, 'Doctolib', 'Développeur back-end junior',
       'Nous recherchons un développeur back-end pour notre équipe Agenda. Stack : Ruby on Rails, PostgreSQL, Redis, Kafka, Docker.
Vous développerez de nouvelles fonctionnalités, optimiserez les requêtes PostgreSQL et participerez aux astreintes.
Une première expérience en développement web et en SQL est indispensable ; la connaissance de Kafka est un plus.',
       TIMESTAMP '2026-11-18 14:30:00', '54 quai Charles Pasqua, 92300 Levallois-Perret'
FROM profil p WHERE p.pseudo = 'alex'
  AND NOT EXISTS (SELECT 1 FROM candidature c WHERE c.profil_id = p.id AND c.entreprise = 'Doctolib' AND c.poste = 'Développeur back-end junior');
INSERT INTO candidature (profil_id, entreprise, poste, texte_annonce, date_heure_entretien, adresse_entretien)
SELECT p.id, 'Decathlon', 'Data analyst en alternance',
       'L''équipe Data Retail recherche un ou une data analyst en alternance. Vous construirez des tableaux de bord Power BI,
écrirez des requêtes SQL sur BigQuery, automatiserez des analyses en Python (pandas) et présenterez vos résultats aux équipes métier.
Rigueur, sens de la communication et appétence pour la statistique sont attendus.',
       TIMESTAMP '2026-11-13 09:30:00', '4 boulevard de Mons, 59650 Villeneuve-d''Ascq'
FROM profil p WHERE p.pseudo = 'sam'
  AND NOT EXISTS (SELECT 1 FROM candidature c WHERE c.profil_id = p.id AND c.entreprise = 'Decathlon' AND c.poste = 'Data analyst en alternance');
INSERT INTO candidature (profil_id, entreprise, poste, texte_annonce, date_heure_entretien, adresse_entretien)
SELECT p.id, 'Capgemini', 'Consultant ERP SAP junior',
       'Vous intégrerez notre practice SAP S/4HANA. Vos missions : recueillir les besoins métier, modéliser les processus (BPMN),
paramétrer les modules finance et achats, rédiger les spécifications fonctionnelles et accompagner la conduite du changement auprès des utilisateurs.
Formation MIAGE ou école de commerce, première expérience ERP souhaitée, anglais professionnel.',
       TIMESTAMP '2026-11-20 11:00:00', '147 quai du Président Roosevelt, 92130 Issy-les-Moulineaux'
FROM profil p WHERE p.pseudo = 'camille'
  AND NOT EXISTS (SELECT 1 FROM candidature c WHERE c.profil_id = p.id AND c.entreprise = 'Capgemini' AND c.poste = 'Consultant ERP SAP junior');

-- Mensurations (F2)
INSERT INTO mensurations (profil_id, tour_poitrine, tour_taille, tour_hanches, longueur_jambe, carrure)
SELECT p.id, 96, 82, 98, 80, 44 FROM profil p WHERE p.pseudo = 'alex'
  AND NOT EXISTS (SELECT 1 FROM mensurations m WHERE m.profil_id = p.id);
INSERT INTO mensurations (profil_id, tour_poitrine, tour_taille, tour_hanches, longueur_jambe, carrure)
SELECT p.id, 88, 70, 96, 76, 39 FROM profil p WHERE p.pseudo = 'sam'
  AND NOT EXISTS (SELECT 1 FROM mensurations m WHERE m.profil_id = p.id);
INSERT INTO mensurations (profil_id, tour_poitrine, tour_taille, tour_hanches, longueur_jambe, carrure)
SELECT p.id, 92, 76, 100, 78, 41 FROM profil p WHERE p.pseudo = 'camille'
  AND NOT EXISTS (SELECT 1 FROM mensurations m WHERE m.profil_id = p.id);

-- Vêtements et leurs mesures (F2) : mesures = corps de référence + aisance idéale + petit décalage
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'VESTE', 'Veste de costume bleu marine', 'Laine, coupe droite, deux boutons' FROM profil p WHERE p.pseudo = 'noa'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Veste de costume bleu marine');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'CARRURE', 45 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Veste de costume bleu marine'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'CARRURE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_POITRINE', 107 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Veste de costume bleu marine'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_POITRINE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 94 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Veste de costume bleu marine'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'VESTE', 'Blazer gris anthracite', 'Coupe ajustée' FROM profil p WHERE p.pseudo = 'noa'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Blazer gris anthracite');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'CARRURE', 47 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Blazer gris anthracite'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'CARRURE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_POITRINE', 112 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Blazer gris anthracite'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_POITRINE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 100 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Blazer gris anthracite'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'VESTE', 'Veste cintrée noire', 'Idéale pour un entretien en cabinet' FROM profil p WHERE p.pseudo = 'camille'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Veste cintrée noire');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'CARRURE', 40 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'camille' AND v.nom = 'Veste cintrée noire'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'CARRURE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_POITRINE', 99 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'camille' AND v.nom = 'Veste cintrée noire'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_POITRINE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 80 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'camille' AND v.nom = 'Veste cintrée noire'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'VESTE', 'Veste en lin beige', 'Plutôt pour l''été' FROM profil p WHERE p.pseudo = 'sam'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Veste en lin beige');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'CARRURE', 47 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'sam' AND v.nom = 'Veste en lin beige'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'CARRURE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_POITRINE', 112 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'sam' AND v.nom = 'Veste en lin beige'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_POITRINE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 97 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'sam' AND v.nom = 'Veste en lin beige'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'CHEMISE', 'Chemise blanche col classique', 'Repassage facile' FROM profil p WHERE p.pseudo = 'noa'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Chemise blanche col classique');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'CARRURE', 45 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Chemise blanche col classique'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'CARRURE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_POITRINE', 106 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Chemise blanche col classique'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_POITRINE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 91 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Chemise blanche col classique'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'CHEMISE', 'Chemisier bleu ciel', NULL FROM profil p WHERE p.pseudo = 'camille'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Chemisier bleu ciel');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'CARRURE', 40 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'camille' AND v.nom = 'Chemisier bleu ciel'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'CARRURE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_POITRINE', 96 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'camille' AND v.nom = 'Chemisier bleu ciel'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_POITRINE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 79 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'camille' AND v.nom = 'Chemisier bleu ciel'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'PANTALON', 'Pantalon de costume bleu marine', 'Assorti à la veste' FROM profil p WHERE p.pseudo = 'noa'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Pantalon de costume bleu marine');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 83 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Pantalon de costume bleu marine'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_HANCHES', 103 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Pantalon de costume bleu marine'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_HANCHES');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'LONGUEUR_JAMBE', 80 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Pantalon de costume bleu marine'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'LONGUEUR_JAMBE');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'PANTALON', 'Pantalon noir à pinces', NULL FROM profil p WHERE p.pseudo = 'sam'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Pantalon noir à pinces');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 77 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'sam' AND v.nom = 'Pantalon noir à pinces'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_HANCHES', 106 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'sam' AND v.nom = 'Pantalon noir à pinces'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_HANCHES');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'LONGUEUR_JAMBE', 77 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'sam' AND v.nom = 'Pantalon noir à pinces'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'LONGUEUR_JAMBE');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'JUPE', 'Jupe droite grise', 'Longueur genou' FROM profil p WHERE p.pseudo = 'camille'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Jupe droite grise');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 71 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'camille' AND v.nom = 'Jupe droite grise'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_HANCHES', 101 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'camille' AND v.nom = 'Jupe droite grise'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_HANCHES');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'JUPE', 'Jupe crayon noire', NULL FROM profil p WHERE p.pseudo = 'noa'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Jupe crayon noire');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 78 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Jupe crayon noire'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_HANCHES', 107 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'noa' AND v.nom = 'Jupe crayon noire'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_HANCHES');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'ROBE', 'Robe fourreau bleu nuit', 'Sobre et élégante' FROM profil p WHERE p.pseudo = 'alex'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Robe fourreau bleu nuit');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_POITRINE', 99 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'alex' AND v.nom = 'Robe fourreau bleu nuit'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_POITRINE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 80 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'alex' AND v.nom = 'Robe fourreau bleu nuit'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_HANCHES', 106 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'alex' AND v.nom = 'Robe fourreau bleu nuit'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_HANCHES');
INSERT INTO vetement (proprietaire_id, type, nom, description)
SELECT p.id, 'ROBE', 'Robe chemise vert sapin', NULL FROM profil p WHERE p.pseudo = 'alex'
  AND NOT EXISTS (SELECT 1 FROM vetement v WHERE v.proprietaire_id = p.id AND v.nom = 'Robe chemise vert sapin');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_POITRINE', 97 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'alex' AND v.nom = 'Robe chemise vert sapin'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_POITRINE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_TAILLE', 78 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'alex' AND v.nom = 'Robe chemise vert sapin'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_TAILLE');
INSERT INTO mesure_vetement (vetement_id, mesure, valeur_cm)
SELECT v.id, 'TOUR_HANCHES', 102 FROM vetement v JOIN profil p ON p.id = v.proprietaire_id
WHERE p.pseudo = 'alex' AND v.nom = 'Robe chemise vert sapin'
  AND NOT EXISTS (SELECT 1 FROM mesure_vetement m WHERE m.vetement_id = v.id AND m.mesure = 'TOUR_HANCHES');
