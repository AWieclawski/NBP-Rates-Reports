package edu.awieclawski.webclients.services.implemented;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import edu.awieclawski.commons.beans.NbpConnectionElements;
import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.webclients.connectors.CustomHttpConnector;
import edu.awieclawski.webclients.filters.CustomWebClientFilter;
import edu.awieclawski.webclients.handlers.CustomErrorHandler;
import edu.awieclawski.webclients.services.NbpIntegrationService;
import edu.awieclawski.webclients.utils.PathGenerator;
import reactor.core.publisher.Mono;

/**
 * end-point rules: http://api.nbp.pl/#kursySingle, http://api.nbp.pl/#kursyFull
 * 
 * @author awieclawski
 * 
 *         according to https://www.baeldung.com/spring-5-webclient
 *
 */

@Service
@DependsOn("nbpConnectionElements")
public class NbpReactServiceImpl implements NbpIntegrationService {
	private static final String ERROR_MSG = "NBP integration REACT service problem!";
	private static final String PARAM = "format";
	private final WebClient webClient;
	private final PathGenerator pathUtil;
	private NbpConnectionElements connElements;

	private String nbpApiUrl;
	private MultiValueMap<String, String> reqParams;
	// NBP end-point elements
	private String aRates;
	private String cRates;
	private String aTableRate;
	private String bTableRate;
	private String cTableRate;
	private String dataFormat;

	@Override
	public DateTimeFormatter getDateFormat() {
		return pathUtil.getDateFormat();
	}

	public NbpReactServiceImpl(
			NbpConnectionElements endPoints,
			PathGenerator pathUtil) {
		this.webClient = WebClient.builder()
				.filters(exchangeFilterFunctions -> {
					exchangeFilterFunctions.add(requestExchangeFilterFunction());
					exchangeFilterFunctions.add(responseExchangeFilterFunction());
				})
				.baseUrl(endPoints.baseUrl)
				.clientConnector(customHttpConnector(endPoints.timeout, endPoints.timeRead, endPoints.timeWrite))
				.build();
		this.pathUtil = pathUtil;
		this.nbpApiUrl = endPoints.baseUrl;
		this.connElements = endPoints;
	}

	/**
	 * NBP end-point elements bean initialize
	 */
	@PostConstruct
	private void endPointsInit() {
		this.aRates = connElements.ratesA;
		this.cRates = connElements.ratesC;
		this.aTableRate = connElements.aTableRate;
		this.bTableRate = connElements.bTableRate;
		this.cTableRate = connElements.cTableRate;
		this.dataFormat = connElements.dataFormat;
		paramsInit();
	}

	private void paramsInit() {
		reqParams = pathUtil.setParamsMap(PARAM, dataFormat);
	}

