package edu.awieclawski.core.bases;

import java.time.LocalDate;

import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.core.facades.BaseFacade;
import edu.awieclawski.webclients.services.NbpIntegrationService;

public class ConnectorsFactory extends BaseFacade{

	// ATypeRateByDateAndSymbol
	protected DataPackageDto doConnecATypeImpl(NbpIntegrationService reactService, LocalDate date, String currSymb,
			int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, date, currSymb) {

			@Override
			protected DataPackageDto execute() {
				return reactService.getATypeRateByDateAndSymbol(date, currSymb);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// ATypeRatesByDateRangeAndSymbol
	protected DataPackageDto doConnectATypeImpl(NbpIntegrationService reactService, LocalDate startDate,
			LocalDate endDate, String currSymb, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, startDate, endDate, currSymb) {

			@Override
			protected DataPackageDto execute() {
				return reactService.getATypeRatesByDatesRangeAndSymbol(startDate, endDate, currSymb);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// CTypeRateByDateAndSymbol
	protected DataPackageDto doConnecCTypeImpl(NbpIntegrationService reactService, LocalDate date, String currSymb,
			int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, date, currSymb) {

			@Override
			protected DataPackageDto execute() {
				return reactService.getCTypeRateByDateAndSymbol(date, currSymb);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// CTypeRatesByDateRangeAndSymbol
	protected DataPackageDto doConnectCTypeImpl(NbpIntegrationService reactService, LocalDate startDate,
			LocalDate endDate, String currSymb, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, startDate, endDate, currSymb) {

			@Override
			protected DataPackageDto execute() {
				return reactService.getCTypeRatesByDatesRangeAndSymbol(startDate, endDate, currSymb);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// ATypeRatesTableByDate
	protected DataPackageDto doConnectATypeImpl(NbpIntegrationService reactService, LocalDate date, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, date) {

			@Override
			protected DataPackageDto execute() {
				return reactService.getATypeTableByDate(date);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// ATypeRateTableByDatesRange
	protected DataPackageDto doConnectATypeImpl(NbpIntegrationService reactService, LocalDate startDate,
			LocalDate endDate, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, startDate, endDate) {

			@Override
			protected DataPackageDto execute() {
				return reactService.getATypeTableByDatesRange(startDate, endDate);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// BTypeRatesTableByDate
	protected DataPackageDto doConnectBTypeImpl(NbpIntegrationService reactService, LocalDate date, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, date) {

			@Override
			protected DataPackageDto execute() {
				return reactService.getBTypeTableByDate(date);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// BTypeRateTableByDatesRange
	protected DataPackageDto doConnectBTypeImpl(NbpIntegrationService reactService, LocalDate startDate,
			LocalDate endDate, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, startDate, endDate) {

			@Override
			protected DataPackageDto execute() {
				return reactService.getBTypeTableByDatesRange(startDate, endDate);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// CTypeRatesTableByDate
	protected DataPackageDto doConnectCTypeImpl(NbpIntegrationService reactService, LocalDate date, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, date) {

			@Override
			protected DataPackageDto execute() {
				return reactService.getCTypeTableByDate(date);
			}
		};
		conn.run(count);

		return conn.getResponseDto();
	}

	// CTypeRateTableByDatesRange
	protected DataPackageDto doConnectCTypeImpl(NbpIntegrationService reactService, LocalDate startDate,
			LocalDate endDate, int count) {

		ConnectorImpl conn = new ConnectorImpl(reactService, startDate, endDate) {

			@Override
			protected DataPackageDto execute() {
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
		protected DataPackageDto execute() {
			throw new UnsupportedOperationException("Method must be overrided during instance creation!");
		}

	}

}
