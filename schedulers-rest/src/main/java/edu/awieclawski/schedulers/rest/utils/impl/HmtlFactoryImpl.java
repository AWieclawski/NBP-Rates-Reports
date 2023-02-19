package edu.awieclawski.schedulers.rest.utils.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import edu.awieclawski.htmltopdf.dtos.DocumentDto;
import edu.awieclawski.models.entities.ExchangeRate;
import edu.awieclawski.models.entities.ExchangeRateTypeA;
import edu.awieclawski.schedulers.rest.utils.HmtlFactory;

@Component
class HmtlFactoryImpl implements HmtlFactory {

	public DocumentDto getDocumentByRates(List<ExchangeRate> rates) {
		LocalDate date = LocalDate.now();
		Set<String> nbpTables = new HashSet<>();
		Set<LocalDate> dates = new HashSet<>();

		StringBuffer table = new StringBuffer();
		StringBuffer header = new StringBuffer("<p><b>Pdf report made of ").append(date).append("</b></p>");
		StringBuffer body = new StringBuffer("<p><u>List of Exchange rates:</u></p>");
		StringBuffer rows = wrapInRow(wrapInCell("Currency:")
		.append(wrapInCell("Rate:"))
		.append(wrapInCell("Valid to:")));
		int dateCounter = 0;

		for (ExchangeRate rate : rates) {
			nbpTables.add(rate.getNbpTable());
			dates.add(rate.getPublished());
			StringBuffer row = new StringBuffer();

			if (dateCounter < dates.size()) {
				row.append(wrapMultiColHeader(rate.getPublished().toString() + " in: " + rate.getNbpTable(), 3));
			}

			String currency = rate.getCurrency().getCode();
			rows = rows.append(rowHandlerByTypeRate(rate, row, currency));
			dateCounter = dates.size();
		}

		table = wrapInTable(rows);
		String nbpTablesSeparated = nbpTables.stream().collect(Collectors.joining(", "));
		header.append("<p><i>NBP table/s considered: ").append(nbpTablesSeparated).append(".</i></p><br>");
		header.append(body).append(table);

		return DocumentDto.builder()
		.footerText("edu.awieclawski.nbp-rates-report " + LocalDateTime.now())
		.htmlContent(header.toString())
		.build();
	}

	private StringBuffer rowHandlerByTypeRate(ExchangeRate rate, StringBuffer row, String currency) {

		if (rate instanceof ExchangeRateTypeA) {
			ExchangeRateTypeA rateTypeA = (ExchangeRateTypeA) rate;
			BigDecimal exchRate = rateTypeA.getRate();
			LocalDate validTo = rate.getPublished().plusDays(1);
			row.append(wrapInRow((wrapInCell(currency)
			.append(wrapInCell(exchRate.toString()))
			.append(wrapInCell(validTo.toString())))));
		}

		return row;
	}

}
