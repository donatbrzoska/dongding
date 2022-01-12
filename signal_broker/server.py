import socket
import time
from threading import Thread

HOST = ''
SIGNAL_SOURCE_PORT = 22222
SIGNAL_SINK_PORT = 22223

DONG_DING_BYTE = b'\x01'
FOOD_READY_BYTE = b'\x02'

connected_sink_clients = []
threads = []
interrupted = False

def broadcast_signal(data):
    for conn, addr in connected_sink_clients:
        print(f"Sending signal to {addr}")
        try:
            conn.send(data)
        except Exception as e:
            print(f"Error for {addr}: {e}")
            print(f"Removing {addr} from signal sinks")
            connected_sink_clients.remove((conn, addr))

def handle_source_client(conn, addr):
    with conn:
        while not interrupted:
            try:
                data = conn.recv(1)
            except socket.timeout:
                pass
            except ConnectionResetError:
                break
            else:
                if data == DONG_DING_BYTE or data == FOOD_READY_BYTE:
                    print(f"Received signal from {addr}: {data}")
                    broadcast_signal(data)

def handle_sink_client(conn, addr):
    print(f"Adding {addr} to signal sinks")
    connected_sink_clients.append((conn, addr))

def serve(port, client_handler):
    socket.setdefaulttimeout(0.2)
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) # allow address reuse shortly after close
        s.bind((HOST, port))
        s.listen()
        while not interrupted:
            try:
                conn, addr = s.accept()
            except socket.timeout:
                pass
            else:
                print(f"Handle client {addr}")
                t = Thread(target=client_handler, args=(conn, addr, ))
                t.start()
                threads.append(t)

                for t in threads:
                    if not t.is_alive():
                        threads.remove(t)

if __name__ == "__main__":
    print("Starting server")
    serve_sink_thread = Thread(target=serve, args=(SIGNAL_SINK_PORT, handle_sink_client,))
    serve_source_thread = Thread(target=serve, args=(SIGNAL_SOURCE_PORT, handle_source_client,))
    serve_sink_thread.start()
    serve_source_thread.start()

    while True:
        try:
            time.sleep(1)
        except KeyboardInterrupt:
            interrupted = True
            break

    print("\nShutting down")
    print("... Waiting for threads to finish")
    serve_sink_thread.join()
    serve_source_thread.join()
    print("... Closing sockets")
    for conn, addr in connected_sink_clients:
        conn.close()
