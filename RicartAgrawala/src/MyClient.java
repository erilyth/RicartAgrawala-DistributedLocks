import java.rmi.*;

public class MyClient{  
	
	public int total_clients = 5;
	public String my_name;
	
	public int unlock(Adder node) throws RemoteException{
		if(node.topqueue().getName() != my_name){
			System.out.println("Error, you are not on top of the queue!");
		}
		else{
			String name = "myProg";
			for(int i=1;i<=total_clients;i++){
				String node_name = name + i;
				if(node_name != my_name){
					node.UnlockRequest(node_name, node.getMyClock());		
				}
			}
			for(int i=1;i<=total_clients;i++){
				String node_name = name + i;
				if(node_name != my_name){
					node.SendAckRequest(node_name);		
				}
			}
		}
		return 1;
	}
	
	public int lock(Adder node) throws RemoteException{
		int cur_time = node.getMyClock();
		cur_time += 1;
		node.setMyClock(cur_time);
		node.addqueue(new QueueElement(my_name,cur_time));
		String name = "myProg";
		for(int i=1;i<=total_clients;i++){
			String node_name = name + i;
			if(node_name != my_name){
				node.LockRequest(node_name);		
			}
		}
		int cur_acks = 0;
		while(cur_acks!=total_clients-1 || node.topqueue().getName()!=my_name){
			cur_acks = 0;
			for(int i=1;i<=total_clients;i++){
				String node_name = name + i;
				if(node_name != my_name && node.getack(node_name) == 1){
					cur_acks += 1;
				}
			}
		}
		return 1;
	}
	
	public void main(String args[]){  
		try{
			Adder stub=new AdderRemote("myProg-" + args[0].toString()); 
			my_name = "myProg-" + args[0].toString();
			Naming.rebind("rmi://localhost:5000/myProg-" + args[0].toString(),stub);
			Thread.sleep(5000); // Wait for 5 seconds
			lock(stub);
			System.out.println("YAY I HAVE THE LOCK!" + my_name);
			Thread.sleep(1000);
			unlock(stub);
		}catch(Exception e){
			
		}
	}
	
}  