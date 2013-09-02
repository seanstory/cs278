package org.cs27x.filewatcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;

public interface FileEvent {

	public Path getFile() ;

	public Kind<?> getEventType() ;
}
