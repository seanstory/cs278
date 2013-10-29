package edu.vanderbilt.cs278.Asgn5;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class IntegrationTest extends TestCase {

	private String ipAddress_;
	private int testPort_;
	private File bigFile_;
	private File receivedFile_;
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		ipAddress_ = InetAddress.getLocalHost().getHostAddress();
		testPort_ = 8888;
		bigFile_ = new File("bigfile.txt");
		receivedFile_ = new File("received_"+bigFile_.getName());
		if (!bigFile_.exists())
			fail("bigfile.txt does not exist!");
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		if (receivedFile_.exists())
			receivedFile_.delete();
	}

	@Test
	public void testTrivial(){
		assertTrue(true);
	}
	
	@Test
	public void testRDTOfBigFile() throws IOException, InterruptedException{
		Thread server = new Thread(new Runnable(){
			public void run(){
				String[] args = {ipAddress_, new Integer(testPort_).toString()};
				try {
					RDTServer.main(args);
				} catch (Exception e) {
					fail(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		Thread client = new Thread(new Runnable(){
			public void run(){
				String[] args = {ipAddress_, new Integer(testPort_).toString(), bigFile_.getName()};
				try {
					RDTClient.main(args);
				} catch (Exception e) {
					fail(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		server.start();
		Thread.sleep(500);//let the server get going
		while (! RDTServer.isRunning(testPort_)){
			Thread.sleep(500);//wait a half second
		}
		client.start();
		Thread.sleep(500);//let the client get going
		while (RDTClient.isRunning(testPort_)){
			Thread.sleep(500);
		}
		
		assertTrue(receivedFile_.exists());
		assertTrue(bigFile_.exists());
		
		//test the equality of the files
		BufferedReader original = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(bigFile_))));
		BufferedReader received = new BufferedReader(new InputStreamReader(new DataInputStream( new FileInputStream(receivedFile_))));
		String s1 =null;
		String s2 =null;
		while ((s1 = original.readLine())!=null){
			s2 = received.readLine();
			if (!s1.equals(s2)){
				fail("contents are different");
			}
		}
		original.close();
		received.close();
		
	}
	
	

}
