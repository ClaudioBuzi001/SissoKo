/*
 * (c) Copyright Leonardo Company S.p.A.. All rights reserved.
 *
 * Any right of industrial and intellectual property on this document,
 * and of technical Know-how herein contained, belongs to
 * Leonardo Company S.p.A. and/or third parties.
 * According to the law, it is forbidden to disclose, reproduce or however
 * use this document and any data herein contained for any use without
 * previous written authorization by Leonardo Company S.p.A.
 *
 */
package processorNew;

import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

import analyzer.AnalyzerLabel;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import auxiliary.track.TrackAggregatorMap;
import auxiliary.track.TrackAliasMap;
import auxiliary.track.TrackBlackMap;
import auxiliary.track.TrackInputConstants;
import auxiliary.track.TrackOutputConstants;
import common.CommonConstants;
import common.Utils;

/**
 * The Class TrackProcessor.
 */
public enum TrackProcessor {
	;

	/** The Constant trackBlackMap. */
	private static final TrackBlackMap trackBlackMap = new TrackBlackMap();

	/** The Constant trackAliasMap. */
	private static final TrackAliasMap trackAliasMap = new TrackAliasMap();

	/** The Constant trackAggregatorMap. */
	public static final TrackAggregatorMap trackAggregatorMap = new TrackAggregatorMap();

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(AnalyzerLabel.class);

	/**
	 * To xml.
	 *
	 * @param track      the track
	 * @param objectNode the object node
	 */
	private static void toXml(final IRawData track, final HeaderNode objectNode) {
		final String loggerCaller = "toXml()";
		try {
			final var flight = Utils_LIS.getFlightFromTrkId(track.getId());
			AnalyzerLabel.analyze(trackBlackMap, trackAggregatorMap, trackAliasMap, track, objectNode);

			addPositionSymbol(track, objectNode);

			addOverSymbol(track, flight, objectNode);

			Utils.addTemplate(Optional.of(track), flight, objectNode);
			final String id = Utils.getTrackId(track.getId());
			String stn = track.getSafeString(CommonConstants.STN);
			objectNode.addLine("ISCTO", Utils_LIS.isScaCto(stn));
			objectNode.addLine("ISCTL", Utils_LIS.isScaCtl(stn));
			objectNode.addLine(CommonConstants.ID, id);

			final var totalMap = Utils.getAllStnConflict(stn);
			// update dei conflitti TCT
			// per non staccare la traccia dalla linea che la collega al primo punto del conflitto
			for (final String jsTCT : totalMap.keySet()) {
				final IRawData rawdataTct = totalMap.get(jsTCT);
				TctProcessor.process(rawdataTct);
			}
		} catch (final Exception e) {
			logger.logError(loggerCaller, e);
		}
	}

