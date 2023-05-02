package edu.awieclawski.commons.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("nbpConnectionElements")
public class NbpConnectionElements {

	@Value("${nbp-api.host}")
	public String baseUrl;

	@Value("${nbp-api.ratesa}")
	public String ratesA;

	@Value("${nbp-api.ratesc}")
	public String ratesC;

	@Value("${nbp-api.atablerate}")
	public String aTableRate;

	@Value("${nbp-api.btablerate}")
	public String bTableRate;

	@Value("${nbp-api.ctablerate}")
	public String cTableRate;

	@Value("${nbp-api.format}")
	public String dataFormat;

	@Value("${nbp-api.attempts}")
	public Integer maxCount;

	@Value("${nbp-api.timeout.connect}")
	public Integer timeout;

	@Value("${nbp-api.timeout.read}")
	public Integer timeRead;

	@Value("${nbp-api.timeout.write}")
	public Integer timeWrite;

	@Value("${rest.api.port}")
	public Integer portNumber;

}
