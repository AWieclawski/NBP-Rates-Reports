package edu.awieclawski.htmltopdf.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PdfGenerator {

	@Autowired
	private FooterPageEvent footerPageEvent;

	@Value("${pages.info}")
	private Boolean pageInfo;

	public ByteArrayInputStream generatePdfStream(List<Element> elements, String footerText) {
		final Document documentA4 = new Document(PageSize.A4, 60f, 50f, 60f, 80f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		if (elements != null && !elements.isEmpty()) {
			try {
				PdfWriter writer = PdfWriter.getInstance(documentA4, out);
				writer.setPageEvent(footerPageEvent.setFooterText(footerText));
				documentA4.open();

				for (Element element : elements) {
					documentA4.add(element);
				}

				documentA4.close();
			} catch (DocumentException e) {
				log.error("Failed to generate pdf as stream! {}", e.getMessage());
			}
		}

		if (pageInfo != null && pageInfo) {
			try {
				out = addPagesInfo(out);
			} catch (DocumentException | IOException e) {
				log.error("Failed to add pages info at streamed pdf document! {}", e.getMessage());
			}
		}

		return new ByteArrayInputStream(out.toByteArray());
	}

	private ByteArrayOutputStream addPagesInfo(ByteArrayOutputStream in) throws DocumentException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PdfReader pdfReader = new PdfReader(in.toByteArray());
		PdfStamper pdfStamper = new PdfStamper(pdfReader, out);
		Font font = footerPageEvent.getFooterFont();
		int n = pdfReader.getNumberOfPages();
		PdfContentByte over;

		for (int i = 1; i <= n; i++) {
			over = pdfStamper.getOverContent(i);
			Phrase footer = new Phrase(String.format("Page %d of %d", i, n), font);
			ColumnText.showTextAligned(over, PdfContentByte.ALIGN_CENTER, footer, footerPageEvent.getXFooter(),
				footerPageEvent.getYFooter() - footerPageEvent.getRowStep(), 0);
		}
		pdfStamper.close();

		return out;
	}

}
