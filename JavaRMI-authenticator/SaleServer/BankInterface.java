package SaleServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BankInterface extends Remote {

	public String validate(String card_number) throws RemoteException; 
}