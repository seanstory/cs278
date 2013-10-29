package edu.vanderbilt.cs278.Asgn5;

import java.io.*;
import java.net.*;
import java.util.*;

public class RDTServer {
	
	public static final int MAXBUFSIZE = 10000; //dictates how large the packets can be
	public static final int MAXFILENAMELENGTH = 30; //how many characters can go in a file name

	public static void main(String[] args) throws Exception {
        if (args.length != 2) {
			System.err.println("usage:  <server_IP_prefix> <server_port>");
			System.exit(1);
		}
		
        String input = "continue";
        Scanner scanner = new Scanner(System.in);
        
        
        while ( ! (input.equals("q") || input.equals("Q") ) ){
        
        	//find the matching system Inet Address
    		String serverIPPrefix = args[0]; 
    		int serverPort = Integer.parseInt(args[1]);
    		InetAddress serverIP = getIPwithPrefix(serverIPPrefix);
    		if (serverIP == null){
    			System.err.println("No matching IP");
    			System.exit(1);
    		}
    		File runningFile = new File(".server_"+serverPort+"_running");
    		runningFile.createNewFile();
    		
    			
    		//Attach a Receiver at the specified port number
    		RDTReceiver recver = new RDTReceiver(serverIP, serverPort);
			System.out.println("Waiting for a client...");
			int fileLength = recver.getFileLength(); 
			String fileName = recver.getFileName();
			System.out.println("Client Found: IP Address: "+recver.getClientIP());
			
			//create the file if it doesn't exist yet
			File recvFile = new File("received_"+fileName);
			if (!recvFile.exists()) {
				recvFile.createNewFile();
		        System.out.println("received_"+fileName + " is created!");
			}
			
			//initialize variables for writing the file
			int totalRecvBytes = 0;
			int recvBytes = 0;
			byte [] buf = new byte[MAXBUFSIZE];
			FileOutputStream fileOut = new FileOutputStream(recvFile);
			
			//receive data from the RDTReceiver and write it, outputting progress
			String outputMessage = "";
			while(totalRecvBytes < fileLength) {
				recvBytes = recver.recvData(buf, MAXBUFSIZE);
				if (recvBytes != -1){
					//debug
					//System.out.println("received "+recver.getAckNo());
					totalRecvBytes += recvBytes;
					fileOut.write(buf, 0, recvBytes);
		            double percent = (double) totalRecvBytes / fileLength * 100;
		            deleteOldOutput(outputMessage.length());
		            outputMessage = "Received " + percent + "% of file.";
		            System.out.print(outputMessage);
				}
	 	    }
			fileOut.close();//make sure to close the file.
			recver.close();
			runningFile.delete();
			
			System.out.println("\nEnter 'Q' to quit, anthing else to continue");
			input = scanner.nextLine();
        }
        scanner.close();
        
	}


	private static InetAddress getIPwithPrefix (String prefix) throws Exception{
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        
        for (NetworkInterface netIf : Collections.list(nets)) {
            System.out.println("Interface: " + netIf.getDisplayName() + " " + netIf.getName());

        	Enumeration<InetAddress> netIPs= netIf.getInetAddresses();
        	for (InetAddress IP : Collections.list(netIPs)){
        		String IPStr = IP.getHostAddress();
                System.out.println("IP: " + IPStr);
        		if (IPStr.startsWith(prefix))
        			return IP;
        	}
        }
        return null;
	}
	
	private static void deleteOldOutput(int length){
		while (length >0){
			System.out.print("\b");
			length --;
		}
	}
	
	public static boolean isRunning(int portnum){
		return new File(".server_"+portnum+"_running").exists();
	}

}
