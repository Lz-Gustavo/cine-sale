import sys, socket
from nws.client import NetWorkSpace

#start tuple space - twistd -y /etc/nws.tac
#deleteVar() - deleta
#find() - return a value
#store() - insere no espaco 
#fetch() - retira do espaco
#fetchTry() - retira nao bloqueante

port = 8765
tsname = sys.argv[1]
host = sys.argv[2]

ts = NetWorkSpace(tsname, host, port, persistent=True)

alpha = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']

try:
    for i in alpha[:int(sys.argv[3])]:
        for j in range(int(sys.argv[3])):
            ts.declare("%s%d%s%d" % (i, j, i, j), 'fifo')
            ts.store("%s%d%s%d" % (i, j, i, j), "%s%d%s%d" % (i, j, i, j))
    print("Creation successful!")
except:
    print ("Creation failed.")
    print ("new_show.py <show_name> <host> <show_size>")