package SaleServer;

import java.rmi.*;

public class BankServer {
	
	public static void main(String[] args) {
		try {
			Naming.rebind("rmi://localhost/BankServer", new Authenticator());
		}	
		catch (Exception e) {}
	}
}