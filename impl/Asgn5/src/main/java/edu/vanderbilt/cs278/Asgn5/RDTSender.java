package edu.vanderbilt.cs278.Asgn5;

import java.net.InetAddress;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class RDTSender {

    private DatagramSocket sendSocket;
    private DatagramPacket sendPacket;
    private InetAddress serverIP;
    private int serverPort;
    private short packetno;
    private String fileName_;
    
	public RDTSender(InetAddress serverIP, int serverPort, String fileName) throws IOException {
        sendSocket = new DatagramSocket();
        sendSocket.setSoTimeout(10);
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        packetno = 0;
        fileName_ = fileName;
	}

	public void sendData(byte[] buf, int length) throws IOException {
        makePacket(buf, length);
        
        boolean recvd = false;
        byte[] ackbuf = new byte[2];
        DatagramPacket ack = new DatagramPacket(ackbuf, ackbuf.length);
        
        //System.out.println("Packet says: " + new String(buf));
        
        while (!recvd) {
            sendSocket.send(sendPacket);
            
            try {
                sendSocket.receive(ack);
                short ackno = ByteBuffer.wrap(ackbuf).getShort();
                if (ackno == packetno) {
                    recvd = true;
                }
                
            } catch (Exception e) {
                sendSocket.send(sendPacket);
            }
        }
        ++packetno;
        
        //System.out.println(new String(buf));
	}
    
    public void sendFileLength(long fileLength) throws IOException {
    	//initialize arrays for file length, file name length, and file name
    	byte[] fileLen = ByteBuffer.allocate(8).putLong(fileLength).array();
    	byte[] fileNameLen = ByteBuffer.allocate(8).putLong(fileName_.length()).array();
    	byte[] fName = fileName_.getBytes();
    	byte[] bytes = new byte[8+8+RDTServer.MAXFILENAMELENGTH];
    	
    	//copy all the above values into one array
    	System.arraycopy(fileLen, 0, bytes, 0, fileLen.length);
    	System.arraycopy(fileNameLen, 0, bytes, fileLen.length, fileNameLen.length);
    	System.arraycopy(fName, 0, bytes, fileLen.length+fileNameLen.length, fName.length);
    	
    	//send the info!
        DatagramPacket lengthPacket = new DatagramPacket(bytes ,bytes.length , serverIP, serverPort);
        sendSocket.send(lengthPacket);
	}
    
    private void makePacket(byte[] buf, int length) {
        byte[] number = ByteBuffer.allocate(2).putShort(packetno).array();
        byte[] buffer = new byte[buf.length + 2];
        
        System.arraycopy(number, 0, buffer, 0, number.length);
        System.arraycopy(buf, 0, buffer, number.length, length);
        sendPacket = new DatagramPacket(buffer, length + 2, serverIP, serverPort);
    }
}
