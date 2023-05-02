package edu.awieclawski.htmltopdf.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HtmlUtils {

	@Value("${html.template}")
	private String htmlTemplate;

	public String makeHtmlReadable(String inputHtml) {
		String output = "";

		if (inputHtml == null) {
			return null;
		}

		output = prepareHtmlWithTemplate(inputHtml);
		output = parseHtmlBreaks(output); // must be used after html sanitizing

		return output;
	}

	/*
	 * According to https://stackoverflow.com/a/29394774
	 */
	private String parseHtmlBreaks(String inputHtml) {
		String output = inputHtml;
		String regex = "(?i)<br */?>";
		String newLine = "<br />";

		if (Objects.nonNull(output)) {
			output = output.replaceAll(regex, newLine);
		}

		return output;
	}

	private String prepareHtmlWithTemplate(String inputHtml) {
		String output = "";

		try {
			InputStream resource = new ClassPathResource(htmlTemplate).getInputStream();
			output = IOUtils.toString(resource, StandardCharsets.UTF_8);
		} catch (IOException i) {
			log.error("Prepare HTML failed!  {}", i.getMessage());
		}

		String regex = "#htmlinput";
		Matcher matcher = Pattern.compile(regex).matcher(output);
		output = matcher.replaceAll(Matcher.quoteReplacement(inputHtml));
		output = sanitizeTags(output);

		return output;
	}

	private String sanitizeTags(String inputHtml) {
		return Jsoup.clean(inputHtml, Safelist.relaxed());
	}

}
