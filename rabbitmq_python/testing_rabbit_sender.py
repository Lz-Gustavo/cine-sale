import pika

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()

channel.queue_declare(queue='pendentes')

channel.basic_publish(exchange='',
                      routing_key='pendentes',
                      body='cliente 1, 123123123020120, a1b2')
print(" [x] Mensagem recebida'")

connection.close()