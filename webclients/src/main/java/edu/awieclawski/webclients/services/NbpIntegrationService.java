package edu.awieclawski.webclients.services;

import java.time.LocalDate;

import org.springframework.http.client.reactive.ClientHttpConnector;

import edu.awieclawski.webclients.dtos.DataResponseDto;

public interface NbpIntegrationService {

	DataResponseDto getATypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol);

	DataResponseDto getATypeRatesByDatesRangeAndSymbol(LocalDate startDate, LocalDate endDate, String currencySymbol);

	DataResponseDto getCTypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol);

	DataResponseDto getCTypeRatesByDatesRangeAndSymbol(LocalDate startDate, LocalDate endDate, String currencySymbol);

	DataResponseDto getATypeTableByDate(LocalDate publicationDate);

	DataResponseDto getATypeTableByDatesRange(LocalDate startDate, LocalDate endDate);

	DataResponseDto getCTypeTableByDate(LocalDate publicationDate);

	DataResponseDto getCTypeTableByDatesRange(LocalDate startDate, LocalDate endDate);

	DataResponseDto getBTypeTableByDate(LocalDate publicationDate);

	DataResponseDto getBTypeTableByDatesRange(LocalDate startDate, LocalDate endDate);
	
	ClientHttpConnector customHttpConnector(int TIMEOUT, int TIMEOUT_READ, int TIMEOUT_WRITE);

}
