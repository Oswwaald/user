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
	 * Permet de mettre en place le lien entre le Serveur et le Registre RMI permettant l'acc�s des m�thodes partag�es au Client.
	 * La m�thode prend en compte la cr�ation du Skeleton (relai du cot� serveur) et l'enregistrement des m�thodes dans le Registre RMI.
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
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	/*
	 * Permet de mettre en place le lien entre le Serveur et le Registre RMI permettant l'acc�s des m�thodes partag�es du Serveur d'authentification.
	 * La m�thode prend en compte la cr�ation du Stub (relai du cot� client [ici Server est le client de AuthServer]) et l'appel de la liste des m�thodes dans le Registre RMI.
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
	 * Permet de cr�er un fichier vide sur le serveur.
	 * On v�rifie la l�gitimit� du client.
	 * La m�thode prend en charge le fait que le nom de fichier est d�j� utilis�.
	 */
	public void create(String fileName, String login, String password) throws RemoteException {
		if (verify(login, password)) {
			File filePath = new File("fichiers/" + fileName);
			if (filePath.exists() && !filePath.isDirectory()) {
				System.out.println("Le fichier" + fileName + "existe d�j�");
			}
			else {
				try {
					filePath.createNewFile();
					System.out.println("Le fichier " + fileName + " a �t� cr�� avec succ�s");
				} catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
			}
		}
		else
			System.out.println("Mauvaises informations de connexion.");
	}
	
	/*
	 * Permet de r�cup�rer le contenu du fichier partag� sur le serveur en fonction de sa version (par le checkum).
	 * On v�rifie la l�gitimit� du client.
	 * La m�thode prend en consid�ration l'absence de fichier.
	 * La m�thode prend en consid�ration le cas o� le fichier sur le serveur poss�de le m�me checksum que le fichier du client.
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
						System.out.println("Le fichier " + fileName + " est d�j� � jour");
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
	 * Permet au Client de mettre � jour un fichier sur le serveur en envoyant directement le contenu au Serveur.
	 * On v�rifie la l�gitimit� du client.
	 * La m�thode prend en compte le fait que le Client ne soit pas le propri�taire d'�criture du fichier.
	 * La m�thode prend en compte le fait que le fichier ne soit pas pr�alablement "lock�" par le Client.
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
    				System.out.println("Le fichier " + fileName + " a �t� mis � jour sur le serveur");
    			} catch (Exception e) {
    				System.err.println("Erreur: " + e.getMessage());
    			}
			}
    		else if (!lockOwner.equals(login)) {
    			System.out.println("Ce fichier est v�rouill� par l'utilisateur " + lockOwner);
    		}
    		else {
    			System.out.println("Veuillez verrouiller le fichier avant de le pousser");
    		}
		}
		else
			System.out.println("Mauvaises informations de connexion.");
    }
    
    /*
     * Permet de v�rouiller un fichier le temps qu'un client le modifie et le mette � jour. 
     * On v�rifie la l�gitimit� du client.
     * A la suite du v�rouillage, le client obtient l'information du propri�taire.
     * La m�thode prend en consid�ration l'absence de fichier.
     */
    public void lock(String fileName, String checksumClient, String login, String password) throws RemoteException {
    	if (verify(login, password)) {
			File filePath = new File(fileName);
			if (filePath.exists() && !filePath.isDirectory()) {	
				String fileLockOwner = (String)fileLocks.putIfAbsent(fileName, login);
				if (fileLockOwner == null) {
					get(fileName, checksumClient, login, password);
				}
				System.out.println("L'utilisateur " + fileLockOwner + "d�tient le verrou pour le fichier " + fileName);
			}
			else {
				System.out.println("Aucun fichier " + fileName + " sur le serveur");
			}	
		}
		else
			System.out.println("Mauvaises informations de connexion.");
    }
    
    /*
     * Permet au Client de pour r�cup�rer la liste des fichiers pr�sents dans le dossier.
     * On v�rifie la l�gitimit� du client.
     * La liste comporte un identifiant de fichier, le nom de fichier et le nom du propri�taire (si applicable).
     */
    public String[] list(String login, String password) throws RemoteException {
    	if (verify(login, password)) {
    		try {
    			String[] fileList = null;
    			File repertory = new File("fichiers/");
    			String files[] = repertory.list();
    			if ((files != null) && (files.length > 0)) {
    				for (int i = 0; i < files.length; i++) {	    
    					String fileLockOwner = (String)fileLocks.get(files[i]);
    					fileList.add(i + ": " + files[i] + " " + fileLockOwner);
    				}	
    			} 
    			return fileList;
    		} catch (Exception e) {
    				System.err.println("Erreur: " + e.getMessage());
    		}
		}
		else
			System.out.println("Mauvaises informations de connexion.");
    	return null;
	}
	
    /*
     * Permet au Client de pouvoir r�cup�rer (synchroniser) tous les fichiers pr�sents sur le serveur.
     * On v�rifie la l�gitimit� du client.
     * Si l'existance du dossier avec plus d'un fichier est v�rifi�e, on applique la m�thode get pour r�cup�rer le contenu.
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
	 * Lancement de la requ�te verify du Serveur de fichier avec le Serveur d'authentification .
	 */
	private boolean verify(String login, String password) throws RemoteException {
		return distantServerStub.verify(login, password);
	}
}
  

