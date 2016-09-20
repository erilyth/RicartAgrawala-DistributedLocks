import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.rmi.*;
import java.util.HashMap;

import com.google.protobuf.CodedInputStream;

import com.example.result.ResultProto.Clock;
import com.example.result.ResultProto.ClockMember;
import com.example.result.ResultProto.Queue;
import com.example.result.ResultProto.QueueElement;

public class MyClient{  
	
	public static int total_clients = 2;
	public static String my_name;
	
	public static int unlock(Adder node) throws RemoteException, MalformedURLException, NotBoundException{
		if(node.topqueue().getName() != my_name){
			System.out.println("Error, you are not on top of the queue!");
		}
		else{
			node.setHasLock(0);
			node.setRequestLock(0);
			node.removequeue();
			System.out.println("Removed myself from the top of my queue");
			while(node.topqueue() != null){
				String node_name = node.topqueue().getName();
				if(!node_name.equals(my_name)){
					Adder receiver_node;
					receiver_node = (Adder)Naming.lookup("rmi://localhost:5000/" + node_name);
					System.out.println("Will send UnlockRequest to " + node_name + " from " + my_name);
					receiver_node.UnlockRequest(node.getName(),node.getMyClock());
					receiver_node.SendAckRequest(node.getName());
					System.out.println("Sent and got back from UnlockRequest to " + node_name + " from " + my_name);
				}
				node.removequeue();
			}
		}
		return 1;
	}
	
	public static int lock(Adder node) throws RemoteException, MalformedURLException, NotBoundException{
		Clock cur_time = node.getMyClock();
		HashMap<String, Integer> cur_clock = cur_time.getClock();
		cur_clock.put(my_name, cur_clock.get(my_name) + 1);
		cur_time.setClock(cur_clock);
		node.setMyClock(cur_time);
		node.setRequestLock(1);
		node.setRequestLockClock(node.getMyClock());
		node.addqueue(new QueueElement(my_name,cur_time));
		String name = "myProg";
		for(int i=1;i<=total_clients;i++){
			String node_name = name + "-" + i;
			System.out.println("Try to find client " + node_name);
			if(!node_name.equals(my_name)){
				Adder receiver_node;
				receiver_node = (Adder)Naming.lookup("rmi://localhost:5000/" + node_name);
				receiver_node.LockRequest(node.getName(),cur_time);
				System.out.println("LockRequest has returned from " + node_name + " to " + my_name);
			}
		}
		System.out.println("Identified all clients and sent lock requests");
		int cur_acks = 0;
		while(cur_acks!=total_clients-1 || !node.topqueue().getName().equals(my_name)){
			System.out.println("Waiting for acks and being top of queue " + my_name);
			System.out.println("Top of the queue is " + node.topqueue());
			cur_acks = 0;
			for(int i1=1;i1<=total_clients;i1++){
				String node_name1 = name + "-" + i1;
				System.out.println("Check ack of " + node_name1);
				if(!node_name1.equals(my_name)){
					if(node.getack(node_name1) == 1){
						cur_acks += 1;
						System.out.println("Have ack" + node_name1);
					}
				}
			}
			System.out.println(cur_acks);
		}
		node.setHasLock(1);
		return 1;
	}
	
	public static void main(String args[]){  
		try{
			Adder stub= new AdderRemote("myProg-" + args[0].toString()); 
			my_name = "myProg-" + args[0].toString();
			String filename = "output.txt";
			Naming.rebind("rmi://localhost:5000/myProg-" + args[0].toString(),stub);
			Thread.sleep(5000); // Wait for 5 seconds
			int i = 0;
			System.out.println("Hello!");
			while(i<2){
				System.out.println("Please give me the lock! " + my_name);
				Clock lock_sent_timer = stub.getMyClock();
				System.out.println("Got clock");
				lock(stub);
				System.out.println("YAY I HAVE THE LOCK! " + my_name);
				File f = new File(filename);
				if(!f.exists()){
				    f.createNewFile();
				    PrintWriter outFile = new PrintWriter(new FileWriter(f));
				    outFile.print("1" + ":" + args[0].toString() + ":" + lock_sent_timer + "\n");
				    outFile.close();
				}
				else{
					BufferedReader br = new BufferedReader(new FileReader(f));
					String line = null;
					String last_line = null;
					while((line = br.readLine()) != null){
						if(line != null){
							last_line = line;
						}
					}
					br.close();
					String[] parts = last_line.split(":");
					int new_counter = Integer.parseInt(parts[0]) + 1;
					PrintWriter outFile = new PrintWriter(new FileWriter(f,true));
					outFile.print(new_counter + ":" + args[0].toString() + ":" + lock_sent_timer + "\n");
					outFile.close();
				}
				unlock(stub);
				System.out.println("I NO LONGER NEED THE LOCK! " + my_name);
				int randomNum = 1 + (int)(Math.random() * 2 * total_clients); 
				Thread.sleep(randomNum * 1000);
				i++;
			}
			System.out.println("Processing has been finished on " + my_name);
			System.out.println("Press Ctrl+C to exit");
		}catch(Exception e){
			
		}
	}
	
}  