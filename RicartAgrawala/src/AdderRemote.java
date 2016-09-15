import java.net.MalformedURLException;
import java.rmi.*;  
import java.rmi.server.*;
import java.util.*;

import java.util.PriorityQueue;

public class AdderRemote extends UnicastRemoteObject implements Adder{  
	
	public PriorityQueue<QueueElement> queue = new PriorityQueue<QueueElement>();
	public HashMap acks;
	public int hasLock = 0;
	public int requestLock = 0; // This would have the sent request timestamp
	public String name;
	public int myClock = 1;
	
	public PriorityQueue<QueueElement> getQueue() {
		return queue;
	}

	public void setQueue(PriorityQueue<QueueElement> queue) {
		this.queue = queue;
	}

	public HashMap getAcks() {
		return acks;
	}

	public void setAcks(HashMap acks) {
		this.acks = acks;
	}

	public int getHasLock() {
		return hasLock;
	}

	public void setHasLock(int hasLock) {
		this.hasLock = hasLock;
	}

	public int getRequestLock() {
		return requestLock;
	}

	public void setRequestLock(int requestLock) {
		this.requestLock = requestLock;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMyClock() {
		return myClock;
	}

	public void setMyClock(int myClock) {
		this.myClock = myClock;
	}
	
	public void addqueue(QueueElement elem){
		queue.add(elem);
	}
	
	public QueueElement removequeue(){
		QueueElement top = queue.peek();
		queue.poll();
		return top;
	}
	
	public QueueElement topqueue(){
		QueueElement top = queue.peek();
		return top;
	}
	
	public int getack(String name){
		if(acks.containsKey(name)){
			return 1;
		}
		return 0;
	}
	
	public void setack(String name){
		acks.put(name, 1);
	}
	
	AdderRemote(String name_cur)throws RemoteException{  
		super();
		name = name_cur;
	}  
	
	public int add(int x,int y){
		return x+y;
	}
	
	public void ReceiveUnlockRequest(String sender, int clock) throws RemoteException{
		QueueElement top = queue.peek();
		if(top.getName() == sender){
			queue.poll();
		}
		else{
			System.out.println("Top not the node which sent the request!");
			while(top.getClock() <= clock){
				queue.poll();
			}
		}
	}
	
	public int UnlockRequest(String receiver, int clock) throws RemoteException{
		try {
			Adder receiver_node=(Adder)Naming.lookup("rmi://localhost:5000/" + receiver);
			receiver_node.ReceiveUnlockRequest(receiver, clock);
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