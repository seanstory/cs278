package edu.vanderbilt.cs278.Asgn5;

import java.io.*;
import java.net.*;

public class RDTClient {

	/**
	 * @param args
	 * 
	 */
	public static final String FILENAME = "bigfile.txt";
	public static final int MAXBUFSIZE = 10000;

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("usage:  <server_IP> <server_port>");
			System.exit(1);
		}

		InetAddress serverIP = InetAddress.getByName(args[0]);
		int serverPort = Integer.parseInt(args[1]);
			
        RDTSender sender = new RDTSender(serverIP, serverPort);		
        
        File sendFile = new File(FILENAME);
        if (!sendFile.exists()) {
            System.out.println(FILENAME + " missing");
            System.exit(1);
        }
        
        long fileLength = sendFile.length();
        
        sender.sendFileLength(fileLength);
                
        int totalSentBytes = 0;
        int readBytes;
        byte[] buf = new byte[MAXBUFSIZE];
        FileInputStream fileIn = new FileInputStream(sendFile);
        
        while(totalSentBytes < fileLength) {
            readBytes = fileIn.read(buf, 0, MAXBUFSIZE);
            //System.out.println(new String(buf));
            sender.sendData(buf, readBytes);
            totalSentBytes += readBytes;
        }
       // System.out.println(totalSentBytes);
        fileIn.close();

	}

}
