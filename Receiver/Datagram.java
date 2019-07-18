import java.io.Serializable;

public class Datagram implements Serializable{
	
	Integer seqNum;
	byte[] fileBytes;
	
	public Datagram(Integer seqNum, byte[] fileBytes) {
		this.seqNum = seqNum;
		this.fileBytes = fileBytes;
	}
	
}
