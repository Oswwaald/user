package ca.polymtl.inf8480.tp1.shared;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * Permet de construire l'interface accedant aux methodes partagees par le Serveur.
 * On retrouve ici les 6 methodes precisees dans la specification du partage de fichier.
 */
public interface ServerInterface extends Remote {
	void create(String fileName, String login, String password) throws RemoteException, IOException;
	String get(String fileName, String checksumClient, String login, String password) throws RemoteException, IOException;
	void push(String fileName, String content, String login, String password) throws RemoteException, IOException;
	void lock(String fileName, String checksumClient, String login, String password) throws RemoteException, IOException;
	void list(String login, String password) throws RemoteException, IOException;
	String[] syncLocalDirectory(String login, String password) throws RemoteException, IOException;
}
