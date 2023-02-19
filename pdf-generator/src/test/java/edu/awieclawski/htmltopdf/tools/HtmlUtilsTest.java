package edu.awieclawski.htmltopdf.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HtmlUtilsTest {

	@Autowired
	private HtmlUtils htmlUtils;

	@Test
	void readValidHtmlReturnOk() {
		String validHtml = "<p>Zażółć jaźń gęślą, <b>zażółć jaźń gęślą [pogrubiony]</b>.</p>";
		Assertions.assertDoesNotThrow(() -> htmlUtils.makeHtmlReadable(validHtml));
		String output = htmlUtils.makeHtmlReadable(validHtml);
		Assertions.assertTrue(output.contains(validHtml));
	}
	
	@Test
	void readHtmlSanitizeTags() {
		String input = "<bad>bad</bad>aa<i>ccc</i><b>ddd</b>";
		String cleaned = "badaa<i>ccc</i><b>ddd</b>";
		Assertions.assertEquals(htmlUtils.makeHtmlReadable(cleaned), htmlUtils.makeHtmlReadable(input));
	}

	@Test
	void readHtmlCorrectBrakeTags() {
		String input = "Brake test: before brake <br> after brake.";
		String cleaned = "Brake test: before brake <br /> after brake.";
		Assertions.assertEquals(htmlUtils.makeHtmlReadable(cleaned), htmlUtils.makeHtmlReadable(input));
	}

	@Test
	void readValidHtmlWithSpecialsReturnOk() {
		String validHtml = getStringsFromAsciiRange(32, 126);
		Assertions.assertDoesNotThrow(() -> htmlUtils.makeHtmlReadable(validHtml));
	}

	private static String getStringsFromAsciiRange(int start, int end) {

		if (start < 0 || end < 0) {
			return null;
		}

		final StringBuilder sb = new StringBuilder();

		for (int i = start; i <= end; i++) {
			sb.append((char) i);
		}

		return sb.toString();
	}

}
