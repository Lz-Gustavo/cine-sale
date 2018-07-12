import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Authenticator extends UnicastRemoteObject implements BankInterface {

	public Integer validate(String card_number) {
		//random return
		return 1;
	}
}

public class BankServer {
	
	public static void main(String[] args) {
		try {
			Naming.rebind("rmi://localhost/BankServer", new Authenticator());
		}	
		catch (Exception e) {}
	}
}