package SaleServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BankInterface extends Remote {

	public int validate(String card_number) throws RemoteException; 
}