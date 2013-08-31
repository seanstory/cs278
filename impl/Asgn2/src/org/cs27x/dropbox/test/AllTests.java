package org.cs27x.dropbox.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/*
 * This file will kick off all the tests for the Asgn2. 
 */
@RunWith(Suite.class)
@SuiteClasses({ DefaultFileManagerTest.class, DropboxCmdProcessorTest.class,
		DropboxFileEventHandlerTest.class, DropboxProtocolTest.class,})
public class AllTests {
	/*
	 * Change if you would like the test file to be written and deleted in a directory other than your workspace.
	 */
	public final static String SYNCFILE1 = "./"; 
}
