import java.util.Scanner;
import java.sql.*;

/**
 * Classe principale de l'application de gestion de recettes.
 * Permet d'ajouter, afficher et supprimer des recettes, auteurs et ingrédients.
 */
public class Main {

    /**
     * Classe représentant une recette dans le système.
     */
    static class Recette {
        /** Titre de la recette. */
        String titre;
        /** Description de la recette. */
        String description;
        /** Instructions de préparation. */
        String instructions;
        /** Temps de préparation en minutes. */
        int temps_preparation;
        /** Temps de cuisson en minutes. */
        int temps_cuisson;
        /** Identifiant de l'auteur. */
        int auteur_id;

        /**
         * Constructeur pour créer une nouvelle recette.
         *
         * @param titre Le titre de la recette
         * @param description La description de la recette
         * @param instructions Les instructions de préparation
         * @param temps_preparation Le temps de préparation en minutes
         * @param temps_cuisson Le temps de cuisson en minutes
         * @param auteur_id L'identifiant de l'auteur de la recette
         */
        Recette(String titre, String description, String instructions, int temps_preparation, int temps_cuisson, int auteur_id) {
            this.titre = titre;
            this.description = description;
            this.instructions = instructions;
            this.temps_preparation = temps_preparation;
            this.temps_cuisson = temps_cuisson;
            this.auteur_id = auteur_id;
        }
    }

    /**
     * Classe représentant un auteur dans le système.
     */
    static class Auteur {
        /** Nom de l'auteur. */
        String nom;
        /** Email de l'auteur. */
        String email;
        /** Biographie de l'auteur. */
        String bio;

        /**
         * Constructeur pour créer un nouvel auteur.
         *
         * @param nom Le nom de l'auteur
         * @param email L'email de l'auteur
         * @param bio La biographie de l'auteur
         */
        Auteur(String nom, String email, String bio) {
            this.nom = nom;
            this.email = email;
            this.bio = bio;
        }
    }

    /**
     * Classe représentant un ingrédient dans le système.
     */
    static class Ingredient {
        /** Nom de l'ingrédient. */
        String nom;
        /** Cote santé sur une échelle de 1 à 5. */
        int cote_sante;

        /**
         * Constructeur pour créer un nouvel ingrédient.
         *
         * @param nom Le nom de l'ingrédient
         * @param cote_sante La cote santé sur une échelle de 1 à 5
         */
        Ingredient(String nom, int cote_sante) {
            this.nom = nom;
            this.cote_sante = cote_sante;
        }
    }

