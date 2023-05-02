package edu.awieclawski.schedulers.rest.services;

import edu.awieclawski.htmltopdf.dtos.BinaryDto;
import edu.awieclawski.htmltopdf.dtos.ReportDto;

public interface PdfRestService {

	BinaryDto getTestPdf();

	BinaryDto getNbpReport(ReportDto report);
}
