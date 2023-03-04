package edu.awieclawski.schedulers.rest.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import edu.awieclawski.commons.beans.NbpEndPointElements;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.core.facades.RateFacade;
import edu.awieclawski.core.services.ConversionService;
import edu.awieclawski.core.services.NbpRequestService;
import edu.awieclawski.htmltopdf.dtos.BinaryDto;
import edu.awieclawski.htmltopdf.dtos.DocumentDto;
import edu.awieclawski.htmltopdf.dtos.ReportDto;
import edu.awieclawski.htmltopdf.services.PdfService;
import edu.awieclawski.models.entities.ExchangeRate;
import edu.awieclawski.schedulers.rest.services.PdfRestService;
import edu.awieclawski.schedulers.rest.utils.HmtlFactory;
import edu.awieclawski.schedulers.validators.ReportDtoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
class PdfRestServiceImpl implements PdfRestService {
	private final PdfService htmlToPdfService;
	private final RateFacade rateFacade;
	private final ConversionService conversionService;
	private final NbpRequestService requestService;
	private final NbpEndPointElements endPoints;
	private final HmtlFactory htmlBuilder;

	@Autowired
	private ReportDtoValidator validator;

	@Override
	public BinaryDto getTestPdf() {
		List<ExchangeRate> rates = rateFacade.getDemo();
		LocalDate date;
		String endPoint = endPoints.aTableRate;

		if (rates == null) {
			date = requestService.getPackagesAndSaveIfDateBeforeLastExchangeRateDate(LocalDate.now(), 0, null, endPoint, NbpType.A,
					null, null);
			rates = conversionService.convertNewDataPackagesAndSave();
		} else {
			date = rates.get(0).getPublished();
		}

		log.info("Report demo date set as: {}", date);
		DocumentDto dto = htmlBuilder.getDocumentByRates(rates);

		return htmlToPdfService.getBinaries(dto);
	}

	@Override
	public BinaryDto getNbpReport(ReportDto report) {
		validator.validate(report);
		List<ExchangeRate> rates = new ArrayList<>();
		LocalDate dateStart = report.getValidToStart().minusDays(1);
		LocalDate dateEnd = report.getValidToEnd() != null
				? report.getValidToEnd().minusDays(1)
				: report.getValidToStart();
		List<String> codes = report.getCurrencies();
		NbpType nbpType = report.getNbpType();
		requestService.getDateOfLastDataPackagesByTypeAndSave(dateStart, 0, null, nbpType, codes, dateEnd);
		List<ExchangeRate> convRates = conversionService.convertNewDataPackagesAndSave();
		log.info("Converted just {} Data Packages.", convRates.size());
		rates.addAll(rateFacade.getByDatesRangeAndSymbolListAndType(dateStart, dateEnd, codes, nbpType));
		rateFacade.makeDistinctById(rates);

		if (CollectionUtils.isEmpty(rates)) {
			htmlToPdfService.getTestPdf();
		}

		DocumentDto dto = htmlBuilder.getDocumentByRates(rates);

		return htmlToPdfService.getBinaries(dto);
	}

}
