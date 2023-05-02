package edu.awieclawski.webclients.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.http.client.reactive.ClientHttpConnector;

import edu.awieclawski.commons.dtos.data.DataPackageDto;

public interface NbpIntegrationService {

	DataPackageDto getATypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol);

	DataPackageDto getATypeRatesByDatesRangeAndSymbol(LocalDate startDate, LocalDate endDate, String currencySymbol);

	DataPackageDto getCTypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol);

	DataPackageDto getCTypeRatesByDatesRangeAndSymbol(LocalDate startDate, LocalDate endDate, String currencySymbol);

	DataPackageDto getATypeTableByDate(LocalDate publicationDate);

	DataPackageDto getATypeTableByDatesRange(LocalDate startDate, LocalDate endDate);

	DataPackageDto getCTypeTableByDate(LocalDate publicationDate);

	DataPackageDto getCTypeTableByDatesRange(LocalDate startDate, LocalDate endDate);

	DataPackageDto getBTypeTableByDate(LocalDate publicationDate);

	DataPackageDto getBTypeTableByDatesRange(LocalDate startDate, LocalDate endDate);

	ClientHttpConnector customHttpConnector(int TIMEOUT, int TIMEOUT_READ, int TIMEOUT_WRITE);
	
	DateTimeFormatter getDateFormat();

}
