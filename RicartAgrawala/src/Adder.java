import java.rmi.*; 
import java.util.HashMap;
import java.util.PriorityQueue;

public interface Adder extends Remote{ 
	public int add(int x,int y)
			throws RemoteException;
	public int ReceiveLockRequest(String sender, int clock)
			throws RemoteException;
	public int LockRequest(String receiver)
			throws RemoteException;
	public int SendAckRequest(String receiver)
			throws RemoteException;
	public void ReceiveAckRequest(String sender)
			throws RemoteException;
	public int UnlockRequest(String receiver, int clock)
			throws RemoteException;
	public void ReceiveUnlockRequest(String receiver, int clock)
			throws RemoteException;
	public PriorityQueue<QueueElement> getQueue();
	public void setQueue(PriorityQueue<QueueElement> queue);
	public HashMap getAcks();
	public void setAcks(HashMap acks);
	public int getHasLock();
	public void setHasLock(int hasLock);
	public int getRequestLock();
	public void setRequestLock(int requestLock);
	public String getName();
	public void setName(String name);
	public void addqueue(QueueElement elem);
	public QueueElement removequeue();
	public int getack(String name);
	public void setack(String name);
	public int getMyClock();
	public void setMyClock(int clock);
	public QueueElement topqueue();
}