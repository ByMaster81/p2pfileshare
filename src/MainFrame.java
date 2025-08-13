import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;
    
    private boolean connected = false;
    private Timer broadcastTimer;
    private JTextArea textAreaDownloading;
    

    public MainFrame(Peer peer) {
    	setTitle("IS THAT A F* NAPSTER?!!");
        
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 700);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFiles = new JMenu("Files");
        menuBar.add(mnFiles);

        JMenuItem mntmConnect = new JMenuItem("Connect");
        mnFiles.add(mntmConnect);

        JMenuItem mntmDisconnect = new JMenuItem("Disconnect");
        mnFiles.add(mntmDisconnect);

        JMenu mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);

        JMenuItem mntmAbout = new JMenuItem("About");
        mnHelp.add(mntmAbout);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panelSharedFolder = new JPanel();
        panelSharedFolder.setBounds(10, 10, 464, 40);
        panelSharedFolder.setBorder(new LineBorder(new Color(0, 0, 0)));
        contentPane.add(panelSharedFolder);
        panelSharedFolder.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JLabel lblSharedFolder = new JLabel("Root of the P2P shared folder:");
        panelSharedFolder.add(lblSharedFolder);

        textField = new JTextField();
        textField.setToolTipText("Root of the P2P shared folder");
        panelSharedFolder.add(textField);
        textField.setColumns(20);

        JButton btnSetSharedFolder = new JButton("Set");
        panelSharedFolder.add(btnSetSharedFolder);

        JPanel panelDestinationFolder = new JPanel();
        panelDestinationFolder.setBounds(10, 60, 464, 40);
        panelDestinationFolder.setBorder(new LineBorder(new Color(0, 0, 0)));
        contentPane.add(panelDestinationFolder);
        panelDestinationFolder.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JLabel lblDestinationFolder = new JLabel("Destination folder:");
        panelDestinationFolder.add(lblDestinationFolder);

        textField_1 = new JTextField();
        textField_1.setToolTipText("Destination folder");
        panelDestinationFolder.add(textField_1);
        textField_1.setColumns(25);

        JButton btnSetDestinationFolder = new JButton("Set");
        panelDestinationFolder.add(btnSetDestinationFolder);

        JPanel panelSettings = new JPanel();
        panelSettings.setBounds(10, 110, 464, 210);
        panelSettings.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Settings"));
        contentPane.add(panelSettings);
        panelSettings.setLayout(null);

        JCheckBox chckbxRootOnly = new JCheckBox("Check new files only in the root");
        chckbxRootOnly.setBounds(10, 20, 300, 25);
        panelSettings.add(chckbxRootOnly);

        JLabel lblExcludedFolders = new JLabel("Folder exclusion:");
        lblExcludedFolders.setBounds(10, 50, 200, 15);
        panelSettings.add(lblExcludedFolders);

        JList<String> listExcludedFolders = new JList<>(new DefaultListModel<>());
        JScrollPane scrollExcludedFolders = new JScrollPane(listExcludedFolders);
        scrollExcludedFolders.setBounds(10, 70, 200, 100);
        panelSettings.add(scrollExcludedFolders);

        JButton btnAddFolder = new JButton("Add");
        btnAddFolder.setBounds(220, 70, 80, 25);
        panelSettings.add(btnAddFolder);

        JButton btnDelFolder = new JButton("Del");
        btnDelFolder.setBounds(220, 100, 80, 25);
        panelSettings.add(btnDelFolder);

        JLabel lblExcludedFiles = new JLabel("Exclude files :");
        lblExcludedFiles.setBounds(310, 50, 200, 15);
        panelSettings.add(lblExcludedFiles);

        JList<String> listExcludedFiles = new JList<>(new DefaultListModel<>());
        JScrollPane scrollExcludedFiles = new JScrollPane(listExcludedFiles);
        scrollExcludedFiles.setBounds(310, 70, 140, 100);
        panelSettings.add(scrollExcludedFiles);

        JButton btnAddFile = new JButton("Add");
        btnAddFile.setBounds(310, 180, 65, 25);
        panelSettings.add(btnAddFile);

        JButton btnDelFile = new JButton("Del");
        btnDelFile.setBounds(385, 180, 65, 25);
        panelSettings.add(btnDelFile);

        JPanel panelDownloading = new JPanel();
        panelDownloading.setBounds(10, 320, 464, 100);
        panelDownloading.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Downloading files"));
        contentPane.add(panelDownloading);
        panelDownloading.setLayout(new BorderLayout());

        
        textAreaDownloading = new JTextArea();
        textAreaDownloading.setEditable(false);
        JScrollPane scrollDownloading = new JScrollPane(textAreaDownloading);
        panelDownloading.add(scrollDownloading, BorderLayout.CENTER);

        JPanel panelFoundFiles = new JPanel();
        panelFoundFiles.setBounds(10, 430, 464, 100);
        panelFoundFiles.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Found files"));
        contentPane.add(panelFoundFiles);
        panelFoundFiles.setLayout(new BorderLayout());
        JScrollPane scrollFoundFiles = new JScrollPane();
        panelFoundFiles.add(scrollFoundFiles, BorderLayout.CENTER);
        
        JList<String> list = new JList<>();
        scrollFoundFiles.setViewportView(list);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(10, 540, 464, 25);
        contentPane.add(btnSearch);

        // Action listener Connect" 
        mntmConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connected = true;
                peer.connectToNetwork();
                JOptionPane.showMessageDialog(null, "Connected to the network.");
            }
        });

        // Disconnect
        mntmDisconnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                peer.stop();
                JOptionPane.showMessageDialog(null, "Disconnected from the overlay network.");
            }
        });
        
        
        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        MainFrame.this, 
                        "Peer-to-Peer File Sharing App\nDeveloped by: Rüştü Yemenici", 
                        "About", 
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        // "Set Shared Folder" button
        btnSetSharedFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sharedFolder = textField.getText();
                peer.setSharedFolder(sharedFolder);
                
            }
        });

        // "Set Destination Folder" button
        btnSetDestinationFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String destinationFolder = textField_1.getText();
               
                peer.setDestinationFolder(destinationFolder);
            }
        });

        // "Add Folder" button
        btnAddFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String folderName = JOptionPane.showInputDialog("Enter folder name to exclude:");
                if (folderName != null) {
                    ((DefaultListModel<String>) listExcludedFolders.getModel()).addElement(folderName);
                }
            }
        });

        // "Delete Folder" button
        btnDelFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = listExcludedFolders.getSelectedIndex();
                if (selectedIndex != -1) {
                    ((DefaultListModel<String>) listExcludedFolders.getModel()).remove(selectedIndex);
                }
            }
        });

        // "Add File" button
        btnAddFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filename = JOptionPane.showInputDialog("Enter filename to exclude:");
                if (filename != null) {
                    ((DefaultListModel<String>) listExcludedFiles.getModel()).addElement(filename);
                    peer.excFilesAdd(filename);
                }
            }
        });

        // "Delete File" button
        btnDelFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = listExcludedFiles.getSelectedIndex();
                if (selectedIndex != -1) {
                    ((DefaultListModel<String>) listExcludedFiles.getModel()).remove(selectedIndex);
                }
            }
        });

        // "Search" button
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                List<String> files = peer.getFiles();
                
                DefaultListModel<String> listModel = new DefaultListModel<>();
                for (String file : files) {
                    listModel.addElement(file);
                }
                list.setModel(listModel);
            }
        });
        
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                
                if (e.getClickCount() == 2) {
                    // Seçilen öğeyi al
                    String selectedItem = list.getSelectedValue();
                    peer.requestFile(selectedItem);
                    
                }
            }
        });

        // "Send File" button ekle
        JButton btnSendFile = new JButton("Send File");
        btnSendFile.setBounds(10, 580, 464, 25);
        contentPane.add(btnSendFile);

        // "Send File" button
        btnSendFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String peerIP = JOptionPane.showInputDialog("Enter Peer IP to send the file:");
                    
                    if (peerIP != null && !peerIP.isEmpty()) {
                        
                        peer.sendFile(selectedFile, peerIP);
                    }
                }
            }
        });
        
        
        broadcastTimer = new Timer();
        broadcastTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                
                if (connected) {
                    peer.connectToNetwork();  
                }
            }
        }, 0, 5000);  // 5 sec
        
    }

    public void updateDownloading(String message) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                
                if (textAreaDownloading != null) {
                    textAreaDownloading.setText(message + "\n");
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}