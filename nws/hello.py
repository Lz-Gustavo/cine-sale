import sys, socket
from nws.client import NetWorkSpace

#deleteVar() - deleta
#find() - return a value
#store() - insere no espaco 
#fetch() - retira do espaco
#fetchTry() - retira nao bloqueante

host = "localhost"
port = 8765
tsname = "tickets"

ts = NetWorkSpace(tsname, host, port, persistent=True)

ts.store("showJotaQuest", "%s %s" % ("A1", "B1"))

print ts.fetch("showJotaQuest")

ts.deleteVar("showJotaQuest")