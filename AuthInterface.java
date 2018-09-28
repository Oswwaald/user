package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthentificationInterface extends AuthServer {
	String newUser(String login, String password) throws RemoteException;
	boolean verify(String login, String password) throws RemoteException;
}
