import java.awt.EventQueue;

public class Main {
    public static void main(String[] args) {
        
        Peer peer = new Peer();
        peer.start();

        EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame(peer);
                frame.setVisible(true);
                peer.setMainFrame(frame);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}