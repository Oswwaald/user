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
	
	private String[] arguments = new String[4];
	private String methodeExec = null;
	
	public void main(String[] args) {
		int i = 1;
		if (args.length > 0) 
		{
			methodeExec = args[0];
			while (i < args.length ) 
			{
				arguments[i - 1] = args[i]; 		
				i++;
			}
		}
		Client client = new Client();
		client.run();
	}

	private ServerInterface localServerStub = null;
	private AuthInterface distantServerStub = null;

	/*
	 * Permet d'assurer le paramétrage de la connexion avec les différents serveurs.
	 * Afin de simplifier le paramétrage des serveurs, il a été établi que le serveur de fichiers était local.
	 * Afin de simplifier le paramétrage des serveurs, il a été établi que le serveur d'authentification était distant.
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
	 * Permet de lancer les différentes exécutions de requêtes apportées par le Client.
	 */
	private void run() {
		if (methodeExec == "newUser") {
			newUser(arguments[0], arguments[1]);
		}
		else if (methodeExec == "create") {
			create(arguments[0], arguments[1], arguments[2]);
		}
		else if (methodeExec == "get") {
			get(arguments[0], arguments[1], arguments[2], arguments[3]);
		}
		else if (methodeExec == "push") {
			push(arguments[0], arguments[1], arguments[2], arguments[3]);
		}
		else if (methodeExec == "lock") {
			lock(arguments[0], arguments[1], arguments[2], arguments[3]);
		}
		else if (methodeExec == "list") {
			list(arguments[0], arguments[1]);
		}
		else if (methodeExec == "syncLocalDirectory") {
			syncLocalDirectory(arguments[0], arguments[1]);
		}
		else 
			System.out.println("La méthode " + methodeExec + " n'existe pas pour le Client...");
	}

	/*
	 * Permet de mettre en place le lien entre le Client et le Registre RMI permettant l'accès des méthodes partagées du Serveur de fichier.
	 * La méthode prend en compte la création du Stub (relai du coté client) et l'appel de la liste des méthodes dans le Registre RMI.
	 */	
	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;
		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("fileserver");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage() + "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		return stub;
	}
	
	/*
	 * Permet de mettre en place le lien entre le Client et le Registre RMI permettant l'accès des méthodes partagées du Serveur d'authentification.
	 * La méthode prend en compte la création du Stub (relai du coté client) et l'appel de la liste des méthodes dans le Registre RMI.
	 */	
	private AuthInterface loadAuthStub(String hostname) {
		AuthInterface stub = null;
		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (AuthInterface) registry.lookup("authserver");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage() + "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		return stub;
	}
	
	/*
	 * Lancement de la requête newUser du Client avec le Serveur d'authentification.
	 */
	 private void newUser(String login, String password) {
		 try
		 {
		 	System.out.println("Le client a bien lancé la requête.");
		 	distantAuthStub.newUser(login, password);
		 	System.out.println("Le Serveur a fini de transmettre la réponse");
		 }
		 catch (RemoteException e) {
            		System.out.println("Erreur: " + e.getMessage());
        	}
	 }
	 
	/*
	 * Lancement de la requête create du Client avec le Serveur d'authentification.
	 */	 
	 private void create(String fileName, String login, String password) {
		 try
		 {
		 	System.out.println("Le client a lancé la requête.");
		 	distantServerStub.create(fileName, login, password);
		 	System.out.println("Le Serveur a fini de transmettre la réponse");
		 }
		 catch (RemoteException e) {
            		System.out.println("Erreur: " + e.getMessage());
        	 }
	 }
	 
	/* 
	 * Lancement de la requête get du Client avec le Serveur d'authentification.
	 */	 
	 private String get(String fileName, String checksumClient, String login, String password) {
		 String file = null;
		 try
		 {
		 	System.out.println("Le client a lancé la requête.");
		 	file = distantServerStub.get(fileName, checksumClient, login, password);
		 	System.out.println("Le Serveur a fini de transmettre la réponse");
		 }
		 catch (RemoteException e) {
            		System.out.println("Erreur: " + e.getMessage());
         	}
        	return file;
	 }
	
	/*
	 * Lancement de la requête push du Client avec le Serveur d'authentification.
	 */
	 private void push(String fileName, String content, String login, String password) {
		 try
		 {
		 	System.out.println("Le client a lancé la requête.");
		 	distantServerStub.push(fileName, content, login, password);
		 	System.out.println("Le Serveur a fini de transmettre la réponse");
		 }
		 catch (RemoteException e) {
            	 System.out.println("Erreur: " + e.getMessage());
        	 }
	 }
	 
/*
	 * Lancement de la requête lock du Client avec le Serveur d'authentification.
	 */
	 private void lock(String fileName, String checksumClient, String login, String password) {
		 try
		 {
		 	System.out.println("Le client a lancé la requête.");
		 	distantServerStub.lock(fileName, checksumClient, login, password);
		 	System.out.println("Le Serveur a fini de transmettre la réponse");
		 }
		 catch (RemoteException e) {
            	 System.out.println("Erreur: " + e.getMessage());
         	 }
	 }
	 
	/*
	 * Lancement de la requête list du Client avec le Serveur d'authentification.
	 */
	 private ArrayList<String> list(String login, String password) {
		 ArrayList<String> files = null;
		 try
		 {
		 	System.out.println("Le client a lancé la requête.");
		 	files = distantServerStub.list(login, password);
		 	System.out.println("Le Serveur a fini de transmettre la réponse");
		 }
		 catch (RemoteException e) {
            	 System.out.println("Erreur: " + e.getMessage());
        	 }
        	 return files;
	 }

	/*
	 * Lancement de la requête newUser du Client avec le Serveur de fichier.
	 */
	/*
	 * Lancement de la requête syncLocalDirectory du Client avec le Serveur d'authentification.
	 */
	 private void syncLocalDirectory(String login, String password) {
		 try
		 {
		 	System.out.println("Le client a lancé la requête.");
		 	distantServerStub.syncLocalDirectory(login, password);
		 	System.out.println("Le Serveur a fini de transmettre la réponse");
		 }
		 catch (RemoteException e) {
            	 System.out.println("Erreur: " + e.getMessage());
        	 }
	 }
}
