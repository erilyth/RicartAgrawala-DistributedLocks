import java.net.MalformedURLException;
import java.rmi.*;  
import java.rmi.server.*;
import java.util.*;

import java.util.PriorityQueue;

import com.google.protobuf.CodedInputStream;

import com.example.result.ResultProto.Clock;
import com.example.result.ResultProto.Clock.Builder;
import com.example.result.ResultProto.ClockMember;
import com.example.result.ResultProto.Queue;
import com.example.result.ResultProto.QueueElement;

public class AdderRemote extends UnicastRemoteObject implements Adder{  
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Queue.Builder queue = Queue.newBuilder();
	public HashMap<String, String> acks = new HashMap<String, String>();
	public int hasLock = 0;
	public int requestLock = 0; // This would have the sent request timestamp
	public Clock.Builder requestLockClockB = Clock.newBuilder();
	public Clock requestLockClock = requestLockClockB.build();
	public String name;
	public Clock.Builder myClockB = Clock.newBuilder();
	public Clock myClock = myClockB.build();

	public int getHasLock() throws RemoteException {
		return hasLock;
	}

	public void setHasLock(int hasLock) throws RemoteException {
		this.hasLock = hasLock;
	}

	public int getRequestLock() throws RemoteException {
		return requestLock;
	}

	public void setRequestLock(int requestLock) throws RemoteException {
		this.requestLock = requestLock;
	}

	public String getName() throws RemoteException {
		return name;
	}

	public void setName(String name) throws RemoteException {
		this.name = name;
	}

	public Clock getMyClock() throws RemoteException {
		return myClock;
	}

	public void setMyClock(Clock clock) throws RemoteException {
		this.myClock = clock;
	}
	
	public void addqueue(QueueElement elem) throws RemoteException {
		queue.add(elem);
	}
	
	public QueueElement removequeue() throws RemoteException {
		QueueElement top = queue.peek();
		queue.poll();
		System.out.println("Remove top of queue of " + name);
		System.out.println("Top of the queue was " + top);
		return top;
	}
	
	public QueueElement topqueue() throws RemoteException {
		QueueElement top = queue.peek();
		return top;
	}
	
	public int getack(String myname) throws RemoteException {
		System.out.println("Check if ack of " + myname + " is there in " + name);
		System.out.println(acks.get(myname));
		if(acks.get(myname)!= null && acks.get(myname).equals("1")){
			return 1;
		}
		else{
			return 0;
		}
	}
	
	public void setack(String name) throws RemoteException {
		acks.put(name, "1");
	}
	
	public void setRequestLockClock(Clock clock) throws RemoteException {
		this.requestLockClock = clock;
	}
	
	AdderRemote(String name_cur) throws RemoteException{  
		super();
		name = name_cur;
		String name = "myProg";
		for(int i=1;i<=2;i++){
			String node_name = name + "-" + i;
			this.myClock.UpdateClock(node_name, 1);
		}
	}
	
	public int UnlockRequest(String sender, Clock clock) throws RemoteException{
		QueueElement top = queue.peek();
		if(top.getName().equals(sender)){
			queue.poll();
			System.out.println("Removed " + sender + "from the queue of " + name);
			return 0;
		}
		else{
			System.out.println("Top not the node which sent the request!");
			while(top != null && clock.compareTo(top.getClock()) == 1 && !top.getName().equals(name)){
				queue.poll();
				top = queue.peek();
			}
			return 0;
		}
	}
	
	public int SendAckRequest(String sender) throws RemoteException{
		System.out.println("Ack received from " + sender + " to " + name);
		acks.put(sender, "1");
		return 0;
	}
	
	public int LockRequest(String sender, Clock clock) throws RemoteException, MalformedURLException, NotBoundException{
		System.out.println("LockRequest received from " + sender + " to " + name);
		//myClock = Math.max(myClock,clock) + 1;
		if(requestLock == 0){
			Adder receiver_node;
			receiver_node = (Adder)Naming.lookup("rmi://localhost:5000/" + sender);
			receiver_node.SendAckRequest(name);
		}
		else if(hasLock != 0){
			QueueElement elem = new QueueElement(sender,clock);
			System.out.println("Added to queue");
			queue.add(elem);
		}
		else if(requestLock != 0){ // requestLock stores the time
			if(requestLockClock.compareTo(clock) == 1){
				Adder receiver_node;
				receiver_node = (Adder)Naming.lookup("rmi://localhost:5000/" + sender);
				receiver_node.SendAckRequest(name);
			}
			else{
				QueueElement elem = new QueueElement(sender,clock);
				queue.add(elem);
				System.out.println("Added to queue");
				return 0;
			}
		}
		return 0;
	}

} 