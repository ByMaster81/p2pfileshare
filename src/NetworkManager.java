import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkManager {
	
	private ServerSocket serverSocket;
	private ConcurrentHashMap<String, List<String>> connectedPeers;
	private static final String BC_ADDRESS = "192.168.1.255";
	private static final int BC_PORT = 55555;
	private String destinationFolder = "/Downloads/"; 
	private String sharedFolder = "/sharedfolder/"; 
	private MainFrame mainFrame;
	private ArrayList<String> excFiles = new ArrayList<>();
	
	
	public NetworkManager() {		
		this.connectedPeers = new ConcurrentHashMap<>();
		
	}
	public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

	
	public void addToExcFiles(String file) {
		excFiles.add(file);
	}
	
	public void removeFromExcFiles(String file) {
		excFiles.remove(file);
	}
	
	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;

	}

	public void setSharedFolder(String newSharedFolder) {
		this.sharedFolder = newSharedFolder;
	}

	// Sunucu başlat
	public void startServer() {
		try {
			serverSocket = new ServerSocket(BC_PORT);
			System.out.println("Server started. Port is: " + BC_PORT);

			// Broadcast dinleyici
			new Thread(this::listenForBroadcasts).start();

			while (true) {
				Socket clientSocket = serverSocket.accept();
				new Thread(() -> handleClient(clientSocket)).start();

			}
		} catch (IOException e) {
			System.err.println("Can not start server: " + e.getMessage());
		}
	}

	private void listenForBroadcasts() {
		try (DatagramSocket socket = new DatagramSocket(BC_PORT)) {
			socket.setBroadcast(true);

			while (true) {
				byte[] chunk = new byte[256];
				DatagramPacket packet = new DatagramPacket(chunk, chunk.length);
				socket.receive(packet);

				String message = new String(packet.getData(), 0, packet.getLength());
				String peerIP = packet.getAddress().getHostAddress();

				
				if (message.startsWith("NEW_PEER|")) {
					String fileList = message.substring("NEW_PEER|".length());
					List<String> files = Arrays.asList(fileList.split(","));

					
					connectedPeers.put(peerIP, files);

					System.out.println("New Peer: " + peerIP);
					System.out.println("Files: " + files);
					System.out.println("PEERS: " + connectedPeers);

					
					sendPeerInfo(peerIP, sharedFolder);
				}
				if (message.startsWith("PEER_INFO|")) {
					String fileList = message.substring("PEER_INFO|".length());
					List<String> files = Arrays.asList(fileList.split(","));

					
					connectedPeers.put(peerIP, files);

					System.out.println("New Peer: " + peerIP);
					System.out.println("Files: " + files);
					System.out.println("PEERS: " + connectedPeers);
				}

				if (message.startsWith("REQUEST|")) {
					
					String requestedFile = message.substring("REQUEST|".length()).trim();

					
					sendFile(fileByName(requestedFile), peerIP);
				}
				if(message.startsWith("DISCONNECT|")) {
					connectedPeers.remove(peerIP);
				}
			}
		} catch (IOException e) {
			System.err.println("An error acquired while listening broadcast: " + e.getMessage());
		}
	}

	
	private void sendPeerInfo(String peerIP, String directoryPath) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			List<String> filesName = getFilesName(directoryPath);
			String message = "PEER_INFO|" + String.join(",", filesName);
			byte[] messageBytes = message.getBytes();
			DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getByName(peerIP),BC_PORT);
			socket.send(packet);
			System.out.println("Shared peer info with " + peerIP + ": " + message);

		} catch (IOException e) {
			System.err.println("Cannot share peer info with " + peerIP + ": " + e.getMessage());
		} finally {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		}
	}

	public void requestFile(String file) {
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket();
			String request = "REQUEST| " + file;
			byte[] requestBytes = request.getBytes();
			String ownerPeerIP = findFileOwner(file);
			DatagramPacket packet = new DatagramPacket(requestBytes, requestBytes.length,
					InetAddress.getByName(ownerPeerIP), BC_PORT);
			socket.send(packet);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	
	public String findFileOwner(String fileName) {
		
		for (Map.Entry<String, List<String>> entry : connectedPeers.entrySet()) {
			String ip = entry.getKey(); 
			List<String> files = entry.getValue(); 

			
			if (files.contains(fileName)) {
				return ip; 
			}
		}
		return null;
	}

	// Broadcast gönder
	public void sendBroadcast() {
		

		try (DatagramSocket socket = new DatagramSocket()) {
			socket.setBroadcast(true);

			List<String> filesName = getFilesName(sharedFolder);

			String message = "NEW_PEER|" + String.join(",", filesName);
			byte[] chunk = message.getBytes();

			DatagramPacket packet = new DatagramPacket(chunk, chunk.length, InetAddress.getByName(BC_ADDRESS), BC_PORT);
			socket.send(packet);

			System.out.println("Broadcast signal sent.");
		} catch (IOException e) {
			System.err.println("Broadcast signal sent error: " + e.getMessage());
		}
	}

	// Dosya isimlerini al

	public List<String> getFilesName(String directory) {
		List<String> filesName = new ArrayList<>();
		try {
			Files.list(Paths.get(directory))
            .filter(Files::isRegularFile)  //dosyaları filtrele
            .forEach(file -> {
                String fileName = file.getFileName().toString();
                boolean exclude = false;

                // Hariç tutulacak dosyaları kontrol et
                for (String excluded : excFiles) {
                    if (excluded.startsWith("*.") && fileName.endsWith(excluded.substring(1))) {
                        exclude = true;
                        break;
                    } else if (excluded.equals(fileName)) {
                        exclude = true;
                        break;
                    }
                }

                
                if (!exclude) {
                    filesName.add(fileName);
                }
                });
        } catch (IOException e) {
            System.err.println("Dosya okumada hata: " + e.getMessage());
        }

		return filesName;
	}

	
	private void handleClient(Socket clientSocket) {
		try {
			InputStream inputStream = clientSocket.getInputStream();
			receiveFile(inputStream, clientSocket);
		} catch (IOException e) {
			System.err.println("Client handling error: " + e.getMessage());
		}
	}

	
	private void receiveFile(InputStream inputStream, Socket socket) {
		try {

			DataInputStream dIS = new DataInputStream(inputStream);
			DataOutputStream dOS = new DataOutputStream(socket.getOutputStream());

			
			String receivedFileName = dIS.readUTF();
			System.out.println("dosya adı: " + receivedFileName);
			int length = dIS.readInt();

			
			File file = new File(destinationFolder, receivedFileName);
			if (!file.exists()) {
				file.createNewFile();
			}

			RandomAccessFile rAF = new RandomAccessFile(file, "rw");
			System.out.println("Dosya uzunluğu alındı: " + length);
			rAF.setLength(length);
			int totalChunks = (int) Math.ceil(length / (double) 256000); // Toplam chunk sayısı
	        int chunkCounter = 0;
			
			int i;
			while ((i = dIS.readInt()) != -1) {
				System.out.println("Alınan parça numarası: " + i);
				rAF.seek(i * 256000);
				int chunkLength = dIS.readInt();
				byte[] toReceive = new byte[chunkLength];
				dIS.readFully(toReceive);
				rAF.write(toReceive);
				dOS.writeInt(i);
				System.out.println("Onay gönderildi: " + i);
				chunkCounter++;
				
				double percentage = (chunkCounter / (double) totalChunks) * 100;
				mainFrame.updateDownloading(receivedFileName + ": " + percentage);
			}
			rAF.close();
			socket.close();
			
			System.out.println("Dosya alındı: " + file.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Dosya alımında hata: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Dosyayı bulma
	public File fileByName(String fileName) {
		File directory = new File(sharedFolder);

		if (!directory.isDirectory()) {
			System.out.println("Geçerli bir dizin değil: " + sharedFolder);
			return null;
		}

		File[] files = directory.listFiles();

		if (files != null) {
			for (File file : files) {
				
				if (file.isFile() && file.getName().equals(fileName)) {
					return file;
				}
			}
		}

		
		System.out.println("Dir error: " + fileName);
		return null;
	}

	
	public void sendFile(File file, String peerIP) {
		try (Socket socket = new Socket(peerIP, BC_PORT);

				DataInputStream dIS = new DataInputStream(socket.getInputStream());
				DataOutputStream dOS = new DataOutputStream(socket.getOutputStream());

				RandomAccessFile rAF = new RandomAccessFile(file, "r")) {

			int length = (int) file.length();
			System.out.println("lenght: " + length);
			int chunkCount = (int) Math.ceil(length / 256000.0);
			int[] checkArray = new int[chunkCount];
			Random random = new Random();

			
			dOS.writeUTF(file.getName());
			System.out.println(file.getName());
			dOS.writeInt(length);

			int loop = 0;
			while (loop < chunkCount) {
				int i = random.nextInt(chunkCount);
				if (checkArray[i] == 0) {
					rAF.seek(i * 256000);
					byte[] toSend = new byte[256000];
					int read = rAF.read(toSend);
					dOS.writeInt(i); 
					dOS.writeInt(read); 
					dOS.write(toSend, 0, read); 
					dOS.flush();
					int ACK = dIS.readInt();
					if (i == ACK) {
						checkArray[i] = 1;
						loop++;
					}
				}
			}
			System.out.println(">>> Sent all chunks to " + socket.getInetAddress().getHostAddress() + "...");
			rAF.close();
			dOS.writeInt(-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Peer bağlantısını kapatma
	public void stopServer() {
		try {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
				
				String message = "DISCONNECT";
				byte[] messageBytes = message.getBytes();
				DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getByName(BC_ADDRESS),BC_PORT);
				socket.send(packet);
				

			} catch (IOException e) {
				System.err.println(e.getMessage());
			} finally {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
			}
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
				connectedPeers.clear();
				System.out.println("Server closed.");
			}
		} catch (IOException e) {
			System.err.println("Server cannot be closed: " + e.getMessage());
		}
	}

	// connectedPeers getter
	public List<String> getFiles() {
		List<String> allFiles = new ArrayList<>();

		
		for (String peerIP : connectedPeers.keySet()) {
			
			List<String> files = connectedPeers.get(peerIP);

			
			allFiles.addAll(files);
		}

		
		return allFiles;
	}
}