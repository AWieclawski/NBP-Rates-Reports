package edu.awieclawski.htmltopdf.services;

import edu.awieclawski.htmltopdf.dtos.BinaryDto;
import edu.awieclawski.htmltopdf.dtos.DocumentDto;

public interface PdfService {

	BinaryDto getTestPdf();

	BinaryDto getBinaries(DocumentDto documentDto);

}