	/**
	 * Adds the position symbol.
	 *
	 * @param track      the track
	 * @param objectNode the object node
	 */
	public static void addPositionSymbol(final IRawData track, final HeaderNode objectNode) {
		String data = "";
		String color = "";

		if (track.getSafeInt(TrackInputConstants.ECAT) == TrackInputConstants.ECAT_VEHIC && Utils.isVisVehicle()) {
			data = TrackOutputConstants.ECAT_VEHIC;
		} else if (track.getSafeInt(TrackInputConstants.ECAT) == TrackInputConstants.ECAT_SUBORBIT) {
			data = TrackOutputConstants.ECAT_SUBORB;
		} else if (track.getSafeInt(TrackInputConstants.ECAT) == TrackInputConstants.ECAT_DRONE) {
			data = (TrackOutputConstants.ECAT);
		} else if (track.getSafeBoolean(TrackInputConstants.IS_SMR_TRACKER)) {
			data = (TrackOutputConstants.SMR);
		} else if (track.getSafeInt(TrackInputConstants.PSC) == TrackInputConstants.PSC_COMB) {
			data = (TrackOutputConstants.PSC_COMB);
		} else if (track.getSafeInt(TrackInputConstants.PSC) == TrackInputConstants.PSC_SSR) {
			data = (TrackOutputConstants.PSC_SSR);
		} else if (track.getSafeInt(TrackInputConstants.PSC) == TrackInputConstants.PSC_PSR) {
			data = (TrackOutputConstants.PSC_PSR);
		} else if (track.getSafeBoolean(TrackInputConstants.IS_TEST_TRANSPONDER_TRACK)) {
			// TODO {VALERIO} - Nessun simbolo associato a "TRANPS" è presente in "resources_Bulgaria"
			data = TrackInputConstants.TRANSP;
		}

		if (track.getSafeBoolean(TrackInputConstants.IS_DUPLICATED)
				&& !track.getSafeString(TrackInputConstants.MODE_3A).equals(TrackInputConstants.MOD_CONSPICUITY)) {
			color = Utils.getTrackOverSymbolColor("IS_DUPLICATED_OVERSYMBOL", "true", color);
		} else {

			if (track.getSafeString(TrackInputConstants.MODE_3A).equals(TrackInputConstants.MOD_CONSPICUITY)) {
				color = Utils.getTrackOverSymbolColor("SSR_CODE", TrackInputConstants.MOD_CONSPICUITY, color);
			}

			if (track.getSafeBoolean(TrackInputConstants.IS_MLAT_TRACKERS)) {
				color = Utils.getTrackOverSymbolColor("IS_MLAT", "2", color);
			}

			if (track.getSafeBoolean(TrackInputConstants.IS_ADS_TRACKERS)
					&& track.getSafeBoolean(TrackInputConstants.IS_ADSB_TRACKER)) {
				color = Utils.getTrackOverSymbolColor("IS_MLAT", "3", color);
			}

			final var flight = Utils_LIS.getFlightFromTrkId(track.getId());
			final String idTCT = AnalyzerLabel.alarmedTct.get(track.getId());

			if (flight.isPresent()) {
				final int lev_up = TrackInputConstants.LEV_UP;
				final int lev_down = TrackInputConstants.LEV_DOWN;
				final int lev = track.getSafeInt(TrackInputConstants.LEV);

				final String template = flight.get().getSafeString(FlightInputConstants.TEMPLATE_STRING);

				final IRawData fl = flight.get();
				final String rvsm = fl.getSafeString(FlightInputConstants.RVSM);
				if (!rvsm.equalsIgnoreCase("W") && (lev >= lev_down && lev <= lev_up)) {
					if (template.contains(FlightOutputConstants.TEMPLATE_NEARBY)) {
						color = Utils.getTemplateColor(template);
					} else {
						color = Utils.getTrackOverSymbolColor(FlightInputConstants.RVSM, "true", color);
					}
				}

				if (!(template.contains(FlightOutputConstants.TEMPLATE_NEARBY)
						|| template.contains(FlightOutputConstants.TEMPLATE_NOTIFIED))) {

					if (idTCT != null) {
						final var rawdataTct = BlackBoardUtility.getDataOpt(DataType.TCT.name(), idTCT);
						if (rawdataTct.isPresent()) {
							final IRawData tct = rawdataTct.get();
							if (!rawdataTct.get().getSafeString(CommonConstants.ALARM_TYPE).isEmpty()) {
								if (tct.getSafeString(CommonConstants.ALARM_TYPE).equals(TrackInputConstants.TMM)) {
									color = Utils.getTrackOverSymbolColor("TCT_ALARM", "DUMMY", color);
								} else if (!tct.getSafeString(CommonConstants.ALARM_TYPE).substring(0, 1).contains("H")
										&& !tct.getSafeString(CommonConstants.ALARM_TYPE).substring(0, 1)
												.contains("V")) {
									color = Utils.getTrackOverSymbolColor("TCT_ALARM", TrackInputConstants.TCT, color);
								} else {
									color = Utils.getTrackOverSymbolColor("TCT_ALARM", "DUMMY", color);
								}
							}
						}
					}
				}

				if (fl.getSafeBoolean(CommonConstants.IS_RNP)) {
					color = Utils.getTrackOverSymbolColor(CommonConstants.IS_RNP, "true", color);
				}
				if (fl.getSafeBoolean(CommonConstants.IS_RCP)) {
					color = Utils.getTrackOverSymbolColor(CommonConstants.IS_RCP, "true", color);
				}
				if (fl.getSafeBoolean(CommonConstants.IS_RSP)) {
					color = Utils.getTrackOverSymbolColor(CommonConstants.IS_RNP, "true", color);
				}

				if (fl.getSafeBoolean(CommonConstants.FLIGHT_ISIOAT)) {
					color = Utils.getTrackOverSymbolColor(CommonConstants.FLIGHT_ISIOAT, "true", color);
				}

			} else {
				if (idTCT != null) {
					final var rawdataTct = BlackBoardUtility.getDataOpt(DataType.TCT.name(), idTCT);
					if (rawdataTct.isPresent()) {
						final IRawData tct = rawdataTct.get();
						if (!rawdataTct.get().getSafeString(CommonConstants.ALARM_TYPE).isEmpty()) {
							if (tct.getSafeString(CommonConstants.ALARM_TYPE).equals(TrackInputConstants.TMM)) {
								color = Utils.getTrackOverSymbolColor("TCT_ALARM", TrackInputConstants.TMM, color);
							} else if (!tct.getSafeString(CommonConstants.ALARM_TYPE).substring(0, 1).contains("H")
									&& !tct.getSafeString(CommonConstants.ALARM_TYPE).substring(0, 1).contains("V")) {
								color = Utils.getTrackOverSymbolColor("TCT_ALARM", TrackInputConstants.TCT, color);
							} else {
								color = Utils.getTrackOverSymbolColor("TCT_ALARM", "DUMMY", color);
							}
						}
					}
				}
			}
		}
		objectNode.addLine(TrackOutputConstants.POS_TAG, data).setAttribute(EdmModelKeys.Attributes.COLOR, color);
	}

