package edu.vanderbilt.cs278.Asgn5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import junit.framework.TestCase;

public class SenderTest extends TestCase {

	private RDTSender senderUnderTest_;
	private InetAddress inetaddr_;
	private int testPort_;
	private File testText_;
	private final String filePath_ = "./testText";
	
	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
		testPort_ = 8888;
		inetaddr_ = InetAddress.getLocalHost();
		senderUnderTest_ = new RDTSender(inetaddr_, testPort_);
		testText_ = new File(filePath_);
		//FileOutputStream fos = new FileOutputStream(filePath_);
		//fos.write("This is a test".getBytes());
		//fos.close();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		testText_.delete();
	}
	
	
	
	@Test
	public void testSendData() throws IOException, InterruptedException{
		String tmp = "This is a test";
		assertFalse(senderUnderTest_.sendData(tmp.getBytes(), tmp.length())); //fail under timeout. Nothing to receive
		new Thread( new Runnable(){
			public void run(){
				try {
					DatagramSocket sock = new DatagramSocket(testPort_,inetaddr_);
					sock.setSoTimeout(2000);//give it a two seconds
					DatagramPacket packet = new DatagramPacket(new byte[30],30);
					sock.receive(packet);
		            
		            int ackno = ByteBuffer.wrap(Arrays.copyOfRange(packet.getData(), 0, 2)).getShort();//extract the value from the 2 bytes.
		            int senderPort = packet.getPort();
		            packet = new DatagramPacket(Arrays.copyOfRange(packet.getData(), 0, 2), 2, inetaddr_, senderPort);
		            sock.send(packet);
		            assertEquals(0, ackno);
					
				} catch (Exception e) {
					fail(e.getMessage());
					e.printStackTrace();
				}
			}
		}).start();
		Thread.sleep(500); //let the other thread get going...
		assertTrue(senderUnderTest_.sendData(tmp.getBytes(), tmp.length()));//true now. Sent and received ack
		assertEquals(1,senderUnderTest_.getPacketNo());
		
	}
	
	@Test
	public void testGetMetricsDatagram() throws SocketException{
		long fileLength = testText_.length();
		DatagramPacket metrics = senderUnderTest_.getMetricsDatagram(fileLength, testText_.getName());
		String metFileName;
		int metFileLen;
		int metFileNameLen;
		byte[] allData = metrics.getData();
		byte[] fileLenData = Arrays.copyOfRange(allData, 0, 8);
		metFileLen = (int) ByteBuffer.wrap(fileLenData).getLong();
		byte[] fileNameLenData = Arrays.copyOfRange(allData, 8,16);
		metFileNameLen = (int) ByteBuffer.wrap(fileNameLenData).getLong();
		byte[] fileName = Arrays.copyOfRange(allData, 16, 16+metFileNameLen);
		metFileName = new String(fileName);
		
		assertEquals(fileLength, metFileLen);
		assertEquals(testText_.getName().length(),metFileNameLen);
		assertEquals(testText_.getName(), metFileName);
	}
	
	

}
