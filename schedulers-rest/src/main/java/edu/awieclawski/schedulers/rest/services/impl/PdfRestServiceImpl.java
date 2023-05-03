package edu.awieclawski.schedulers.rest.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import edu.awieclawski.commons.beans.NbpConnectionElements;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeBDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeCDto;
import edu.awieclawski.commons.dtos.data.RatesByDateDto;
import edu.awieclawski.commons.dtos.data.RatesReportDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.commons.mappers.RatesByDateMapper;
import edu.awieclawski.core.dtos.ConnResultDto;
import edu.awieclawski.core.facades.RateFacade;
import edu.awieclawski.core.services.ConversionService;
import edu.awieclawski.core.services.NbpRequestService;
import edu.awieclawski.html.service.HtmlProducer;
import edu.awieclawski.htmltopdf.defaults.PdfDefaults;
import edu.awieclawski.htmltopdf.dtos.BinaryDto;
import edu.awieclawski.htmltopdf.dtos.DocumentDto;
import edu.awieclawski.htmltopdf.dtos.ReportDto;
import edu.awieclawski.htmltopdf.services.PdfService;
import edu.awieclawski.schedulers.rest.services.PdfRestService;
import edu.awieclawski.schedulers.validators.ReportDtoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
class PdfRestServiceImpl extends PdfDefaults implements PdfRestService {
	private final PdfService htmlToPdfService;
	private final RateFacade rateFacade;
	private final ConversionService conversionService;
	private final NbpRequestService requestService;
	private final NbpConnectionElements endPoints;
	private final HtmlProducer htmlProducer;

	@Autowired
	private ReportDtoValidator validator;

	@Override
	public BinaryDto getTestPdf() {
		List<ExchangeRateDto> rates = rateFacade.getDemo();
		LocalDate date;
		String endPoint = endPoints.aTableRate;

		if (rates == null) {
			date = requestService.connGetResults(ConnResultDto.builder()
					.startDate(LocalDate.now())
					.endPoint(endPoint)
					.nbpType(NbpType.A)
					.counter(0)
					.build())
					.getStartDate();
			rates = conversionService.convertNewDataPackagesAndSave();
		} else {
			date = rates.get(0).getPublished();
		}

		log.info("Report demo date set as: {}", date);
		DocumentDto dto = getDto(rates);
		dto.concatToHtml(BRAKE_TAG.concat(TEST_HTML_PL));

		return htmlToPdfService.getBinaries(dto);
	}

	@Override
	public BinaryDto getNbpReport(ReportDto report) {
		validator.validate(report);
		List<ExchangeRateDto> rates = new ArrayList<>();
		int dayMinus = 1; // for 'A' and 'B' valid to is one day after published

		if (report.getNbpType().equals(NbpType.C)) {
			dayMinus = 0; // for C 'published' is 'effectiveDate'
		}

		LocalDate validToStart = report.getValidToStart().minusDays(dayMinus);
		LocalDate validToEnd = report.getValidToEnd() != null
				? report.getValidToEnd().minusDays(dayMinus)
				: report.getValidToStart();
		List<String> codes = report.getCurrencies();
		NbpType nbpType = report.getNbpType();
		requestService.getDateOfLastDataPackagesByTypeAndSave(ConnResultDto.builder()
				.startDate(validToStart)
				.endDate(validToEnd)
				.codes(codes)
				.nbpType(nbpType)
				.counter(0)
				.build());
		List<ExchangeRateDto> convRates = conversionService.convertNewDataPackagesAndSave();
		log.info("Converted just {} Data Packages.", convRates.size());
		rates.addAll(rateFacade.getByDatesRangeAndSymbolListAndType(validToStart, validToEnd, codes, nbpType));
		rateFacade.makeDistinctById(rates);
		rates.sort(Comparator.comparing(ExchangeRateDto::getPublished));

		if (CollectionUtils.isEmpty(rates)) {
			htmlToPdfService.getTestPdf();
		}

		DocumentDto dto = getDto(rates);

		return htmlToPdfService.getBinaries(dto);
	}

	private DocumentDto getDto(List<ExchangeRateDto> rates) {
		Set<String> nbpTables = new HashSet<>();

		for (ExchangeRateDto rate : rates) {
			nbpTables.add(rate.getNbpTable());
		}

		DocumentDto dto = null;
		String nbpTablesSeparated = nbpTables.stream().collect(Collectors.joining(", "));

		RatesReportDto report = RatesReportDto.builder()
				.nbpTablesSeparated(nbpTablesSeparated)
				.reportDate(LocalDate.now())
				.ratesByDate(new ArrayList<>())
				.build();

		List<RatesByDateDto> ratesByDto = RatesByDateMapper.mapTo(rates);
		report.getRatesByDate().addAll(ratesByDto);
		dto = getDocumentDtoByTypeRate(report);

		return dto;
	}

	private DocumentDto getDocumentDtoByTypeRate(RatesReportDto report) {
		ExchangeRateDto proov = null;
		String content = null;

		try {
			proov = report.getRatesByDate().get(0).getRates().get(0);
		} catch (Exception e) {
			log.error("No ExchangeRateDto object found!");
		}

		if (proov != null) {
			if (proov instanceof ExchangeRateTypeADto || proov instanceof ExchangeRateTypeBDto) {
				content = htmlProducer.midTableBuilder(report);
			} else if (proov instanceof ExchangeRateTypeCDto) {
				content = htmlProducer.askBidTableBuilder(report);
			}
		}

		return DocumentDto.builder()
				.footerText(TEST_FOOTER_TXT.concat("-") + LocalDateTime.now())
				.htmlContent(content)
				.build();
	}

}
