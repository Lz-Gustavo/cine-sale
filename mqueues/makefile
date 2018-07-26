CC = javac
LIBS = ./libs/amqp-client-4.0.2.jar:./libs/slf4j-api-1.7.21.jar:./libs/slf4j-simple-1.7.22.jar
RUN = java
PCK = SaleServer

transactions: $(PCK)/Transactions.java $(PCK)/BankInterface.java
	$(CC) $(PCK)/*.java  -cp $(LIBS)

clean:
	rm $(PCK)/*.class

run-transactions:
	$(RUN) -cp .:$(LIBS) $(PCK).Transactions