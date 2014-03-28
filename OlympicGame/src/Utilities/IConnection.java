package Utilities;

import java.rmi.*;


public interface IConnection extends Remote {
	/**
	 * 
	 * @param eventType: event Name
	 * @param teamName: team Name
	 * @param score: score value
	 * @param metalType: metal Type including: gold, silver and bronze
	 * @throws RemoteException
	 * this interface is used to define the interface to communicate with server
	 * setScore and incrementMetalTally are two methods between server and Cacofonix update process
	 * getScore and getMedalTally are two methods between server and clients
	 */
	String getScore(String eventType) throws RemoteException;
	void setScore(String teamName, String eventType, int score) throws RemoteException;
	String getMedalTally(String teamName) throws RemoteException;
	void incrementMetalTally(String teamName, String metalType) throws RemoteException;
}