	/**
	 * Adds the over symbol.
	 *
	 * @param track      the track
	 * @param flight     the flight
	 * @param objectNode the object node
	 */
	private static void addOverSymbol(final IRawData track, final Optional<IRawData> flight,
			final HeaderNode objectNode) {

		final StringBuilder data = new StringBuilder();

		if (track.getSafeInt(TrackInputConstants.ECAT) != TrackInputConstants.ECAT_DRONE
				&& track.getSafeInt(TrackInputConstants.ECAT) != TrackInputConstants.ECAT_VEHIC) {
			final int lev_up = TrackInputConstants.LEV_UP;
			final int lev_down = TrackInputConstants.LEV_DOWN;
			final int lev = track.getSafeInt(TrackInputConstants.LEV);

			if (track.getSafeInt(TrackInputConstants.SPI) == 1) {
				data.append(",").append(TrackOutputConstants.SPI);
			}
			if (track.getSafeBoolean(TrackInputConstants.COAST)) {
				data.append(",").append(TrackOutputConstants.COAST);
			}
			if (track.getSafeInt(TrackInputConstants.MODE_S) != 0) {
				// TODO {VALERIO} - Nessun simbolo associato a "MODES" è presente in "resources_Bulgaria"
				data.append(",").append(TrackOutputConstants.MODE_S_1);
			}
			if (track.getSafeBoolean(TrackInputConstants.IS_DUPLICATED)) {
				data.append(",").append(TrackOutputConstants.IS_DUPLICATED);
			}
			if (track.getSafeBoolean(TrackInputConstants.IS_NAVIGATED)) {
				data.append(",").append(TrackOutputConstants.IS_NAVIGATED);
			}
			if (track.getSafeBoolean(TrackInputConstants.IS_ADS_TRACKERS)
					&& track.getSafeBoolean(TrackInputConstants.IS_ADSB_TRACKER)) {
				data.append(",").append(TrackOutputConstants.ADSB_TRACKER);
			}
			if (track.getSafeBoolean(TrackInputConstants.IS_ADS_TRACKERS)
					|| track.getSafeBoolean(TrackInputConstants.IS_MLAT_TRACKERS)) {
				data.append(",").append(TrackOutputConstants.ADS);
			}

			if (track.getSafeBoolean(TrackInputConstants.IS_MLAT_TRACKERS)) {
				data.append(",").append(TrackOutputConstants.WAM);
			}

			if (track.getSafeBoolean(TrackInputConstants.IS_DUPLICATED_RCS)) {
				data.append(",").append(TrackInputConstants.IS_DUPLICATED_RCS);
			}
			if (track.getSafeBoolean(TrackInputConstants.IS_FPT_TRACKER)) {
				data.append(",").append(TrackOutputConstants.FPT);
			}
			if (AnalyzerLabel.isByPassOrdDard() || AnalyzerLabel.getIsByPassState() == 1) {
				data.append(",").append(TrackInputConstants.BYP);
			}

			final String track_mode_3A = track.getSafeString(TrackInputConstants.MODE_3A);
			if (track_mode_3A.equals(TrackInputConstants.MOD_CONSPICUITY)) {
				data.append(",").append(TrackOutputConstants.CONSPICUITY);
			}
			if (flight.isPresent()) {
				final IRawData fl = flight.get();
				final String rvsm = fl.getSafeString(FlightInputConstants.RVSM);
				if (!rvsm.equalsIgnoreCase("W") && (lev >= lev_down && lev <= lev_up)) {
					data.append(",").append(TrackOutputConstants.NO_RVSM);
				}

				if (fl.getSafeBoolean(CommonConstants.IS_RNP)) {
					data.append(",").append(CommonConstants.IS_RNP);
				}
				if (fl.getSafeBoolean(CommonConstants.IS_RCP)) {
					data.append(",").append(CommonConstants.IS_RCP);
				}
				if (fl.getSafeBoolean(CommonConstants.IS_RSP)) {
					data.append(",").append(CommonConstants.IS_RSP);
				}

				if (fl.getSafeBoolean(CommonConstants.FLIGHT_ISIOAT)) {
					data.append(",").append(CommonConstants.FLIGHT_ISIOAT);
				}

			}
		} else if (track.getSafeInt(TrackInputConstants.ECAT) == TrackInputConstants.ECAT_VEHIC
				&& !Utils.isVisVehicle()) {
			final int lev_up = TrackInputConstants.LEV_UP;
			final int lev_down = TrackInputConstants.LEV_DOWN;
			final int lev = track.getSafeInt(TrackInputConstants.LEV);

			if (track.getSafeInt(TrackInputConstants.SPI) == 1) {
				// TODO {VALERIO} - Nessun simbolo associato a "SPI" è presente in "resources_Bulgaria"
				data.append(",").append(TrackOutputConstants.SPI);
			}
			if (track.getSafeBoolean(TrackInputConstants.COAST)) {
				data.append(",").append(TrackOutputConstants.COAST);
			}
			if (track.getSafeInt(TrackInputConstants.MODE_S) != 0) {
				data.append(",").append(TrackOutputConstants.MODE_S_1);
			}
			if (track.getSafeBoolean(TrackInputConstants.IS_DUPLICATED)) {
				data.append(",").append(TrackOutputConstants.IS_DUPLICATED);
			}
			if (track.getSafeBoolean(TrackInputConstants.IS_NAVIGATED)) {
				data.append(",").append(TrackOutputConstants.IS_NAVIGATED);
			}
			if (track.getSafeBoolean(TrackInputConstants.IS_ADSB_TRACKER)) {
				data.append(",").append(TrackOutputConstants.ADSB_TRACKER);
			}
			if (track.getSafeBoolean(TrackInputConstants.IS_ADS_TRACKERS)
					|| track.getSafeBoolean(TrackInputConstants.IS_MLAT_TRACKERS)) {
				data.append(",").append(TrackOutputConstants.ADS);
			}

			/*
			 * if (track.getSafeBoolean(TrackInputConstants.IS_MLAT_TRACKERS)) {
			 * data.append(",").append(TrackOutputConstants.WAM); }
			 */
			if (track.getSafeBoolean(TrackInputConstants.IS_DUPLICATED_RCS)) {
				data.append(",").append(TrackInputConstants.IS_DUPLICATED_RCS);
			}
			if (track.getSafeBoolean(TrackInputConstants.IS_FPT_TRACKER)) {
				data.append(",").append(TrackOutputConstants.FPT);
			}

			if (AnalyzerLabel.isByPassOrdDard() || AnalyzerLabel.getIsByPassState() == 1) {
				data.append(",").append(TrackInputConstants.BYP);
			}

			final String track_mode_3A = track.getSafeString(TrackInputConstants.MODE_3A);
			if (track_mode_3A.equals(TrackInputConstants.MOD_CONSPICUITY)) {
				data.append(",").append(TrackOutputConstants.CONSPICUITY);
			}
			if (flight.isPresent()) {
				final IRawData fl = flight.get();
				final String rvsm = fl.getSafeString(FlightInputConstants.RVSM);
				if (!rvsm.equalsIgnoreCase("W") && (lev >= lev_down && lev <= lev_up)) {
					data.append(",").append(TrackOutputConstants.NO_RVSM);
				}

				if (fl.getSafeBoolean(FlightInputConstants.NO_APM_ALARM)) {
					data.append(",").append(TrackOutputConstants.NO_APM_ALARM);
				}

			}
		}

		objectNode.addLine(TrackOutputConstants.OVER_TAG, data.toString());
	}

