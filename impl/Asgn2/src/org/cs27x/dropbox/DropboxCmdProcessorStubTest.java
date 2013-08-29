package org.cs27x.dropbox;

import static org.junit.Assert.*;

import java.net.URI;
import java.nio.file.Path;

import org.cs27x.filewatcher.FileStatesInterface;
import org.junit.Before;
import org.junit.Test;

public class DropboxCmdProcessorStubTest {
	
	private DropboxCmdProcessor dcp;
	private FileStatesInterface fsi;
	private FileManager fm;

	@Before
	public void setUp() throws Exception {
		
		fsi = new FileStatesStub();
		fm = new FileManagerStub();
		dcp = new DropboxCmdProcessor(fsi, fm);
	}

	@Test
	public void test() {
		DropboxCmd dc = new DropboxCmd();
		dc.setOpCode(DropboxCmd.OpCode.REMOVE);
		
		dcp.updateFileState(dc, Path.getRoot());
	}

}
