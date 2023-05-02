package edu.awieclawski.htmltopdf.defaults;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;

import edu.awieclawski.htmltopdf.tools.RootFilesResources;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PdfDefaults {
	private final RootFilesResources rootResources = new RootFilesResources();

	// CONSTs
	public static final BaseFont BASE_PL_FONT = getBasePolishFont();
	public static final Font HELVETICA = new Font(BASE_PL_FONT, Font.NORMAL);
	public static final String TEST_HTML_PL = "<br><br><p>Please find example text below:</p>"
			+ "<p>Style changes inside each paragraph of combined list using Polish font: </p>"
			+ "<ol>"
			+ "<li>Nadrzędny paragraf uporządkowany numerycznie</li>"
			+ "<ul>"
			+ "<li>[normalny]: zażółć jaźń gęślą, tudzież [pogrubiony]: <b>zażółć jaźń gęślą</b>, </li>"
			+ "<li>[normalny]: zażółć jaźń gęślą, tudzież [pochylony]: <i>zażółć jaźń gęślą</i>, </li>"
			+ "</ul>"
			+ "<li>Natępny paragraf nadrzędny także uporządkowany numerycznie</li>"
			+ "<ul>"
			+ "<li>[normalny]: zażółć jaźń gęślą, tudzież [podkreślony]: <u>zażółć jaźń gęślą</u>, </li>"
			+ "<li>[normalny]: zażółć jaźń gęślą, tudzież [pochylony | pogrubiony]: <b><i>zażółć jaźń gęślą</i></b>.</li>"
			+ "</ul>"
			+ "</ol>"
			+ "<p>Brake test inside one paragraph: <br> after brake, <br> after brake again.</p>";
	public static final String TEST_FOOTER_TXT = "edu.awieclawski.nbp-rates-report";

	protected BaseFont getFontFromPath(String path, String fontFileName) {
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

	protected CssFile getCssFile(String path) {
		return XMLWorkerHelper.getCSS(getCssStream(path));
	}

	protected InputStream getCssStream(String path) {
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
