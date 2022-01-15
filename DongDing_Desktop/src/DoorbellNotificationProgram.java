import java.awt.*;

public class DoorbellNotificationProgram {

    private Communicator communicator = null;
    private final TrayIcon trayIcon;
    private final Image normalImage = Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/bell_no_ring.png"));
    private final Image notifyImage = Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/bell_ring.png"));
    private final Image pendingImage = Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/pending.png"));
    private Thread imageResetThread = null;
    private boolean runImageReset = false;
    private boolean runCommunicationLoop = false;

    public DoorbellNotificationProgram() {
        communicator = new Communicator("192.168.178.74", 22223);
        trayIcon = new TrayIcon(pendingImage, "Doorbell Notifications", createPopupMenu());
        trayIcon.setImageAutoSize(true);
        addTrayIconToSystemTray();
    }

    public void start() {
        if (!communicator.startConnection()) {
            throw new IllegalStateException("Unable to start communication!", communicator.getRecentException());
        }
        trayIcon.setImage(normalImage);
        runCommunicationLoop = true;
        while (runCommunicationLoop) {
            System.out.println("Waiting for doorbell notification...");
            Communicator.NotificationResult result = communicator.receiveNotification();
            switch (result) {
                case DOORBELL_NOTIFICATION:
                    System.out.println("Received Doorbell notification!");
                    playSound(SoundPlayer.Sound.DOORBELL);
                    break;
                case MEAL_NOTIFICATION:
                    System.out.println("Received meal notification!");
                    playSound(SoundPlayer.Sound.MEALBELL);
                    break;
                case NO_NOTIFICATION:
                    //noop
                    break;
                case DISCONNECTED:
                    runImageReset = false;
                    trayIcon.setImage(pendingImage);
                    runCommunicationLoop = false;
                    break;
            }
        }
    }

    public void stop() {
        runCommunicationLoop = false;
        communicator.stopConnection();
        SystemTray.getSystemTray().remove(trayIcon);
    }

    private PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu();
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.addActionListener(e -> {
            communicator.stopConnection();
            System.exit(0);
        });
        popup.add(exitMenuItem);
        return popup;
    }

    private void addTrayIconToSystemTray() {
        if (!SystemTray.isSupported()) {
            throw new IllegalStateException("System Tray not supported!");
        }
        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            throw new IllegalStateException("Failed to add tray icon!", e);
        }
    }

    private void playSound(SoundPlayer.Sound soundType) {
        SoundPlayer.playSound(soundType);
        trayIcon.setImage(notifyImage);
        if (imageResetThread != null && imageResetThread.isAlive()) {
            runImageReset = false;
            while (imageResetThread != null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //noop
                }
            }
        }
        runImageReset = true;
        imageResetThread = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    Thread.sleep(100);
                    if (!runImageReset) {
                        imageResetThread = null;
                        return;
                    }
                }
                trayIcon.setImage(normalImage);
            } catch (InterruptedException e) {
                //noop
            }
        });
        imageResetThread.start();
    }
}
