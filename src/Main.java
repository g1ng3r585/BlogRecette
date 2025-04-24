import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static List<Ingredient> ingredients = new ArrayList<>();

    // Classe pour représenter une recette
    static class Recette {
        String titre;
        String description;
        String instruction;
        int temps_preparation;
        int temps_cuisson;

        Recette(String titre, String description, String instruction, int temps_preparation, int temps_cuisson) {
            this.titre = titre;
            this.description = description;
            this.instruction = instruction;
            this.temps_preparation = temps_preparation;
            this.temps_cuisson = temps_cuisson;
        }
    }

    // Classe pour représenter un auteur
    static class Auteur {
        String nom;
        String prenom;
        String email;
        String bio;

        Auteur(String nom, String prenom, String email, String bio) {
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.bio = bio;
        }
    }

    static class Ingredient {
        String nom;
        int cote_sante;

        Ingredient(String nom, int cote_sante) {
            this.nom = nom;
            this.cote_sante = cote_sante;
        }
    }
    

    // Listes pour stocker les recettes et les auteurs
    static List<Recette> recettes = new ArrayList<>();
    static List<Auteur> auteurs = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choix;

        do {
            System.out.println("\n=== MENU RECETTES ===");
            System.out.println("1. Ajouter un auteur");
            System.out.println("2. Ajouter une recette");
            System.out.println("3. Ajouter un ingrédient");
            System.out.println("4. Voir les recettes");
            System.out.println("5. Supprimer une recette");
            System.out.println("6. Supprimer un auteur");
            System.out.println("7. Quitter");
            System.out.print("Votre choix : ");
            choix = sc.nextInt();
            sc.nextLine(); // Consommer le \n

            switch (choix) {
                case 1:
                    ajouterAuteur(sc);
                    break;
                case 2:
                    ajouterRecette(sc);
                    break;
                case 3:
                    ajouterIngredient(sc); 
                    break;
                case 4:
                    afficherRecettes();
                    break;
                case 5:
                    supprimerRecette(sc);
                    break;
                case 6:
                    supprimerAuteur(sc);
                    break;
                case 7:
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
            
        } while (choix != 7);

        sc.close();
    }

    // Méthode pour ajouter un auteur
    private static void ajouterAuteur(Scanner sc) {
        System.out.print("Nom de l'auteur : ");
        String nom = sc.nextLine();
        System.out.print("Prénom de l'auteur : ");
        String prenom = sc.nextLine();
        System.out.print("Email de l'auteur : ");
        String email = sc.nextLine();
        System.out.print("Bio de l'auteur : ");
        String bio = sc.nextLine();

        auteurs.add(new Auteur(nom, prenom, email, bio));
        System.out.println("Auteur ajouté !");
    }

    // Méthode pour ajouter une recette
    private static void ajouterRecette(Scanner sc) {
        String titre;
        // Vérification que le titre n'est pas vide
        do {
            System.out.print("Titre de la recette : ");
            titre = sc.nextLine();
            if (titre.trim().isEmpty()) {
                System.out.println("Le titre ne peut pas être vide. Essayez encore !");
            }
        } while (titre.trim().isEmpty());

        String description;
        // Vérification que la description n'est pas vide
        do {
            System.out.print("Description : ");
            description = sc.nextLine();
            if (description.trim().isEmpty()) {
                System.out.println("La description ne peut pas être vide. Essayez encore !");
            }
        } while (description.trim().isEmpty());

        String instruction;
        // Vérification que les instructions ne sont pas vides
        do {
            System.out.print("Instruction : ");
            instruction = sc.nextLine();
            if (instruction.trim().isEmpty()) {
                System.out.println("Les instructions ne peuvent pas être vides. Essayez encore !");
            }
        } while (instruction.trim().isEmpty());

        int temps_preparation = -1;
        int temps_cuisson = -1;

        // Vérification pour le temps de préparation
        while (true) {
            System.out.print("Temps de préparation (en minutes) : ");
            if (sc.hasNextInt()) {
                temps_preparation = sc.nextInt();
                if (temps_preparation < 0) {
                    System.out.println("Le temps ne peut pas être négatif !");
                } else {
                    break;
                }
            } else {
                System.out.println("Veuillez entrer un nombre entier pour le temps de préparation !");
                sc.next(); // consomme la mauvaise entrée
            }
        }

        // Vérification pour le temps de cuisson
        while (true) {
            System.out.print("Temps de cuisson (en minutes) : ");
            if (sc.hasNextInt()) {
                temps_cuisson = sc.nextInt();
                if (temps_cuisson < 0) {
                    System.out.println("Le temps ne peut pas être négatif !");
                } else {
                    break;
                }
            } else {
                System.out.println("Veuillez entrer un nombre entier pour le temps de cuisson !");
                sc.next(); // consomme la mauvaise entrée
            }
        }

        sc.nextLine(); // consomme le saut de ligne restant

        // Ajouter la recette après avoir validé tous les champs
        recettes.add(new Recette(titre, description, instruction, temps_preparation, temps_cuisson));
        System.out.println("Recette ajoutée !");
    }



    // Méthode pour afficher les recettes
    private static void afficherRecettes() {
        if (recettes.isEmpty()) {
            System.out.println("Aucune recette enregistrée.");
        } else {
            System.out.println("Liste des recettes :");
            for (Recette r : recettes) {
                System.out.println("- " + r.titre + " : " + r.description);
            }
        }
    }

    // Méthode pour supprimer une recette
    private static void supprimerRecette(Scanner sc) {
        if (recettes.isEmpty()) {
            System.out.println("Aucune recette à supprimer.");
            return;
        }

        System.out.print("Entrez le titre de la recette à supprimer : ");
        String titreASupprimer = sc.nextLine();

        boolean recetteSupprimee = false;
        for (Recette r : recettes) {
            if (r.titre.equalsIgnoreCase(titreASupprimer)) {
                recettes.remove(r);
                recetteSupprimee = true;
                System.out.println("Recette supprimée !");
                break;
            }
        }

        if (!recetteSupprimee) {
            System.out.println("Recette non trouvée !");
        }
    }

    // Méthode pour supprimer un auteur
    private static void supprimerAuteur(Scanner sc) {
        if (auteurs.isEmpty()) {
            System.out.println("Aucun auteur à supprimer.");
            return;
        }

        System.out.print("Entrez le nom de l'auteur à supprimer : ");
        String nomAuteur = sc.nextLine();

        boolean auteurSupprime = false;
        for (Auteur a : auteurs) {
            if (a.nom.equalsIgnoreCase(nomAuteur)) {
                auteurs.remove(a);
                auteurSupprime = true;
                System.out.println("Auteur supprimé !");
                break;
            }
        }

        if (!auteurSupprime) {
            System.out.println("Auteur non trouvé !");
        }
    }

    // Méthode pour ajouter un ingrédient
    private static void ajouterIngredient(Scanner sc) {
        System.out.print("Nom de l'ingrédient : ");
        String nom = sc.nextLine();

        int cote_sante = -1;
        while (true) {
            System.out.print("Cote santé (1 à 5) : ");
            if (sc.hasNextInt()) {
                cote_sante = sc.nextInt();
                if (cote_sante >= 1 && cote_sante <= 5) {
                    break;
                } else {
                    System.out.println("Cote invalide. Elle doit être entre 1 et 5.");
                }
            } else {
                System.out.println("Veuillez entrer un entier valide (1 à 5).");
                sc.next(); // consommer la mauvaise entrée
            }
        }

        sc.nextLine(); // consommer le \n après le nextInt()

        ingredients.add(new Ingredient(nom, cote_sante));
        System.out.println("Ingrédient ajouté !");
    }

}
