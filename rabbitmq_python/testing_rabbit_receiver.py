import pika

#Fila finalizados
connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()

channel.queue_declare(queue='finalizados')


def callback(ch, method, properties, body):
    assento1 = "D4"
    assento2 = "A3"
    corpo = body.split('-')[1]

    if((corpo == assento1) or corpo == assento2):
        if(body.split('-')[2] == "aprovado"):
            print(" [x] Aprovado! %r" % body)
        elif(body.split('-')[2] == "rejeitado"):
            print(" [x] Reprovado! %r" % body)
        else:
            print(" [x] Fez nada! %r" % body)
    exit(0)

channel.basic_consume(callback, queue='finalizados', no_ack=True)    

print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()