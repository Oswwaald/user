package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/*
 * Permet de construire l'interface acc�dant aux m�thodes partag�es par le Serveur.
 * On retrouve ici les 6 m�thodes pr�cis�es dans la sp�cification du partage de fichier.
 */
public interface ServerInterface extends Remote {
	void create(String fileName, String login, String password) throws RemoteException;
	String get(String fileName, String checksumClient, String login, String password) throws RemoteException;
	void push(String fileName, String content, String login, String password) throws RemoteException;
	void lock(String fileName, String checksumClient, String login, String password) throws RemoteException;
	ArrayList<String> list(String login, String password) throws RemoteException;
	String[] syncLocalDirectory(String login, String password) throws RemoteException;
}
