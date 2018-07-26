CC = javac
LIBS = ../libs/amqp-client-4.0.2.jar:../libs/slf4j-api-1.7.21.jar:../libs/slf4j-simple-1.7.22.jar
RUN = java


recv: Recv_finalizados.java Recv_pendentes.java
	$(CC) Recv_finalizados.java Recv_pendentes.java -cp $(LIBS)

clean:
	rm *.class

run-p:
	$(RUN) -cp .:$(LIBS) Recv_pendentes

run-f:
	$(RUN) -cp .:$(LIBS) Recv_finalizados