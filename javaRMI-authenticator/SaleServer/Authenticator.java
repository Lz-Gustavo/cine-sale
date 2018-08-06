package SaleServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Authenticator extends UnicastRemoteObject implements BankInterface {

	public int validate(String card_number) throws RemoteException {
		//random return
		if (Integer.parseInt(card_number) % 2 == 0) {
			return 1;
		}
		return 0;
	}
	public Authenticator() throws RemoteException {}
}