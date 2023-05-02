package edu.awieclawski.webclients.utils;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PathGenerator {
	public final static String EXC_MSG = "UriComponentsBuilder exception {} thrown.";
	public final static String ERR_MSG = "Path generator error!";

	@Value("${nbp-api.date-pattern}")
	private String DATE_PATTERN;

	private DateTimeFormatter DATE_TIME_FORMATTER;
	
	public DateTimeFormatter getDateFormat() {
	return DATE_TIME_FORMATTER;
	}

	@PostConstruct
	private void initDateFormatter() {
		DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	}

	public URI buildFinalUri(MultiValueMap<String, String> params, URI fullUri) {
		URI uri;
		try {
			uri = UriComponentsBuilder.fromUri(fullUri)
					.queryParams(params).build().toUri();
		} catch (Exception i) {
			log.error(EXC_MSG, i.getClass(), i);
			throw new RuntimeException(ERR_MSG);
		}

		return uri;
	}
	
	public URI completeUri(String endPoint, String currencySymbol, LocalDate publicationDate) {
		URI uri;
		try {
			uri = UriComponentsBuilder
					.fromPath(endPoint)
					.pathSegment(currencySymbol.toLowerCase())
					.pathSegment(publicationDate.format(Objects.requireNonNull(DATE_TIME_FORMATTER)))
					.build().toUri();
		} catch (Exception i) {
			log.error(EXC_MSG, i.getClass(), i);
			throw new RuntimeException(ERR_MSG);
		}
		
		return uri;
	}

	public URI completeUri(String endPoint, LocalDate publicationDate) {
		URI uri;
		try {
			uri = UriComponentsBuilder
					.fromPath(endPoint)
					.pathSegment(publicationDate.format(Objects.requireNonNull(DATE_TIME_FORMATTER)))
					.build().toUri();
		} catch (Exception i) {
			log.error(EXC_MSG, i.getClass(), i);
			throw new RuntimeException(ERR_MSG);
		}

		return uri;
	}
	
	public URI completeUri(String endPoint, String currencySymbol, LocalDate startDate, LocalDate endDate) {
		URI uri;
		try {
			uri = UriComponentsBuilder
					.fromPath(endPoint)
					.pathSegment(currencySymbol.toLowerCase())
					.pathSegment(startDate.format(Objects.requireNonNull(DATE_TIME_FORMATTER)))
					.pathSegment(endDate.format(Objects.requireNonNull(DATE_TIME_FORMATTER)))
					.build().toUri();
		} catch (Exception i) {
			log.error(EXC_MSG, i.getClass(), i);
			throw new RuntimeException(ERR_MSG);
		}
		
		return uri;
	}
	
	public URI completeUri(String endPoint, LocalDate startDate, LocalDate endDate) {
		URI uri;
		try {
			uri = UriComponentsBuilder
					.fromPath(endPoint)
					.pathSegment(startDate.format(Objects.requireNonNull(DATE_TIME_FORMATTER)))
					.pathSegment(endDate.format(Objects.requireNonNull(DATE_TIME_FORMATTER)))
					.build().toUri();
		} catch (Exception i) {
			log.error(EXC_MSG, i.getClass(), i);
			throw new RuntimeException(ERR_MSG);
		}
		
		return uri;
	}

	public MultiValueMap<String, String> setParamsMap(String key, String value) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.setAll(Map.of(key, value));

		return params;
	}

	public MultiValueMap<String, String> addMapToParams(MultiValueMap<String, String> params, Map<String, String> map) {
		params.setAll(map);

		return params;
	}

}
