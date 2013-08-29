package org.cs27x.dropbox;

import java.io.IOException;
import java.nio.file.Path;

import org.cs27x.filewatcher.FileEvent;
import org.cs27x.filewatcher.FileState;
import org.cs27x.filewatcher.FileStatesInterface;

public class FileManagerStub implements FileManager {

	@Override
	public Path resolve(String relativePathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Path p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void write(Path p, byte[] data, boolean overwrite)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Path p) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
