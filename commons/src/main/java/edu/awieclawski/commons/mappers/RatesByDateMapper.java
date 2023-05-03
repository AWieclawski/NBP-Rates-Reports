package edu.awieclawski.commons.mappers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.awieclawski.commons.dtos.data.RatesByDateDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RatesByDateMapper {

	public static List<RatesByDateDto> mapTo(List<ExchangeRateDto> rates) {
		Set<LocalDate> dates = new HashSet<>(); // must be set to avoid duplicates
		List<RatesByDateDto> ratesByDate = new ArrayList<>();
		RatesByDateDto header = null;
		int dateCounter = 0;

		for (ExchangeRateDto rate : rates) {
			dates.add(rate.getPublished());

			if (dateCounter < dates.size()) {
				header = RatesByDateDto.builder()
						.nbpTable(rate.getNbpTable())
						.published(rate.getPublished())
						.rates(new ArrayList<>())
						.build();
				ratesByDate.add(header);
			}

			if (header != null) {
				header.getRates().add(rate);
			}

			dateCounter = dates.size();
		}

		return ratesByDate;
	}

}
