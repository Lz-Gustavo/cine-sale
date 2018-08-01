from nws.client import NetWorkSpace
import sys

port = 8765
tsname = sys.argv[1]
host = sys.argv[2]

ts = NetWorkSpace(tsname, host, port)

try:
    for i in ts.listVars(wsName=sys.argv[1], format='dict'):
        print i
except:
    print("There's no more seats. Sorry :(")