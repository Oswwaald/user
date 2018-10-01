package ca.polymtl.inf8480.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.HashMap;
import java.util.Map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.io.IOUtils;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Server implements ServerInterface {
	
	Map<String,String> fileLocks = new HashMap();
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public Server() {
		super();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancÃ© ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	/*
	 * Permet de créer un fichier vide sur le serveur.
	 * On vérifie la légitimité du client.
	 * La méthode prend en charge le fait que le nom de fichier est déjà utilisé.
	 */
	public void create(String fileName, String login, String password)
	{
		if (verify(login, password))
		{
			String filePath = "fichiers/" + fileName;
			if (filePath.exists() && !filePath.isDirectory())
				System.out.println("Le fichier" + fileName + "existe déjà");
			else
			{
				File newFile = new File(filePath);
				newFile.createNewFile();
				System.out.println("Le fichier " + fileName + " a été créé avec succès");
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
	public String get(String fileName, String checksumClient, String login, String password)
	{
		if (verify(login, password))
		{
			String filePath = "fichiers/" + fileName;
			if (filePath.exists() && !filePath.isDirectory())
			{		
				File fileToGet = new File(filePath);
				FileInputStream fis = new FileInputStream(fileToGet);
				String checksumServer = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
				if (checksumClient == null || checksumClient != checksumServer)
				{
					String contenuFile = IOUtils.toString(fis).trim();
					return contenuFile
				}
				else(checksumClient == checksumServer)
					System.out.println("Le fichier " + fileName + " est déjà à jour");
			}
			else
			{
				System.out.println("Aucun fichier " + fileName + " sur le serveur");
			}
		}
		else
			System.out.println("Mauvaises informations de connexion.");
    }
    
	/*
	 * 
	 */
    public void push(String fileName, byte[] content, String login, String password)
    {
    	if (verify(login, password))
		{
    		String lockOwner = (String)fileLocks.get(fileName);
    		if (lockerOwner == null)
    			System.out.println("Veuillez verrouiller le fichier avant de le pousser");
    		if (!lockOwner.equals(login) {
    			System.out.println("Ce fichier est vérouillé par l'utilisateur " + lockOwner);
    		}
    		File fileToOverwrite = new File("fichiers/" + fileName);
    		try {
    			FileWriter fw= new FileWriter(fileToOverwrite);
    			fw.write(content);
    			fw.close();
    		} 
    		fileLocks.remove(fileName);
    		System.out.println("Le fichier " + fileName + " a été mis à jour sur le serveur");
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
    public void lock(String fileName, String checksumClient, String login, String password)
    {
    	if (verify(login, password))
		{
			String filePath = "fichiers/" + fileName;
			if (filePath.exists() && !filePath.isDirectory()) 
			{	
				String fileLockOwner = (String)fileLocks.putIfAbsent(fileName, login);
				if (fileLockOwner == null) {
					get(fileName, checksumClient, String login, String password);
				}
				System.out.println("L'utilisateur " + fileLockOwner + "détient le verrou pour le fichier " + fileName);
			}
			else 
			{
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
    public ArrayList<String> list(String login, String password)
	{
    	if (verify(login, password))
		{
    		ArrayList<String> fileList = new ArrayList();
    		File repertory = new File("fichiers/");
    		String files[] = repertory.list();
    		if ((files != null) && (files.length > 0)) 
    		{
    			for (int i = 0; i < files.length; i++) 
    			{	    
    				String fileLockOwner = (String)fileLocks.get(file);
    				fileList.add(i + ": " + files[i] + " " + fileLockOwner);
    			}
    		}
    		return fileList;
		}
		else
			System.out.println("Mauvaises informations de connexion.");
	}
	
    /*
     * Permet au Client de pouvoir récupérer (synchroniser) tous les fichiers présents sur le serveur.
     * On vérifie la légitimité du client.
     * Si l'existance du dossier avec plus d'un fichier est vérifiée, on applique la méthode get pour récupérer le contenu.
     */
	public void syncLocalDirectory(String login, String password)
    {
		if (verify(login, password))
		{
			File repertory = new File("fichiers/");
			String files[] = repertory.list();
			if ((files != null) && (files.length > 0)) 
			{
				for (int i = 0; i < files.length; i++) 
				{	    
					get(files[i], null, login, password);
				}
			}
		}
		else
			System.out.println("Mauvaises informations de connexion.");
	}
}
