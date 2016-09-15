import java.net.MalformedURLException;
import java.rmi.*;  
import java.rmi.server.*;
import java.util.*;

import java.util.PriorityQueue;

public class AdderRemote extends UnicastRemoteObject implements Adder{  
	
	private PriorityQueue<QueueElement> queue = new PriorityQueue<QueueElement>();
	private HashMap acks;
	private int hasLock = 0;
	private int requestLock = 0; // This would have the sent request timestamp
	private String name;
	private int myClock = 1;
	
	AdderRemote()throws RemoteException{  
		super();
	}  
	
	public int add(int x,int y){
		return x+y;
	}
	
	public void ReceiveUnlockRequest(String sender) throws RemoteException{
		QueueElement top = queue.peek();
		if(top.getName() == sender){
			queue.poll();
		}
	}
	
	public int UnlockRequest(String receiver, int clock) throws RemoteException{
		try {
			Adder receiver_node=(Adder)Naming.lookup("rmi://localhost:5000/" + receiver);
			receiver_node.ReceiveUnlockRequest(receiver);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void ReceiveAckRequest(String sender) throws RemoteException{
		acks.put(sender,1);
	}
	
	public int SendAckRequest(String receiver) throws RemoteException{
		try {
			Adder receiver_node=(Adder)Naming.lookup("rmi://localhost:5000/" + receiver);
			receiver_node.ReceiveAckRequest(name);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int ReceiveLockRequest(String sender, int clock) throws RemoteException {
		if(requestLock == 0){
			SendAckRequest(sender);
		}
		else if(hasLock != 0){
			QueueElement elem = new QueueElement(sender,clock);
			queue.add(elem);
		}
		else if(requestLock != 0){
			if(clock<requestLock){
				SendAckRequest(sender);
			}
			else{
				QueueElement elem = new QueueElement(sender,clock);
				queue.add(elem);
			}
		}
		return 0;
	}
	
	public int LockRequest(String receiver) throws RemoteException{
		try {
			Adder receiver_node=(Adder)Naming.lookup("rmi://localhost:5000/" + receiver);
			// set requestlock = 1
			receiver_node.ReceiveLockRequest(name,myClock);
			acks.clear();
		} catch (MalformedURLException e) {	
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

} 