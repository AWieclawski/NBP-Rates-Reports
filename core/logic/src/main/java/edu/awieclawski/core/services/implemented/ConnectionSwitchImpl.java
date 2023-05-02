package edu.awieclawski.core.services.implemented;

import java.time.LocalDate;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import edu.awieclawski.commons.beans.NbpConnectionElements;
import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.converters.exceptions.ConverterException;
import edu.awieclawski.core.dtos.ConnResultDto;
import edu.awieclawski.core.services.ConnectionSwitch;
import edu.awieclawski.core.services.NbpDataPackageService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@DependsOn("nbpConnectionElements")
public class ConnectionSwitchImpl implements ConnectionSwitch {

	private final NbpDataPackageService dataService;
	private final NbpConnectionElements endPoints;

	@Override
	public ConnResultDto getByEndPoint(ConnResultDto connDto) {
		Pair<Boolean, DataPackageDto> pair = Pair.of(false, DataPackageDto.builder().build());
		String currencyCode = connDto.getCurrSymb();
		LocalDate endDate = connDto.getEndDate();
		LocalDate startDate = connDto.getStartDate();
		String endPoint = connDto.getEndPoint();

		if (Objects.equals(endPoints.aTableRate, endPoint) && endDate == null) {
			pair = dataService
					.getATypeTableByDateAndSave(ConnResultDto.builder().startDate(startDate).counter(0).build());
		} else if (Objects.equals(endPoints.ratesA, endPoint) && endDate == null) {
			pair = dataService.getATypeRateByDateAndSymbolAndSave(
					ConnResultDto.builder().startDate(startDate).currSymb(currencyCode).counter(0).build());
		} else if (Objects.equals(endPoints.aTableRate, endPoint) && endDate != null) {
			pair = dataService.getATypeTableByDateRangeAndSave(
					ConnResultDto.builder().startDate(startDate).endDate(endDate).counter(0).build());
		} else if (Objects.equals(endPoints.ratesA, endPoint) && endDate != null) {
			pair = dataService.getATypeRatesByDateRangeAndSymbolAndSave(
					ConnResultDto.builder().startDate(startDate).endDate(endDate).currSymb(currencyCode).counter(0)
							.build());
		} else if (Objects.equals(endPoints.bTableRate, endPoint) && endDate == null) {
			pair = dataService
					.getBTypeTableByDateAndSave(ConnResultDto.builder().startDate(startDate).counter(0).build());
		} else if (Objects.equals(endPoints.bTableRate, endPoint) && endDate != null) {
			pair = dataService.getBTypeTableByDateRangeAndSave(
					ConnResultDto.builder().startDate(startDate).endDate(endDate).counter(0).build());
		} else if (Objects.equals(endPoints.cTableRate, endPoint) && endDate == null) {
			pair = dataService
					.getCTypeTableByDateAndSave(ConnResultDto.builder().startDate(startDate).counter(0).build());
		} else if (Objects.equals(endPoints.ratesC, endPoint) && endDate == null) {
			pair = dataService.getCTypeRateByDateAndSymbolAndSave(
					ConnResultDto.builder().startDate(startDate).currSymb(currencyCode).counter(0).build());
		} else if (Objects.equals(endPoints.cTableRate, endPoint) && endDate != null) {
			pair = dataService.getCTypeTableByDateRangeAndSave(
					ConnResultDto.builder().startDate(startDate).endDate(endDate).counter(0).build());
		} else if (Objects.equals(endPoints.ratesC, endPoint) && endDate != null) {
			pair = dataService.getCTypeRatesByDateRangeAndSymbolAndSave(
					ConnResultDto.builder().startDate(startDate).endDate(endDate).currSymb(currencyCode).counter(0)
							.build());
		}

		else {
			throw new ConverterException("Ralated NBP end-point not supported: " + endPoint);
		}

		if (pair != null) {
			connDto.setSucceed(pair.getKey());

			if (pair.getValue() != null) {
				connDto.getPackages().add(pair.getValue());
			}
		}

		return connDto;
	}

}
