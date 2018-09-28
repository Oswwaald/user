package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Server {
	String create(String fileName, String login, String password) throws RemoteException;
	byte[] get(String fileName, String checksumClient, String login, String password) throws RemoteException;
	String push(String fileName, byte[] content, String login, String password) throws RemoteException;
	String lock(String fileName, String checksumClient, String login, String password) throws RemoteException;
	ArrayList<String> list(String login, String password) throws RemoteException;
	void syncLocalDirectory(String login, String password) throws RemoteException;
}
