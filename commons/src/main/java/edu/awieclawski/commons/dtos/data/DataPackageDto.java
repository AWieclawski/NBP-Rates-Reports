package edu.awieclawski.commons.dtos.data;

import java.time.LocalDate;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = { "id", "url", "endPoint", "createdAt" })
public class DataPackageDto {
	private Long id;
	private String jsonData;
	private String url;
	private String endPoint;
	private Boolean processed;
	private Boolean converted;
	private LocalDate createdAt;

	public String getInfo() {
		return "DataPackage [id=" + id + ", url=" + url + "]";
	}

}
