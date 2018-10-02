package ca.polymtl.inf8480.tp1.server;

import java.io.File;
import java.io.FileWriter;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf8480.tp1.shared.AuthInterface;

public class AuthServer implements AuthInterface {
	
	private static File connexionFile = new File("connexion.dat");
	
	/*
	 * Permet de créer le fichier connexion.txt qui va contenir les identifiants et les mots de passe.
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
	 * Permet de mettre en place le lien entre le Serveur d'Authentification et le Registre RMI permettant l'accès des méthodes partagées au Client et au Serveur.
	 * La méthode prend en compte la création du Skeleton (relai du coté serveur) et l'enregistrement des méthodes dans le Registre RMI.
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
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	public void newUser(String login, String password) {
		try {
			File fileToOverwrite = connexionFile;
			FileWriter fw = new FileWriter(fileToOverwrite);
			fw.write(content);
			fw.close();
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	
	public boolean verify(String login, String password) {
		if {
			
			return true;
		}
		else {
			return false;
		}
		
	}
}
