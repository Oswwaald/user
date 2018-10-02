package ca.polymtl.inf8480.tp1.server;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.*;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.shared.AuthInterface;

public class Server implements ServerInterface {

	Map<String,String> fileLocks = new HashMap<String, String>();
	private AuthInterface distantServerStub = null;
	
	public static void main(String[] args) {
		Server server = new Server();
		
		server.run();
	}

	public Server() {
		super();
		distantServerStub = loadAuthStub("132.207.12.243");
	}

	/*
	 * Permet de mettre en place le lien entre le Serveur et le Registre RMI permettant l'accès des méthodes partagées au Client.
	 * La méthode prend en compte la création du Skeleton (relai du coté serveur) et l'enregistrement des méthodes dans le Registre RMI.
	 */
	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			ServerInterface skeleton = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("fileserver", skeleton);
			System.out.println("Serveur de fichiers en marche.");
		} catch (ConnectException e) {
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancÃ© ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	/*
	 * Permet de mettre en place le lien entre le Serveur et le Registre RMI permettant l'accès des méthodes partagées du Serveur d'authentification.
	 * La méthode prend en compte la création du Stub (relai du coté client [ici Server est le client de AuthServer]) et l'appel de la liste des méthodes dans le Registre RMI.
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
	 * Permet de créer un fichier vide sur le serveur.
	 * On vérifie la légitimité du client.
	 * La méthode prend en charge le fait que le nom de fichier est déjà utilisé.
	 */
	public void create(String fileName, String login, String password) throws RemoteException {
		if (verify(login, password)) {
			File filePath = new File("fichiers/" + fileName);
			if (filePath.exists() && !filePath.isDirectory()) {
				System.out.println("Le fichier" + fileName + "existe déjà");
			}
			else {
				try {
					filePath.createNewFile();
					System.out.println("Le fichier " + fileName + " a été créé avec succès");
				} catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
			}
		}
		else
			System.out.println("Mauvaises informations de connexion.");
	} 
	
	/*
	 * Permet de récupérer le contenu du fichier partagé sur le serveur en fonction de sa version (par le checkum).
	 * On vérifie la légitimité du client.
	 * La méthode prend en considération l'absence de fichier.
	 * La méthode prend en considération le cas où le fichier sur le serveur possède le même checksum que le fichier du client.
	 */
	public String get(String fileName, String checksumClient, String login, String password) throws RemoteException {
		if (verify(login, password)) {
			File fileToGet = new File("fichiers/" + fileName);
			if (fileToGet.exists()) {
				try {
					BufferedInputStream ajout = new BufferedInputStream(new FileInputStream(fileToGet));
					StringWriter sortie = new StringWriter();
					int pointeur;
					while ((pointeur=ajout.read()) != -1)
						sortie.write(pointeur);
					sortie.flush();
					sortie.close();
					ajout.close();
					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(password.getBytes());
					byte[] digest = md.digest();
					String checksumServer = DatatypeConverter.printHexBinary(digest).toUpperCase();;
					if (checksumClient == null || checksumClient != checksumServer) {
						return sortie.toString();	
					}
					else
						System.out.println("Le fichier " + fileName + " est déjà à jour");
				}catch (IOException e) {
					System.out.println("Erreur: " + e.getMessage());
				}catch (NoSuchAlgorithmException e) {
					System.out.println("Erreur: " + e.getMessage());
				}
			}
			else {
				System.out.println("Aucun fichier " + fileName + " sur le serveur");
			}
		}
		else
			System.out.println("Mauvaises informations de connexion.");
		return null;
    }
	
	/*
	 * Permet au Client de mettre à jour un fichier sur le serveur en envoyant directement le contenu au Serveur.
	 * On vérifie la légitimité du client.
	 * La méthode prend en compte le fait que le Client ne soit pas le propriétaire d'écriture du fichier.
	 * La méthode prend en compte le fait que le fichier ne soit pas préalablement "locké" par le Client.
	 */
    public void push(String fileName, String content, String login, String password) throws RemoteException {
    	if (verify(login, password)) {
    		String lockOwner = (String)fileLocks.get(fileName);
    		if (lockOwner.equals(login)) {
    			try {
    				File fileToOverwrite = new File(fileName);
    				FileWriter fw = new FileWriter(fileToOverwrite);
    				fw.write(content);
    				fw.close();
    				fileLocks.remove(fileName);
    				System.out.println("Le fichier " + fileName + " a été mis à jour sur le serveur");
    			} catch (Exception e) {
    				System.err.println("Erreur: " + e.getMessage());
    			}
			}
    		else if (!lockOwner.equals(login)) {
    			System.out.println("Ce fichier est vérouillé par l'utilisateur " + lockOwner);
    		}
    		else {
    			System.out.println("Veuillez verrouiller le fichier avant de le pousser");
    		}
		}
		else
			System.out.println("Mauvaises informations de connexion.");
    }
    
    /*
     * Permet de vérouiller un fichier le temps qu'un client le modifie et le mette à jour. 
     * On vérifie la légitimité du client.
     * A la suite du vérouillage, le client obtient l'information du propriétaire.
     * La méthode prend en considération l'absence de fichier.
     */
    public void lock(String fileName, String checksumClient, String login, String password) throws RemoteException {
    	if (verify(login, password)) {
			File filePath = new File(fileName);
			if (filePath.exists() && !filePath.isDirectory()) {	
				String fileLockOwner = (String)fileLocks.putIfAbsent(fileName, login);
				if (fileLockOwner == null) {
					get(fileName, checksumClient, login, password);
				}
				System.out.println("L'utilisateur " + fileLockOwner + "détient le verrou pour le fichier " + fileName);
			}
			else {
				System.out.println("Aucun fichier " + fileName + " sur le serveur");
			}	
		}
		else
			System.out.println("Mauvaises informations de connexion.");
    }
    
    /*
     * Permet au Client de pour récupérer la liste des fichiers présents dans le dossier.
     * On vérifie la légitimité du client.
     * La liste comporte un identifiant de fichier, le nom de fichier et le nom du propriétaire (si applicable).
     */
    public String[] list(String login, String password) throws RemoteException {
	ArrayList<String> fileList = null;
    	if (verify(login, password)) {
    		try {

    			File repertory = new File("fichiers/");
    			String files[] = repertory.list();
    			if ((files != null) && (files.length > 0)) {
    				for (int i = 0; i < files.length; i++) {	    
    					String fileLockOwner = (String)fileLocks.get(files[i]);
    					fileList.add(i + ": " + files[i] + " " + fileLockOwner);
    				}	
    			} 
    		} catch (Exception e) {
    			System.err.println("Erreur: " + e.getMessage());
    		}
    	}
    	else
    		System.out.println("Mauvaises informations de connexion.");
	String[] fileArray = fileList.toArray(new String[0]);    
    	return fileList;
    }
	
    /*
     * Permet au Client de pouvoir récupérer (synchroniser) tous les fichiers présents sur le serveur.
     * On vérifie la légitimité du client.
     * Si l'existance du dossier avec plus d'un fichier est vérifiée, on applique la méthode get pour récupérer le contenu.
     */
	public String[] syncLocalDirectory(String login, String password) throws RemoteException {
		String insertion[];
		if (verify(login, password)) {
			
			File repertory = new File("fichiers/");
			String files[] = repertory.list();
			if ((files != null) && (files.length > 0)) {
				for (int i = 0; i < files.length; i++) {	    
					 insertion[i] = get(files[i], null, login, password);
				}
			}
		}
		else
			System.out.println("Mauvaises informations de connexion.");
		return insertion;
	}
	
	/*
	 * Lancement de la requête verify du Serveur de fichier avec le Serveur d'authentification .
	 */
	private boolean verify(String login, String password) throws RemoteException {
		return distantServerStub.verify(login, password);
	}
}
  

