package org.cs27x.dropbox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.cs27x.filewatcher.FileEvent;
import org.cs27x.filewatcher.FileState;
import org.cs27x.filewatcher.FileStatesInterface;

public class FileStatesStub implements FileStatesInterface {

	@Override
	public FileState getState(Path p) {
		FileTime ft = FileTime.fromMillis(1000);
		FileState fs = new FileState(100, ft);
		
		return fs;
	}

	@Override
	public FileState getOrCreateState(Path p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileState insert(Path p) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileEvent filter(FileEvent evt) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
