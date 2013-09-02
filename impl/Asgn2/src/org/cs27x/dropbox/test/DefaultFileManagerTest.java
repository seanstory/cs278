package org.cs27x.dropbox.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.cs27x.dropbox.DefaultFileManager;
import org.junit.Before;
import org.junit.Test;


public class DefaultFileManagerTest {

	private DefaultFileManager dfm_;
	private Path testPath_;
	
	
	@Before
	public void setUp() throws Exception {
		testPath_ = Paths.get(AllTests.SYNCFILE1);
		dfm_ = new DefaultFileManager(testPath_);
	}
	

	@Test
	public void testDefaultFileManager() {
		assertNotNull(dfm_);
	}

	@Test
	public void testExists() throws IOException  {
		Path newFile = testPath_.resolve("foo.txt");
		OutputStream out = Files.newOutputStream(newFile);
		byte[] data = "DefaultFileManagerTest.testExists()".getBytes();
		out.write(data);
		assertEquals(true, dfm_.exists(newFile));
		Files.delete(newFile); //cleanup
	}

	@Test
	public void testWrite() throws IOException {
		Path newFile = testPath_.resolve("foo.txt");
		Files.createFile(newFile);
		Files.write(newFile, "don't overwrite".getBytes(),StandardOpenOption.WRITE);
		dfm_.write(newFile, "DefaultFileManagerTest.testWrite()".getBytes(), false);
		String content = new String(Files.readAllBytes(newFile));
		//Test that overwriting does not occur
		assertEquals("don't overwrite",content);
		
		dfm_.write(newFile, "did overwrite".getBytes(), true);
		content = new String(Files.readAllBytes(newFile));
		//Test that overwriting did occur
		assertEquals("did overwrite",content);
		
		Files.delete(newFile);//cleanup
	}

	@Test
	public void testDelete() throws IOException {
		Path newFile = testPath_.resolve("foo.txt");
		Files.createFile(newFile);
		assertEquals(true,Files.exists(newFile));
		dfm_.delete(newFile);
		assertEquals(false,Files.exists(newFile));
	}

	@Test
	public void testResolve() {
		String s = "foo.txt";
		Path newFile = testPath_.resolve(s);
		assertEquals(newFile,dfm_.resolve(s));
	}

}
