from nws.client import NetWorkSpace
import sys

port = 8765

try:
    ts = NetWorkSpace(sys.argv[1], sys.argv[2], port, persistent=True)


    print("Creation successful!")
except:
    print("Creation failed.")
    print("start_tuplespace.py <tuplespace_name> <host>")