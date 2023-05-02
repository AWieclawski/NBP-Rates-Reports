package edu.awieclawski.html.service;

import edu.awieclawski.commons.dtos.data.RatesReportDto;

public interface HtmlProducer {

	String askBidTableBuilder(RatesReportDto report);

	String midTableBuilder(RatesReportDto report);

}
