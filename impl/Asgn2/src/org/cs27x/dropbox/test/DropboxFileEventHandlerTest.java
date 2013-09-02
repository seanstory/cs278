package org.cs27x.dropbox.test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.cs27x.dropbox.DropboxCmd.OpCode;
import org.cs27x.dropbox.DropboxProtocol;
import org.cs27x.dropbox.FileManager;
import org.cs27x.filewatcher.DropboxFileEventHandler;
import org.cs27x.filewatcher.FileEvent;
import org.cs27x.filewatcher.FileStates;
import org.junit.Before;
import org.junit.Test;

public class DropboxFileEventHandlerTest {
	FileManager fm_;
	FileStates fs_;
	DropboxProtocol dp_;
	Path p_;


	@Before
	public void setUp() throws Exception {
		p_ = Paths.get("foo");

		fs_ = mock(FileStates.class);
		fm_ = mock(FileManager.class);
		dp_ = mock(DropboxProtocol.class);
		when(fm_.resolve(any(String.class))).thenReturn(p_);
		
	}

	@Test
	public void testHandleForCreate() throws IOException {
		FileEvent fe = new FileEventStub(ENTRY_CREATE);
		when(fs_.filter(any(FileEvent.class))).thenReturn(fe);
		DropboxFileEventHandler handler = new DropboxFileEventHandler(fm_, fs_, dp_);
		handler.handle(fe);
		verify(dp_, times(1)).addFile(eq(fe.getFile()));
	}
	
	@Test
	public void testHandleForModify() throws IOException {
		FileEvent fe = new FileEventStub(ENTRY_MODIFY);
		when(fs_.filter(any(FileEvent.class))).thenReturn(fe);
		DropboxFileEventHandler handler = new DropboxFileEventHandler(fm_, fs_, dp_);
		handler.handle(fe);
		verify(dp_, times(1)).updateFile(eq(fe.getFile()));
	}
	
	@Test
	public void testHandleForDelete() throws IOException {
		FileEvent fe = new FileEventStub(ENTRY_DELETE);
		when(fs_.filter(any(FileEvent.class))).thenReturn(fe);
		DropboxFileEventHandler handler = new DropboxFileEventHandler(fm_, fs_, dp_);
		handler.handle(fe);
		verify(dp_, times(1)).removeFile(eq(fe.getFile()));
	}

}
