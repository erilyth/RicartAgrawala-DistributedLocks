import java.net.MalformedURLException;
import java.rmi.*; 
import java.util.PriorityQueue;

public interface Adder extends Remote{ 
	public int LockRequest(String receiver, int clock)
			throws RemoteException, MalformedURLException, NotBoundException;
	public int SendAckRequest(String receiver)
			throws RemoteException;
	public int UnlockRequest(String receiver, int clock)
			throws RemoteException;
	public PriorityQueue<QueueElement> getQueue() throws RemoteException;
	public void setQueue(PriorityQueue<QueueElement> queue) throws RemoteException;
	public int getHasLock() throws RemoteException;
	public void setHasLock(int hasLock) throws RemoteException;
	public int getRequestLock() throws RemoteException;
	public void setRequestLock(int requestLock) throws RemoteException;
	public String getName() throws RemoteException;
	public void setName(String name) throws RemoteException;
	public void addqueue(QueueElement elem) throws RemoteException;
	public QueueElement removequeue() throws RemoteException;
	public int getack(String name) throws RemoteException;
	public void setack(String name) throws RemoteException;
	public int getMyClock() throws RemoteException;
	public void setMyClock(int clock) throws RemoteException;
	public QueueElement topqueue() throws RemoteException;
}