    /**
     * Point d'entrée principal de l'application.
     * Affiche un menu permettant à l'utilisateur d'interagir avec le système de recettes.
     *
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        try {
            DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données:");
            e.printStackTrace();
            return;
        }
        
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
            System.out.println("7. Voir les auteurs");
            System.out.println("8. Voir les ingrédients");
            System.out.println("9. Quitter");
            System.out.print("Votre choix : ");
            choix = sc.nextInt();
            sc.nextLine();

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
                    afficherAuteurs();
                    break;
                case 8:
                    afficherIngredients();
                    break;
                case 9:
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
            
        } while (choix != 9);

        sc.close();
        DatabaseConnection.closeConnection();
    }

    /**
     * Ajoute un nouvel auteur dans la base de données.
     *
     * @param sc Scanner pour lire les entrées utilisateur
     */
    private static void ajouterAuteur(Scanner sc) {
        System.out.print("Nom de l'auteur : ");
        String nom = sc.nextLine();
        System.out.print("Email de l'auteur : ");
        String email = sc.nextLine();
        System.out.print("Bio de l'auteur : ");
        String bio = sc.nextLine();

        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                    "INSERT INTO Auteur (nom, email, bio) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, nom);
            pstmt.setString(2, email);
            pstmt.setString(3, bio);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("\nAuteur ajouté avec succès !");
                    System.out.println("------------------");
                    System.out.println("ID: " + id);
                    System.out.println("Nom: " + nom);
                    System.out.println("Email: " + email);
                    if (bio != null && !bio.isEmpty()) {
                        System.out.println("Bio: " + bio);
                    }
                    System.out.println("------------------");
                } else {
                    System.out.println("Auteur ajouté avec succès mais impossible de récupérer l'ID.");
                }
            } else {
                System.out.println("Échec de l'ajout de l'auteur.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'auteur : " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
    }

    /**
     * Affiche la liste de tous les auteurs dans la base de données.
     */
    private static void afficherAuteurs() {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id_auteur, nom, email, bio FROM Auteur");
            
            boolean found = false;
            System.out.println("\nListe des auteurs :");
            System.out.println("------------------");
            
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id_auteur");
                String nom = rs.getString("nom");
                String email = rs.getString("email");
                String bio = rs.getString("bio");
                
                System.out.println(id + ". " + nom + " (" + email + ")");
                if (bio != null && !bio.trim().isEmpty()) {
                    System.out.println("   Bio: " + bio);
                }
                System.out.println();
            }
            
            System.out.println("------------------");
            
            if (!found) {
                System.out.println("Aucun auteur enregistré.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des auteurs : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
    }

    /**
     * Ajoute une nouvelle recette dans la base de données.
     * Permet également d'ajouter des ingrédients à la recette.
     *
     * @param sc Scanner pour lire les entrées utilisateur
     */
    private static void ajouterRecette(Scanner sc) {
        afficherAuteurs();
        
        System.out.print("\nEntrez l'ID de l'auteur de la recette : ");
        int auteur_id;
        try {
            auteur_id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID d'auteur invalide. Opération annulée.");
            return;
        }

        String titre;
        do {
            System.out.print("Titre de la recette : ");
            titre = sc.nextLine();
            if (titre.trim().isEmpty()) {
                System.out.println("Le titre ne peut pas être vide. Essayez encore !");
            }
        } while (titre.trim().isEmpty());

        String description;
        do {
            System.out.print("Description : ");
            description = sc.nextLine();
            if (description.trim().isEmpty()) {
                System.out.println("La description ne peut pas être vide. Essayez encore !");
            }
        } while (description.trim().isEmpty());

        String instructions;
        do {
            System.out.print("Instructions : ");
            instructions = sc.nextLine();
            if (instructions.trim().isEmpty()) {
                System.out.println("Les instructions ne peuvent pas être vides. Essayez encore !");
            }
        } while (instructions.trim().isEmpty());

        int temps_preparation = -1;
        int temps_cuisson = -1;

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
                sc.next();
            }
        }

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
                sc.next();
            }
        }

        sc.nextLine();

        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            String sql = "INSERT INTO Recette (titre, description, instructions, temps_preparation, temps_cuisson, auteur_id) VALUES (?, EMPTY_CLOB(), EMPTY_CLOB(), ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, new String[] {"id_recette"});
            
            pstmt.setString(1, titre);
            pstmt.setInt(2, temps_preparation);
            pstmt.setInt(3, temps_cuisson);
            pstmt.setInt(4, auteur_id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    
                    PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE Recette SET description = ?, instructions = ? WHERE id_recette = ?");
                    updateStmt.setString(1, description);
                    updateStmt.setString(2, instructions);
                    updateStmt.setInt(3, id);
                    updateStmt.executeUpdate();
                    updateStmt.close();
                    
                    System.out.println("\nRecette ajoutée avec succès !");
                    System.out.println("------------------");
                    System.out.println("ID: " + id);
                    System.out.println("Titre: " + titre);
                    System.out.println("Description: " + description);
                    System.out.println("Instructions: " + instructions);
                    System.out.println("Temps de préparation: " + temps_preparation + " min");
                    System.out.println("Temps de cuisson: " + temps_cuisson + " min");
                    System.out.println("------------------");
                    System.out.println("\nVoulez-vous ajouter des ingrédients à cette recette? (O/N)");
                    String reponse = sc.nextLine();
                    if (reponse.equalsIgnoreCase("O")) {
                        ajouterIngredientsRecette(sc, id);
                    }
                } else {
                    System.out.println("Recette ajoutée avec succès mais impossible de récupérer l'ID.");
                }
            } else {
                System.out.println("Échec de l'ajout de la recette.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la recette : " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
    }

    /**
     * Ajoute des ingrédients à une recette existante.
     *
     * @param sc Scanner pour lire les entrées utilisateur
     * @param idRecette ID de la recette à laquelle ajouter des ingrédients
     */
    private static void ajouterIngredientsRecette(Scanner sc, int idRecette) {
        boolean continuer = true;
        
        while (continuer) {
            Statement stmt = null;
            ResultSet rs = null;
            try {
                Connection conn = DatabaseConnection.getConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT id_ingredient, nom FROM Ingredient ORDER BY nom");
                
                System.out.println("\nListe des ingrédients disponibles :");
                System.out.println("-------------------------------");
                
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("id_ingredient");
                    String nom = rs.getString("nom");
                    System.out.println(id + ". " + nom);
                }
                
                if (!found) {
                    System.out.println("Aucun ingrédient disponible. Veuillez d'abord en ajouter.");
                    return;
                }
                
                System.out.print("\nEntrez l'ID de l'ingrédient à ajouter : ");
                int idIngredient;
                try {
                    idIngredient = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("ID invalide. Opération annulée.");
                    return;
                }
                
                System.out.print("Quantité (ex: '2 tasses', '100g') : ");
                String quantite = sc.nextLine();
                
                PreparedStatement pstmtCheck = conn.prepareStatement("SELECT COUNT(*) FROM Ingredient WHERE id_ingredient = ?");
                pstmtCheck.setInt(1, idIngredient);
                ResultSet rsCheck = pstmtCheck.executeQuery();
                
                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                    PreparedStatement pstmtInsert = conn.prepareStatement(
                        "INSERT INTO Recette_Ingredient (id_recette, id_ingredient, quantite) VALUES (?, ?, ?)");
                    pstmtInsert.setInt(1, idRecette);
                    pstmtInsert.setInt(2, idIngredient);
                    pstmtInsert.setString(3, quantite);
                    
                    int rowsAffected = pstmtInsert.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Ingrédient ajouté à la recette !");
                    } else {
                        System.out.println("Échec de l'ajout de l'ingrédient à la recette.");
                    }
                    
                    pstmtInsert.close();
                } else {
                    System.out.println("Ingrédient non trouvé !");
                }
                
                rsCheck.close();
                pstmtCheck.close();
                
                System.out.print("\nVoulez-vous ajouter un autre ingrédient? (O/N) : ");
                String reponse = sc.nextLine();
                continuer = reponse.equalsIgnoreCase("O");
                
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'ajout des ingrédients : " + e.getMessage());
                continuer = false;
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
                }
            }
        }
    }

    /**
     * Affiche la liste de toutes les recettes avec leurs ingrédients.
     */
    private static void afficherRecettes() {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                "SELECT r.id_recette, r.titre, r.description, r.temps_preparation, r.temps_cuisson, a.nom as auteur_nom " +
                "FROM Recette r " +
                "JOIN Auteur a ON r.auteur_id = a.id_auteur");
            
            boolean found = false;
            System.out.println("\nListe des recettes :");
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id_recette");
                String titre = rs.getString("titre");
                String description = rs.getString("description");
                int temps_prep = rs.getInt("temps_preparation");
                int temps_cuisson = rs.getInt("temps_cuisson");
                String auteur = rs.getString("auteur_nom");
                
                System.out.println("ID: " + id + " - " + titre);
                System.out.println("  Par: " + auteur);
                System.out.println("  Description: " + description);
                System.out.println("  Temps de préparation: " + temps_prep + " min, Temps de cuisson: " + temps_cuisson + " min");
                
                afficherIngredientsRecette(id);
                
                System.out.println();
            }
            
            if (!found) {
                System.out.println("Aucune recette enregistrée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des recettes : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
    }

    /**
     * Affiche les ingrédients associés à une recette spécifique.
     *
     * @param idRecette ID de la recette dont on veut afficher les ingrédients
     */
    private static void afficherIngredientsRecette(int idRecette) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                "SELECT i.nom, ri.quantite FROM Recette_Ingredient ri " +
                "JOIN Ingredient i ON ri.id_ingredient = i.id_ingredient " +
                "WHERE ri.id_recette = ?");
            
            pstmt.setInt(1, idRecette);
            rs = pstmt.executeQuery();
            
            boolean foundIngredients = false;
            System.out.println("  Ingrédients:");
            
            while (rs.next()) {
                foundIngredients = true;
                String nom = rs.getString("nom");
                String quantite = rs.getString("quantite");
                
                System.out.println("    - " + quantite + " de " + nom);
            }
            
            if (!foundIngredients) {
                System.out.println("    Aucun ingrédient enregistré pour cette recette.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des ingrédients : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
    }

    /**
     * Supprime une recette de la base de données.
     *
     * @param sc Scanner pour lire les entrées utilisateur
     */
    private static void supprimerRecette(Scanner sc) {
        System.out.print("Entrez l'ID de la recette à supprimer : ");
        int idRecette;
        try {
            idRecette = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID invalide. Opération annulée.");
            return;
        }

        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtDelete = null;
        ResultSet rs = null;
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            pstmtSelect = conn.prepareStatement(
                "SELECT r.*, a.nom as auteur_nom FROM Recette r " +
                "JOIN Auteur a ON r.auteur_id = a.id_auteur " +
                "WHERE r.id_recette = ?");
            pstmtSelect.setInt(1, idRecette);
            rs = pstmtSelect.executeQuery();
            
            if (rs.next()) {
                String titre = rs.getString("titre");
                String description = rs.getString("description");
                String auteurNom = rs.getString("auteur_nom");
                int tempsPrep = rs.getInt("temps_preparation");
                int tempsCuisson = rs.getInt("temps_cuisson");
                
                pstmtDelete = conn.prepareStatement("DELETE FROM Recette WHERE id_recette = ?");
                pstmtDelete.setInt(1, idRecette);
                
                int rowsAffected = pstmtDelete.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("\nRecette supprimée avec succès !");
                    System.out.println("------------------");
                    System.out.println("ID: " + idRecette);
                    System.out.println("Titre: " + titre);
                    System.out.println("Auteur: " + auteurNom);
                    System.out.println("Description: " + description);
                    System.out.println("Temps de préparation: " + tempsPrep + " min");
                    System.out.println("Temps de cuisson: " + tempsCuisson + " min");
                    System.out.println("------------------");
                } else {
                    System.out.println("Échec de la suppression de la recette.");
                }
            } else {
                System.out.println("Recette non trouvée !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la recette : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtSelect != null) pstmtSelect.close();
                if (pstmtDelete != null) pstmtDelete.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
    }

    /**
     * Supprime un auteur de la base de données.
     * Les recettes associées à cet auteur seront également supprimées.
     *
     * @param sc Scanner pour lire les entrées utilisateur
     */
    private static void supprimerAuteur(Scanner sc) {
        System.out.print("Entrez l'ID de l'auteur à supprimer : ");
        int idAuteur;
        try {
            idAuteur = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID invalide. Opération annulée.");
            return;
        }

        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtDelete = null;
        ResultSet rs = null;
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            pstmtSelect = conn.prepareStatement("SELECT * FROM Auteur WHERE id_auteur = ?");
            pstmtSelect.setInt(1, idAuteur);
            rs = pstmtSelect.executeQuery();
            
            if (rs.next()) {
                String nom = rs.getString("nom");
                String email = rs.getString("email");
                String bio = rs.getString("bio");
                
                pstmtDelete = conn.prepareStatement("DELETE FROM Auteur WHERE id_auteur = ?");
                pstmtDelete.setInt(1, idAuteur);
                
                int rowsAffected = pstmtDelete.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("\nAuteur supprimé avec succès !");
                    System.out.println("------------------");
                    System.out.println("ID: " + idAuteur);
                    System.out.println("Nom: " + nom);
                    System.out.println("Email: " + email);
                    if (bio != null && !bio.isEmpty()) {
                        System.out.println("Bio: " + bio);
                    }
                    System.out.println("------------------");
                    System.out.println("Toutes ses recettes ont également été supprimées.");
                } else {
                    System.out.println("Échec de la suppression de l'auteur.");
                }
            } else {
                System.out.println("Auteur non trouvé !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'auteur : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtSelect != null) pstmtSelect.close();
                if (pstmtDelete != null) pstmtDelete.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
    }

    /**
     * Ajoute un nouvel ingrédient dans la base de données.
     *
     * @param sc Scanner pour lire les entrées utilisateur
     */
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
                sc.next();
            }
        }

        sc.nextLine();

        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                    "INSERT INTO Ingredient (nom, cote_sante) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nom);
            pstmt.setInt(2, cote_sante);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("\nIngrédient ajouté avec succès !");
                    System.out.println("------------------");
                    System.out.println("ID: " + id);
                    System.out.println("Nom: " + nom);
                    System.out.println("Cote santé: " + cote_sante + "/5");
                    System.out.println("------------------");
                } else {
                    System.out.println("Ingrédient ajouté avec succès mais impossible de récupérer l'ID.");
                }
            } else {
                System.out.println("Échec de l'ajout de l'ingrédient.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'ingrédient : " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
    }

    /**
     * Affiche la liste de tous les ingrédients dans la base de données.
     */
    private static void afficherIngredients() {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id_ingredient, nom, cote_sante FROM Ingredient ORDER BY nom");
            
            boolean found = false;
            System.out.println("\nListe des ingrédients :");
            System.out.println("---------------------");
            
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id_ingredient");
                String nom = rs.getString("nom");
                int coteSante = rs.getInt("cote_sante");
                
                System.out.println(id + ". " + nom + " (Cote santé: " + coteSante + "/5)");
            }
            
            System.out.println("---------------------");
            
            if (!found) {
                System.out.println("Aucun ingrédient enregistré.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des ingrédients : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
    }
}