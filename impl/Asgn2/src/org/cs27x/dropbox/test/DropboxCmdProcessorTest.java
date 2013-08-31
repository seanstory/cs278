package org.cs27x.dropbox.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.cs27x.dropbox.DropboxCmd;
import org.cs27x.dropbox.DropboxCmd.OpCode;
import org.cs27x.dropbox.DropboxCmdProcessor;
import org.cs27x.dropbox.FileManager;
import org.cs27x.filewatcher.FileState;
import org.cs27x.filewatcher.FileStates;

public class DropboxCmdProcessorTest {

	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws IOException{
	}

	@Test
	public void testRemoveWithStub() {
		FileState state = new FileState(0, FileTime.fromMillis(0));
		DropboxCmdProcessor proc = new DropboxCmdProcessor(new FileStatesStub(state), new FileManagerStub(FileTime.fromMillis(0)));
		DropboxCmd cmd = new DropboxCmd();
		cmd.setOpCode(OpCode.REMOVE);
		cmd.setPath("foo");
		proc.updateFileState(cmd, Paths.get("foo"));
		assertEquals(-1, state.getSize());
		
	}
	
	@Test
	public void testAddUpdateMock() throws Exception {

		final FileState state = new FileState(0, FileTime.fromMillis(0));
		final FileTime changeTime = FileTime.fromMillis(101);

		FileStates states = mock(FileStates.class);
		FileManager mgr = mock(FileManager.class);
		when(mgr.getLastModifiedTime(any(Path.class))).thenReturn(changeTime);
		when(states.getOrCreateState(any(Path.class))).thenReturn(state);
		when(states.getState(any(Path.class))).thenReturn(state);

		DropboxCmdProcessor proc = new DropboxCmdProcessor(states, mgr);

		DropboxCmd cmd = new DropboxCmd();
		cmd.setOpCode(OpCode.ADD);
		cmd.setPath("foo");
		byte[] data = new byte[1011];
		cmd.setData(data);
		proc.updateFileState(cmd, Paths.get("foo"));

		assertEquals(changeTime, state.getLastModificationDate());
		assertEquals(data.length, state.getSize());

		state.setLastModificationDate(FileTime.fromMillis(1));
		state.setSize(0);

		cmd.setOpCode(OpCode.UPDATE);
		cmd.setPath("foo");
		cmd.setData(data);
		proc.updateFileState(cmd, Paths.get("foo"));

		assertEquals(changeTime, state.getLastModificationDate());
		assertEquals(data.length, state.getSize());
	}
	
	@Test
	public void testAddUpdateWithStub() {

		FileState state = new FileState(0, FileTime.fromMillis(0));
		FileTime changeTime = FileTime.fromMillis(101);

		FileStates states = new FileStatesStub(state);
		FileManager mgr = new FileManagerStub(changeTime);

		DropboxCmdProcessor proc = new DropboxCmdProcessor(states, mgr);

		DropboxCmd cmd = new DropboxCmd();
		cmd.setOpCode(OpCode.ADD);
		cmd.setPath("foo");
		byte[] data = new byte[1011];
		cmd.setData(data);
		proc.updateFileState(cmd, Paths.get("foo"));

		assertEquals(changeTime, state.getLastModificationDate());
		assertEquals(data.length, state.getSize());

		state.setLastModificationDate(FileTime.fromMillis(1));
		state.setSize(0);

		cmd.setOpCode(OpCode.UPDATE);
		cmd.setPath("foo");
		cmd.setData(data);
		proc.updateFileState(cmd, Paths.get("foo"));

		assertEquals(changeTime, state.getLastModificationDate());
		assertEquals(data.length, state.getSize());
	}


	@Test
	public void testCmdReceivedWithMock() throws IOException {
		final FileState state = new FileState(0, FileTime.fromMillis(0));
		final FileTime changeTime = FileTime.fromMillis(101);
		
		FileStates states = mock(FileStates.class);
		FileManager mgr = mock(FileManager.class);
		when(mgr.getLastModifiedTime(any(Path.class))).thenReturn(changeTime);
		when(states.getOrCreateState(any(Path.class))).thenReturn(state);
		when(states.getState(any(Path.class))).thenReturn(state);
		when(mgr.resolve(any(String.class))).thenReturn(Paths.get("foo"));
		
		//test REMOVE command
		DropboxCmd cmd = new DropboxCmd();
		cmd.setOpCode(OpCode.REMOVE);
		cmd.setPath("foo");
		DropboxCmdProcessor proc = new DropboxCmdProcessor(states, mgr);
		proc.cmdReceived(cmd);
		verify(mgr,times(1)).delete(eq(Paths.get("foo")));
		
		//test ADD command
		cmd.setOpCode(OpCode.ADD);
		byte[] data = new byte[100];
		cmd.setData(data);
		cmd.setPath("foo");
		proc.cmdReceived(cmd);
		verify(mgr, times(1)).write(eq(Paths.get("foo")), eq(data), eq(false));
		
		//test UPDATE command
		cmd.setOpCode(OpCode.UPDATE);
		proc.cmdReceived(cmd);
		verify(mgr, times(1)).write(eq(Paths.get("foo")), eq(data), eq(true));
	}


}
