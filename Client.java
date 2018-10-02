package ca.polymtl.inf8480.tp1.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.shared.AuthInterface;

public class Client {
	
	private String[] arguments = new String[4];
	private String methodeExec = null;
	
	public void main(String[] args) {
		int i = 1;
		if (args.length > 0) {
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
	 * Permet d'assurer le parametrage de la connexion avec les differents serveurs.
	 * Afin de simplifier le parametrage des serveurs, il a ete etabli que le serveur de fichiers etait local.
	 * Afin de simplifier le parametrage des serveurs, il a ete etabli que le serveur d'authentification etait distant.
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
	 * Permet de lancer les differentes executions de requetes apportees par le Client.
	 * On retrouve ici la methode partagee par le Seuveur d'authentification et les 6 methodes partagees par le Serveur de fichiers.
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
			System.out.println("La methode " + methodeExec + " n'existe pas pour le Client...");
	}

	/*
	 * Permet de mettre en place le lien entre le Client et le Registre RMI permettant l'acces des methodes partagees du Serveur de fichier.
	 * La methode prend en compte la creation du Stub (relai du cote client) et l'appel de la liste des methodes dans le Registre RMI.
	 */	
	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;
		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("fileserver");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage() + "' n'est pas defini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		return stub;
	}
	
	/*
	 * Permet de mettre en place le lien entre le Client et le Registre RMI permettant l'acces des methodes partagees du Serveur d'authentification.
	 * La methode prend en compte la creation du Stub (relai du cote client) et l'appel de la liste des methodes dans le Registre RMI.
	 */	
	private AuthInterface loadAuthStub(String hostname) {
		AuthInterface stub = null;
		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (AuthInterface) registry.lookup("authserver");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage() + "' n'est pas defini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		return stub;
	}
	
	/*
	 * Lancement de la requete newUser du Client avec le Serveur d'authentification.
	 * On recupere les etats de la requete a titre d'informations sur le suivi de la demande (Optionnel).
	 */
	 private void newUser(String login, String password) {
		 try
		 {
		 	System.out.println("Le client a bien lance la requete.");
		 	distantServerStub.newUser(login, password);
		 	System.out.println("Le Serveur a fini de transmettre la reponse");
		 } catch (RemoteException e) {
            System.out.println("Erreur: " + e.getMessage());
		 }
	 }
	 
	/*
	 * Lancement de la requete create du Client avec le Serveur de fichiers.
	 * On recupere les etats de la requete a titre d'informations sur le suivi de la demande (Optionnel).
	 */	 
	 private void create(String fileName, String login, String password) {
		 try
		 {
			System.out.println("Le client a lance la requete.");
		 	localServerStub.create(fileName, login, password);
		 	System.out.println("Le Serveur a fini de transmettre la reponse");
		 } catch (RemoteException e) {
			 System.out.println("Erreur: " + e.getMessage());
		 } catch (IOException e) {
			 System.out.println("Erreur: " + e.getMessage());			 
		 }
	 }
	 
	/* 
	 * Lancement de la requete get du Client avec le Serveur de fichiers.
	 * On recupere les etats de la requete a titre d'informations sur le suivi de la demande (Optionnel).
	 */	 
	 private void get(String fileName, String checksumClient, String login, String password) {
		 String file = null;
		 try
		 {
			 System.out.println("Le client a lance la requete.");
			 file = localServerStub.get(fileName, checksumClient, login, password);
			 System.out.println("Le Serveur a fini de transmettre la reponse");
			 File filePath = new File(fileName);
			 if (!filePath.exists()) {
				 try {
					 filePath.createNewFile();
				 }catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
			 try {
	    			FileWriter fw = new FileWriter(filePath);
	    			fw.write(file);
	    			fw.close();
	    			System.out.println("Le fichier " + fileName + " a ete mis a jour sur le client");
				 } catch (Exception e) {
	    			System.err.println("Erreur: " + e.getMessage());
				 }
		 } catch (RemoteException e) {
            		System.out.println("Erreur: " + e.getMessage());
		 } catch (IOException e) {
			 System.out.println("Erreur: " + e.getMessage());			 
		 }
	 }
	
	/*
	 * Lancement de la requete push du Client avec le Serveur de fichiers.
	 * On recupere les etats de la requete a titre d'informations sur le suivi de la demande (Optionnel).
	 */
	 private void push(String fileName, String content, String login, String password) {
		 try
		 {
		 	System.out.println("Le client a lance la requete.");
		 	localServerStub.push(fileName, content, login, password);
		 	System.out.println("Le Serveur a fini de transmettre la reponse");
		 } catch (RemoteException e) {
            	 System.out.println("Erreur: " + e.getMessage());
		 } catch (IOException e) {
			 System.out.println("Erreur: " + e.getMessage());			 
		 }
	 }
	 
	 /*
	  * Lancement de la requete lock du Client avec le Serveur de fichiers.
	  * On recupere les etats de la requete a titre d'informations sur le suivi de la demande (Optionnel).
	  */
	 private void lock(String fileName, String checksumClient, String login, String password) {
		 try
		 {
		 	System.out.println("Le client a lance la requete.");
		 	localServerStub.lock(fileName, checksumClient, login, password);
		 	System.out.println("Le Serveur a fini de transmettre la reponse");
		 } catch (RemoteException e) {
            	 System.out.println("Erreur: " + e.getMessage());
		 } catch (IOException e) {
			 System.out.println("Erreur: " + e.getMessage());			 
		 }
	 }
	 
	/*
	 * Lancement de la requete list du Client avec le Serveur de fichiers.
	 * On recupere les etats de la requete a titre d'informations sur le suivi de la demande (Optionnel).
	 */
	 private void list(String login, String password) {
		 try
		 {
		 	System.out.println("Le client a lance la requete.");
		 	localServerStub.list(login, password);
		 	System.out.println("Le Serveur a fini de transmettre la reponse");
		 } catch (RemoteException e) {
            	 System.out.println("Erreur: " + e.getMessage());
		 } catch (IOException e) {
			 System.out.println("Erreur: " + e.getMessage());			 
		 }
	 }

	/*
	 * Lancement de la requete syncLocalDirectory du Client avec le Serveur de fichiers.
	 * On recupere les etats de la requete a titre d'informations sur le suivi de la demande (Optionnel).
	 */
	 private void syncLocalDirectory(String login, String password) {
		 try
		 {
		 	System.out.println("Le client a lance la requete.");
		 	localServerStub.syncLocalDirectory(login, password);
		 	System.out.println("Le Serveur a fini de transmettre la reponse");
		 } catch (RemoteException e) {
            	 System.out.println("Erreur: " + e.getMessage());
		 } catch (IOException e) {
			 System.out.println("Erreur: " + e.getMessage());			 
		 }
	 }
}
