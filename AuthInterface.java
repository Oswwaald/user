package ca.polymtl.inf8480.tp1.shared;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * Permet de construire l'interface accedant aux methodes partagees par le Serveur d'Authentification.
 * On retrouve ici les 2 methodes precisees dans la specification de l'authentification.
 */
public interface AuthInterface extends Remote {
	void newUser(String login, String password) throws RemoteException;
	boolean verify(String login, String password) throws RemoteException, IOException;
}
