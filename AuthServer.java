package ca.polymtl.inf8480.tp1.server;
 import java.io.File;
import java.io.FileWriter;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
@@ -9,16 +11,30 @@
import ca.polymtl.inf8480.tp1.shared.AuthInterface;
 public class AuthServer implements AuthInterface {
 	
	private static File connexionFile = new File("connexion.txt");
	
	/*
	 * Permet de créer le fichier connexion.txt qui va contenir les identifiants et les mots de passe.
	 */
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
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
@@ -41,12 +57,26 @@ private void run() {
		}
	}
	
	public String newUser(String login, String password) {
		
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
