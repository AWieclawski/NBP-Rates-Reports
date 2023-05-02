package edu.awieclawski.htmltopdf.tools;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import edu.awieclawski.htmltopdf.defaults.PdfDefaults;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Slf4j
/**
 * According to https://stackoverflow.com/a/15919071
 * 
 * prototype bean instance could be different for many users
 * 
 * @author awieclawski
 *
 */
public class FooterPageEvent extends PdfPageEventHelper {// implements PdfDefaults {

	private String footerTxt = "";

	@Getter
	private Font footerFont;

	@Getter
	@Value("${font.footer-size}")
	private Integer fontSize;

	@Getter
	private float xFooter;

	@Getter
	private float yFooter;

	@Getter
	private float rFooter; // rotation

	@Getter
	private float rowStep;

	@PostConstruct
	private void populateInnerFonts() {
		if (PdfDefaults.BASE_PL_FONT != null && fontSize != null) {
			footerFont = new Font(PdfDefaults.BASE_PL_FONT, fontSize, Font.ITALIC);

			if (footerFont.getBaseFont() != null) {
				log.info("Document Footer Font succesfully initialized, basing on: {} ",
						footerFont.getBaseFont().getPostscriptFontName());
			}
		} else {
			log.error("Footer font initialisation after Spring context building failed!");
		}
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		putOnDocument(writer, document);
	}

	public void putOnDocument(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		Phrase footer = new Phrase(footerTxt, footerFont);
		rowStep = fontSize * 2f;
		xFooter = (document.right() - document.left()) / 2 + document.leftMargin();
		yFooter = document.bottom() - rowStep;
		rFooter = 0;
		ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, xFooter, yFooter, rFooter);
	}

	public FooterPageEvent setFooterText(String footerText) {
		this.footerTxt = footerText;

		return this;
	}

}
