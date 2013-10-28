package edu.vanderbilt.cs278.Asgn5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import junit.framework.TestCase;

public class ReceiverTest extends TestCase {

	private RDTReceiver receiver_;
	private InetAddress ipAddress_;
	private int port_;
	
	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
		ipAddress_ = InetAddress.getLocalHost();
		port_ = 8890;
		receiver_ = new RDTReceiver(ipAddress_, port_);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		receiver_.close();
	}
	

	@Test
	public void testGetMetrics() throws IOException{
		long expectedFileLength = 100;
		String expectedFileName = "TestFileName";
		long expectedFileNameLength = expectedFileName.length();
		byte[] fLenData = ByteBuffer.allocate(8).putLong(expectedFileLength).array();
		byte[] fNameLenData = ByteBuffer.allocate(8).putLong(expectedFileNameLength).array();
		byte[] fNameData = expectedFileName.getBytes();
		byte[] sendData = new byte[8+8+RDTServer.MAXFILENAMELENGTH];
		
		System.arraycopy(fLenData, 0, sendData, 0, 8);
		System.arraycopy(fNameLenData, 0, sendData, 8, 8);
		System.arraycopy(fNameData, 0, sendData, 16, fNameData.length);
		
		final DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,ipAddress_,port_);
		final DatagramSocket sendSocket = new DatagramSocket();
		new Thread(new Runnable(){
			public void run(){
				try {
					sendSocket.setSoTimeout(0);
					sendSocket.send(sendPacket);
					sendSocket.close();
				} catch (IOException e) {
					fail(e.getMessage());
					e.printStackTrace();
				}
			}
		}).start();
		int receivedFileLen = receiver_.getFileLength();
		assertEquals(expectedFileLength, receivedFileLen);
		assertEquals(ipAddress_.getHostAddress(), receiver_.getClientIP());
		assertEquals(expectedFileName, receiver_.getFileName());
		
		
	}
	
	@Test
	public void testReceiveData() throws IOException, InterruptedException{
		assertEquals(0,receiver_.getAckNo());//starts with ack0;
		Thread sender = new Thread( new Runnable(){
			public void run(){
				short ack1 = 1;
				byte[] ackPak1 = ByteBuffer.allocate(2).putShort(ack1).array();
				short ack2 = 0;
				byte[] ackPak2 = ByteBuffer.allocate(2).putShort(ack2).array();
				short ack3 = 1;
				byte[] ackPak3 = ByteBuffer.allocate(2).putShort(ack3).array();
				short ack4 = 1;
				byte[] ackPak4 = ByteBuffer.allocate(2).putShort(ack4).array();
				
				String testData = "this is test data";
				byte[] testDataBytes = testData.getBytes();
				byte[] toSend1 = constructByteArray(ackPak1, testDataBytes);
				byte[] toSend2 = constructByteArray(ackPak2, testDataBytes);
				byte[] toSend3 = constructByteArray(ackPak3, testDataBytes);
				byte[] toSend4 = constructByteArray(ackPak4, testDataBytes);
				
				try {
					DatagramSocket sendSock = new DatagramSocket();
					DatagramPacket p1 = new DatagramPacket(toSend1,toSend1.length, ipAddress_, port_);
					sendSock.send(p1);
					DatagramPacket p2 = new DatagramPacket(toSend2,toSend2.length, ipAddress_, port_);
					sendSock.send(p2);
					DatagramPacket p3 = new DatagramPacket(toSend3,toSend3.length, ipAddress_, port_);
					sendSock.send(p3);
					DatagramPacket p4 = new DatagramPacket(toSend4,toSend4.length, ipAddress_, port_);
					sendSock.send(p4);
					
					sendSock.close();
				} catch (Exception e) {
					fail(e.getMessage());
					e.printStackTrace();
				} 
			}
		});
		sender.start();
		
		byte[] buf = new byte[2+"this is test data".getBytes().length];
		int result1 = receiver_.recvData(buf, buf.length);
		int result2 = receiver_.recvData(buf, buf.length);
		int result3 = receiver_.recvData(buf, buf.length);
		int result4 = receiver_.recvData(buf, buf.length);
		
		assertEquals(-1, result1);
		assertNotSame(-1, result2);
		assertNotSame(-1, result3);
		assertEquals(-1, result4);
		assertEquals(2,receiver_.getAckNo());
	}
	
	private byte[] constructByteArray(byte[] ack, byte[] data){
		byte[] result = new byte[ack.length + data.length];
		System.arraycopy(ack, 0, result, 0, ack.length);
		System.arraycopy(data, 0, result, ack.length, data.length);
		return result;
	}
}
