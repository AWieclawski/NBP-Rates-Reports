package edu.awieclawski.htmltopdf.defaults;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;

import edu.awieclawski.htmltopdf.tools.RootResources;

public interface PdfDefaults {
	Logger log = LoggerFactory.getLogger(PdfDefaults.class);
	RootResources rootResources = new RootResources();
	BaseFont BASE_PL_FONT = getBasePolishFont();

	default BaseFont getFontFromPath(String path, String fontFileName) {
		byte[] bytes = rootResources.getResourceAsBytes(path);
		BaseFont font = null;

		try {
			font = BaseFont.createFont(fontFileName, BaseFont.CP1250, BaseFont.EMBEDDED, true, bytes, null, false);
		} catch (DocumentException | IOException e) {
			log.error("Failed to laod font from path {} | {}", path, e.getMessage());
		}

		return font;
	}

	private static BaseFont getBasePolishFont() {
		BaseFont helvetica = null;
		try {
			helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
		} catch (DocumentException | IOException e) {
			log.error("Failed to load Polish font! {}", e.getMessage());
		}

		return helvetica;
	}

	default CssFile getCssFile(String path) {
		return XMLWorkerHelper.getCSS(getCssStream(path));
	}

	default InputStream getCssStream(String path) {
		InputStream resource = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
		try {
			resource = new ClassPathResource(path).getInputStream();
			log.info("Succesfully load CSS file from path {}", path);
		} catch (IOException e) {
			log.error("Failed to load CSS from path {} | {}", path, e.getMessage());
		}
		return resource;
	}
}
