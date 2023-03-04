package edu.awieclawski.commons.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("endPointElements")
public class NbpEndPointElements {

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

}
