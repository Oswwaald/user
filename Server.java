package ca.polymtl.inf8480.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Server implements ServerInterface {
	
	fileLocks = new HashMap();
	
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
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	public String create(String fileName, String login, String password)
	{
		if (verify(login, password))
		{
			String filePath = "fichiers/" + fileName;
			if (filePath.exists() && !filePath.isDirectory())
				return "Le fichier" + fileName + "existe deja";
			else
			{
				File newFile = new File(filePath);
				return "Le fichier " + fileName + " a ete cree avec succes";
			}
		}
		
		else
			return "Mauvaises informations de connexion";
	}
	
	public byte[] get(String fileName, String checksumClient, String login, String password)
	{
		if (verify(login, password))
		{
			String filePath = "fichiers/" + fileName;
			if (filePath.exists() && !filePath.isDirectory())
			{		
				File fileToGet = new File(filePath);
				if (checksumClient == null)
					return ////TODO
				FileInputStream fis = new FileInputStream(fileToGet);
				String checksumServer = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
				if(checksumClient == checksumServer)
					return fileToGet();
				else
					return "Les checksums ne correspondent pas";
			}
			else
			{
				return "Aucun fichier " + fileName + " sur le serveur";
			}
		}
		
		else
			return "Mauvaises informations de connexion";
    }
    
    public String push(String fileName, byte[] content, String login, String password)
    {
      String lockOwner = (String)fileLocks.get(fileName);
      if (lockerOwner == null)
		return "Veuillez verrouiller le fichier avant de le pousser";
      if (!lockOwner.equals(login) {
        return "Ce fichier est verouille par l utilisateur " + lockOwner;
      }
      File fileToOverwrite = new File("fichiers/" + fileName);
        try {
          FileWriter fw= new FileWriter(fileToOverwrite);
          fw.write(content);
          fw.close();
        } 
        fileLocks.remove(fileName);
		return "Le fichier " + fileName + " a ete MAJ sur le serveur";
      }
      
    public String lock(String fileName, String checksumClient, String login, String password)
    {
      String fileLockOwner = (String)fileLocks.putIfAbsent(fileName, login);
      if (fileLockOwner == null) {
		get(fileName, checksumClient, String login, String password);
      }
      return "L utilisateur " + fileLockOwner + "detient le verrou pour le fichier " + fileName;
    }
    
    public ArrayList<String> list(String login, String password)
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
			return "Mauvaises informations de connexion";
	}
}
  

