package edu.awieclawski.schedulers.validators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import edu.awieclawski.htmltopdf.dtos.ReportDto;
import edu.awieclawski.schedulers.exceptions.ReportValidationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReportDtoValidator {

	private List<String> errors;
	private List<String> warns;

	public void validate(ReportDto dto) {
		init();
		validateDto(dto);
		validateDates(dto);
		validateCurrencies(dto);

		if (!errors.isEmpty()) {
			throw new ReportValidationException(errors);
		}

		if (!warns.isEmpty()) {
			log.warn("Report Validator warning: {}", listToMessage(warns));
		}

	}

	private void init() {
		errors = new ArrayList<>();
		warns = new ArrayList<>();
	}

	private void validateDto(ReportDto dto) {

		if (Objects.isNull(dto)) {
			errors.add("Validation failed! Report data object should not be null. ");
		}
	}

	private void validateDates(ReportDto dto) {
		dto.setValidToStart(validFuture(dto.getValidToStart()));
		dto.setValidToEnd(validFuture(dto.getValidToEnd()));

		if (Objects.nonNull(dto.getValidToStart())) {

			if (Objects.isNull(dto.getValidToEnd())) {
				dto.setValidToEnd(dto.getValidToStart());
				warns.add("ValidToEnd should not be null. Override by ValidToStart.");
			} else if (dto.getValidToEnd().isBefore(dto.getValidToStart())) {
				LocalDate tmpDate = dto.getValidToEnd();
				dto.setValidToEnd(dto.getValidToStart());
				dto.setValidToStart(tmpDate);
				warns.add("ValidToEnd should not be before ValidToStart. Dates replaced. ");
			}
		} else if (Objects.nonNull(dto.getValidToEnd())) {

			if (Objects.isNull(dto.getValidToStart())) {
				dto.setValidToStart(dto.getValidToEnd());
				warns.add("ValidToStart should not be null. Override by ValidToEnd.");
			} else if (dto.getValidToStart().isAfter(dto.getValidToEnd())) {
				LocalDate tmpDate = dto.getValidToStart();
				dto.setValidToStart(dto.getValidToEnd());
				dto.setValidToEnd(tmpDate);
				warns.add("ValidToStart should not be after ValidToEnd. Dates replaced. ");
			}
		} else if (Objects.isNull(dto.getValidToStart()) && Objects.isNull(dto.getValidToEnd())) {
			errors.add("Validation failed! Both dates should not be null.");
		}

	}

	private void validateCurrencies(ReportDto dto) {

		if (Objects.nonNull(dto) && CollectionUtils.isEmpty(dto.getCurrencies())) {
			warns.add("Currencies list is null or empty. All available Currencies will be presented.");
		} else if (Objects.nonNull(dto) && !dto.getCurrencies().isEmpty()) {
			List<String> currencies = dto.getCurrencies();
			List<String> toRemove = new ArrayList<>();
			currencies.stream().filter(Objects::nonNull).forEach(c -> {
				if (c.length() > 3) {
					warns.add("The request list contains symbol with length bigger than 3! [" + c
							+ "] will be removed from the request list. ");
					toRemove.add(c);
				}
			});
			currencies.stream().filter(Objects::nonNull).forEach(c -> {
				if (!c.matches("[a-zA-Z]+")) {
					warns.add("The request list contains symbol consisting not only letters! [" + c
							+ "] will be removed from the request list. ");
					toRemove.add(c);
				}
			});
			currencies.removeAll(toRemove);
			dto.setCurrencies(currencies);
		}
	}

	private LocalDate validFuture(LocalDate date) {

		if (date == null) {
			return date;
		}

		if (date.isAfter(LocalDate.now().minusDays(1L))) {
			warns.add("Future date " + date + " changed to yesterday.");
		}

		return date.isBefore(LocalDate.now()) ? date : LocalDate.now().minusDays(1L);
	}

	private static String listToMessage(List<String> messages) {
		return String.join(" | ", messages);
	}

}
