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
package service.flight;

import java.util.Optional;
import java.util.Random;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.flight.FlightInputConstants;

/**
 * The Class LogoDemoAggregatorService.
 *
 * @author latorrem
 */
public class LOGOCompanyAggregatorService implements IAggregatorService {
	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 *
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final String callsign = inputJson.getSafeString(FlightInputConstants.CALLSIGN);
		final String ades = inputJson.getSafeString("ADES");
		final String adep = inputJson.getSafeString("ADEP");
		final String etd = inputJson.getSafeString("ETD");
		String livrea;
		String companyName = "";
		String adepName = "";
		String adesName = "";
		String etd_ESB = "";
		String airlinesCode = callsign.substring(0, 3);

		if (!ades.isEmpty()) {
			Optional<IRawData> jsonDafifAdes = BlackBoardUtility.getDataOpt("DAFIF_ARPT", ades);
			if (jsonDafifAdes.isPresent()) {
				adesName = jsonDafifAdes.get().getSafeString("NAME");
			}

		}

		if (!adep.isEmpty()) {
			Optional<IRawData> jsonDafifAdep = BlackBoardUtility.getDataOpt("DAFIF_ARPT", adep);
			if (jsonDafifAdep.isPresent()) {
				adepName = jsonDafifAdep.get().getSafeString("NAME");
			}

		}

		if (!etd.isEmpty()) {
			etd_ESB = etd.substring(0, 2) + ":" + etd.substring(2, 4);
		}

