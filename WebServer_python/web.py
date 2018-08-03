import sys
import pika
import requests
from flask import Flask, render_template
from nws.client import NetWorkSpace

#Le IP
if sys.argv[1:]:
	WS_HOST=sys.argv[1]
else:
	WS_HOST="localhost"
#Le porta
if sys.argv[2:]:
	PORT=sys.argv[2]
else:
	PORT=8080

app = Flask(__name__)

@app.route("/")
def main():

	page = "<form method=\"GET\">"
	page += "<input placeholder=\"Nome do espetaculo\"></input> <br><br>"
	page += "<button type=\"submit\">Consultar Espetaculo</button>"
	page += "</form>"

	return page

@app.route("/consulta/<string:espetaculo>")
def consulta(espetaculo):

	TUPLE_HOST = "localhost"
	TUPLE_PORT = 8765
	MQUEUE_HOST = "localhost"

	espetaculo = espetaculo.split('<')[1]
	espetaculo = espetaculo.split('>')[0]
	tsname = espetaculo

	ts = NetWorkSpace(tsname, TUPLE_HOST, TUPLE_PORT)

	seats = ""
	seats = '<link rel="stylesheet" type="text/css" media="screen" href="../static/index.css" />'
	seats += '<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>'

	try:
		for i in ts.listVars(wsName=tsname, format='dict'):
			seats += "<button><a href=\"/compra/<" + i + ">/<" + espetaculo + ">\">"
			seats += i
			seats += "</button></a>"
			
	except:
		seats = "There's no more seats. Sorry :("
	
	return seats

@app.route("/compra/<string:assento>/<string:espetaculo>/<string:cartao>", methods=['GET', 'POST'])
def compra(assento, espetaculo, cartao):
	
	TUPLE_HOST = "localhost"
	TUPLE_PORT = 8765
	MQUEUE_HOST = "localhost"

	assento = assento.split('<')[1]
	assento = assento.split('>')[0]

	espetaculo = espetaculo.split('<')[1]
	espetaculo = espetaculo.split('>')[0]
	tsname = espetaculo

	cartao = cartao.split('<')[1]
	cartao = cartao.split('>')[0]

	ts = NetWorkSpace(tsname, TUPLE_HOST, TUPLE_PORT)
	aprovado = -1

	try:    
		seat = ts.fetchTry(assento)
		#Envio para fila de mensagens

		if (seat == None):
			exit(0)

		connection = pika.BlockingConnection(pika.ConnectionParameters(MQUEUE_HOST))
		channel = connection.channel()

		channel.queue_declare(queue='pendentes')
		channel.basic_publish(exchange='', routing_key='pendentes', body=cartao + '-' + seat)
		print(" [x] Mensagem recebida'")

		connection.close()
		ts.deleteVar(seat)

		#Fila finalizados
		connection = pika.BlockingConnection(pika.ConnectionParameters(host=MQUEUE_HOST))
		channel = connection.channel()

		channel.queue_declare(queue='finalizados')

		def callback(ch, method, properties, body):
			corpo = body.split('-')[1]

			if(corpo == assento):
				if(body.split('-')[2] == "aprovado"):
					aprovado = 1
					print(" [x] Aprovado! %r" % body)
				elif(body.split('-')[2] == "rejeitado"):
					aprovado = 0
					print(" [x] Reprovado! %r" % body)
			print aprovado
			channel.stop_consuming()

		channel.basic_consume(callback, queue='finalizados', no_ack=True)    

		print(' [*] Waiting for messages. To exit press CTRL+C')
		channel.start_consuming()

		print aprovado
		if aprovado == 1:
			return "Compra aprovada"
		elif aprovado == 0:
			return "Compra reprovada, tente com outra forma de pagamento."
		return "Falha na compra."

		channel.close()
		connection.close()

	except:
		print aprovado
		if aprovado == 1:
			return "Compra aprovada"
		elif aprovado == 0:
			return "Compra reprovada, tente com outra forma de pagamento."
		return "Falha na compra."

if __name__ == "__main__":
	app.run(debug=True, host=WS_HOST, port=PORT)
