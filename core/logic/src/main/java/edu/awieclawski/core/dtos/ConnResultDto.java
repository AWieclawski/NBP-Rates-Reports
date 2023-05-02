package edu.awieclawski.core.dtos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.commons.dtos.enums.NbpType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnResultDto {

	private LocalDate startDate;
	private LocalDate endDate;
	private String currSymb;
	private int counter;

	private String endPoint;
	private List<DataPackageDto> packages;
	private NbpType nbpType;
	private List<String> codes;
	private Boolean succeed;

	public void addPackages(List<DataPackageDto> packages) {
		getPackages().addAll(packages);
	}

	public void addCodes(List<String> codes) {
		getCodes().addAll(codes);
	}

	public List<String> getCodes() {
		if (this.codes == null) {
			this.codes = new ArrayList<>();
		}
		return codes;
	}

	public List<DataPackageDto> getPackages() {
		if (this.packages == null) {
			this.packages = new ArrayList<>();
		}
		return packages;
	}

	public boolean getSucceed() {
		if (this.succeed == null) {
			this.succeed = false;
		}
		return succeed;
	}

}
