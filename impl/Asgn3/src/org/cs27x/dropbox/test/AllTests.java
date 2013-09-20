package org.cs27x.dropbox.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DefaultFileManagerTest.class, DropboxCmdProcessorTest.class,
		FileStatesTest.class })
public class AllTests {

}
