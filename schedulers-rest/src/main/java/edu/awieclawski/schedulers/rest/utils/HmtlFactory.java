package edu.awieclawski.schedulers.rest.utils;

import java.util.List;

import edu.awieclawski.htmltopdf.dtos.DocumentDto;
import edu.awieclawski.models.entities.ExchangeRate;

public interface HmtlFactory {

	DocumentDto getDocumentByRates(List<ExchangeRate> rates);

	default StringBuffer wrapInCell(String input) {
		return new StringBuffer().append("<td>").append(input).append("</td>");
	}

	default StringBuffer wrapMultiColHeader(String input, int colspan) {
		return new StringBuffer().append("<tr><th colspan=\"" + colspan + "\">Published at: ").append(input)
				.append("</th></tr>");
	}

	default StringBuffer wrapMultiColCell(String input, int colspan) {
		return new StringBuffer().append("<tr><td colspan=\"" + colspan + "\">Published at: ").append(input)
				.append("</td></tr>");
	}

	default StringBuffer wrapInHeaderCell(String input) {
		return new StringBuffer().append("<th>").append(input).append("</th>");
	}

	default StringBuffer wrapInRow(StringBuffer input) {
		return new StringBuffer().append("<tr>").append(input).append("</tr>");
	}

	default StringBuffer wrapInTable(StringBuffer input) {
		return new StringBuffer().append("<table>").append(input).append("</table>");
	}

}
