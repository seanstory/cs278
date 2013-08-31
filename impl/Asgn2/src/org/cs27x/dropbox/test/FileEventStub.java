package org.cs27x.dropbox.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent.Kind;

import org.cs27x.filewatcher.FileEvent;

public class FileEventStub implements FileEvent {
	
	private Kind<?> event_type_;
	
	public FileEventStub(Kind<?> et){
		event_type_ = et;
	}

	@Override
	public Path getFile() {
		return Paths.get("foo");
	}

	@Override
	public Kind<?> getEventType() {
		return event_type_;
	}

}
