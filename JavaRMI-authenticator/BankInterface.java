import java.rmi.Remote;

public interface BankInterface extends Remote {

	public Integer validate(String card_number); 
}