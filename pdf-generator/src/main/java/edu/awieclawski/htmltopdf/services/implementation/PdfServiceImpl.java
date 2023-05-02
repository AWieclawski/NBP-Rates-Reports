package edu.awieclawski.htmltopdf.services.implementation;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Element;
import com.itextpdf.tool.xml.ElementList;

import edu.awieclawski.htmltopdf.defaults.PdfDefaults;
import edu.awieclawski.htmltopdf.dtos.BinaryDto;
import edu.awieclawski.htmltopdf.dtos.DocumentDto;
import edu.awieclawski.htmltopdf.exceptions.NotFoundException;
import edu.awieclawski.htmltopdf.services.PdfService;
import edu.awieclawski.htmltopdf.services.XhtmlConverterService;
import edu.awieclawski.htmltopdf.tools.PdfGenerator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
final class PdfServiceImpl extends PdfDefaults implements PdfService {
	private static final Integer COUNTER_LIMIT = 999999;
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(
			String.valueOf(COUNTER_LIMIT).replaceAll("[1-9]", "0"));

	private final XhtmlConverterService xhtmlConverterService;
	private final PdfGenerator pdfGenerator;
	private Integer counter;

	@Override
	public BinaryDto getTestPdf() {
		DocumentDto documentDto = DocumentDto.builder()
				.footerText(TEST_FOOTER_TXT)
				.htmlContent(TEST_HTML_PL)
				.build();

		return getBinaries(documentDto);
	}

	@Override
	public BinaryDto getBinaries(DocumentDto documentDto) {
		List<Element> elements = new ArrayList<>();

		if (documentDto.getHtmlContent() == null) {
			throw new NotFoundException("Html content not found!");
		}

		ElementList table = xhtmlConverterService.convert(documentDto.getHtmlContent(), HELVETICA);
		elements.addAll(table);
		setCounter();

		ByteArrayInputStream inputStream = pdfGenerator.generatePdfStream(elements, documentDto.getFooterText());
		return BinaryDto.builder()
				.name(getFileName())
				.mimeType(MediaType.APPLICATION_PDF)
				.bytes(inputStream != null ? inputStream.readAllBytes() : new byte[0])
				.build();
	}

	private String getFileName() {
		String decimals = DECIMAL_FORMAT.format(counter);
		String timesign = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));

		return String.format("%s%s.pdf", timesign, decimals);
	}

	private void setCounter() {

		if (counter == null || counter > COUNTER_LIMIT) {
			counter = 1;
		}

		counter++;
	}

}
