package edu.vanderbilt.cs278.Asgn5;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.io.*;
import java.net.*;
import java.nio.*;

public class RDTReceiver {
    
    private DatagramSocket recvSocket;
    private DatagramPacket in;
    private InetAddress senderIP;
    private int senderport;
    private short packetno;

	public RDTReceiver(InetAddress serverIP, int serverPort) throws IOException {
		recvSocket = new DatagramSocket(serverPort, serverIP);
        packetno = 0;
	}

	public int recvData(byte[] buf, int length) throws IOException {
		// CS283 Lab 5 Assignment. Please implement
        byte[] buffer = new byte[buf.length + 2];
        in = new DatagramPacket(buffer, buffer.length);
        
        short ackno = -1;
        byte[] ackbuf = new byte[2];
        
        while (ackno != packetno) {
            recvSocket.receive(in);
            
            System.arraycopy(buffer, 0, ackbuf, 0, ackbuf.length);
            ackno = ByteBuffer.wrap(ackbuf).getShort();
            DatagramPacket ack = new DatagramPacket(ackbuf, ackbuf.length, senderIP, senderport);
            recvSocket.send(ack);
        }
        System.arraycopy(buffer, ackbuf.length, buf, 0, length);
        //System.out.println("Packet revcd: " + new String(buffer));
        
        
        
        ++packetno;
        
        return in.getLength() - 2;
	}
    
    public int getFileLength() throws IOException {
		// CS283 Lab 5 Assignment. Please implement
        byte[] len = new byte[8];
        in = new DatagramPacket(len, len.length);
        recvSocket.receive(in);
        senderIP = in.getAddress();
        senderport = in.getPort();
        return (int) ByteBuffer.wrap(len).getLong();
	}
}
