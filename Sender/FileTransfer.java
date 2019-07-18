import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileTransfer implements Runnable {
	
	static InetAddress receiverIP;
	static Integer receiverPort;
	Integer senderPort;
	String fileName;
	static Integer timeout;
	static DatagramSocket ds = null;
	static Integer seqNum = 1;
	static byte[] fileBytes;
	static private long startTime = 0;
	static private long stopTime = 0;

	
	public FileTransfer(String receiverIP, Integer receiverPort, Integer senderPort, String fileName, Integer timeout) {
		try {
			FileTransfer.receiverIP = InetAddress.getByName(receiverIP);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileTransfer.receiverPort = receiverPort;
		this.senderPort = senderPort;
		this.fileName = fileName;
		FileTransfer.timeout = timeout;
	}
	
	public void run() {
		
		int numBytesRead = 0;
		long fileSize = 0;
		fileBytes = new byte[124];
		
		BufferedInputStream inF = null;
		
		try {
			ds = new DatagramSocket(senderPort);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			inF = new BufferedInputStream(new FileInputStream(fileName));
			fileSize = inF.available();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		try {
			while((numBytesRead = inF.read(fileBytes, 0, 124)) != -1) {
				
				if(numBytesRead < 124) {
					byte[] data = new byte[numBytesRead];
					data = Arrays.copyOfRange(fileBytes, 0, numBytesRead);
					SendDatagram(data);
				}else {
					SendDatagram(fileBytes);
				}
				
				ACK ack;
				
				ack = receiveACK();
				
				seqNum++;				

				fileBytes = new byte[124];
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(numBytesRead == -1) {
			try {
				SendEOT();
				receiveFinalACK();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		stopTime = System.currentTimeMillis();
		System.out.println("");
		System.out.println("-------------------------------------------");
		System.out.println("Total Transmission Time Report");
		System.out.println("-------------------------------------------");
		System.out.println("Approximate File Size in Bytes: "+ fileSize);
		double totalTime = Math.round((stopTime-startTime)*100)/100;
		System.out.println("Time to transfer in seconds: " + (totalTime / 1000));
		
		
		ds.close();
		try {
			inF.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void SendDatagram(byte[] fileBytes) throws IOException {
		
		if(seqNum == 1) {
			startTime = System.currentTimeMillis();
		}
		
		ByteArrayOutputStream outB = null;
		ObjectOutputStream outO = null;
		
		outB = new ByteArrayOutputStream();
		outO = new ObjectOutputStream(outB);
		
		Datagram dg = new Datagram(seqNum,fileBytes);
		outO.writeObject(dg);
		outO.flush();
		outO.reset();
		
		byte[] data = outB.toByteArray();
		
		//System.out.println("Sending datagram with sequence number: " + seqNum);
		DatagramPacket dp = new DatagramPacket(data, data.length, receiverIP, receiverPort);
		ds.send(dp);
		
		outB.close();
		outO.close();
		
	}
	
	public static void SendEOT() throws IOException {
		
		ByteArrayOutputStream outB = null;
		ObjectOutputStream outO = null;
		
		outB = new ByteArrayOutputStream();
		outO = new ObjectOutputStream(outB);
		
		EOT eot = new EOT(-1);
		outO.writeObject(eot);
		outO.flush();
		outO.reset();
		
		byte[] data = outB.toByteArray();
		
		//System.out.println("Sending EOT Datagram");
		DatagramPacket dp = new DatagramPacket(data, data.length, receiverIP, receiverPort);
		ds.send(dp);
		
		outB.close();
		outO.close();
		
	}
	
	public static ACK receiveACK() throws IOException, ClassNotFoundException {
		
		ByteArrayInputStream inB = null;
		ObjectInputStream inO = null;
		ACK ack = null;
		
		byte[] receivedData = new byte[1024];
		DatagramPacket dp = new DatagramPacket(receivedData, 1024);
		
		ds.setSoTimeout(timeout);
		boolean received = false;
		while(!received) {
			try {
				ds.receive(dp);
				byte[] inData = dp.getData();
				inB = new ByteArrayInputStream(inData);
				inO = new ObjectInputStream(inB);
				
				ack = (ACK) inO.readObject();
				//System.out.println("Received ACK: " + ack.seqNum);
				if(ack.seqNum.intValue() == seqNum) {
					received = true;
				}
				else {
					SendDatagram(fileBytes);
				}
			}catch(SocketTimeoutException e) {
				SendDatagram(fileBytes);
			}
		}
		byte[] inData = dp.getData();
		inB = new ByteArrayInputStream(inData);
		inO = new ObjectInputStream(inB);
		
		inB.close();
		inO.close();
		
		return ack;
	}
	
	public static ACK receiveFinalACK() throws IOException, ClassNotFoundException {
		
		ByteArrayInputStream inB = null;
		ObjectInputStream inO = null;
		ACK ack = null;
		
		byte[] receivedData = new byte[1024];
		DatagramPacket dp = new DatagramPacket(receivedData, 1024);
		
		ds.setSoTimeout(timeout);
		boolean received = false;
		while(!received) {
			try {
				ds.receive(dp);
				
				byte[] inData = dp.getData();
				inB = new ByteArrayInputStream(inData);
				inO = new ObjectInputStream(inB);
				
				ack = (ACK) inO.readObject();
				
				if(ack.seqNum.intValue() == -1) {
					//System.out.println("Receieve Ack: -1");
					received = true;
				}
			}catch(SocketTimeoutException e) {
				SendEOT();
			}
		}
		
		
		inB.close();
		inO.close();
		
		return ack;
	}
}
