package SaleServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Authenticator extends UnicastRemoteObject implements BankInterface {

	public String validate(String card_number) throws RemoteException {
		//random return
		return "Accepted card sir";
	}
	public Authenticator() throws RemoteException {}
}