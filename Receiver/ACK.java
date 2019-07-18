import java.io.Serializable;

public class ACK implements Serializable{
	Integer seqNum;
	
	public ACK(Integer seqNum){
		this.seqNum = seqNum;
	}
}
