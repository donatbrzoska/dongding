import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DoorbellNotificationProgram program = new DoorbellNotificationProgram();
        try {
            program.start();
        } catch (Exception e) {
            System.err.println("Creating Error message...");
            program.stop();
            e.printStackTrace();
            StringBuilder sb = new StringBuilder("An error has occurred:\n\n");
            sb.append(e.getClass().getName());
            sb.append(": ");
            sb.append(e.getMessage());
            sb.append("\n");
            for (StackTraceElement element : e.getStackTrace()) {
                sb.append("\t");
                sb.append(element.toString());
                sb.append("\n");
            }
            attachCause(sb, e.getCause());
            JFrame frame = new JFrame("An error occurred");
            frame.setUndecorated(true);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
            JOptionPane.showMessageDialog(frame,
                    new JTextArea(sb.toString()),
                    "An error occurred", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            System.exit(1);
        }
        System.exit(1);
    }

    private static void attachCause(StringBuilder sb, Throwable throwable) {
        sb.append("Caused by: ");
        sb.append(throwable.getClass().getName());
        sb.append(": ");
        sb.append(throwable.getMessage());
        sb.append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\t");
            sb.append(element.toString());
            sb.append("\n");
        }
        if (throwable.getCause() != null) {
            attachCause(sb, throwable.getCause());
        }
    }
}
