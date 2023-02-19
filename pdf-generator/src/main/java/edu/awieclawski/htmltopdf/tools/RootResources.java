package edu.awieclawski.htmltopdf.tools;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RootResources {

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
			}

			try {
				inputStream.close();
			} catch (IOException i) {
				log.error("Input stream close failed! {} | Error message: {} ", path, i.getMessage());
			}

		} else {
			log.error("No input stream get from path: {}", path);
		}

		return bytes;
	}

	private InputStream getInputStream(String path) {
		return getClass().getResourceAsStream(getResourcePath(path));
	}

	private String getResourcePath(String path) {
		return String.format("/%s/%s", ROOT, path);
	}

}
