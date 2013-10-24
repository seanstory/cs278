package edu.vanderbilt.cs278.Asgn5;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClientTest.class, ReceiverTest.class, SenderTest.class,
		ServerTest.class })
public class AllTests {

}
