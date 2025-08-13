import java.io.File;
import java.util.List;


public class Peer {
    private NetworkManager networkManager;
    
    public Peer() {
        
        this.networkManager = new NetworkManager();
    }

    public void start() {
    	new Thread(() -> networkManager.startServer()).start();
    }

    public void stop() {
        networkManager.stopServer();
    }

    public void connectToNetwork() {
        networkManager.sendBroadcast();
    }
    
    public void requestFile(String file) {
    	networkManager.requestFile(file);
    }
    
    public void sendFile(File file , String peerIP) {
        networkManager.sendFile(file, peerIP);
    }
   public void setDestinationFolder(String destination) {
	   networkManager.setDestinationFolder(destination);
   }
   
   public void setSharedFolder(String destination) {
	   networkManager.setSharedFolder(destination);
   }
   
   public List<String> getFiles() {
	return networkManager.getFiles();
	   
   }
   public void setMainFrame(MainFrame mainFrame) {
       networkManager.setMainFrame(mainFrame);
   }
   public void excFilesAdd(String file) {
	   networkManager.addToExcFiles(file);
   }
   public void excFilesRemove(String file) {
	   networkManager.removeFromExcFiles(file);
   }
}