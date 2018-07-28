from nws.client import NetWorkSpace
import sys
import pika

host = "localhost"
port = 8765
tsname = "rouge"

ts = NetWorkSpace(tsname, host, port)

try:    
    seat = ts.fetchTry(sys.argv[1])
    #Envio para fila de mensagens

    if (seat == None):
        exit(0)

    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()

    channel.queue_declare(queue='pendentes')

    channel.basic_publish(exchange='',
                        routing_key='pendentes',
                        body='cliente 1-123123123020120-' + seat)
    print(" [x] Mensagem recebida'")

    connection.close()

    ts.deleteVar(seat)

except:
    print("Fail. Try again.")
    print("choose_seat.py <seat>")