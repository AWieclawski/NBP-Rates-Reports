package edu.awieclawski.schedulers.rest.utils.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import edu.awieclawski.htmltopdf.dtos.DocumentDto;
import edu.awieclawski.models.entities.ExchangeRate;
import edu.awieclawski.models.entities.ExchangeRateTypeA;
import edu.awieclawski.models.entities.ExchangeRateTypeB;
import edu.awieclawski.models.entities.ExchangeRateTypeC;
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
		StringBuffer rows = null;
		int cols = 0;

		if (CollectionUtils.isNotEmpty(rates)) {
			rows = headerHandlerByTypeRate(rates.get(0));
			cols = setColumnsNo(rates.get(0));
		}

		int dateCounter = 0;

		for (ExchangeRate rate : rates) {
			nbpTables.add(rate.getNbpTable());
			dates.add(rate.getPublished());
			StringBuffer row = new StringBuffer();

			if (dateCounter < dates.size()) {
				row.append(wrapMultiColHeader(rate.getPublished().toString() + " in: " + rate.getNbpTable(), cols));
			}

			String currency = rate.getCurrency().getCode();
			rows = rows.append(rowHandlerByTypeRate(rate, row, currency));
			dateCounter = dates.size();
		}

		if (Objects.nonNull(rows)) {
			table = wrapInTable(rows);
		}

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
			ExchangeRateTypeA rateInn = (ExchangeRateTypeA) rate;
			BigDecimal exchRate = rateInn.getRate();
			LocalDate validTo = rate.getPublished().plusDays(1);
			row.append(wrapInRow((wrapInCell(currency)
					.append(wrapInCell(exchRate != null ? exchRate.toString() : ""))
					.append(wrapInCell(validTo.toString())))));
		} else if (rate instanceof ExchangeRateTypeB) {
			ExchangeRateTypeB rateInn = (ExchangeRateTypeB) rate;
			BigDecimal exchRate = rateInn.getRate();
			LocalDate validTo = rate.getPublished().plusDays(1);
			row.append(wrapInRow((wrapInCell(currency)
					.append(wrapInCell(exchRate != null ? exchRate.toString() : ""))
					.append(wrapInCell(validTo.toString())))));
		} else if (rate instanceof ExchangeRateTypeC) {
			ExchangeRateTypeC rateInn = (ExchangeRateTypeC) rate;
			BigDecimal ask = rateInn.getAsk();
			BigDecimal bid = rateInn.getBid();
			LocalDate validTo = rate.getPublished().plusDays(1);
			LocalDate tradingDate = rateInn.getTradingDate();
			row.append(wrapInRow((wrapInCell(currency)
					.append(wrapInCell(ask != null ? ask.toString() : ""))
					.append(wrapInCell(bid != null ? bid.toString() : ""))
					.append(wrapInCell(validTo.toString()))
					.append(wrapInCell(tradingDate.toString())))));
		}

		return row;
	}

	private StringBuffer headerHandlerByTypeRate(ExchangeRate rate) {
		StringBuffer row = new StringBuffer("");

		if (rate instanceof ExchangeRateTypeA || rate instanceof ExchangeRateTypeB) {
			row = wrapInRow(wrapInCell("Currency:")
					.append(wrapInCell("Rate:"))
					.append(wrapInCell("Valid to:")));
		} else if (rate instanceof ExchangeRateTypeC) {
			row = wrapInRow(wrapInCell("Currency:")
					.append(wrapInCell("Ask:"))
					.append(wrapInCell("Bid:"))
					.append(wrapInCell("Valid to:"))
					.append(wrapInCell("Trading date:")));
		}
		return row;
	}

	private int setColumnsNo(ExchangeRate rate) {
		int i = 0;
		if (rate instanceof ExchangeRateTypeA || rate instanceof ExchangeRateTypeB) {
			i = 3;
		} else if (rate instanceof ExchangeRateTypeC) {
			i = 5;
		}
		return i;
	}

}
