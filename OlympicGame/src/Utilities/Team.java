package Utilities;

import java.util.HashMap;

public class Team {
	
	//name of the eam 
	public String teamName;
	public int gold = 0;
	public int silver = 0;
	public int bronze = 0;
	public long timestamp = 0;
	
	public HashMap<String,Integer> scores = new HashMap<String,Integer>();
	
	
	public Team(String teamName){
		this.teamName = teamName;
	}
	
	public synchronized void incrementMetalTally(String metalType){
		if (metalType.equals("gold"))
			gold++;
		if (metalType.equals("silver"))
			silver++;
		if (metalType.equals("bronze"))
			bronze++;
	}

	public synchronized void setScore(String event, int score){
		scores.put(event, score);
	}
	
	public synchronized int getScore(String event){
		if(scores.get(event) == null)
			return 0;
		return scores.get(event);
	}
}
