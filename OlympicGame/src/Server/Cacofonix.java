package Server;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import Utilities.IConnection;
import Utilities.Names;
import Utilities.Team;


public class Cacofonix {
	static IConnection connection;
	
	public Cacofonix(String address){
		try {
			connection = (IConnection) Naming.lookup(address);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Update process successfully connected to server");
	}
	
	public static class setScore implements Runnable{
		int updateRate;
		public setScore(int rate){
			updateRate = rate;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int i = 0;
			while(true){
				
				try {
					connection.setScore(Names.teamNames[i%6],Names.events[i%6], i);
					Thread.sleep(updateRate);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
		}
		
	}
	
	public static class setTally implements Runnable{
		int rate;
		public setTally(int rate){
			this.rate = rate;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int j = 0;
			while(true){
				try {
					connection.incrementMetalTally(Names.teamNames[j%6], Names.metals[j%3]);
					Thread.sleep(rate);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				j++;
				
			}
		}
		
	}
	public static void main(String[] args){
		if(args.length < 2) {
			System.out.print("args: update rate for score, update rate for tally(ms), (option) server address");
			return;
		} if(args.length == 2){
			Cacofonix c = new Cacofonix("rmi://localhost:10001/Olympic");
		} if(args.length == 3){
			Cacofonix c = new Cacofonix("rmi://"+args[2]+":10001/Olympic");
		}
			
		setScore ss = new setScore(Integer.parseInt(args[0]));
		setTally st = new setTally(Integer.parseInt(args[1]));
		Thread t1 = new Thread(ss);
		Thread t2 = new Thread(st);
		t1.start();
		t2.start();
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		t1.stop();
//		t2.stop();
	}
}
