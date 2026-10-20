package fr.miage.premiereimpression.candidature.domain.algo;

import java.util.Set;

/**
 * Mots sans valeur discriminante, écrits sans accents (ils sont comparés après normalisation).
 * Trois familles : mots grammaticaux français, mots grammaticaux anglais, vocabulaire générique des annonces.
 */
final class MotsVides {

    private static final Set<String> FRANCAIS = Set.of(
            "au", "aux", "avec", "ce", "ces", "cette", "dans", "de", "des", "du", "elle", "en", "et", "eux", "il",
            "ils", "je", "la", "le", "les", "leur", "leurs", "lui", "ma", "mais", "me", "meme", "mes", "moi", "mon",
            "ne", "nos", "notre", "nous", "on", "ou", "par", "pas", "pour", "qu", "que", "qui", "sa", "se", "ses",
            "son", "sur", "ta", "te", "tes", "toi", "ton", "tu", "un", "une", "vos", "votre", "vous", "est", "sont",
            "etre", "avoir", "sera", "serez", "etes", "avez", "aurez", "ete", "fait", "faire", "plus", "tres",
            "tout", "tous", "toute", "toutes", "ainsi", "afin", "selon", "entre", "chez", "sans", "sous", "vers",
            "si", "comme", "dont", "aussi", "autre", "autres", "bien", "peu", "deja", "etc", "via", "lors");

    private static final Set<String> ANGLAIS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "has", "have", "in", "is", "it", "its",
            "of", "on", "or", "our", "that", "the", "their", "this", "to", "we", "will", "with", "you", "your");

    private static final Set<String> ANNONCES = Set.of(
            "poste", "postes", "mission", "missions", "profil", "profils", "candidat", "candidate", "candidats",
            "candidature", "entreprise", "equipe", "equipes", "rejoindre", "rejoignez", "recherchons", "recherche",
            "cdi", "cdd", "hf", "fh", "h", "f", "contrat", "alternance", "stage", "stagiaire", "alternant",
            "alternante", "description", "offre", "type", "lieu", "date", "debut", "duree", "mois", "an", "ans",
            "annee", "annees", "souhaite", "souhaitee", "souhaitees", "idealement", "egalement", "notamment",
            "participer", "participerez", "serez", "chargee", "charge", "travaillerez", "accompagne", "accompagnee",
            "apprecie", "appreciee", "appreciees", "attendu", "attendus", "attendue", "aupres", "sein", "cadre",
            "indispensable", "requis", "requise", "bonne", "bonnes", "bon", "bons", "premiere", "premier",
            "nouvelle", "nouvelles", "nouveau", "nouveaux", "connaissance", "connaissances");

    private MotsVides() {
    }

    static boolean contient(String motNormalise) {
        return FRANCAIS.contains(motNormalise) || ANGLAIS.contains(motNormalise) || ANNONCES.contains(motNormalise);
    }
}
