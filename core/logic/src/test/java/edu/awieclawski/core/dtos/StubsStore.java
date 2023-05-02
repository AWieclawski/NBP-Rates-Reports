package edu.awieclawski.core.dtos;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;

public class StubsStore {

	public static List<CurrencyDto> noIdCurrencyDuplicates() {
		CurrencyDto currOne = CurrencyDto.builder().code("ABC").build(); // Doubled
		CurrencyDto currTwo = CurrencyDto.builder().code("BCD").build();
		CurrencyDto currThree = CurrencyDto.builder().code("CDE").build(); // Doubled
		CurrencyDto currFour = CurrencyDto.builder().code("DRF").build();
		CurrencyDto currFive = CurrencyDto.builder().code("ABC").build();
		CurrencyDto currSix = CurrencyDto.builder().code("CDE").build();
		// 4 unique elements
		List<CurrencyDto> list =
				new LinkedList<>(Arrays.asList(currOne, currTwo, currThree, currFour, currFive, currSix));

		return list;
	}

	public static List<DataPackageDto> noIdDataPackageDuplicates() {
		DataPackageDto currOne = DataPackageDto.builder().url("ABC").endPoint("ABCe").build(); // Doubled
		DataPackageDto currTwo = DataPackageDto.builder().url("BCD").endPoint("BCDe").build();
		DataPackageDto currThree = DataPackageDto.builder().url("CDE").endPoint("CDEe").build(); // Doubled
		DataPackageDto currFour = DataPackageDto.builder().url("DRF").endPoint("DRFe").build();
		DataPackageDto currFive = DataPackageDto.builder().url("ABC").endPoint("ABCe").build();
		DataPackageDto currSix = DataPackageDto.builder().url("CDE").endPoint("CDEe").build();
		// 4 unique elements
		List<DataPackageDto> list =
				new LinkedList<>(Arrays.asList(currOne, currTwo, currThree, currFour, currFive, currSix));

		return list;
	}

	public static List<ExchangeRateTypeADto> noIdExchangeRateDuplicates() {
		// 4 unique elements
		ExchangeRateTypeADto currOne = ExchangeRateTypeADto.builder().nbpTable("ABC")
				.currency(CurrencyDto.builder().code("ABC").build()).build(); // Doubled
		ExchangeRateTypeADto currTwo = ExchangeRateTypeADto.builder().nbpTable("BCD")
				.currency(CurrencyDto.builder().code("ABC").build()).build();
		ExchangeRateTypeADto currThree = ExchangeRateTypeADto.builder().nbpTable("CDE")
				.currency(CurrencyDto.builder().code("ABC").build()).build(); // Doubled
		ExchangeRateTypeADto currFour = ExchangeRateTypeADto.builder().nbpTable("DRF")
				.currency(CurrencyDto.builder().code("ABC").build()).build();
		ExchangeRateTypeADto currFive = ExchangeRateTypeADto.builder().nbpTable("ABC")
				.currency(CurrencyDto.builder().code("ABC").build()).build();
		ExchangeRateTypeADto currSix = ExchangeRateTypeADto.builder().nbpTable("CDE")
				.currency(CurrencyDto.builder().code("ABC").build()).build();
		List<ExchangeRateTypeADto> list =
				new LinkedList<>(Arrays.asList(currOne, currTwo, currThree, currFour, currFive, currSix));

		return list;
	}

}
