package org.cs27x.filewatcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;

public class FileEventImpl implements FileEvent {

	private final Kind<?> eventType_;
	private final Path file_;

	public FileEventImpl(Kind<?> eventType, Path file) {
		super();
		eventType_ = eventType;
		file_ = file;
	}

	public Path getFile() {
		return file_;
	}

	public Kind<?> getEventType() {
		return eventType_;
	}
}
