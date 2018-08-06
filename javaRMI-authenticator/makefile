CC = javac
PCK = SaleServer

server: SaleServer/Authenticator.java SaleServer/BankInterface.java SaleServer/BankServer.java
	$(CC) $(PCK)/*.java

clean:
	rm $(PCK)/*.class

run-server:
	java $(PCK).BankServer
