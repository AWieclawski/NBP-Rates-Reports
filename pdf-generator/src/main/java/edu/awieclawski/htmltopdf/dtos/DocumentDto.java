package edu.awieclawski.htmltopdf.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDto {
	String footerText;
	String htmlContent;

	public void concatToHtml(String input) {
		this.htmlContent = getHtmlContent() != null ? getHtmlContent().concat(input) : input;
	}
}
