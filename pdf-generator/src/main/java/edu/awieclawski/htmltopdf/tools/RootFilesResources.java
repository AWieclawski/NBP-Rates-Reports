package edu.awieclawski.htmltopdf.tools;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * https://www.baeldung.com/spring-classpath-file-access
 * 
 * @author awieclawski
 *
 */
@Component
@Slf4j
public class RootFilesResources {

	@Value("${root.files-path}")
	private static final String ROOT = "files";

	public byte[] getResourceAsBytes(String path) {
		InputStream inputStream = getInputStream(path);
		byte[] bytes = new byte[0];

		if (inputStream != null) {

			try {
				bytes = IOUtils.toByteArray(inputStream);
			} catch (IOException i) {
				log.error("Get resource from path {} as Bytes failed! {}", path, i.getMessage());
			} finally {

				try {
					inputStream.close();
				} catch (IOException i) {
					log.error("Input stream close failed! {} | Error message: {} ", path, i.getMessage());
				}
			}

		} else {
			log.error("No input stream get from path: {}", path);
		}

		return bytes;
	}

	private InputStream getInputStream(String path) {
		InputStream inputStream = null;

		try {
			inputStream = new ClassPathResource(getResourcePath(path)).getInputStream();
		} catch (IOException e) {
			log.error("Get class path resource from path {} as InputStream failed! {}", path, e.getMessage());
		}

		return inputStream;
	}

	private String getResourcePath(String path) {
		return String.format("/%s/%s", ROOT, path);
	}

}
