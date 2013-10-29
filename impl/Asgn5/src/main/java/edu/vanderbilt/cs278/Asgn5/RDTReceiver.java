package edu.vanderbilt.cs278.Asgn5;


import java.net.InetAddress;
import java.io.*;
import java.net.*;
import java.nio.*;

public class RDTReceiver {
    
    private DatagramSocket recvSocket_;
    private InetAddress senderIP_;
    private int senderport_;
    private String fileName_;
    private short packetno_;

	public RDTReceiver(InetAddress serverIP, int serverPort) throws IOException {
		recvSocket_ = new DatagramSocket(serverPort, serverIP);
        packetno_ = 0;
	}

	public int recvData(byte[] buf, int length) throws IOException {
        //initialize varialbles for this receipt
		byte[] buffer = new byte[buf.length + 2];//initialize the buffer for the full packet to be received
        DatagramPacket in = new DatagramPacket(buffer, buffer.length);//initialize the Datagram Packet
        //short ackno = -1;
        byte[] ackbuf = new byte[2];
        
        //wait for - and then receive and store - the next packet
       // while (ackno != packetno_) {
        //System.out.println("Waiting to receive "+packetno_);
        recvSocket_.receive(in);
        setSender(in.getAddress(), in.getPort());
        System.arraycopy(buffer, 0, ackbuf, 0, ackbuf.length);
        short ackno = ByteBuffer.wrap(ackbuf).getShort();//extract the value from the 2 bytes.
        //System.out.println("Received ack "+ackno);
        //send an ack no matter what. 
        DatagramPacket ack = new DatagramPacket(ackbuf, ackbuf.length, senderIP_, senderport_);
        recvSocket_.send(ack);
        //System.out.println("Sent ack "+ackno);
        //}
        System.arraycopy(buffer, ackbuf.length, buf, 0, length);
        //System.out.println("Packet revcd: " + new String(buffer));
        
        
        if (ackno == packetno_){
        	++packetno_;
        	return in.getLength() - 2;
        }
        else
        	return -1;
        
        
	}
    
    public int getFileLength() throws IOException {
        byte[] fullPacket = new byte[8+8+RDTServer.MAXFILENAMELENGTH];//8 for file length, 8 for file name char[] length, + the space for the filename. 
        DatagramPacket in = new DatagramPacket(fullPacket, fullPacket.length);
        recvSocket_.receive(in);//blocks until can receive
        setSender(in.getAddress(), in.getPort());
        byte[] fileLen = new byte[8];
        byte[] fileNameLen = new byte[8];
        System.arraycopy(fullPacket, 0, fileLen, 0, 8);
        System.arraycopy(fullPacket, 8, fileNameLen, 0, 8);
        int nameLen = (int) ByteBuffer.wrap(fileNameLen).getLong();
        byte[] fileName = new byte[nameLen];
        System.arraycopy(fullPacket, 16, fileName, 0, nameLen);
        fileName_ = new String(fileName);
       
        return (int) ByteBuffer.wrap(fileLen).getLong();
	}
    
    public String getClientIP(){
    	return senderIP_.getHostAddress();
    }
    
    public String getFileName(){
    	return fileName_;
    }
    
    public int getAckNo(){
    	return packetno_;
    }
    
    public void close(){
    	recvSocket_.close();
    }
    
    private void setSender(InetAddress ip, int port){
    	senderIP_ = ip;
    	senderport_ = port;
    }
}
