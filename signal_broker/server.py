import socket
import select
from threading import Thread

HOST = ''
PORT = 22222

SIGNAL_BYTE = b'\x01'
ASSUME_SIGNAL_SINK_AFTER = 5

connected_clients = []
threads = []

def broadcast_signal():
    print(f"Broadcasting ...")
    for conn, addr in connected_clients:
        try:
            conn.send(SIGNAL_BYTE)
        except Exception as e:
            print(f"Error for {addr}: {e}")
            print(f"Removing {addr} from signal sinks")
            connected_clients.remove((conn, addr))

def handle_client(conn, addr):
    print(f"Handle client {addr}")
    data_ready = select.select([conn], [], [], ASSUME_SIGNAL_SINK_AFTER)[0]
    if data_ready:
        data = conn.recv(1)
        if data == SIGNAL_BYTE:
            print(f"Received signal: {data}")
            broadcast_signal()
        else:
            print(f"Received junk: {data}")
    else:
        print(f"Adding {addr} to signal sinks")
        connected_clients.append((conn, addr))

def serve():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) # allow address reuse shortly after close
        s.bind((HOST, PORT))
        s.listen()
        while True:
            try:
                conn, addr = s.accept()
                t = Thread(target=handle_client, args=(conn, addr, ))
                t.start()
                threads.append(t)

                for t in threads:
                    if not t.is_alive():
                        threads.remove(t)
            except KeyboardInterrupt:
                break

if __name__ == "__main__":
    print("Starting server")
    serve()

    print("Shutting down")
    print(" ... Waiting for threads to finish")
    for t in threads:
        t.join()

    print("... Closing sockets")
    for conn, addr in connected_clients:
        conn.close()
