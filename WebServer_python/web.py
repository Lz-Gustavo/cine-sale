import os
import sys
import time
import pika
import requests
from nws.client import NetWorkSpace
from flask import Flask, render_template, render_template_string, flash, request
from wtforms import Form, TextField, TextAreaField, validators, StringField, SubmitField

app = Flask(__name__)
app.config['SECRET_KEY'] = '7d441f27d441f27567d441f2b6176a'

#Le IP
if sys.argv[1:]:
	HOST=sys.argv[1]
else:
	HOST="localhost"

#Le porta
if sys.argv[2:]:
	PORT=sys.argv[2]
else:
	PORT=8080	

#Le IP Tuple
if sys.argv[3:]:
	HOST_TS=sys.argv[3]
else:
	HOST_TS="localhost"

#Le porta Tuple
if sys.argv[4:]:
	PORT_TS=sys.argv[4]
else:
	PORT_TS=8765

aprovado = -1

class CreateForm(Form):
	name = TextField('Evento:', validators=[validators.required()])
	seats_ = TextField('No de Assentos:', validators=[validators.required()])
	
class FormCartao(Form):
	cartao = TextField('Cartao:', validators=[validators.required()])

def result(r):
	global aprovado

	if(r == 1):
		aprovado = 1
	elif(r == 0):
		aprovado = 0

def fila(assento):

	#Fila finalizados
	connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
	channel = connection.channel()

	channel.queue_declare(queue='finalizados')

	def callback(ch, method, properties, body):
		corpo = body.split('-')[1]
		
		if(corpo == assento):
			if(body.split('-')[2] == "aprovado"):
				result(1)
				print(" [x] Aprovado! %r" % body)
				channel.stop_consuming()
				
			elif(body.split('-')[2] == "rejeitado"):
				result(0)
				print(" [x] Reprovado! %r" % body)
				channel.stop_consuming()
				
	channel.basic_consume(callback, queue='finalizados', no_ack=True)    

	print(' [*] Waiting for messages. To exit press CTRL+C')
	channel.start_consuming()

	channel.close()
	connection.close()
	
	return aprovado

@app.route("/")
def main():
	main_page = '<link rel="stylesheet" type="text/css" media="screen" href="../static/index.css" />'
	main_page += "<h1>Distributed Tickets</h1>"
	main_page += "<button class=\"b_eventos\"><a href=\"/eventos\">Eventos</a></button><br>"
	main_page += "<button class=\"b_eventos\"><a href=\"/criar_evento\">Criar evento</a></button>"

	return main_page

@app.route("/criar_evento", methods=['GET', 'POST'])
def criar():

	form = CreateForm(request.form)

	print form.errors
	if request.method == 'POST':
		name=request.form['name']
		seats_ =request.form['seats_']

		if form.validate():
			name = name.replace(' ', '\ ')
			os.system('python ../nws/new_show.py ' + name + ' ' + HOST_TS + ' ' + seats_)

			flash('Evento criado com sucesso!')
			no_assentos = int(seats_)*int(seats_)
			flash('Nome: ' + name + ' No de Assentos: ' + str(no_assentos))
		else:
			flash('All the form fields are required. ')

	return render_template('criar_evento.html', form=form)

@app.route("/eventos")
def espetaculos():
	page = '<link rel="stylesheet" type="text/css" media="screen" href="../static/index.css" />'
	page += '<h1>Eventos</h1>'
	ts = NetWorkSpace("database", HOST_TS, PORT_TS)
	for i in ts.listVars(wsName='database', format='dict'):
		page += "<button class=\"show\"><a href=\"/consulta/<" + i + ">\">" + i + "</a></button><br>"
	page += '<br><br><button class="show"><a href="/">Home</a></button>'
	return page

@app.route("/consulta/<string:espetaculo>", methods=['GET', 'POST'])
def consulta(espetaculo):

	form = FormCartao(request.form)

	print form.errors
	if request.method == 'POST':
		cartao=request.form['cartao']
		print cartao

	espetaculo = espetaculo.split('<')[1]
	espetaculo = espetaculo.split('>')[0]

	port = PORT_TS
	tsname = espetaculo
	host = HOST_TS

	ts = NetWorkSpace(tsname, host, port)

	seats = ""
	br = 0
	try:
		seats = '<link rel="stylesheet" type="text/css" media="screen" href="../static/index.css" />'
		lista = ts.listVars(wsName=tsname, format='dict')
		if not (lista):
			seats += "<h1> Sinto muito! Evento esgotado. </h1>"
		else:
			seats += '<form action="" method="post">'
			seats += '{{ form.csrf }}'
			seats += '<div class="input text">'
			seats += '{{ form.cartao.label }} {{ form.cartao }}'
			seats += '</div><br>'
			seats += '<button type="submit">Confirmar cartao</button>'
			seats += '</form>'

			if form.validate():
				seats += "<h1> Assentos </h1>"
				for i in sorted(lista):
					seats += "<a href=\"/compra/<" + i + ">/<" + espetaculo + ">/<" + cartao + ">\"><button>"
					seats += i
					seats += "</button></a>"
					br += 1
					if (br == 10):
						seats += "<br>"
						br = 0

		seats += '<br><br><button class="show"><a href="/">Home</a></button>'
	except:
		raise
		seats = "There's no more seats. Sorry :("
	
	return render_template_string(seats, form=form)

@app.route("/compra/<string:assento>/<string:espetaculo>/<string:cartao>")
def compra(assento, espetaculo,cartao):

	assento = assento.split('<')[1]
	assento = assento.split('>')[0]

	espetaculo = espetaculo.split('<')[1]
	espetaculo = espetaculo.split('>')[0]

	cartao = cartao.split('<')[1]
	cartao = cartao.split('>')[0]

	host = HOST_TS
	port = PORT_TS
	tsname = espetaculo

	ts = NetWorkSpace(tsname, host, port)

	try:    
		seat = ts.fetchTry(assento)
		ts.deleteVar(seat)

		if (seat == None):
			exit(0)

		#Envio para fila de mensagens
		connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
		channel = connection.channel()

		channel.queue_declare(queue='pendentes')

		channel.basic_publish(exchange='',
							routing_key='pendentes',
							body=cartao + '-' + seat)
		print(" [x] Mensagem recebida'")

		connection.close()

		aprovado = fila(assento)

		if aprovado == 1:
			retorno = '<link rel="stylesheet" type="text/css" media="screen" href="../static/index.css" />'
			retorno += "<h1>Compra aprovada! Obrigado por comprar conosco :D</h1>"
			retorno += '<br><br><button class="show"><a href="/">Home</a></button>'
			return retorno
		elif aprovado == 0:
			ts.store(seat, assento)
			retorno = '<link rel="stylesheet" type="text/css" media="screen" href="../static/index.css" />'
			retorno += "<h1>Compra reprovada :( Tente com outra forma de pagamento.</h1>"
			retorno += '<br><br><button class="show"><a href="/">Home</a></button>'
			return retorno
		else:
			retorno = '<link rel="stylesheet" type="text/css" media="screen" href="../static/index.css" />'
			retorno += "<h1>Falha na compra</h1>"
			retorno += '<br><br><button class="show"><a href="/">Home</a></button>'
			return retorno
	
	except:
		raise
		return "Except: Falha na compra."

if __name__ == "__main__":
	app.run(debug=True, host='0.0.0.0', port=PORT)
