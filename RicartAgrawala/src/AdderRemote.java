import java.net.MalformedURLException;
import java.rmi.*;  
import java.rmi.server.*;
import java.util.*;

import java.util.PriorityQueue;

public class AdderRemote extends UnicastRemoteObject implements Adder{  
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public PriorityQueue<QueueElement> queue = new PriorityQueue<QueueElement>();
	public HashMap<String, String> acks = new HashMap<String, String>();
	public int hasLock = 0;
	public int requestLock = 0; // This would have the sent request timestamp
	public String name;
	public int myClock = 1;
	
	public PriorityQueue<QueueElement> getQueue() throws RemoteException{
		return queue;
	}

	public void setQueue(PriorityQueue<QueueElement> queue) throws RemoteException {
		this.queue = queue;
	}

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

	public int getMyClock() throws RemoteException {
		return myClock;
	}

	public void setMyClock(int myClock) throws RemoteException {
		this.myClock = myClock;
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
	
	AdderRemote(String name_cur) throws RemoteException{  
		super();
		name = name_cur;
	}
	
	public int UnlockRequest(String sender, int clock) throws RemoteException{
		QueueElement top = queue.peek();
		if(top.getName().equals(sender)){
			queue.poll();
			System.out.println("Removed " + sender + "from the queue of " + name);
			return 0;
		}
		else{
			System.out.println("Top not the node which sent the request!");
			while(top != null && top.getClock() <= clock && !top.getName().equals(name)){
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
	
	public int LockRequest(String sender, int clock) throws RemoteException, MalformedURLException, NotBoundException{
		System.out.println("LockRequest received from " + sender + " to " + name);
		System.out.println(name + " requestLock = " + requestLock);
		myClock = Math.max(myClock,clock) + 1;
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
			if(clock<requestLock){
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