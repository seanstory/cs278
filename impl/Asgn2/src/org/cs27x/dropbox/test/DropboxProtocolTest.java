package org.cs27x.dropbox.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.cs27x.dropbox.DropboxCmd;
import org.cs27x.dropbox.DropboxCmd.OpCode;
import org.cs27x.dropbox.DropboxProtocol;
import org.cs27x.dropbox.DropboxTransport;
import org.cs27x.dropbox.DropboxTransportListener;
import org.cs27x.dropbox.FileManager;
import org.cs27x.filewatcher.FileStates;
import org.junit.Before;
import org.junit.Test;

public class DropboxProtocolTest {

	FileStates fs_;
	FileManager fm_;
	DropboxTransport dt_;
	Path p_;
	DropboxProtocol dp_;
	
	@Before
	public void setUp() throws Exception {
		fs_ = mock(FileStates.class);
		fm_ = mock(FileManager.class);
		dt_ = mock(DropboxTransport.class);
		p_ = Paths.get(AllTests.SYNCFILE1+"/foo.txt");
		dp_ = new DropboxProtocol(dt_, fs_, fm_);
	}

	@Test
	public void testDropboxProtocol() {
		verify(dt_, times(1)).addListener(any(DropboxTransportListener.class));
	}


	@Test
	public void testAddFile() throws IOException {
		Files.createFile(p_);
		DropboxCmd cmd = dp_.addFile(p_);
		assertCmd(cmd,OpCode.ADD, true);
		Files.delete(p_);//cleanup
	}

	@Test
	public void testRemoveFile() {
		DropboxCmd cmd = dp_.removeFile(p_);
		assertCmd(cmd,OpCode.REMOVE, false);
	}

	@Test
	public void testUpdateFile() throws IOException {
		Files.createFile(p_);
		DropboxCmd cmd = dp_.updateFile(p_);
		assertCmd(cmd,OpCode.UPDATE, true);
		Files.delete(p_);//cleanup
	}
	
	private void assertCmd(DropboxCmd cmd,OpCode oc, boolean fileData){
		assertEquals(cmd.getPath(), p_.getFileName().toString());
		assertEquals(cmd.getOpCode(),oc);
		if (fileData){
			try {
				assertEquals(cmd.getData(), IOUtils.toByteArray(Files.newInputStream(p_)));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
