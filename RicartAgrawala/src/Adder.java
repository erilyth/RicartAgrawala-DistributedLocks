import java.net.MalformedURLException;
import java.rmi.*; 
import java.util.PriorityQueue;

import com.google.protobuf.CodedInputStream;

import com.example.result.ResultProto.Clock;
import com.example.result.ResultProto.ClockMember;
import com.example.result.ResultProto.Queue;
import com.example.result.ResultProto.QueueElement;

public interface Adder extends Remote{ 
	public int LockRequest(String receiver, Clock clock)
			throws RemoteException, MalformedURLException, NotBoundException;
	public int SendAckRequest(String receiver)
			throws RemoteException;
	public int UnlockRequest(String receiver, Clock clock)
			throws RemoteException;
	public void setRequestLockClock(Clock clock) throws RemoteException;
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
	public Clock getMyClock() throws RemoteException;
	public void setMyClock(Clock clock) throws RemoteException;
	public QueueElement topqueue() throws RemoteException;
}