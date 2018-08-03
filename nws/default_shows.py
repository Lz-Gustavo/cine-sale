import sys, socket
from nws.client import NetWorkSpace

#start tuple space - twistd -y /etc/nws.tac
#deleteVar() - deleta
#find() - return a value
#store() - insere no espaco 
#fetch() - retira do espaco
#fetchTry() - retira nao bloqueante

port = 8765
host = sys.argv[1]

shows = ["Red Hot Chili Peppers", "ACDC", "Linkin Park", "Iron Maiden"]

for tsname in shows:

    data = NetWorkSpace("database", host, port, persistent=True)
    data.declare(tsname, 'fifo')
    data.store(tsname, tsname)

    ts = NetWorkSpace(tsname, host, port, persistent=True)

    alpha = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']

    try:
        for i in alpha[:10]:
            for j in range(10):
                ts.declare("%s%d" % (i, j), 'fifo')
                ts.store("%s%d" % (i, j), "%s%d" % (i, j))
        print("Creation successful!")
    except:
        print ("Creation failed.")
        print ("new_show.py <show_name> <host> <show_size>")