package edu.awieclawski.htmltopdf.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ch.qos.logback.classic.Level;
import edu.awieclawski.commons.tools.MemmoryAppender;

/**
 * 
 * @author awieclawski
 *
 */
@SpringBootTest
class RootResourcesTest extends MemmoryAppender<RootFilesResources> {

	@Autowired
	private RootFilesResources rootResources;

	@Test
	void existingTestfileInExistingRootDirectoryReturnsBytesOk() {
		Assertions.assertTrue(rootResources.getResourceAsBytes("tests/testfile.txt").length > 0);
	}

	@Test
	void notExistingTestfileInExistingRootDirectoryLogError() {
		initMemoryAppender(Level.ERROR);
		rootResources.getResourceAsBytes("tests/nonefile.txt");
		Assertions.assertTrue(countEventsForLogger() > 0);
		cleanUpMemoryAppender();
	}

}
