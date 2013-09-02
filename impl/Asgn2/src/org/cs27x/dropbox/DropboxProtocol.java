package org.cs27x.dropbox;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.cs27x.dropbox.DropboxCmd.OpCode;
import org.cs27x.filewatcher.FileStates;
import org.cs27x.filewatcher.FileStatesInterface;

public class DropboxProtocol {

	private final DropboxTransport transport_;
	
	private final DropboxCmdProcessor cmdProcessor_;

	public DropboxProtocol(DropboxTransport transport, FileStatesInterface states, FileManager filemgr) {
		transport_ = transport;
		cmdProcessor_ = new DropboxCmdProcessor(states,filemgr);
		transport_.addListener(cmdProcessor_);
	}

	public void connect(String initialPeer) {
		transport_.connect(initialPeer);
	}

	public void publish(DropboxCmd cmd) {
		transport_.publish(cmd);
	}

	public DropboxCmd addFile(Path p){
		return addOrUpdateFile(p, OpCode.ADD);
	}
	
	public DropboxCmd updateFile(Path p){
		return addOrUpdateFile(p, OpCode.UPDATE);
	}
	
	private DropboxCmd addOrUpdateFile(Path p, OpCode oc) {
		DropboxCmd cmd = new DropboxCmd();
		cmd.setOpCode(oc);
		cmd.setPath(p.getFileName().toString());

		try {

			try (InputStream in = Files.newInputStream(p)) {
				byte[] data = IOUtils.toByteArray(in);
				cmd.setData(data);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		publish(cmd);
		return cmd;
	}

	public DropboxCmd removeFile(Path p) {
		DropboxCmd cmd = new DropboxCmd();
		cmd.setOpCode(OpCode.REMOVE);
		cmd.setPath(p.getFileName().toString());
		publish(cmd);
		return cmd;
	}

}