	/**
	 * Request remote address example:
	 * http://api.nbp.pl/api/exchangerates/rates/a/usd/2016-04-04?format=json
	 */
	@Override
	public DataPackageDto getATypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol) {
		final URI uriBuilt = pathUtil.completeUri(aRates, currencySymbol, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataPackageDto.builder()
				.jsonData(getRatesAsString(reqParams, uriBuilt))
				.url(targetUrl)
				.endPoint(aRates)
				.build();
	}

	/**
	 * Request remote address example:
	 * http://api.nbp.pl/api/exchangerates/rates/a/usd/2016-04-04/2016-04-09?format=json
	 */
	@Override
	public DataPackageDto getATypeRatesByDatesRangeAndSymbol(LocalDate startDate, LocalDate endDate,
			String currencySymbol) {
		final URI uriBuilt = pathUtil.completeUri(aRates, currencySymbol, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataPackageDto.builder()
				.jsonData(getRatesAsString(reqParams, uriBuilt))
				.url(targetUrl)
				.endPoint(aRates)
				.build();
	}

	/**
	 * Request remote address example:
	 * http://api.nbp.pl/api/exchangerates/rates/c/usd/2016-04-04?format=json
	 */
	@Override
	public DataPackageDto getCTypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol) {
		final URI uriBuilt = pathUtil.completeUri(cRates, currencySymbol, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataPackageDto.builder()
				.jsonData(getRatesAsString(reqParams, uriBuilt))
				.url(targetUrl)
				.endPoint(cRates)
				.build();
	}

	/**
	 * Request remote address example:
	 * http://api.nbp.pl/api/exchangerates/rates/c/usd/2016-04-04/2016-04-09?format=json
	 */
	@Override
	public DataPackageDto getCTypeRatesByDatesRangeAndSymbol(LocalDate startDate, LocalDate endDate,
			String currencySymbol) {
		final URI uriBuilt = pathUtil.completeUri(cRates, currencySymbol, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataPackageDto.builder()
				.jsonData(getRatesAsString(reqParams, uriBuilt))
				.url(targetUrl)
				.endPoint(cRates)
				.build();
	}

	/**
	 * Request remote address example:
	 * http://api.nbp.pl/api/exchangerates/tables/a/2016-04-04?format=json
	 */
	@Override
	public DataPackageDto getATypeTableByDate(LocalDate publicationDate) {
		final URI uriBuilt = pathUtil.completeUri(aTableRate, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataPackageDto.builder()
				.jsonData(getRatesAsString(reqParams, uriBuilt))
				.url(targetUrl)
				.endPoint(aTableRate)
				.build();
	}

	/**
	 * Request remote address example:
	 * http://api.nbp.pl/api/exchangerates/tables/a/2016-03-22/2016-03-26?format=json
	 */
	@Override
	public DataPackageDto getATypeTableByDatesRange(LocalDate startDate, LocalDate endDate) {
		final URI uriBuilt = pathUtil.completeUri(aTableRate, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataPackageDto.builder()
				.jsonData(getRatesAsString(reqParams, uriBuilt))
				.url(targetUrl)
				.endPoint(aTableRate)
				.build();
	}

	/**
	 * Request remote address example:
	 * http://api.nbp.pl/api/exchangerates/tables/c/2016-04-04?format=json
	 */
	@Override
	public DataPackageDto getCTypeTableByDate(LocalDate publicationDate) {
		final URI uriBuilt = pathUtil.completeUri(cTableRate, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataPackageDto.builder()
				.jsonData(getRatesAsString(reqParams, uriBuilt))
				.url(targetUrl)
				.endPoint(cTableRate)
				.build();
	}

	/**
	 * Request remote address example:
	 * http://api.nbp.pl/api/exchangerates/tables/c/2016-03-22/2016-03-26?format=json
	 */
	@Override
	public DataPackageDto getCTypeTableByDatesRange(LocalDate startDate, LocalDate endDate) {
		final URI uriBuilt = pathUtil.completeUri(cTableRate, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataPackageDto.builder()
				.jsonData(getRatesAsString(reqParams, uriBuilt))
				.url(targetUrl)
				.endPoint(cTableRate)
				.build();
	}

	/**
	 * Request remote address example:
	 * http://api.nbp.pl/api/exchangerates/tables/b/2016-04-04?format=json
	 */
	@Override
	public DataPackageDto getBTypeTableByDate(LocalDate publicationDate) {
		final URI uriBuilt = pathUtil.completeUri(bTableRate, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataPackageDto.builder()
				.jsonData(getRatesAsString(reqParams, uriBuilt))
				.url(targetUrl)
				.endPoint(bTableRate)
				.build();
	}

	/**
	 * Request remote address example:
	 * http://api.nbp.pl/api/exchangerates/tables/b/2016-03-22/2016-03-26?format=json
	 */
	@Override
	public DataPackageDto getBTypeTableByDatesRange(LocalDate startDate, LocalDate endDate) {
		final URI uriBuilt = pathUtil.completeUri(bTableRate, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataPackageDto.builder()
				.jsonData(getRatesAsString(reqParams, uriBuilt))
				.url(targetUrl)
				.endPoint(bTableRate)
				.build();
	}

	private String getRatesAsString(MultiValueMap<String, String> params, URI uri) {
		return this.webClient.get().uri(uriBuilder -> uriBuilder.path(uri.toString()).queryParams(params).build())
				.accept(MediaType.APPLICATION_JSON).retrieve()
				.onStatus(httpStatus -> !httpStatus.is2xxSuccessful(), errorHandler()).bodyToMono(String.class).block();
	}

	@Override
	public ClientHttpConnector customHttpConnector(int timeout, int timeRead, int timeWrite) {
		return new CustomHttpConnector().clientConnector(timeout, timeRead, timeWrite, false);
	}

	private Function<ClientResponse, Mono<? extends Throwable>> errorHandler() {
		return new CustomErrorHandler().handleErrorResponse(ERROR_MSG);
	}

	private ExchangeFilterFunction requestExchangeFilterFunction() {
		return new CustomWebClientFilter().loggRequest();
	}

	private ExchangeFilterFunction responseExchangeFilterFunction() {
		return new CustomWebClientFilter().loggResponse();
	}

}
