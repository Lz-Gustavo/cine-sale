package SaleServer;

import java.rmi.*;
import java.util.Scanner;
import java.io.IOException;

//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.*;

//import SaleServer.BankInterface;

public class Transactions3q {

	private final static String PEN_QUEUE = "pendentes";
	private final static String APR_QUEUE = "aprovados";
	private final static String REJ_QUEUE = "rejeitados";
	private final static String HOST_NAME = "localhost";

	private static ConnectionFactory factory_pen;
	private static Connection connection_pen;
	private static Channel channel_pen;
	private static Consumer consumer_pen;

	private static ConnectionFactory factory_apr;
	private static Connection connection_apr;
	private static Channel channel_apr;
	private static Consumer consumer_apr;

	private static ConnectionFactory factory_rej;
	private static Connection connection_rej;
	private static Channel channel_rej;
	private static Consumer consumer_rej;


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

		// consumer_pen = new DefaultConsumer(channel_pen) {
		// 	@Override
		// 	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
		// 	throws IOException {
				
		// 		String message = new String(body, "UTF-8");
		// 		System.out.println(" [x] Received '" + message + "'");
		// 		//return message;
		// 	}
		// };


		//configuring connection to aproved orders queue

		factory_apr = new ConnectionFactory();
		factory_apr.setHost(HOST_NAME);
		connection_apr = factory_apr.newConnection();
		channel_apr = connection_apr.createChannel();
		channel_apr.queueDeclare(APR_QUEUE, false, false, false, null);

		//configuring connection to rejected orders queue

		factory_rej = new ConnectionFactory();
		factory_rej.setHost(HOST_NAME);
		connection_rej = factory_rej.newConnection();
		channel_rej = connection_rej.createChannel();
		channel_rej.queueDeclare(REJ_QUEUE, false, false, false, null);
	}

	private static String extract_card(String message) {
		// message format: 'card_number-seat_num'
		// ex: 1234-A1

		String[] parts = message.split("-");
		return parts[0];
	}

	public static void main(String[] argv) throws Exception {

		config_connection();
		GetResponse transaction;
		Scanner scan = new Scanner(System.in);
		String msg_count = new String();

		for (;;) {

			//channel_pen.basicConsume(PEN_QUEUE, true, consumer_pen);
			
			msg_count = scan.nextLine();

			if (msg_count.equals("")) {

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
						channel_apr.basicPublish("", APR_QUEUE, null, transaction.getBody());
					}
					else {

						//publish on rejected transaction queue
						System.out.println("Status: transacao rejeitada!");
						channel_rej.basicPublish("", REJ_QUEUE, null, transaction.getBody());
					}
					System.out.println("----------------------\n");

					//remove message from pending queue
					channel_pen.basicReject(tag, false);

				} catch (Exception e) {
					
					System.out.println("Exception: " +e);
				}
			}
		}
	}
}