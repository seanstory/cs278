package edu.vanderbilt.cs278.Asgn5;


import java.net.InetAddress;
import java.io.*;
import java.net.*;
import java.nio.*;

public class RDTReceiver {
    
    private DatagramSocket recvSocket_;
    private DatagramPacket in_;
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
        in_ = new DatagramPacket(buffer, buffer.length);//initialize the Datagram Packet
        short ackno = -1;
        byte[] ackbuf = new byte[2];
        
        //wait for - and then receive and store - the next packet
        while (ackno != packetno_) {
            recvSocket_.receive(in_);
            
            System.arraycopy(buffer, 0, ackbuf, 0, ackbuf.length);
            ackno = ByteBuffer.wrap(ackbuf).getShort();//extract the value from the 2 bytes.
            
            //send an ack no matter what. 
            DatagramPacket ack = new DatagramPacket(ackbuf, ackbuf.length, senderIP_, senderport_);
            recvSocket_.send(ack);
        }
        System.arraycopy(buffer, ackbuf.length, buf, 0, length);
        //System.out.println("Packet revcd: " + new String(buffer));
        
        
        
        ++packetno_;
        
        return in_.getLength() - 2;
	}
    
    public int getFileLength() throws IOException {
        byte[] fullPacket = new byte[8+8+RDTServer.MAXFILENAMELENGTH];//8 for file length, 8 for file name char[] length, + the space for the filename. 
        in_ = new DatagramPacket(fullPacket, fullPacket.length);
        recvSocket_.receive(in_);//blocks until can receive
        senderIP_ = in_.getAddress();
        senderport_ = in_.getPort();
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
}
