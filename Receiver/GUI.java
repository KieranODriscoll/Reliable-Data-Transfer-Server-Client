import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class GUI {
	
	JFrame clientFrame;
	private FlowLayout frameLayout;
	
	private JPanel topBuffer;
	private JPanel midLeft;
	private JPanel midRight;
	private JPanel bottomLeft;
	private JPanel bottomMid;
	private JPanel bottomRight;
	
	private JLabel senderHostAddressLabel;
	private JTextArea senderHostAddress;
	private JLabel senderPortLabel;
	private JTextArea senderPort;
	private JLabel receiverPortLabel;
	private JTextArea receiverPort;
	private JLabel fileNameInputLabel;
	private JTextArea fileNameInput;
	
	private ButtonGroup reliability;
	private JRadioButton reliableOption;
	private JRadioButton unreliableOption;
	
	JButton transferButton;
	private JLabel receivedPacketsDisplay;
	private JLabel receivedPacketsDisplayLabel;
	
	private JLabel transferComplete;
	
	public GUI() {
		newClient();
	}
	
	public void newClient() {
		clientFrame = new JFrame ("UDP DataTransfer - Receiver");
		clientFrame.getContentPane().setBackground(Color.WHITE);
		frameLayout = new FlowLayout();
		
		SimpleAttributeSet aSet = new SimpleAttributeSet();
	    StyleConstants.setAlignment(aSet,StyleConstants.ALIGN_CENTER);
		
		clientFrame.setLayout(frameLayout);
		clientFrame.setSize(680,260);
		clientFrame.setLocation(50, 50);
		clientFrame.setVisible(true);
		clientFrame.setResizable(false);
		clientFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
     		}        
		});
		
		Border blackBorder = BorderFactory.createLineBorder(Color.BLACK);
		Border whiteBorder = BorderFactory.createLineBorder(Color.WHITE);
		
		topBuffer = new JPanel();
		topBuffer.setBackground(Color.WHITE);
		topBuffer.setPreferredSize(new Dimension(640,5));
		topBuffer.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		clientFrame.add(topBuffer);
		
		midLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		midLeft.setBackground(Color.WHITE);
		midLeft.setPreferredSize(new Dimension(390,110));
		midLeft.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		clientFrame.add(midLeft);
		
		midRight = new JPanel();
		midRight.setBackground(Color.WHITE);
		midRight.setPreferredSize(new Dimension(250,110));
		midRight.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		clientFrame.add(midRight);
		
		bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottomLeft.setBackground(Color.WHITE);
		bottomLeft.setPreferredSize(new Dimension(100,80));
		bottomLeft.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		clientFrame.add(bottomLeft);
		
		bottomMid = new JPanel();
		bottomMid.setBackground(Color.WHITE);
		bottomMid.setPreferredSize(new Dimension(290,80));
		bottomMid.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		clientFrame.add(bottomMid);
		
		bottomRight = new JPanel();
		bottomRight.setBackground(Color.WHITE);
		bottomRight.setPreferredSize(new Dimension(250,80));
		bottomRight.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		clientFrame.add(bottomRight);
		
		senderHostAddressLabel = new JLabel ("Host Address of sender: ");
		senderHostAddressLabel.setBorder(BorderFactory.createCompoundBorder(whiteBorder,
	            BorderFactory.createEmptyBorder(1,1,1,1)));
		midLeft.add(senderHostAddressLabel);
		senderHostAddress = new JTextArea(1,22);
		senderHostAddress.setBorder(BorderFactory.createCompoundBorder(blackBorder,
	            BorderFactory.createEmptyBorder(1,1,1,1)));
		midRight.add(senderHostAddress);
		
		senderPortLabel = new JLabel ("UDP port number used by sender to receive ACK's from sender: ");
		senderPortLabel.setBorder(BorderFactory.createCompoundBorder(whiteBorder,
	            BorderFactory.createEmptyBorder(1,1,1,1)));
		midLeft.add(senderPortLabel);
		senderPort = new JTextArea(1,22);
		senderPort.setBorder(BorderFactory.createCompoundBorder(blackBorder,
	            BorderFactory.createEmptyBorder(1,1,1,1)));
		midRight.add(senderPort);
		
		receiverPortLabel = new JLabel ("UDP port number used by receiver to receive data from sender: ");
		receiverPortLabel.setBorder(BorderFactory.createCompoundBorder(whiteBorder,
	            BorderFactory.createEmptyBorder(1,1,1,1)));
		midLeft.add(receiverPortLabel);
		receiverPort = new JTextArea(1,22);
		receiverPort.setBorder(BorderFactory.createCompoundBorder(blackBorder,
	            BorderFactory.createEmptyBorder(1,1,1,1)));
		midRight.add(receiverPort);
		
		fileNameInputLabel = new JLabel ("Name of file to write received data: ");
		fileNameInputLabel.setBorder(BorderFactory.createCompoundBorder(whiteBorder,
	            BorderFactory.createEmptyBorder(1,1,1,1)));
		midLeft.add(fileNameInputLabel);
		fileNameInput = new JTextArea(1,22);
		fileNameInput.setBorder(BorderFactory.createCompoundBorder(blackBorder,
	            BorderFactory.createEmptyBorder(1,1,1,1)));
		midRight.add(fileNameInput);
		
		reliability = new ButtonGroup();
		reliableOption = new JRadioButton ("Reliable");
		reliableOption.setBackground(Color.WHITE);
		reliableOption.setSelected(true);
		unreliableOption = new JRadioButton ("Unreliable");
		unreliableOption.setBackground(Color.WHITE);
		reliability.add(reliableOption);
		reliability.add(unreliableOption);
		bottomLeft.add(reliableOption);
		bottomLeft.add(unreliableOption);
		
		transferButton = new JButton("TRANSFER");
		transferButton.setPreferredSize(new Dimension(280,70));
		bottomMid.add(transferButton);
		
		receivedPacketsDisplayLabel = new JLabel("Number of received in-order packets:");
		bottomRight.add(receivedPacketsDisplayLabel);
		receivedPacketsDisplay = new JLabel("0",SwingConstants.CENTER);
		receivedPacketsDisplay.setMinimumSize(new Dimension (246,20));
		receivedPacketsDisplay.setPreferredSize(new Dimension (246,20));
		receivedPacketsDisplay.setBorder(BorderFactory.createCompoundBorder(blackBorder,
	            BorderFactory.createEmptyBorder(1,1,1,1)));
		bottomRight.add(receivedPacketsDisplay);
		
		transferComplete = new JLabel("File Transfer Complete",SwingConstants.CENTER);
		bottomRight.add(transferComplete);
		transferComplete.setVisible(false);
		
		clientFrame.validate();
        clientFrame.repaint();
        
	}
	
	public String getSenderHostAddress() {
		return this.senderHostAddress.getText();
	}
	
	public String getSenderPort() {
		return this.senderPort.getText();
	}
	
	public String getReceiverPort() {
		return this.receiverPort.getText();
	}
	
	public String getFileNameInput() {
		return this.fileNameInput.getText();
	}
	
	public boolean getReliable() {
		return this.reliableOption.isSelected();
	}
	
	public void setCurrentCount(String s) {
		this.receivedPacketsDisplay.setText(s);
	}
	
	public void setTransferComplete() {
		this.transferComplete.setVisible(true);

	}
	
	public void setTransferInvisible(){
		this.transferComplete.setVisible(false);
	}
	
	public void redraw () {
		this.clientFrame.validate();
        this.clientFrame.repaint();
	}
	
	

}
