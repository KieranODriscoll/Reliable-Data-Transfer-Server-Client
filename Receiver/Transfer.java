import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Transfer implements Runnable {

	static Integer seqNum = 1;
	static Integer prevSeqNum = 0;
	static DatagramSocket ds = null;
	static boolean EOTReceived = false;
	static String senderIP;
	static Integer senderPort;
	static Integer receiverPort;
	static String fileName;
	GUI client;

	static boolean dropped = false;

	public Transfer(GUI client) {
		this.client = client;
	}

	@Override
	public void run() {

		try {
			InitiateTransfer();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		boolean resent = false;

		while (!EOTReceived) {
			Datagram dg = null;
			try {
				dg = ReceiveDatagram();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (dg != null) {
				if (dg.seqNum.intValue() == prevSeqNum) {
					try {
						SendACK(prevSeqNum);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					resent = true;
				} else {
					try {
						SendACK(seqNum);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if (!EOTReceived && resent != true && dropped != true) {
				//System.out.println("EOT not received, incrementing sequence number and writing to file");
				client.setCurrentCount("" + seqNum);
				prevSeqNum = seqNum;
				seqNum++;
				try {
					WriteToFile(dg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				client.redraw();
			}

			resent = false;
		}
		
		try {
			SendACK(-1);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		client.setTransferComplete();
		
		try {
			SendEOTACKUntilTimeout();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Timeout, shutting socket");
		seqNum = 1;
		prevSeqNum = 0;
		EOTReceived = false;
		ds.close();
		
		return;

	}

	public void InitiateTransfer() throws IOException {
		senderIP = client.getSenderHostAddress();
		senderPort = Integer.parseInt(client.getSenderPort());
		receiverPort = Integer.parseInt(client.getReceiverPort());
		fileName = client.getFileNameInput();
		ds = new DatagramSocket(receiverPort);
		client.setTransferInvisible();
		System.out.println("Datagram Socket start at port: " + receiverPort);
	}

	public static void SendACK(Integer num) throws IOException {

		ByteArrayOutputStream outB = new ByteArrayOutputStream();
		ObjectOutputStream outO = new ObjectOutputStream(outB);

		//System.out.println("Sending ACK for packet num " + num);
		ACK ack = new ACK(num);

		outO.writeObject(ack);
		outO.flush();
		outO.reset();

		byte[] outData = outB.toByteArray();
		DatagramPacket dpOut = new DatagramPacket(outData, outData.length, InetAddress.getByName(senderIP), senderPort);
		ds.send(dpOut);
		
		outB.close();
		outO.close();

	}

	public Datagram ReceiveDatagram() throws IOException, ClassNotFoundException {

		Datagram dg = null;
		EOT eot = null;

		byte[] receivedData = new byte[1024];
		DatagramPacket dp = new DatagramPacket(receivedData, 1024);
		ds.receive(dp);
		byte[] data = dp.getData();

		ByteArrayInputStream inB = new ByteArrayInputStream(data);
		ObjectInputStream inO = new ObjectInputStream(inB);

		Object o = inO.readObject();

		if (o instanceof Datagram) {
			dg = (Datagram) o;
			//System.out.println("Datagram object received");
			//System.out.println("Datagram has sequence number: " + dg.seqNum.intValue());
			if (!client.getReliable()) {
				if (dg.seqNum.intValue() % 10 == 0 && dropped == false) {
					System.out.println("PACKET DROPPED Sequence Number: " + dg.seqNum);
					dropped = true;
					return null;
				}
			}
			dropped = false;
		} else if (o instanceof EOT) {
			eot = (EOT) o;
			//System.out.println("End of Transmission received, sending ACK");
			EOTReceived = true;
			return null;
		}

		inB.close();
		inO.close();
		
		return dg;

	}

	public static void WriteToFile(Datagram dg) throws FileNotFoundException, IOException {

		try (FileOutputStream fos = new FileOutputStream(fileName, true)) {
			fos.write(dg.fileBytes);
			fos.close();
		}

	}
	
	public static void SendEOTACKUntilTimeout() throws IOException, ClassNotFoundException {
		
		ByteArrayInputStream inB = null;
		ObjectInputStream inO = null;
		EOT eot = null;
		
		byte[] receivedData = new byte[1024];
		DatagramPacket dp = new DatagramPacket(receivedData, 1024);
		
		ds.setSoTimeout(5000);
		boolean received = false;
		while(!received) {
			try {
				ds.receive(dp);
				
				byte[] inData = dp.getData();
				inB = new ByteArrayInputStream(inData);
				inO = new ObjectInputStream(inB);
				
				eot = (EOT) inO.readObject();
				
				if(eot.eotNum.intValue() == -1) {
					//System.out.println("Receieve Ack: -1");
					received = true;
					SendACK(-1);
				}
			}catch(SocketTimeoutException e) {
				return;
			}
		}
	}

}
