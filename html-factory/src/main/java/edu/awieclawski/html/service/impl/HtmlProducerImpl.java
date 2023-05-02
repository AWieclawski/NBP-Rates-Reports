package edu.awieclawski.html.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import edu.awieclawski.commons.dtos.data.RatesReportDto;
import edu.awieclawski.html.service.HtmlProducer;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HtmlProducerImpl implements HtmlProducer {

	@Override
	public String askBidTableBuilder(RatesReportDto report) {
		return outputBuilder(report, "ratesAskBid.ftlh", "Rates mid-table ");
	}

	@Override
	public String midTableBuilder(RatesReportDto report) {
		return outputBuilder(report, "ratesMid.ftlh", "Rates ask-bid-table ");
	}

	private String outputBuilder(RatesReportDto report, String templateName, String errMsg) {
		FreeMarkerConfigurer freeMarkerConfigurer;
		String defaultHtml = "<p>No report data found!</p>";
		Configuration cfg;
		Map<String, Object> input = new HashMap<>();
		input.put("report", report);
		Template template = null;
		freeMarkerConfigurer = getFreeMarkerConfigurer();

		try {
			cfg = freeMarkerConfigurer.getConfiguration();
			template = cfg.getTemplate(templateName);
		} catch (IOException e) {
			log.error("{} template building error: {}", errMsg, e.getMessage());
		}

		Writer stringWriter = new StringWriter();

		if (template != null) {
			try {
				template.process(input, stringWriter);
			} catch (TemplateException | IOException e) {
				log.error("{} string writer building error: {}", errMsg, e.getMessage());
			} finally {
				try {
					stringWriter.close();
				} catch (IOException e) {
					log.error("{} close string writer building error: {}", errMsg, e.getMessage());
				}
			}
		}

		return stringWriter.toString().length() > 0 ? stringWriter.toString() : defaultHtml;
	}

	/**
	 * https://freemarker.apache.org/docs/api/freemarker/template/Configuration.html
	 * 
	 * @return
	 */
	protected static FreeMarkerConfigurer getFreeMarkerConfigurer() {
		Properties freemarkerSettings = new Properties();
		freemarkerSettings.put("template_update_delay", "1");
		freemarkerSettings.put("defaultEncoding", "UTF-8");
		freemarkerSettings.put("template_exception_handler", "rethrow");
		freemarkerSettings.put("number_format", "#,###,##0.0000");
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		configurer.setTemplateLoaderPath("classpath:/templates");
		configurer.setFreemarkerSettings(freemarkerSettings);
		try {
			configurer.afterPropertiesSet();
		} catch (TemplateException | IOException e) {
			log.error("FreeMarkerConfigurer building error: {}", e.getMessage());
		}
		return configurer;
	}

}
