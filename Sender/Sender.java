
public class Sender {
	
	public static void main(String[] args) throws Exception {
		//String receiverIP = "127.0.0.1";
		//Integer receiverPort = 8181;
		//Integer senderPort = 8282;
		//String fileName = "test.pdf";
		//Integer timeout = 50;
		
		String receiverIP = null;
		Integer receiverPort = null;
		Integer senderPort = null;
		String fileName = null;
		Integer timeout = null;
		
		
		
		if (args.length == 5) {
			receiverIP = args[0];
			receiverPort = Integer.parseInt(args[1]);
			senderPort = Integer.parseInt(args[2]);
			fileName = args[3];
			timeout = Integer.parseInt(args[4]);
			timeout = timeout / 1000;
		} else {
			System.out.println("Only 5 arguments should be supplied");
			System.exit(1);
		}
		
		System.out.println("Starting to send file " + fileName);
		new FileTransfer(receiverIP, receiverPort, senderPort, fileName, timeout).run();
		
		
		
	}

}
