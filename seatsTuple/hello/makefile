CC = javac
LIBS = ./lib/jini-core.jar:./lib/jini-ext.jar:./lib/outrigger.jar:./lib/reggie.jar:./lib/sun-util.jar:./lib/reggie-dl.jar

hello: hello.java Mensagem.java SpaceUtils.java
	$(CC) hello.java Mensagem.java SpaceUtils.java -cp $(LIBS)

clean:
	rm *.class