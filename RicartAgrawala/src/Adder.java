import java.rmi.*; 

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
	public void ReceiveUnlockRequest(String receiver)
			throws RemoteException;
}