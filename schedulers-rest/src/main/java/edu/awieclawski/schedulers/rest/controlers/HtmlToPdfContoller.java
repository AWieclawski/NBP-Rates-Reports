package edu.awieclawski.schedulers.rest.controlers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.awieclawski.htmltopdf.dtos.BinaryDto;
import edu.awieclawski.htmltopdf.dtos.ReportDto;
import edu.awieclawski.schedulers.rest.services.PdfRestService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/stream")
@RestController
public class HtmlToPdfContoller {
	private final PdfRestService pdfRestService;

	@GetMapping("/pdf-test")
	public ResponseEntity<byte[]> getPdfTest() {
		BinaryDto resultDto = pdfRestService.getTestPdf();

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Downloaded_" + resultDto.getName())
				.contentType(resultDto.getMimeType())
				.body(resultDto.getBytes());
	}

	@PostMapping("/pdf-report")
	public ResponseEntity<byte[]> getPdfHtml(@RequestBody ReportDto report) {
		BinaryDto resultDto = pdfRestService.getNbpReport(report);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Downloaded_" + resultDto.getName())
				.contentType(resultDto.getMimeType())
				.body(resultDto.getBytes());
	}

}
