import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;

public class Receiver {

	private static GUI client;
	

	public static void main(String[] args) {
		
		client = new GUI();
		
		client.transferButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent connectEvent) {
				
				Transfer transfer = new Transfer(client);
				
				Thread thread = new Thread(transfer);
				
				thread.start();
				
			}
		});
	}

}
