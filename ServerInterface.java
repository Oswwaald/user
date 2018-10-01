package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * Permet de construire l'interface accédant aux méthodes partagées par le Serveur.
 * On retrouve ici les 6 méthodes précisées dans la spécification du partage de fichier.
 */
public interface ServerInterface extends Remote {
	void create(String fileName, String login, String password) throws RemoteException;
	byte[] get(String fileName, String checksumClient, String login, String password) throws RemoteException;
	void push(String fileName, byte[] content, String login, String password) throws RemoteException;
	void lock(String fileName, String checksumClient, String login, String password) throws RemoteException;
	ArrayList<String> list(String login, String password) throws RemoteException;
	void syncLocalDirectory(String login, String password) throws RemoteException;
}
