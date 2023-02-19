package edu.awieclawski.htmltopdf.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Font;

import edu.awieclawski.htmltopdf.defaults.PdfDefaults;
import edu.awieclawski.htmltopdf.dtos.BinaryDto;
import edu.awieclawski.htmltopdf.dtos.DocumentDto;

public interface PdfService extends PdfDefaults {
	static final Logger log = LoggerFactory.getLogger(PdfService.class);
	Font HELVETICA = new Font(BASE_PL_FONT, Font.NORMAL);
	String TEST_HTML_PL = "<p>Any NBP Exchange Rate not Found! Please find example text below:</p>"
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
	String TEST_FOOTER_TXT = "edu.awieclawski.nbp-rates-report";

	BinaryDto getTestPdf();

	BinaryDto getBinaries(DocumentDto documentDto);

}
