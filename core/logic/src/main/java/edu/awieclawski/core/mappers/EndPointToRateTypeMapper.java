package edu.awieclawski.core.mappers;

import java.util.Objects;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Component;

import edu.awieclawski.commons.beans.NbpConnectionElements;
import edu.awieclawski.models.entities.ExchangeRate;
import edu.awieclawski.models.entities.ExchangeRateTypeA;
import edu.awieclawski.models.entities.ExchangeRateTypeB;
import edu.awieclawski.models.entities.ExchangeRateTypeC;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EndPointToRateTypeMapper {

	private final NbpConnectionElements endPoints;

	@SuppressWarnings("unchecked")
	public <T extends ExchangeRate> Class<T> map(String endPoint) {

		if (nullCheck(endPoint)) {
			return null;
		}

		if (Objects.equals(endPoints.ratesA, endPoint) || Objects.equals(endPoints.aTableRate, endPoint)) {
			return (Class<T>) ExchangeRateTypeA.class;
		} else if (Objects.equals(endPoints.ratesC, endPoint) || Objects.equals(endPoints.cTableRate, endPoint)) {
			return (Class<T>) ExchangeRateTypeC.class;
		} else if (Objects.equals(endPoints.bTableRate, endPoint)) {
			return (Class<T>) ExchangeRateTypeB.class;
		}
		throw new EntityNotFoundException(
		"Mapped end-point " + endPoint + " does not fit the method!");
	}

	private static boolean nullCheck(Object o) {
		return Objects.isNull(o);
	}

}
