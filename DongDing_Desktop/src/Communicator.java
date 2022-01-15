import java.io.*;
import java.net.Socket;

public class Communicator {

    private boolean connectionActive = false;
    private final String ip;
    private final int port;
    private Socket clientSocket;
    private DataInputStream in = null;
    private Exception recentException = null;

    public Communicator(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean startConnection() {
        if (connectionActive) {
            System.out.println("Connection already active!");
            return true;
        }
        try {
            clientSocket = new Socket(ip, port);
            in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            connectionActive = true;
        } catch (IOException e) {
            e.printStackTrace();
            recentException = e;
        }
        return connectionActive;
    }

    public NotificationResult receiveNotification() {
        if (!connectionActive) {
            throw new IllegalStateException("No active connection!");
        }
        try {
            byte input = in.readByte();
            switch (input) {
                case 1:
                    return NotificationResult.DOORBELL_NOTIFICATION;
                case 2:
                    return NotificationResult.MEAL_NOTIFICATION;
                default:
                    return NotificationResult.NO_NOTIFICATION;
            }
        } catch (Exception e) {
            e.printStackTrace();
            recentException = e;
            stopConnection();
            return NotificationResult.DISCONNECTED;
        }
    }

    public void stopConnection() {
        try {
            in.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
            recentException = e;
        }
        connectionActive = false;
    }

    public Exception getRecentException() {
        return recentException;
    }

    enum NotificationResult {
        MEAL_NOTIFICATION, DOORBELL_NOTIFICATION, NO_NOTIFICATION, DISCONNECTED
    }
}
