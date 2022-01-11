import socket
import sys
import time

HOST = 'localhost'
PORT = 22222
SIGNAL_BYTE = None
DONG_DING_BYTE = b'\x01'
FOOD_READY_BYTE = b'\x02'

if not len(sys.argv) > 1:
    print("You have to specify the kind of signal source as program argument...\nValid signals are [1, 2]")
    sys.exit(1)
else:
    if sys.argv[1] == "1":
        SIGNAL_BYTE = DONG_DING_BYTE
    elif sys.argv[1] == "2":
        SIGNAL_BYTE = FOOD_READY_BYTE
    else:
        print(f"Invalid kind of signal: {sys.argv[1]}\nValid signals are [1, 2]")
        sys.exit(1)

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
        time.sleep(2)
        s.send(SIGNAL_BYTE)
        print(f"Sent {SIGNAL_BYTE}")