		switch (airlinesCode) {
		case "FDB":
			livrea = "FlighDubai";
			companyName = "FLIGHT DUBAI";
			break;
		case "QTR":
			livrea = "Qatar2";
			companyName = "QATAR AIRWAYS";
			break;
		case "UAE":
			livrea = "Emirates2";
			companyName = "EMIRATES";
			break;
		case "DLH":
			livrea = "Luft2";
			companyName = "LUFTHANSA";
			break;
		case "AZA":
			livrea = "Alitalia";
			companyName = "ITA AIRWAYS";
			break;
		case "SAA":
			livrea = "Africa2";
			companyName = "SOUTH AFRICAN AIRLINES";
			break;
		case "EZY":
			livrea = "Easy";
			companyName = "EASY JET";
			break;
		case "DLA":
			livrea = "Dolomiti";
			companyName = "AIR DOLOMITI";
			break;
		case "ISS":
			livrea = "Meridiana";
			companyName = "MERIDIANA";
			break;
		case "AFL":
			livrea = "Aeroflot2";
			companyName = "AEROFLOT";
			break;
		case "JAI":
			livrea = "Jet2";
			companyName = "JET AIRWAYS";
			break;
		case "ETD":
			livrea = "Etd2";
			companyName = "ETHIAD";
			break;
		case "GFA":
			livrea = "Gulf2";
			companyName = "GULF AIR";
			break;
		case "RJA":
			livrea = "Jord2";
			companyName = "ROYAL JORDANIAN";
			break;
		case "EIN":
			livrea = "Irland2";
			companyName = "IRELAND AIRLINES";
			break;
		case "PAL":
			livrea = "Phill2";
			companyName = "PHILIPPINE AIRLINES";
			break;
		case "JAL":
			livrea = "Japan2";
			companyName = "JAPAN AIRLINES";
			break;
		case "OAL":
			livrea = "Olympic2";
			companyName = "OLYMPIC AIRLINES";
			break;
		case "SVA":
			livrea = "Saudi2";
			companyName = "SAUDI ARABIAN AIRLINES";
			break;
		case "THY":
			livrea = "Turk";
			companyName = "TURKISH";
			break;
		case "AFR":
			livrea = "AirFrance2";
			companyName = "AIR FRANCE";
			break;
		case "HAL":
			livrea = "Hawaii";
			companyName = "HAWAIAN AIRLINES";
			break;
		case "BAW":
			livrea = "British2";
			companyName = "BRITISH AIRLINES";
			break;
		case "OMA":
			livrea = "Oman";
			companyName = "OMAN AIR";
			break;
		case "CEB":
			livrea = "Cebu";
			companyName = "CEBU PACIFIC";
			break;
		case "CPA":
			livrea = "Cathay2";
			companyName = "CATHAY PACIFIC";
			break;
		case "DAL":
			livrea = "Delta2";
			companyName = "DELTA AIRLINES";
			break;
		case "MAS":
			livrea = "Malaysian2";
			companyName = "MALAYSIAN AIRLINES";
			break;
		case "RYR":
			livrea = "Ryan2";
			companyName = "RYANAIR";
			break;
		case "RBA":
			livrea = "Brunei2";
			companyName = "ROYAL BRUNEY";
			break;
		case "SIA":
			livrea = "Singapore2";
			companyName = "SINGAPORE AIRLINES";
			break;
		case "KAL":
			livrea = "Korean2";
			companyName = "KOREAN AIRLINES";
			break;
		case "ELY":
			livrea = "Israel2";
			companyName = "ISRAEL AIRLINES";
			break;
		case "ASA":
			livrea = "Alaska2";
			companyName = "ALASKA AIRLINES";
			break;
		case "IBE":
			livrea = "Iberia2";
			companyName = "IBERIA";
			break;
		case "CSN":
			livrea = "Cinasou2";
			companyName = "CHINA SOUTHERN";
			break;
		case "SWR":
			livrea = "Swiss";
			companyName = "SWISS AIR";
			break;
		case "QFA":
			livrea = "Qantas2";
			companyName = "QUANTAS";
			break;
		case "HVN":
			livrea = "Vietnam2";
			break;
		case "AAL":
			livrea = "American2";
			companyName = "AMERICAN AIRLINES";
			break;
		case "ANA":
			livrea = "Ana2";
			companyName = "ALL NIPPON AIRWAYS";
			break;
		case "ACA":
			livrea = "Canada2";
			companyName = "CANADA AIRLINES";
			break;
		case "CAL":
			livrea = "Cineairlines2";
			companyName = "CHINA AIRLINES";
			break;
		case "MSR":
			livrea = "Egypt2";
			companyName = "EGYPT AIR";
			break;
		case "AVA":
			livrea = "Avianca2";
			companyName = "AVIANCA AIRLINES";
			break;
		case "BWA":
			livrea = "Caribbean";
			companyName = "CARIBBEAN AIRLINES";
			break;
		case "CCA":
			livrea = "CinaAir2";
			companyName = "AIR CHINA";
			break;
		case "AIC":
			livrea = "Airindia2";
			companyName = "AIR INDIA";
			break;
		case "BTK":
			livrea = "Batik2";
			companyName = "BATIK AIRLINES";
			break;
		case "BEL":
			livrea = "Brussels2";
			companyName = "BRUSSELS AIRLINE";
			break;
		case "KLM":
			livrea = "Klm2";
			companyName = "KLM AIRLINES";
			break;
		case "EVA":
			livrea = "Eva2";
			companyName = "EVA AIRLINES";
			break;
		case "SEY":
			livrea = "Sey2";
			companyName = "AIR SEYCHELLES";
			break;
		case "AFJ":
			livrea = "Alliance2";
			companyName = "ALLIANCE AIRLINES";
			break;
		case "KQA":
			livrea = "Kenya2";
			companyName = "KENYA AIRWAYS";
			break;
		case "ICE":
			livrea = "Iceland2";
			companyName = "ICELAND AIRLINES";
			break;
		case "CES":
			livrea = "CinaEast";
			companyName = "CHINA EASTERN";
			break;
		case "ETH":
			livrea = "Ethiopia2";
			companyName = "ETHIOPIAN AIRLINES";
			break;
		case "MEA":
			livrea = "Lebanon2";
			companyName = "MIDDLE EAST ARILINES";
			break;
		case "FIN":
			livrea = "Fin2";
			companyName = "FINNAIR";
			break;
		case "IRA":
			livrea = "Iran";
			companyName = "IRAN AIR";
			break;
		case "KAC":
			livrea = "Kuwait";
			companyName = "KUWAIT AIRLINES";
			break;
		case "ABY":
			livrea = "Airarabia";
			companyName = "AIR ARABIA";
			break;
		case "AMX":
			livrea = "Mexico2";
			companyName = "AIR MEXICO";
			break;
		case "ANZ":
			livrea = "Newzeland2";
			companyName = "NEW ZEELAND AIRLINES";
			break;
		case "PIA":
			livrea = "Pakistan";
			companyName = "PAKISTAN AIRLINES";
			break;
		case "SAS":
			livrea = "Sas2";
			companyName = "SCANDINAVIAN AIRLINES";
			break;
		case "THA":
			livrea = "Thai2";
			companyName = "THAY AIRWAYS";
			break;
		case "AXM":
			livrea = "Airasia2";
			companyName = "AIR ASIAN";
			break;
		case "GIA":
			livrea = "Garuda2";
			companyName = "GARUDA AIRLINES";
			break;
		case "CLX":
			livrea = "Cebu";
			companyName = "CARGOLUX AIR";
			break;
		case "JZR":
			livrea = "Eva2";
			companyName = "JAZEERA AIR";
			break;
		case "VLG":
			livrea = "Vueling";
			companyName = "VUELING AIRLINES";
			break;
		default:
			livrea = "Garuda2";
			companyName = "";
			break;
		}

		dataNode.addLine("COMPANY", companyName);
		dataNode.addLine("LIVREA", livrea);

		String[] gate = { "A01", "B04", "G01", "A10", "E01", "C01", "E04", "D20" };

		Random rand = new Random();

		int Rand_item = rand.nextInt(gate.length);

		String[] status = { "BOARDING", "ON TIME", "DELAYED" };
		int status_item = rand.nextInt(status.length);

		String st = status[status_item];
		String gt = gate[Rand_item];
		dataNode.addLine("ADEP_NAME", adepName.trim());
		dataNode.addLine("ADES_NAME", adesName.trim());
		dataNode.addLine("ETD_ESB", etd_ESB.trim());
		dataNode.addLine("GATE", gt.trim());
		dataNode.addLine("STATE", st.trim());

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {

		/** The service name. */
		String service_name = "LOGO";
		return service_name;
	}

}
