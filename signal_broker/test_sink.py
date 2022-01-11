import socket
import sys

HOST = 'localhost'
PORT = 22223
DONG_DING_BYTE = b'\x01'
FOOD_READY_BYTE = b'\x02'

s = None
for res in socket.getaddrinfo(HOST, PORT, socket.AF_UNSPEC, socket.SOCK_STREAM):
    af, socktype, proto, canonname, sa = res
    try:
        s = socket.socket(af, socktype, proto)
    except OSError as msg:
        s = None
        continue
    try:
        s.connect(sa)
    except OSError as msg:
        s.close()
        s = None
        continue
    break

if s is None:
    print('could not open socket')
    sys.exit(1)

print('Connected')
with s:
    while True:
        data = s.recv(1)
        if data == DONG_DING_BYTE:
            print("DONG DING")
        elif data == FOOD_READY_BYTE:
            print("Food is ready!")