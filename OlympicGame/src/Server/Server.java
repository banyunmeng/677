package Server;


import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
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
	 * This is Server class, it implements all the methods in IConnection interface
	 * and these methods are executed synchronously
	 * Server has a HashMap<String, Team> tally structure to store the tallies and scores
	 * of teams in ongoing events
	 */

	HashMap<String, Team> tally = new HashMap<String, Team>();
	
	public Server() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args){
		Server con;
		try {
			con = new Server();
			//register server to port 10001
			LocateRegistry.createRegistry(10001);
			//bind server object to a rmi address
			Naming.bind("rmi://localhost:10001/Olympic", con);		
			System.out.println("start server");
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

	//increment the metal records for a specific team
	public synchronized void incrementMetalTally(String teamName, String metalType){
		Team t = tally.get(teamName);
		//if this team is not exist currently, build a new record
		if(t == null){
			t = new Team(teamName);
		}
		t.incrementMetalTally(metalType);
		tally.put(teamName, t);
		System.out.println("increment Metal for " + teamName + " a metal of " + metalType);
		
		//print tally for all teams
		String s = "";
		System.out.println("--------------------------------------");
		for(Team t1: tally.values()){
			System.out.println(t1.teamName + " gold " + t1.gold + " silver " + t1.silver + " bronze " + t1.bronze);
		}
		System.out.println("--------------------------------------");
	}

	//get Metal tally for a specific team
	public synchronized String getMedalTally(String teamName) throws RemoteException {
		// TODO Auto-generated method stub
		Team t = tally.get(teamName);
		if(t == null){
			return "No such team";
		}
		return "Team " + teamName + ": gold " + t.gold + " silver " + t.silver + " bronze " + t.bronze; 
	}
	
	//get score of a specific event
	public synchronized String getScore(String eventType) throws RemoteException {
		// TODO Auto-generated method stub
		String s = eventType + " scores: ";
		for(Team t:tally.values()){
			s += t.teamName+ ":"+ t.getScore(eventType) + " ";
		}
		return s;
	}
	
	//set scores for a team in a specific event
	public synchronized void setScore(String teamName, String eventType, int score){
		Team t = tally.get(teamName);
		if(t == null){
			t = new Team(teamName);
		}
		t.setScore(eventType, score);
		tally.put(teamName, t);
		System.out.println("setScore " + teamName + " " + eventType + " " + score);
	}
	
}
