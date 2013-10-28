package edu.vanderbilt.cs278.Asgn5;

import java.net.InetAddress;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class RDTSender {

    private DatagramSocket sendSocket_;
    private InetAddress serverIP_;
    private int serverPort_;
    private short packetno_;
    
	public RDTSender(InetAddress serverIP, int serverPort) throws SocketException {
        sendSocket_ = new DatagramSocket();
        sendSocket_.setSoTimeout(10);
        serverIP_ = serverIP;
        serverPort_ = serverPort;
        packetno_ = 0;
	}

	public boolean sendData(byte[] buf, int length) throws IOException {
        DatagramPacket packetToSend = makePacket(buf, length);
        byte[] ackbuf = new byte[2];
        DatagramPacket ack = new DatagramPacket(ackbuf, ackbuf.length);
        sendSocket_.send(packetToSend);
        try {
            sendSocket_.receive(ack);
            short ackno = ByteBuffer.wrap(ackbuf).getShort();
            if (ackno == packetno_) {
                ++packetno_;
                return true;
            }
            else
            	return false;
            
        } catch (Exception e) {
            return false;
        }
	}
    
    public DatagramPacket getMetricsDatagram(long fileLength, String fileName){
    	//initialize arrays for file length, file name length, and file name
    	byte[] fileLen = ByteBuffer.allocate(8).putLong(fileLength).array();
    	byte[] fileNameLen = ByteBuffer.allocate(8).putLong(fileName.length()).array();
    	byte[] fName = fileName.getBytes();
    	byte[] bytes = new byte[8+8+RDTServer.MAXFILENAMELENGTH];
    	
    	//copy all the above values into one array
    	System.arraycopy(fileLen, 0, bytes, 0, fileLen.length);
    	System.arraycopy(fileNameLen, 0, bytes, fileLen.length, fileNameLen.length);
    	System.arraycopy(fName, 0, bytes, fileLen.length+fileNameLen.length, fName.length);
    	
    	//send the info!
        DatagramPacket metricsPacket = new DatagramPacket(bytes ,bytes.length , serverIP_, serverPort_);
        return metricsPacket;
        //sendSocket.send(lengthPacket);
	}
    
    public DatagramSocket getSendSocket(){
    	return sendSocket_;
    }
    
    protected int getPacketNo(){
    	return packetno_;
    }
    
    private DatagramPacket makePacket(byte[] buf, int length) {
        byte[] number = ByteBuffer.allocate(2).putShort(packetno_).array();
        byte[] buffer = new byte[buf.length + 2];
        
        System.arraycopy(number, 0, buffer, 0, number.length);
        System.arraycopy(buf, 0, buffer, number.length, length);
        return  new DatagramPacket(buffer, length + 2, serverIP_, serverPort_);
    }
    
    public void close(){
    	sendSocket_.close();
    }
}
