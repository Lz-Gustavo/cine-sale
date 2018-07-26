package SaleServer;

import java.rmi.*;
import java.util.Scanner;
import java.io.IOException;

//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.*;

//import SaleServer.BankInterface;

public class Transactions {

	private final static String PEN_QUEUE = "pendentes";
	private final static String FIN_QUEUE = "finalizados";
	private final static String HOST_NAME = "localhost";

	private static ConnectionFactory factory_pen;
	private static Connection connection_pen;
	private static Channel channel_pen;
	private static Consumer consumer_pen;

	private static ConnectionFactory factory_fin;
	private static Connection connection_fin;
	private static Channel channel_fin;
	private static Consumer consumer_fin;

	private static String card_validate(String card_number) {
		
		BankInterface bank;
		String name = "rmi://localhost/BankServer";
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
		//System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		consumer_pen = new DefaultConsumer(channel_pen) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
				
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
				//return message;
			}
		};

		//configuring connection to finished orders queue

		factory_fin = new ConnectionFactory();
		factory_fin.setHost(HOST_NAME);
		connection_fin = factory_fin.newConnection();
		channel_fin = connection_fin.createChannel();
      
		channel_fin.queueDeclare(FIN_QUEUE, false, false, false, null);
		//System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		consumer_fin = new DefaultConsumer(channel_fin) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
				
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
				//return message;
			}
		};
	}

	public static void main(String[] argv) throws Exception {

		config_connection();
		GetResponse transaction;

		for (;;) {

			//channel_pen.basicConsume(PEN_QUEUE, true, consumer_pen);

			Scanner scan = new Scanner(System.in);
			String msg_count = new String();

			msg_count = scan.nextLine();

			if (msg_count.equals("")) {

				try {
					transaction = channel_pen.basicGet(PEN_QUEUE, false);
					String message = new String(transaction.getBody());
					
					System.out.println("----------------------");
					System.out.println("Recv: " +message);

					String authentication = card_validate(message);
					System.out.println("Bank: "+authentication);

					if (authentication.equals("1")) {
					
						System.out.println("Status: transacao aprovada!");
						channel_fin.basicPublish("", FIN_QUEUE, null, transaction.getBody());
					}
					else {
						System.out.println("Status: transacao rejeitada!");
					}
					System.out.println("----------------------\n");

					channel_pen.basicConsume(PEN_QUEUE, true, consumer_pen);

				} catch (Exception e) {
					
					System.out.println("Exception: " +e);
				}
			}
		}
	}
}