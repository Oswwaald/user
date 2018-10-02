package ca.polymtl.inf8480.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.shared.AuthInterface;

public class Client {
	
	/*
	 * Ebauche d'id�e permettant de transmettre les bons arguments aux bonnes fonctions du Client ?! A d�velopper...
	 */
	private int i = 1;
	private String methodeExec = null;
	private String argument1 = null;
	private String argument2 = null;
	private String argument3 = null;
	private String argument4 = null;
	
	public void main(String[] args) {
		
		if (args.length > 0) {
			methodeExec = args[0];
			while (i < args.length ) {
				"argument"+String.valueof(i) = arg[i]; 							//Pas sur du tout que �a compile �a...
				i++;
			}
		}
		Client client = new Client();
		client.run();
	}

	private ServerInterface localServerStub = null;
	private AuthInterface distantServerStub = null;

	/*
	 * Permet d'assurer le param�trage de la connexion avec les diff�rents serveurs.
	 * Afin de simplifier le param�trage des serveurs, il a �t� �tabli que le serveur de fichiers �tait local.
	 * Afin de simplifier le param�trage des serveurs, il a �t� �tabli que le serveur d'authentification �tait distant.
	 */
	public Client() {
		super();
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		localServerStub = loadServerStub("127.0.0.1");
		distantServerStub = loadAuthStub("132.207.12.243");
	}

	/*
	 * Permet de lancer les diff�rentes ex�cutions de requ�tes apport�es par le Client.
	 */
	private void run() {
		if (methodeExec == "newUser") {
			newUser(argument1, argument2);
		}
		else if (methodeExec == "create") {
			create(argument1, argument2, argument3);
		}
		else if (methodeExec == "get") {
			get(argument1, argument2, argument3, argument4);
		}
		else if (methodeExec == "push") {
			push(argument1, argument2, argument3, argument4);
		}
		else if (methodeExec == "lock") {
			lock(argument1, argument2, argument3, argument4);
		}
		else if (methodeExec == "list") {
			list(argument1, argument2);
		}
		else if (methodeExec == "syncLocalDirectory") {
			syncLocalDirectory(argument1, argument2);
		}
		else 
			System.out.println("La m�thode " + methodeExec + " n'existe pas pour le Client...");
	}

	/*
	 * Permet de mettre en place le lien entre le Client et le Registre RMI permettant l'acc�s des m�thodes partag�es du Serveur de fichier.
	 * La m�thode prend en compte la cr�ation du Stub (relai du cot� client) et l'appel de la liste des m�thodes dans le Registre RMI.
	 */	
	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;
		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("fileserver");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage() + "' n'est pas d�fini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		return stub;
	}
	
	/*
	 * Permet de mettre en place le lien entre le Client et le Registre RMI permettant l'acc�s des m�thodes partag�es du Serveur d'authentification.
	 * La m�thode prend en compte la cr�ation du Stub (relai du cot� client) et l'appel de la liste des m�thodes dans le Registre RMI.
	 */	
	private AuthInterface loadAuthStub(String hostname) {
		AuthInterface stub = null;
		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (AuthInterface) registry.lookup("authserver");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage() + "' n'est pas d�fini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		return stub;
	}
	
	/*
	 * Lancement de la requ�te newUser du Client avec le Serveur d'authentification.
	 */
	 private void newUser(String login, String password) {
		 System.out.println("Le client a bien lanc� la requ�te.");
		 distantServerStub.newUser(login, password);
		 System.out.println("Le Serveur a fini de transmettre la r�ponse");
	 }
	 
	/*
	 * Lancement de la requ�te newUser du Client avec le Serveur de fichier.
	 */	 
	 private void create(String fileName, String login, String password) {
		 System.out.println("Le client a lanc� la requ�te.");
		 localServerStub.create(fileName, login, password);
		 System.out.println("Le Serveur a fini de transmettre la r�ponse");
	 }
	 
	/* 
	 * Lancement de la requ�te newUser du Client avec le Serveur de fichier.
	 */	 
	 private String get(String fileName, String checksumClient, String login, String password) {
		 System.out.println("Le client a lanc� la requ�te.");
		 localServerStub.get(fileName, checksumClient, login, password);
		 System.out.println("Le Serveur a fini de transmettre la r�ponse");
	 }
	
	/*
	 * Lancement de la requ�te newUser du Client avec le Serveur de fichier.
	 */
	 private void push(String fileName, String content, String login, String password) {
		 System.out.println("Le client a lanc� la requ�te.");
		 localServerStub.push(filename, content, login, password);
		 System.out.println("Le Serveur a fini de transmettre la r�ponse");
	 }
	 
	/*
	 * Lancement de la requ�te newUser du Client avec le Serveur de fichier.
	 */
	 private void lock(String fileName, String checksumClient, String login, String password) {
		 System.out.println("Le client a lanc� la requ�te.");
		 localServerStub.lock(filename, checksumClient, login, password);
		 System.out.println("Le Serveur a fini de transmettre la r�ponse");
	 }
	 
	/*
	 * Lancement de la requ�te newUser du Client avec le Serveur de fichier.
	 */
	 private ArrayList<String> list(String login, String password) {
		 System.out.println("Le client a lanc� la requ�te.");
		 return localServerStub.list(login, password);
		 System.out.println("Le Serveur a fini de transmettre la r�ponse");
	 }

	/*
	 * Lancement de la requ�te newUser du Client avec le Serveur de fichier.
	 */
	 private void syncLocalDirectory(String login, String password) {
		 System.out.println("Le client a lanc� la requ�te.");
		 localServerStub.syncLocalDirectory(login, password);
		 System.out.println("Le Serveur a fini de transmettre la r�ponse");
	 }
}
