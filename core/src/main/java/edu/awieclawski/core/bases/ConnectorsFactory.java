package edu.awieclawski.core.bases;

import java.time.LocalDate;

import edu.awieclawski.webclients.dtos.DataResponseDto;
import edu.awieclawski.webclients.services.NbpIntegrationService;

public class ConnectorsFactory {

	// ATypeRateByDateAndSymbol
	protected DataResponseDto doConnecATypeImpl(NbpIntegrationService reactService, LocalDate date, String currSymb,
			int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, date, currSymb) {

			@Override
			protected DataResponseDto execute() {
				return reactService.getATypeRateByDateAndSymbol(date, currSymb);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// ATypeRatesByDateRangeAndSymbol
	protected DataResponseDto doConnectATypeImpl(NbpIntegrationService reactService, LocalDate startDate,
			LocalDate endDate, String currSymb, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, startDate, endDate, currSymb) {

			@Override
			protected DataResponseDto execute() {
				return reactService.getATypeRatesByDatesRangeAndSymbol(startDate, endDate, currSymb);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}


	// CTypeRateByDateAndSymbol
	protected DataResponseDto doConnecCTypeImpl(NbpIntegrationService reactService, LocalDate date, String currSymb,
			int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, date, currSymb) {

			@Override
			protected DataResponseDto execute() {
				return reactService.getCTypeRateByDateAndSymbol(date, currSymb);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// CTypeRatesByDateRangeAndSymbol
	protected DataResponseDto doConnectCTypeImpl(NbpIntegrationService reactService, LocalDate startDate,
			LocalDate endDate, String currSymb, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, startDate, endDate, currSymb) {

			@Override
			protected DataResponseDto execute() {
				return reactService.getCTypeRatesByDatesRangeAndSymbol(startDate, endDate, currSymb);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// ATypeRatesTableByDate
	protected DataResponseDto doConnectATypeImpl(NbpIntegrationService reactService, LocalDate date, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, date) {

			@Override
			protected DataResponseDto execute() {
				return reactService.getATypeTableByDate(date);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// ATypeRateTableByDatesRange
	protected DataResponseDto doConnectATypeImpl(NbpIntegrationService reactService, LocalDate startDate,
			LocalDate endDate, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, startDate, endDate) {

			@Override
			protected DataResponseDto execute() {
				return reactService.getATypeTableByDatesRange(startDate, endDate);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// BTypeRatesTableByDate
	protected DataResponseDto doConnectBTypeImpl(NbpIntegrationService reactService, LocalDate date, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, date) {

			@Override
			protected DataResponseDto execute() {
				return reactService.getBTypeTableByDate(date);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// BTypeRateTableByDatesRange
	protected DataResponseDto doConnectBTypeImpl(NbpIntegrationService reactService, LocalDate startDate,
			LocalDate endDate, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, startDate, endDate) {

			@Override
			protected DataResponseDto execute() {
				return reactService.getBTypeTableByDatesRange(startDate, endDate);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// CTypeRatesTableByDate
	protected DataResponseDto doConnectCTypeImpl(NbpIntegrationService reactService, LocalDate date, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, date) {

			@Override
			protected DataResponseDto execute() {
				return reactService.getCTypeTableByDate(date);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// CTypeRateTableByDatesRange
	protected DataResponseDto doConnectCTypeImpl(NbpIntegrationService reactService, LocalDate startDate,
			LocalDate endDate, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, startDate, endDate) {

			@Override
			protected DataResponseDto execute() {
				return reactService.getCTypeTableByDatesRange(startDate, endDate);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	/**
	 * private class to extend BaseConnnector and establish different parameterized
	 * connections
	 * 
	 * @author awieclawski
	 *
	 */
	private class ConnectorImpl extends BaseConnnector {

		private ConnectorImpl(NbpIntegrationService reactService, LocalDate date, String currSymb) {
			// the constructor arguments are used only in overridden above method run
		}

		private ConnectorImpl(NbpIntegrationService reactService, LocalDate startDate, LocalDate endDate,
				String currSymb) {
			// the constructor arguments are used only in overridden above method run
		}

		private ConnectorImpl(NbpIntegrationService reactService, LocalDate date) {
			// the constructor arguments are used only in overridden above method run
		}

		private ConnectorImpl(NbpIntegrationService reactService, LocalDate startDate, LocalDate endDate) {
			// the constructor arguments are used only in overridden above method run
		}

		@Override
		protected DataResponseDto execute() {
			throw new UnsupportedOperationException("Method must be overrided during instance creation!");
		}

	}

}
