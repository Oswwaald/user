package ca.polymtl.inf8480.tp1.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf8480.tp1.shared.AuthInterface;

public class AuthServer implements AuthInterface {
	
	private static File connexionFile = new File("connexion.txt");
	private PrintWriter out;
	private BufferedReader br;
	
	/*
	 * Permet de creer le fichier connexion.txt qui va contenir les identifiants et les mots de passe des utilisateurs.
	 */
	public static void main(String[] args) {
		AuthServer server = new AuthServer();
		if (!connexionFile.exists() && !connexionFile.isDirectory()) {
			try {
				connexionFile.createNewFile();
			} catch (Exception e) {
				System.err.println("Erreur: " + e.getMessage());
			}
			server.run();
		}
	}

	public AuthServer() {
		super();
	}
	
	/*
	 * Permet de mettre en place le lien entre le Serveur d'Authentification et le Registre RMI permettant l'acces des methodes partagees au Client et au Serveur.
	 * La methode prend en compte la creation du Skeleton (relai du cote serveur) et l'enregistrement des methodes dans le Registre RMI.
	 */
	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			AuthInterface skeleton = (AuthInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("authserver", skeleton);
			System.out.println("Server d'authentification en marche.");
		} catch (ConnectException e) {
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lance ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	/*
	 * La methode permet de creer un nouvel usager en ajoutant son login et son MDP dans le fichier de connexion.
	 * La methode prend en consideration le fait de continuer a remplir le fichier et non pas ecraser le contenu.
	 * La methode prend en consideration la creation d'une nouvelle ligne a la fin de chaque nouvelle entree.
	 */
	public void newUser(String login, String password) {
		try {
			FileWriter fw = new FileWriter("connexion.txt", true);
    		BufferedWriter bw = new BufferedWriter(fw);
    		out = new PrintWriter(bw);
    		out.println(login + " " + password + "\n");
		} catch (IOException e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	/*
	 * Cette methode verifie si un usager possede le login et le password passes en parametres et retourne un booleen pour valider les informations.
	 * La methode renvoie un boolean en sortie.
	 */		
	public boolean verify(String login, String password) throws IOException {
		br = new BufferedReader (new FileReader (connexionFile));
		String line;
		boolean loginAccepted = false;
		while( (line = br.readLine() ) != null) 
		{		
		    String[] parts = line.split(" ");
		    if (parts[0] == login){
			    if (parts[1] == password){    
			    	loginAccepted = true;
		        }
			    else {      
			    	System.err.println("Mauvais mot de passe");
			    }
		     }
		     else {
		    	 System.err.println("L'utilisateur n'existe pas");
		     }
		 }
		return loginAccepted;
	}
}
