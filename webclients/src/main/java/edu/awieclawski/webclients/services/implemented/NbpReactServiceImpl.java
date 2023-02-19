package edu.awieclawski.webclients.services.implemented;

import java.net.URI;
import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import edu.awieclawski.commons.beans.NbpEndpointBeans;
import edu.awieclawski.webclients.connectors.CustomHttpConnector;
import edu.awieclawski.webclients.dtos.DataResponseDto;
import edu.awieclawski.webclients.services.NbpReactService;
import edu.awieclawski.webclients.utils.PathGenerator;

/**
 * end-point rules: http://api.nbp.pl/#kursySingle, http://api.nbp.pl/#kursyFull
 * 
 * @author awieclawski
 * 
 *         according to https://www.baeldung.com/spring-5-webclient
 *
 */

@Service
public class NbpReactServiceImpl implements NbpReactService {
	private static final String NOT_IMPLEMENTED = "Not implemented yet!";
	private static final String PARAM = "format";
	private final WebClient webClient;
	private final PathGenerator pathUtil;
	private NbpEndpointBeans endPoints;

	private String nbpApiUrl;
	// NBP end-point beans
	private String aRates;
	private String cRates;
	private String aTableRate;
	private String bTableRate;

	@Value("${nbp-api.format}")
	private String dataFormat;

	public NbpReactServiceImpl(
		NbpEndpointBeans endPoints,
		PathGenerator pathUtil,
		@Value("${nbp-api.timeout.connect}") int timeout,
		@Value("${nbp-api.timeout.read}") int timeRead,
		@Value("${nbp-api.timeout.write}") int timeWrite,
		@Value("${nbp-api.host}") String baseUrl) {
		this.webClient = WebClient.builder()
			.filters(exchangeFilterFunctions -> {
				exchangeFilterFunctions.add(requestExchangeFilterFunction());
				exchangeFilterFunctions.add(responseExchangeFilterFunction());
			})
			.baseUrl(baseUrl)
			.clientConnector(customHttpConnector(timeout, timeRead, timeWrite))
			.build();
		this.pathUtil = pathUtil;
		this.nbpApiUrl = baseUrl;
		this.endPoints = endPoints;
	}

	/**
	 * NBP endpoint beans initialize
	 */
	@PostConstruct
	private void endPointsInit() {
		this.aRates = endPoints.ratesA;
		this.cRates = endPoints.ratesC;
		this.aTableRate = endPoints.aTableRate;
		this.bTableRate = endPoints.bTableRate;
	}

	@Override
	public DataResponseDto getATypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol) {
		final MultiValueMap<String, String> params = pathUtil.setParamsMap(PARAM, dataFormat);
		final URI uriBuilt = pathUtil.completeUri(aRates, currencySymbol, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(params, uriBuilt).toString();

		return DataResponseDto.builder()
			.jsonData(getRatesAsString(params, uriBuilt))
			.url(targetUrl)
			.endPoint(aRates)
			.build();
	}

	@Override
	public DataResponseDto getATypeRatesByDatesRangeAndSymbol(LocalDate startDate, LocalDate endDate,
		String currencySymbol) {
		final MultiValueMap<String, String> params = pathUtil.setParamsMap(PARAM, dataFormat);
		final URI uriBuilt = pathUtil.completeUri(aRates, currencySymbol, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(params, uriBuilt).toString();

		return DataResponseDto.builder()
			.jsonData(getRatesAsString(params, uriBuilt))
			.url(targetUrl)
			.endPoint(aRates)
			.build();
	}

	@Override
	public DataResponseDto getCTypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public DataResponseDto getCTypeRatesByDateRangeAndSymbol(LocalDate startDate, LocalDate endDate,
		String currencySymbol) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public DataResponseDto getATypeRatesTableByDate(LocalDate publicationDate) {
		final MultiValueMap<String, String> params = pathUtil.setParamsMap(PARAM, dataFormat);
		final URI uriBuilt = pathUtil.completeUri(aTableRate, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(params, uriBuilt).toString();

		return DataResponseDto.builder()
			.jsonData(getRatesAsString(params, uriBuilt))
			.url(targetUrl)
			.endPoint(aTableRate)
			.build();
	}

	@Override
	public DataResponseDto getATypeRatesTableByDatesRange(LocalDate startDate, LocalDate endDate) {
		final MultiValueMap<String, String> params = pathUtil.setParamsMap(PARAM, dataFormat);
		final URI uriBuilt = pathUtil.completeUri(aTableRate, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(params, uriBuilt).toString();

		return DataResponseDto.builder()
			.jsonData(getRatesAsString(params, uriBuilt))
			.url(targetUrl)
			.endPoint(aTableRate)
			.build();
	}

	@Override
	public DataResponseDto getCTypeRateTableByDate(LocalDate publicationDate) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public DataResponseDto getCTypeRateTableByDateRange(LocalDate startDate, LocalDate endDate) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	private String getRatesAsString(MultiValueMap<String, String> params, URI uri) {
		return this.webClient.get()
			.uri(uriBuilder -> uriBuilder.path(uri.toString())
				.queryParams(params)
				.build())
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.onStatus(httpStatus -> !httpStatus.is2xxSuccessful(), errorHandler())
			.bodyToMono(String.class)
			.block();
	}

	@Override
	public ClientHttpConnector customHttpConnector(int timeout, int timeRead, int timeWrite) {
		return new CustomHttpConnector().clientConnector(timeout, timeRead, timeWrite, false);
	}

}
