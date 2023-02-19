package edu.awieclawski.webclients.services;

import java.time.LocalDate;

import org.springframework.http.client.reactive.ClientHttpConnector;

import edu.awieclawski.webclients.dtos.DataResponseDto;

public interface NbpIntegrationService {

	DataResponseDto getATypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol);

	DataResponseDto getATypeRatesByDatesRangeAndSymbol(LocalDate startDate, LocalDate endDate,
			String currencySymbol);

	DataResponseDto getCTypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol);

	DataResponseDto getCTypeRatesByDateRangeAndSymbol(LocalDate startDate, LocalDate endDate,
			String currencySymbol);

	DataResponseDto getATypeRatesTableByDate(LocalDate publicationDate);

	DataResponseDto getATypeRatesTableByDatesRange(LocalDate startDate, LocalDate endDate);

	DataResponseDto getCTypeRateTableByDate(LocalDate publicationDate);

	DataResponseDto getCTypeRateTableByDateRange(LocalDate startDate, LocalDate endDate);

	ClientHttpConnector customHttpConnector(int TIMEOUT, int TIMEOUT_READ, int TIMEOUT_WRITE);

}
