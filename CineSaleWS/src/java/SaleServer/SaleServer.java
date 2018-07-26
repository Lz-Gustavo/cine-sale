package SaleServer;

import java.text.SimpleDateFormat;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.util.*;
import java.rmi.*;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

/**
 * @author GSanM
 * @author lzgustavo
 */

@WebService(serviceName = "SaleServer")
public class SaleServer {
	
	private final Map<String, String> TupleSpace = new HashMap<> ();
	
	private final static String PEN_QUEUE = "pendentes";
	private final static String APR_QUEUE = "aprovados";
	private final static String REJ_QUEUE = "rejeitados";
	private final static String HOST_NAME = "localhost";

	@WebMethod(operationName = "getTime")
	public String getTime() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(cal.getTime());
	}
	
	@WebMethod(operationName = "validate")
	public String validate(String card_number) {
		
		BankInterface bank;
		String name = "rmi://localhost/BankServer";
		int permission;
		
		try {
			bank = (BankInterface) Naming.lookup(name);
			permission = bank.validate(card_number);
			
			if (permission == 1) {
				return "Accepted card sir";
			}
			return "Rejected card, thief";
			
		} catch (Exception e) {
			return ("Bank error: " + e);
		}
	}
	
	@WebMethod(operationName = "populate")
	public void populate(String name) {
		
		for (int i = 0; i < 10; i++) {
			TupleSpace.put("A"+i, name);
		}
	}
	
	@WebMethod(operationName = "consulta_espetaculo")
	public String consulta_estaculo(String name) {
	// IDEIA: pega todas tuplas que moldam o nome do espetaculo
		
		String avaiable = "disponiveis: ";

		if (TupleSpace.containsValue(name)) {
			for (Map.Entry<String, String> entry : TupleSpace.entrySet()) {
				
				if (entry.getValue().equals(name)) {
					avaiable = avaiable + entry.getKey() + "-";		
				}
			}
		}
		else {
			avaiable = ("espetaculo '" + name + "' not found");
		}
		return avaiable;
	}
	
	@WebMethod(operationName = "reserva")
	public String compra(String tuple, String card_number) throws Exception {
	// IDEIA: take do espaco de tuplas, insere na fila de pendentes

		if (TupleSpace.containsKey(tuple)) {

			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(HOST_NAME);
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(PEN_QUEUE, false, false, false, null);
			
			// remove a tupla do hashmap e publica mensagem de reserva na fila
			TupleSpace.remove(tuple);
			String message = card_number+"-"+tuple;
			channel.basicPublish("", PEN_QUEUE, null, message.getBytes());
			
			channel.close();
			connection.close();
			
			return (" [x] Sent message: '" + message + "'");
		}
		else {
			return ("Assento indisponivel");
		}
	}
}