	/**
	 * Delete.
	 *
	 * @param jsonTrack the json track
	 */
	public static void delete(final IRawData jsonTrack) {
		AnalyzerLabel.alarmedTct.remove(jsonTrack.getId());
		final var rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.DELETE, EdmModelKeys.HeaderType.TRACK);
		objectNode.addLine(CommonConstants.ID, Utils.getTrackId(jsonTrack.getId()));
		Utils.doTCPClientSender(rootData);
	}

	/**
	 * Process.
	 *
	 * @param jsonTrack the json track
	 */
	public static void process(final IRawData jsonTrack) {
		if (Utils.isPrototypingTest()) {
			final var flight = Utils_LIS.getFlightFromTrkId(jsonTrack.getId());
			if (flight.isPresent()) {
				var rawDataClone = RawDataFactory.create(jsonTrack);
				rawDataClone.setId(jsonTrack.getId());
				rawDataClone.put(CommonConstants.FLIGHT_NUM, flight.get().getSafeString(CommonConstants.FLIGHT_NUM));
				Utils.forceFieldValuePRE(rawDataClone, true);

				final DataRoot rootData = DataRoot.createMsg();
				final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
				toXml(rawDataClone, objectNode);

				Utils.forceAllFieldsColor(rawDataClone, rootData);
				Utils.forceFieldColor(rawDataClone, rootData);
				Utils.forceFieldValuePOST(rawDataClone, rootData, true);

				Utils.doTCPClientSender(rootData);
			} else {
				XMLCreation(jsonTrack);
			}
		} else {
			XMLCreation(jsonTrack);
		}
	}

	/**
	 * XML creation.
	 *
	 * @param data the data
	 */
	private static void XMLCreation(final IRawData data) {
		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
		toXml(data, objectNode);

		Utils.doTCPClientSender(rootData);
	}
}
