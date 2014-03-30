package Server;


import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import Server.Cacofonix.setScore;
import Server.Cacofonix.setTally;
import Utilities.IConnection;
import Utilities.Team;

public class Server extends UnicastRemoteObject implements IConnection{
	/**
	 * This is Server class, receives the request from clients and communicate with database
	 * id is the id of the server
	 * count is used to collect request frequency for this server
	 * timeOffset is the difference between the average time of the three server processes and system time
	 * leader is used to record which server is the leader after election 
	 */
	int id;
	int count;
	long timeOffset;
	IConnection db;
	static IConnection server2;
	public int leader;
	
	public class Clock implements Runnable{
		@Override
		public void run() {
			System.out.println("start clock");
			while(true){
				try {
					long tmp = System.nanoTime();
					long tmp1 = db.clock();
					long tmp2 = server2.clock();
					
					//average time of the three machines
					long avg = (tmp+tmp1+tmp2)/3;
					System.out.println("avg "+ avg);
					
					//set time offset
					server2.setTimeoff(avg - tmp2);
					db.setTimeoff(avg - tmp1);
					setTimeoff(avg - tmp);
					
					//print out request frequency every 5 seconds
					System.out.println("request frequency "+count/5);
					count = 0;
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	
	}
	
	public Server(int id, String dbAddress) throws RemoteException, MalformedURLException, NotBoundException {
		super();
		this.id = id;
		db = (IConnection) Naming.lookup(dbAddress);
		System.out.println("Cnnected to db");
		
	}

	//increment the metal records for a specific team
	public synchronized void incrementMetalTally(String teamName, String metalType) throws RemoteException{
		db.incrementMetalTally(teamName, metalType);
		count++;
	}

	//get Metal tally for a specific team
	public synchronized String getMedalTally(String teamName) throws RemoteException {
		count++;
		return db.getMedalTally(teamName);
		
	}
	
	//get score of a specific event
	public synchronized String getScore(String eventType) throws RemoteException {
		count++;
		return db.getScore(eventType);
		
	}
	
	//set scores for a team in a specific event
	public synchronized void setScore(String teamName, String eventType, int score) throws RemoteException{
		db.setScore(teamName, eventType, score);
		count++;
	}
	
	public int getId() throws RemoteException{
		return id;
	}


	@Override
	public long clock() throws RemoteException {
		return System.nanoTime();
	}
	
	@Override
	public void setleader(int id) throws RemoteException {
		leader = id;
	}
	
	/**
	 * 
	 * @param args
	 * args[0] the id of this server
	 * args[1] the ip address of another server 
	 * args[2] the ip address of database
	 */
	public static void main(String[] args){
		if(args.length != 3){
			System.out.println("args: id of this server, ip address of another server, ip address of database");
			return;
		}
		Server server = null;
		try {
			server = new Server(Integer.parseInt(args[0]), "rmi://"+args[2]+":10001/db");
			//register server to port 10001
			LocateRegistry.createRegistry(10001);
			//bind server object to a rmi address
			Naming.bind("rmi://localhost:10001/server", server);		
			System.out.println("start server " + server.id);
			Thread.sleep(5000);
			server2 = (IConnection) Naming.lookup("rmi://"+args[1]+":10001/server");
			server.leaderElection();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void leaderElection() throws RemoteException {
		System.out.println("start leader election");
		if (this.id > server2.getId()){
			leader = this.id;
			System.out.println("leader is "+ leader);
			server2.setleader(leader); 
			db.setleader(leader);
		}
		
		if(this.id == leader){
			Clock clockthread = new Clock();
			Thread t = new Thread(clockthread);
			t.start();
		}
	}

	@Override
	public void setTimeoff(long timeoff) throws RemoteException {
		this.timeOffset = timeoff;
		System.out.println(id +" timeoff is "+timeoff);
	}
}
