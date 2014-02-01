package com.maspain.chitchatserver;

import java.net.InetAddress;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ChitChatServer extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	private String title = "ChitChat Server";
	private static String serverURL = "http://server.maspain.com/";
	private String portRange = "50000-50009";
	private int maxChannels = 10;
	private String ip;
	private int startingPort = 50000;
	private  Server[] serverList;
	
	private JTextPane textLog;
	private JComboBox<String> comboBoxChannelList;
	private JButton btnRefresh;
	private int currentChannel = 0;
	private TimerTask task;
	private Timer timer;
	
	public ChitChatServer(String ip) {
		this.ip = ip;
		timer = new Timer();
				
		serverList = new Server[maxChannels];
		for (int i = 0; i < maxChannels; i++) {
			// Create a Server object, which will automatically begin accepting connections
			serverList[i] = new Server(startingPort + i);
		}
		
		createWindow();
		setBehaviors();
	}
	
	private void createWindow() {
		setResizable(false);
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(820, 540);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblServerPortNumber = new JLabel("Server port number:");
		lblServerPortNumber.setBounds(19, 6, 124, 16);
		contentPane.add(lblServerPortNumber);
		
		JLabel lblServerIpAddress = new JLabel("Server IP address:");
		lblServerIpAddress.setBounds(280, 6, 110, 16);
		contentPane.add(lblServerIpAddress);
		
		JLabel lblPortRange = new JLabel(portRange);
		lblPortRange.setBounds(155, 6, 115, 16);
		contentPane.add(lblPortRange);
		
		JLabel lblIpAddress = new JLabel(ip);
		lblIpAddress.setBounds(402, 6, 115, 16);
		contentPane.add(lblIpAddress);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 75, 808, 437);
		contentPane.add(scrollPane);
		
		textLog = new JTextPane();
		textLog.setFont(new Font("Monaco", Font.PLAIN, 11));
		textLog.setEditable(false);
		scrollPane.setViewportView(textLog);
		
		comboBoxChannelList = new JComboBox<String>();
		comboBoxChannelList.setModel(new DefaultComboBoxModel<String>(new String[] { "Channel 1", "Channel 2", "Channel 3", "Channel 4", "Channel 5", "Channel 6", "Channel 7", "Channel 8", "Channel 9", "Channel 10" }));
		comboBoxChannelList.setSelectedIndex(currentChannel);
		comboBoxChannelList.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.setColumnHeaderView(comboBoxChannelList);
		
		JLabel lblLog = new JLabel("Log:");
		lblLog.setBounds(19, 34, 27, 29);
		contentPane.add(lblLog);
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(58, 34, 117, 29);
		contentPane.add(btnRefresh);
	}
	
	private void setBehaviors() {
		comboBoxChannelList.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				currentChannel = comboBoxChannelList.getSelectedIndex();
				showLog(currentChannel);
			}
		});
		
		btnRefresh.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				showLog(currentChannel);
			}
		});
		
		task = new TimerTask() {
			public void run() {
				showLog();	
			}
		};
		timer.scheduleAtFixedRate(task, 0, 5000);
	}
	
	public void showLog(int channelNumber) {
		textLog.setText(serverList[channelNumber].getLog());
		textLog.setCaretPosition(textLog.getDocument().getLength());
	}
	
	public void showLog() {
		textLog.setText(serverList[currentChannel].getLog());
		textLog.setCaretPosition(textLog.getDocument().getLength());
	}
	
	public static void main(String args[]) {
		InetAddress address;
		try {
			address = InetAddress.getByName(new URL(serverURL).getHost());
			ChitChatServer frame = new ChitChatServer(address.getHostAddress());
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
