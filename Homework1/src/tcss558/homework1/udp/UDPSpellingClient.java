package tcss558.homework1.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import tcss558.homework1.Log;

public class UDPSpellingClient {
	public static void main(String[] args){
		if(args.length < 3){
			printHelp();
			return;
		}
		
		InetAddress host;

		try{
			host = InetAddress.getByName(args[0]);
		}
		catch(UnknownHostException e){
			Log.err(String.format("Host '%s' is unknown", e.getMessage()));
			return;
		}
		
		InetSocketAddress socketAddress;
		try{
			socketAddress = new InetSocketAddress(host, Integer.parseInt(args[1]));
		}
		catch(NumberFormatException nfe){
			Log.err("Port number must be an integer");
			return;
		}
		catch(IllegalArgumentException iae){
			Log.err(String.format("Port number '%s' is not valid", args[1]));
			return;
		}
		
		Log.out("InetSocketAddress was succesfully created");
				
		for(int i=2; i<args.length;i++){
			queryWord(args[i]);
		}
		
		Log.out("end of program");
	}
	
	private static void printHelp(){
		System.out.println("about text");
	}
	
	private static void queryWord(String word){
		
	}
}
