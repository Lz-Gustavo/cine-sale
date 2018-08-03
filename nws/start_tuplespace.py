from nws.client import NetWorkSpace
import sys

#twistd -y /etc/nws.tac

port = 8765

try:
    ts = NetWorkSpace("database", localhost, port, persistent=True)

    print("Creation successful!")
except:
    print("Creation failed.")
    print("start_tuplespace.py")