package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * Permet de construire l'interface accédant aux méthodes partagées par le Serveur d'Authentification.
 * On retrouve ici les 2 méthodes précisées dans la spécification de l'authentification.
 */
public interface AuthInterface extends Remote {
	void newUser(String login, String password) throws RemoteException;
	boolean verify(String login, String password) throws RemoteException;
}