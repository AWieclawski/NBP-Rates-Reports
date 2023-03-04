package edu.awieclawski.webclients.services.implemented;

import java.net.URI;
import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import edu.awieclawski.commons.beans.NbpEndPointElements;
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
@DependsOn("endPointElements")
public class NbpReactServiceImpl implements NbpReactService {
	private static final String PARAM = "format";
	private final WebClient webClient;
	private final PathGenerator pathUtil;
	private NbpEndPointElements endPoints;

	private String nbpApiUrl;
	private MultiValueMap<String, String> reqParams;
	// NBP end-point elements
	private String aRates;
	private String cRates;
	private String aTableRate;
	private String bTableRate;
	private String cTableRate;

//	@Value("${nbp-api.format}")
	private String dataFormat;

	public NbpReactServiceImpl(
			NbpEndPointElements endPoints,
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
	 * NBP end-point elements bean initialize
	 */
	@PostConstruct
	private void endPointsInit() {
		this.aRates = endPoints.ratesA;
		this.cRates = endPoints.ratesC;
		this.aTableRate = endPoints.aTableRate;
		this.bTableRate = endPoints.bTableRate;
		this.cTableRate = endPoints.cTableRate;
		this.dataFormat = endPoints.dataFormat;
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
	public DataResponseDto getATypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol) {
//		final MultiValueMap<String, String> params = pathUtil.setParamsMap(PARAM, dataFormat);
		final URI uriBuilt = pathUtil.completeUri(aRates, currencySymbol, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataResponseDto.builder()
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
	public DataResponseDto getATypeRatesByDatesRangeAndSymbol(LocalDate startDate, LocalDate endDate,
			String currencySymbol) {
//		final MultiValueMap<String, String> params = pathUtil.setParamsMap(PARAM, dataFormat);
		final URI uriBuilt = pathUtil.completeUri(aRates, currencySymbol, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataResponseDto.builder()
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
	public DataResponseDto getCTypeRateByDateAndSymbol(LocalDate publicationDate, String currencySymbol) {
		final URI uriBuilt = pathUtil.completeUri(cRates, currencySymbol, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataResponseDto.builder()
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
	public DataResponseDto getCTypeRatesByDatesRangeAndSymbol(LocalDate startDate, LocalDate endDate,
			String currencySymbol) {
		final URI uriBuilt = pathUtil.completeUri(cRates, currencySymbol, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataResponseDto.builder()
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
	public DataResponseDto getATypeTableByDate(LocalDate publicationDate) {
//		final MultiValueMap<String, String> params = pathUtil.setParamsMap(PARAM, dataFormat);
		final URI uriBuilt = pathUtil.completeUri(aTableRate, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataResponseDto.builder()
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
	public DataResponseDto getATypeTableByDatesRange(LocalDate startDate, LocalDate endDate) {
//		final MultiValueMap<String, String> params = pathUtil.setParamsMap(PARAM, dataFormat);
		final URI uriBuilt = pathUtil.completeUri(aTableRate, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataResponseDto.builder()
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
	public DataResponseDto getCTypeTableByDate(LocalDate publicationDate) {
		final URI uriBuilt = pathUtil.completeUri(cTableRate, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataResponseDto.builder()
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
	public DataResponseDto getCTypeTableByDatesRange(LocalDate startDate, LocalDate endDate) {
		final URI uriBuilt = pathUtil.completeUri(cTableRate, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataResponseDto.builder()
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
	public DataResponseDto getBTypeTableByDate(LocalDate publicationDate) {
		final URI uriBuilt = pathUtil.completeUri(bTableRate, publicationDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataResponseDto.builder()
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
	public DataResponseDto getBTypeTableByDatesRange(LocalDate startDate, LocalDate endDate) {
		final URI uriBuilt = pathUtil.completeUri(bTableRate, startDate, endDate);
		String targetUrl = nbpApiUrl + pathUtil.buildFinalUri(reqParams, uriBuilt).toString();

		return DataResponseDto.builder()
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

}
