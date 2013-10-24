package edu.vanderbilt.cs278.Asgn5;

import java.io.*;
import java.net.*;
import java.util.*;

public class RDTServer {
	
	public static final String FILENAME = "bigfile_RDTreceived.txt";
	public static final int MAXBUFSIZE = 10000;

	public static void main(String[] args) throws Exception {
        if (args.length != 2) {
			System.err.println("usage:  <server_IP_prefix> <server_port>");
			System.exit(1);
		}
		
		String serverIPPrefix = args[0];
		int serverPort = Integer.parseInt(args[1]);
		InetAddress serverIP = getIPwithPrefix(serverIPPrefix);
		if (serverIP == null){
			System.err.println("No matching IP");
			System.exit(1);
		}
			
		
		RDTReceiver recver = new RDTReceiver(serverIP, serverPort);
		
		File recvFile = new File(FILENAME);
		if (!recvFile.exists()) {
			recvFile.createNewFile();
	        System.out.println(FILENAME + " is created!");
		}
		
		int fileLength = recver.getFileLength();
				
		int totalRecvBytes = 0;
		int recvBytes = 0;
		byte [] buf = new byte[MAXBUFSIZE];
		FileOutputStream fileOut = new FileOutputStream(recvFile);
		
		while(totalRecvBytes < fileLength) {
			recvBytes = recver.recvData(buf, MAXBUFSIZE);
			totalRecvBytes += recvBytes;
			fileOut.write(buf, 0, recvBytes);
            double per = (double) totalRecvBytes / fileLength * 100;
            System.out.println("Received " + per + "% of file.");
 	    }
		fileOut.close();
        
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

}
