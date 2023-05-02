package edu.awieclawski.html.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeCDto;
import edu.awieclawski.commons.dtos.data.RatesByDateDto;
import edu.awieclawski.commons.dtos.data.RatesReportDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.html.service.impl.HtmlProducerImpl;

@SpringBootTest
class HtmlProducerTest {

	@Autowired
	private HtmlProducer producer;

	private RatesReportDto report;
	private String nbpTable;
	private LocalDate date;

	@BeforeEach
	void init() {
		producer = new HtmlProducerImpl();
		date = LocalDate.now().minusDays(2);
		report = RatesReportDto.builder()
				.nbpTablesSeparated("raz, dwa, trzy")
				.reportDate(LocalDate.now())
				.ratesByDate(new ArrayList<>())
				.build();
	}

	@Test
	void testAskBidTableBuilder() {
		// given
		nbpTable = "AskBidTable";
		String[] result = new String[1];
		List<RatesByDateDto> ratesByDate = ratesByDate(nbpTable, date, getRateTypeCDtos(nbpTable, date, "EUR", "euro"));
		report.getRatesByDate().addAll(ratesByDate);
		// when
		Assertions.assertDoesNotThrow(() -> result[0] = producer.askBidTableBuilder(report));
		// then
		Assertions.assertTrue(result[0].contains(nbpTable));
		System.out.println(" >>> result HTML:\n" + result[0].toString());
	}

	@Test
	void testMidTableBuilder() {
		// given
		nbpTable = "MidTable";
		String[] result = new String[1];
		List<RatesByDateDto> ratesByDate = ratesByDate(nbpTable, date,
				getRateTypeADtos(nbpTable, date, "USD", "dolar"));
		report.getRatesByDate().addAll(ratesByDate);
		// when
		Assertions.assertDoesNotThrow(() -> result[0] = producer.midTableBuilder(report));
		// then
		Assertions.assertTrue(result[0].contains(nbpTable));
		System.out.println(" >>> result HTML:\n" + result[0].toString());
	}

	private List<RatesByDateDto> ratesByDate(String nbpTable, LocalDate date, List<ExchangeRateDto> dtos) {
		List<RatesByDateDto> ratesByDate = new ArrayList<>();
		RatesByDateDto rate = RatesByDateDto.builder()
				.nbpTable(nbpTable)
				.published(date)
				.rates(new ArrayList<>())
				.build();
		rate.getRates().addAll(dtos);
		ratesByDate.add(rate);
		return ratesByDate;
	}

	// new ExchangeRatesDto

	private List<ExchangeRateDto> getRateTypeADtos(String nbpTable, LocalDate date, String code,
			String desc) {
		List<ExchangeRateDto> list = new ArrayList<>();
		list.add(ExchangeRateTypeADto.builder()
				.currency(getCurrencyDto(code, desc))
				.nbpTable(nbpTable)
				.published(date)
				.validTo(date != null ? date.plusDays(1) : date)
				.rate(BigDecimal.ONE)
				.build());
		return list;
	}

	private List<ExchangeRateDto> getRateTypeCDtos(String nbpTable, LocalDate date, String code,
			String desc) {
		List<ExchangeRateDto> list = new ArrayList<>();
		list.add(ExchangeRateTypeCDto.builder()
				.currency(getCurrencyDto(code, desc))
				.nbpTable(nbpTable)
				.published(date)
				.ask(BigDecimal.ONE)
				.bid(BigDecimal.TEN)
				.tradingDate(date.minusDays(1))
				.build());
		return list;
	}

	private CurrencyDto getCurrencyDto(String code, String desc) {
		return CurrencyDto.builder()
				.code(code)
				.description(desc)
				.build();
	}

}
