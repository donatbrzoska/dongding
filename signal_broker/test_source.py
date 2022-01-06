import socket
import sys

HOST = 'localhost'
PORT = 22222
SIGNAL_BYTE = b'\x01'

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
    s.send(SIGNAL_BYTE)
    print(f"Sent {SIGNAL_BYTE}")