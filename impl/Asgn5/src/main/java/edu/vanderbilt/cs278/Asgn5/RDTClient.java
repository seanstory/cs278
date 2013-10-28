package edu.vanderbilt.cs278.Asgn5;

import java.io.*;
import java.net.*;

public class RDTClient {

	//public static final String FILENAME = "bigfile.txt";
	public static final int MAXBUFSIZE = RDTServer.MAXBUFSIZE;//how large the packets can be

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("usage:  <server_IP> <server_port> <file_to_send>");
			System.exit(1);
		}

		InetAddress serverIP = InetAddress.getByName(args[0]);
		int serverPort = Integer.parseInt(args[1]);
		String fileName = args[2];
			
       		
        
        File sendFile = new File(fileName);
        if (!sendFile.exists()) {
            System.out.println(fileName + " missing");
            System.exit(1);
        }
        
        RDTSender sender = new RDTSender(serverIP, serverPort);
        
        long fileLength = sendFile.length();
        
        DatagramPacket metricsData = sender.getMetricsDatagram(fileLength, fileName);
        DatagramSocket sendSock = sender.getSendSocket();
        sendSock.send(metricsData);
                
        int totalSentBytes = 0;
        int readBytes;
        byte[] buf = new byte[MAXBUFSIZE+2];
        FileInputStream fileIn = new FileInputStream(sendFile);
        
        while(totalSentBytes < fileLength) {
            readBytes = fileIn.read(buf, 0, MAXBUFSIZE);
            //System.out.println(new String(buf));
            while ( ! sender.sendData(buf, readBytes)){
            	//try until send is successful
            }
            totalSentBytes += readBytes;
        }
       // System.out.println(totalSentBytes);
        fileIn.close();

	}

}
