package Server;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import Utilities.IConnection;
import Utilities.Team;
/**
 * 
 * @author bym
 * Database stores the scores and tallies of all the teams.
 * It communicates with servers for leader election and clock synchronization.
 * This database stores data in hashmap in memory and periodically flush data into file 
 */
public class Database extends UnicastRemoteObject implements IConnection{
	
	int id;
	int leader;
	long timeOffset;
	HashMap<String, Team> tally = new HashMap<String, Team>();
	
	
	public Database() throws RemoteException{
		super();
		id = 0;
	}

	
	public static void main(String[] args){
		Database db;
		try {
			db = new Database();
			//register server to port 10001
			LocateRegistry.createRegistry(10001);
			//bind server object to a rmi address
			Naming.bind("rmi://localhost:10001/db", db);		
			System.out.println("start database " + db.id);
			} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public synchronized String getScore(String eventType) throws RemoteException {
		String s = eventType + " scores : \n";
		for(Team t:tally.values()){
			s += t.teamName+ ":"+ t.getScore(eventType) + " with timestamp "+ t.timestamp+"\n";
		}
		return s;
	}

	@Override
	public synchronized void setScore(String teamName, String eventType, int score)
			throws RemoteException {
		Team t = tally.get(teamName);
		if(t == null){
			t = new Team(teamName);
		}
		t.timestamp = System.nanoTime() + timeOffset;
		t.setScore(eventType, score);
		tally.put(teamName, t);
		System.out.println("setScore " + teamName + " " + eventType + " " + score +" with timestamp "+ t.timestamp);
	}

	@Override
	public synchronized String getMedalTally(String teamName) throws RemoteException {
		Team t = tally.get(teamName);
		if(t == null){
			return "No such team";
		}
		return "Team " + teamName + ": gold " + t.gold + " silver " + t.silver + " bronze " + t.bronze
				+ " with timestamp "+t.timestamp; 
	
	}

	@Override
	public void incrementMetalTally(String teamName, String metalType)
			throws RemoteException {
		Team t = tally.get(teamName);
		//if this team is not exist currently, build a new record
		if(t == null){
			t = new Team(teamName);
		}
		t.timestamp = System.nanoTime() + timeOffset;
		t.incrementMetalTally(metalType);
		tally.put(teamName, t);
		System.out.println("increment Metal for " + teamName + " a metal of " + metalType 
				+" with timestamp " + t.timestamp);
		
		//print tally for all teams
		System.out.println("--------------------------------------");
		for(Team t1: tally.values()){
			System.out.println(t1.teamName + " gold " + t1.gold + " silver " + t1.silver + " bronze " + t1.bronze 
					+ " with timestamp " + t1.timestamp);
		}
		System.out.println("--------------------------------------");
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
		System.out.println("leader is "+leader);
	}

	@Override
	public void setTimeoff(long timeoff) throws RemoteException {
		this.timeOffset = timeoff;
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println(id +" timeoff is "+timeoff);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
	}
}
