package edu.awieclawski.webclients.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class DataResponseDto {
	private String jsonData;
	private String url;
	private String endPoint;

	public DataResponseDto(String jsonData, String url, String endPoint) {
		this.jsonData = jsonData;
		this.url = url;
		this.endPoint = endPoint;
	}

}
