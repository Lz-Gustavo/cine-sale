package SaleServer;

import java.rmi.*;
import java.util.Scanner;
import java.io.IOException;
import com.rabbitmq.client.*;

public class Transactions {

	private final static String PEN_QUEUE = "pendentes";
	private final static String FIN_QUEUE = "finalizados";
	private final static String HOST_NAME = "localhost";
	private final static String RMI_HOST = "localhost";

	private static ConnectionFactory factory_pen;
	private static Connection connection_pen;
	private static Channel channel_pen;
	private static Consumer consumer_pen;

	private static ConnectionFactory factory_fin;
	private static Connection connection_fin;
	private static Channel channel_fin;

	private static String card_validate(String card_number) {
		
		BankInterface bank;
		String name = "rmi://"+RMI_HOST+"/BankServer";
		int permission;
		
		try {
			bank = (BankInterface) Naming.lookup(name);
			permission = bank.validate(card_number);
			return Integer.toString(permission);

		} catch (Exception e) {
			return ("Bank error: " + e);
		}
	}

	private static void config_connection() throws Exception {

		//configuring connection to pending orders queue
		factory_pen = new ConnectionFactory();
		factory_pen.setHost(HOST_NAME);
		connection_pen = factory_pen.newConnection();
		channel_pen = connection_pen.createChannel();
		channel_pen.queueDeclare(PEN_QUEUE, false, false, false, null);

		//configuring connection to aproved orders queue
		factory_fin = new ConnectionFactory();
		factory_fin.setHost(HOST_NAME);
		connection_fin = factory_fin.newConnection();
		channel_fin = connection_fin.createChannel();
		channel_fin.queueDeclare(FIN_QUEUE, false, false, false, null);
	}

	private static String extract_card(String message) {
		// message format: 'card_number-seat_num-status'
		// ex: 1234-A1-aprovada

		String[] parts = message.split("-");
		return parts[0];
	}

	public static void main(String[] argv) throws Exception {

		config_connection();
		GetResponse transaction;
		Scanner scan = new Scanner(System.in);
		String msg_count = new String();

		for (;;) {
			
			//wait for 5s
			Thread.sleep(5000);

			//msg_count = scan.nextLine();

			//if (msg_count.equals("")) {

				try {
					transaction = channel_pen.basicGet(PEN_QUEUE, false);
					String message = new String(transaction.getBody());
					long tag = transaction.getEnvelope().getDeliveryTag();
					
					System.out.println("----------------------");
					System.out.println("Recv: " +message);

					String authentication = card_validate(extract_card(message));
					System.out.println("Bank: "+authentication);

					if (authentication.equals("1")) {
					
						//publish on aproved transactions queue
						System.out.println("Status: transacao aprovada!");
						message += "-aprovado";
						System.out.println("Send: "+message);
						channel_fin.basicPublish("", FIN_QUEUE, null, message.getBytes());
					}
					else {

						//publish on rejected transaction queue
						System.out.println("Status: transacao rejeitada!");
						message += "-rejeitado";
						System.out.println("Send: "+message);
						channel_fin.basicPublish("", FIN_QUEUE, null, message.getBytes());
					}
					System.out.println("----------------------\n");

					//remove message from pending queue
					channel_pen.basicReject(tag, false);

				} catch (Exception e) {
					
					System.out.println("Order not found: " +e);
				}
			//}
		}
	}
}