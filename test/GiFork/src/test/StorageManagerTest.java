/**
 *
 */
package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.data_exchange.gateways.jip.JIPClientThread;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.GiForkMain_LIS;

/**
 * @author ALBANESED
 *
 */
class StorageManagerTest {

	/**
	 *
	 */
	String jsonFlight = "{\"FLIGHT_NUM\":0,\"COO_REM\":0,\"IND_FREQ\":\"0\",\"SPDMACH\":\"\",\"NON_RVSM_EQUIPPED_FLAG\":true,\"IFIXID\":\"-1\",\"TOOVERFLYROUTE\":\"TIKVA  VELBA \",\"FPT_LATITUDE\":0,\"IAMFIRSTSECTOR\":true,\"FP_POINT_PDIS5\":\"48\",\"OLDI_CRC\":\" \",\"ISARR\":false,\"TEMPLATE_STRING\":\"CONTROLLED\",\"LDVAVALUE\":0,\"SCT\":\"6\",\"FLC\":0,\"GMNOTPRIOR\":0,\"IS_GOF\":\"GAT\",\"ADEP\":\"LIRF\",\"MES_REV\":\"\",\"NTZA_GLOB_FILT\":false,\"ISCOAST\":false,\"FP_POINT_Y0\":\"41.33388888888889\",\"COORD_IN\":\"false\",\"MAFIX\":\"     \",\"SURV\":\"N\",\"ISGROUND\":false,\"CFS\":2,\"ISABINOROUTE\":false,\"EOME_POINT_OUT\":1,\"RATECLIMIND\":\"\",\"DAIW_GLOB_FILT\":false,\"PHD\":\"65535\",\"HLD_INST_CONF\":false,\"SELTIME\":\"0743\",\"ACS\":0,\"REG\":\"\",\"EIME_POINT_OUT\":1,\"SIA\":false,\"XFLDRAWING\":0,\"TAC_EVENT\":0,\"FR\":\"I\",\"EXT_POINT_OUT_RX_STATUS\":0,\"A\":false,\"FPP\":\"P\",\"IS_RPAS\":false,\"HLD_IN_PROG\":false,\"ISLIVE\":true,\"DCS\":\"RCD_NOT_RECEIVED\",\"DCL_STATE_IMAGE\":\"\",\"HLDE\":\"\",\"MIOS\":\"\",\"HLDF\":\"\",\"POINT_OUT_RX\":false,\"ISTERMINATED\":false,\"SID\":\"\",\"SPD\":\"65535\",\"CFL\":250,\"FP_POINT_Y5\":\"41.96638888888889\",\"IS_COORD_TAC\":false,\"CF\":\"VELBA\",\"IS_SUA\":\"\",\"EXITTIME\":0,\"IS_COORD_OLDI_TIP\":false,\"VR_INST_CONF\":false,\"FP_POINT_CDIS0\":\"0\",\"MASK_UDS\":0,\"PDC\":\"\",\"FP_POINT_CDIS1\":\"0\",\"FP_POINT_CDIS2\":\"0\",\"FTYPE\":\"S\",\"GMFLAG\":1,\"CXUDS\":-1,\"EPP_WARNING_TIME\":\"FALSE\",\"APAM_GLOB_FILT\":false,\"FP_POINT_NAME4\":\"ENDTT\",\"XFLDRAWING_STATUS\":0,\"FP_POINT_PDIS3\":\"40\",\"STN\":5864,\"POINT_OUT_TX\":false,\"DHA\":false,\"DLE\":\"N\",\"ISACTIVE\":false,\"GND_DEV\":false,\"IS_COORD_DCT\":false,\"COORDTIMER\":false,\"FDM_SECTOR\":\"CURRENT\",\"FULLROUTE\":\"DOBAR DCT TIKVA DCT VELBA\",\"RJC\":\"\",\"EXT_POINT_OUT_RX\":false,\"VCUDS\":-1,\"FP_POINT_SPT3\":\"NEXT\",\"ETL\":\"1007\",\"TACTIME\":\"\",\"ISDEP_LABEL\":false,\"ETN\":\"1007\",\"MNV_HDG\":\"0\",\"INBRRQLEVEL\":\"\",\"CFL_WARNING_FLAG\":true,\"IS_COORD_OLDI_XFL\":false,\"XFLALERT\":\"\",\"PLV\":250,\"FREE_TEXT\":\"\",\"PFL\":\"0\",\"HLD_STATE\":\"\",\"ADES\":\"EGGL\",\"TOC\":\"\",\"SFPL_TYPE\":0,\"CMUDS\":-1,\"VSN\":\"\",\"IS_COORD_PEL\":false,\"DATA_TYPE\":\"FLIGHT_EXTFLIGHT\",\"MAL\":0,\"BRNAV\":\"B\",\"MOOS_WARN\":\"\",\"FP_POINT_X1\":\"20.99527777777778\",\"SNC\":\"\",\"NDA\":0,\"MCP\":\"0\",\"EXT_POINT_OUT_TX\":false,\"WTC\":\"H\",\"THE_HOLDING_INSTR_NON_CONF_VALUE\":0,\"CLR\":\"X\",\"NSSR_CODE\":\"     \",\"XFL\":\"250\",\"FP_POINT_X5\":\"22.88111111111111\",\"FP_POINT_X0\":\"20.496944444444445\",\"MES_REV_WARN\":\"\",\"IS_COORD_TOC\":false,\"COORD_OUT\":\"false\",\"TRANSPOINTLONGITUDE\":-1,\"ISCPDLCCONNECTED\":false,\"XRQLEV1\":\"0\",\"ETA\":\"\",\"NEXT_FIR\":\"LBSR\",\"XRQLEV2\":\"0\",\"DSA\":\"NO_DIALOGUE\",\"LOF_NAN_COLOR\":-1,\"DSG\":\"NO_DIALOGUE\",\"LAT\":\"\",\"FP_POINT_CTIM4\":\"8\",\"FP_POINT_CTIM0\":\"0\",\"FP_POINT_CTIM2\":\"0\",\"FP_POINT_CTIM1\":\"0\",\"FP_POINT_CTIM3\":\"7\",\"MES_REV_COLOR\":-1,\"RNP\":0,\"RIIFEXT\":\"\",\"AMM\":\"\",\"AMB_STATUS\":0,\"GNSS\":0,\"PAS\":\"65535\",\"RTI_ENABLED\":true,\"FP_POINT_SPT5\":\"COP_OUT\",\"FIELD10B\":\"\",\"LCH\":0,\"RIIF\":\"\",\"FP_POINT_NAME1\":\"CLKEX\",\"LCI\":0,\"IS_FPT\":true,\"CORLM_STATUS\":1,\"XRQMSG\":\"\",\"SSR_CODE\":\"A2436\",\"CPB\":true,\"FDM_POSITION\":\"ONLY_SECT\",\"GM_CONFLICT_ID\":65535,\"OFIX\":\"VELBA\",\"IS_COORD_CHE\":false,\"MDS\":\"N\",\"IS_COORD_XRQ\":false,\"GMUD\":0,\"XRQOLDLEV1\":\"0\",\"XRQOLDLEV2\":\"0\",\"OLDI_XFL\":\"250\",\"ASS_SPD_INST_CONF\":false,\"OLDI_SPL\":\"\",\"CS_DRAWING\":1,\"BUST\":\"\",\"SMT\":false,\"HDG_INST_CONF\":false,\"DHRPN\":false,\"OLDI_EST\":\" \",\"TOD\":false,\"IS_COORD_TOC_MAN\":false,\"CTS\":0,\"ARC_ARROW\":\"\",\"FP_POINT_NAME2\":\"CLKPN\",\"CTOT\":\"\",\"LSV\":\"-1\",\"PRT\":\"32768\",\"STR\":\"\",\"DATE\":\"220427\",\"THE_CFL_INST_NON_CONF_DIMENSION\":0,\"STS\":\"\",\"NPB\":false,\"STX\":\"\",\"CALLSIGN\":\"MT1\",\"STY\":0,\"GMOWNP\":0,\"NEARTOEXIT\":false,\"ISSTARTUP\":false,\"SQKCONDITION\":true,\"PSFL\":\"0\",\"SUN\":\"1\",\"OFIXID\":\"5\",\"THE_ASSIGN_SPEED_INSTR_NON_CONF_DIMENSION\":0,\"FP_POINT_X2\":\"21.042222222222225\",\"IS_COORD_OLDI_RRQ\":false,\"IS_COORD_CSP\":false,\"ISSEL\":false,\"FP_POINT_NAME3\":\"TIKVA\",\"ARC\":\"\",\"POT\":\"\",\"MIS\":\"\",\"PELDRAWING\":0,\"RIOFEXT\":\"\",\"EPP_WARNING_LEVEL\":\"FALSE\",\"FSSR\":\"     \",\"OPER\":\"U\",\"FP_POINT_OVF0\":true,\"FP_POINT_OVF2\":true,\"ATD_F\":\"\",\"FP_POINT_OVF1\":true,\"PELDRAWING_STATUS\":0,\"FDM_FLSTATUS\":\"INACTIVE\",\"FP_POINT_TAS0\":378,\"FP_POINT_TAS1\":378,\"LAT_COAST\":\"\",\"AMB_STN\":0,\"FP_POINT_Y2\":\"41.338055555555556\",\"FP_POINT_OVF5\":false,\"FP_POINT_OVF4\":false,\"FP_POINT_TAS2\":378,\"FP_POINT_OVF3\":false,\"ICAO_COUNTRY_SFPL\":\"\",\"FP_POINT_TAS5\":372,\"FP_POINT_NUM\":6,\"FP_POINT_TAS3\":370,\"FP_POINT_TAS4\":369,\"NRF\":\"TIKVA\",\"RIOF\":\"\",\"TCUDS\":-1,\"MIOS_COLOR\":-1,\"GAT_OAT\":\"G\",\"ATA\":\"\",\"THE_HEADING_INSTR_NON_CONF_AMPLITUDE\":0,\"LDVADIR\":\"LEFT\",\"EQ_833_STATUS\":\"Y\",\"NUM_AC\":1,\"ISSCL\":true,\"HEADING_DIRECTION\":\"175.5945806451691\",\"ISPENDING\":false,\"FP_POINT_SPT4\":\"NULL\",\"ISDEP\":false,\"FP_POINT_CDIS4\":\"49\",\"ISFLDI\":true,\"MOOS_COLOR\":-1,\"FP_POINT_X4\":\"22.078611111111112\",\"FP_POINT_CDIS3\":\"40\",\"EQ_833_PERMISSION\":\"\",\"FP_POINT_SPT1\":\"NULL\",\"LON_COAST\":\"\",\"FP_POINT_SPT2\":\"NULL\",\"NRF_ETO\":\"0820\",\"FP_POINT_PDIS1\":\"0\",\"THE_HOLDING_INSTR_NON_CONF_DIMENSION\":0,\"FP_POINT_PDIS0\":\"0\",\"FP_POINT_PDIS2\":\"0\",\"IS_COORD_COF\":false,\"FP_POINT_PDIS4\":\"9\",\"FDM_FPLSTATUS\":\"PR\",\"FP_POINT_Y4\":\"41.442499999999995\",\"CSSR\":\"A2436\",\"PSR\":\"0\",\"TRUEAIRSPEEDINKNOTS\":\"378\",\"ISHLD\":false,\"SFPL_STATE\":\"LE\",\"MES_TRA_COLOR\":-1,\"FP_POINT_ETO3\":\"0820\",\"FP_POINT_ETO5\":\"0829\",\"ISTCL\":true,\"CPDLC_NEXT_FREQ\":\"\",\"CFL_DEVIATION\":false,\"PEL\":\"250\",\"FP_POINT_ETO4\":\"0821\",\"FP_POINT_PT1\":\"U\",\"AHDG\":\"0\",\"TCAS\":\"\",\"FP_POINT_PT2\":\"U\",\"FP_POINT_PT0\":\"X\",\"PSSR_CODE\":\"     \",\"ISVFL\":false,\"ARWY\":\"\",\"THE_ASSIGN_SPEED_INSTR_NON_CONF_VALUE\":0,\"ICAO_CODE_SFPL\":\"\",\"FP_POINT_ETO0\":\"0810\",\"HDM\":0,\"FP_POINT_PT4\":\"E\",\"COPNAME\":\"VELBA\",\"FP_POINT_PT5\":\"F\",\"RFL\":\"250\",\"FIELDPBN\":\"A1\",\"FP_POINT_PT3\":\"F\",\"HDR\":\"\",\"FP_POINT_ETO1\":\"0813\",\"VCDDS\":-1,\"FP_POINT_ETO2\":\"0813\",\"MNV_COORD_STATUS\":0,\"IS_COORD_ROF\":false,\"FP_POINT_SPT0\":\"COP_INB\",\"ELTI\":\"0200\",\"COORD_STATUS\":\"0\",\"OUTRRQLEVEL\":\"\",\"LOF_NAN\":\"\",\"MES_TRA_WARN\":\"\",\"IS_COORD_OLDI_RTI\":false,\"SPD_DEV\":false,\"WCF\":0,\"IS_COORD_LCH\":false,\"NAN_OLS\":\"32\",\"EXT_POINT_OUT_TX_STATUS\":0,\"NAN_OIM\":\"78\",\"COASTTIME\":\"\",\"ISFIREXITLIST\":true,\"LINKED\":true,\"THE_VRCD_INSTR_NON_CONF_VALUE\":0,\"HLD\":false,\"RONIDX\":\"0\",\"FP_POINT_SOP5\":6,\"FP_POINT_SOP4\":6,\"FIELD10A\":\"RWY\",\"FP_POINT_SOP1\":6,\"FP_POINT_SOP0\":6,\"ESB_TYPE\":\"\",\"FP_POINT_SOP3\":6,\"FP_POINT_SOP2\":6,\"RMDDS\":-1,\"OLDINEXFIRFREQ\":false,\"MOOS\":\"\",\"FP_POINT_Y1\":\"41.33777777777778\",\"XRQPN1\":\"\",\"ATY\":\"A310\",\"XRQPN2\":\"\",\"IFIX\":\"\",\"RMUDS\":-1,\"STARJUNCTPT\":\"DOBAR\",\"FRQ\":\"123.800\",\"ROUTE\":\"DOBAR CLKEX CLKPN TIKVA ENDTT VELBA \",\"SCDDS\":-1,\"IAMLASTSECTOR\":true,\"WCP\":\"0\",\"EAT\":\"\",\"MIOS_WARN\":\"\",\"LOF_OLM\":\"78\",\"MSAW_GLOB_FILT\":false,\"FP_POINT_NAME0\":\"DOBAR\",\"MNV_ISCOORDIN\":\"false\",\"FP_POINT_PTIM4\":\"1\",\"IS_COORD_OLDI_PEL\":false,\"ETD\":\"0810\",\"ARR_PKBAY\":\"\",\"VR_RATE_DEV\":false,\"FP_POINT_PTIM5\":\"8\",\"COORDTRANS\":\"FALSE\",\"FP_POINT_PTIM3\":\"7\",\"GMSTATUS\":0,\"FP_POINT_PTIM2\":\"0\",\"FP_POINT_PTIM1\":\"0\",\"NTS\":3,\"FP_POINT_PTIM0\":\"0\",\"IS_COORD_XFL\":false,\"LOF_OIM\":\"78\",\"TIP_ENABLED\":true,\"MSE\":0,\"STAR\":\"       \",\"FP_POINT_LRP3\":false,\"OWN\":6,\"FP_POINT_LRP5\":false,\"FP_POINT_LRP4\":false,\"FP_POINT_LRP1\":false,\"MNV_TYPE\":0,\"FP_POINT_LRP0\":false,\"THE_VRCD_INSTR_NON_CONF_DIMENSION\":0,\"EPP_WARNING_POSITION\":\"FALSE\",\"STCA_GLOB_FILT\":false,\"FP_POINT_LEV0\":250,\"IAF_ETO\":\"\",\"FP_POINT_LEV3\":250,\"FP_POINT_LEV4\":250,\"FP_POINT_LEV1\":250,\"FP_POINT_LEV2\":250,\"FP_POINT_LRP2\":true,\"FP_POINT_LEV5\":250,\"TAM\":\"\",\"TCAS_ADSC\":0,\"LOGNTY\":0,\"LOF_OLS\":\"32\",\"FIELD18STS\":\"\",\"VERT_DEV_FLAG\":false,\"MTS\":2,\"FDM_ATCFUNC\":\"NONE\",\"TEMPLATE\":2,\"MNV_DPN\":\"\",\"FP_POINT_CTIM5\":\"16\",\"BCKSSR_CODE\":\"     \",\"DEPRWY\":\"\",\"FP_POINT_XRF3\":false,\"FP_POINT_XRF2\":false,\"NO_APM_ALARM\":false,\"NAN_OIS\":\"32\",\"FP_POINT_XRF1\":false,\"FP_POINT_XRF0\":false,\"NAN_OLM\":\"78\",\"FP_POINT_XRF5\":false,\"FP_POINT_XRF4\":false,\"OLDI_ETO\":\"0829\",\"NEXT_FIR_FRQ\":\"127.000\",\"FPT_LONGITUDE\":0,\"ISSIL\":false,\"OBT\":\"0542\",\"EET\":0,\"DEP_PKBAY\":\"\",\"SAF\":0,\"COORDINATE_COAST\":\"\",\"FP_POINT_X3\":\"21.923055555555557\",\"LTI\":0,\"MES_TRA\":\"REV\",\"NEXT_FIR_AOI\":\"AO4\",\"LTO\":0,\"GMDD\":0,\"FP_POINT_Y3\":\"41.33916666666667\",\"NXS\":0,\"NSC\":false,\"EFL\":0,\"ISTAXI\":false,\"VAW\":false,\"TRANSPOINTLATITUDE\":-1,\"OWNTRA\":0,\"CCA\":0,\"FP_POINT_CDIS5\":\"97\",\"THE_CFL_INST_NON_CONF_VALUE\":0,\"TACSTATUS\":\"N\",\"SCUDS\":-1,\"SIDJUNCTPT\":\"DOBAR\",\"SPDKNOTS\":\"\",\"NTC\":false,\"LOF_OIS\":\"32\",\"ISPAL\":false,\"RVSM\":\"W\",\"FP_POINT_NAME5\":\"VELBA\",\"CCS\":0,\"GMALARM\":0}";
	/**
	 *
	 */
	InnerObserverMessage obs = new InnerObserverMessage();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Thread th2 = new Thread(new Runnable() {

			@Override
			public void run() {
				String[] argsGf = new String[1];
				argsGf[0] = "Macedonia";
				GiForkMain_LIS.main(argsGf);

			}
		});
		th2.start();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		JIPClientThread.addJsonBB(jsonFlight);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		String jsString = jsonFlight.replace("\"OPER\":\"U\"", "\"OPER\":\"D\"");
		JIPClientThread.addJsonBB(jsString);
		obs.mChanged = false;
	}

	/**
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 *
	 */
	@Test
	final void testStorageManager() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Constructor<StorageManager> constructor = StorageManager.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
			assertTrue(false);
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException() instanceof IllegalStateException);
		}

	}

	/**
	 * Test method for {@link com.gifork.blackboard.StorageManager#getContainerStorage()}.
	 */
	@Test
	final void testGetContainerStorage() {
		assertNotEquals(null, StorageManager.getInstance().getContainerStorage());
	}

	/**
	 * Test method for
	 * {@link com.gifork.blackboard.StorageManager#manageStorage(com.gifork.commons.data.IRawData)}.
	 */
	@Test
	final void testManageStorage() {
		StorageManager.manageStorage(RawDataFactory.createFromJson(jsonFlight));
		assert (StorageManager.getInstance().getItemStorageOpt(DataType.FLIGHT_EXTFLIGHT.name(), "0").isPresent());
	}

	/**
	 * Test method for {@link com.gifork.blackboard.StorageManager#getStorage(java.lang.String)}.
	 */
	@Test
	final void testGetStorage() {
		assertNotEquals(null, StorageManager.getInstance().getStorage(DataType.FLIGHT_EXTFLIGHT.name()));
	}

	/**
	 * Test method for {@link com.gifork.blackboard.StorageManager#getItemsStorage(java.lang.String)}.
	 */
	@Test
	final void testGetItemsStorage() {
		assertNotEquals(null, StorageManager.getInstance().getItemsStorage(DataType.FLIGHT_EXTFLIGHT.name()));
	}

	/**
	 * Test method for {@link com.gifork.blackboard.StorageManager#getItemsStorageList()}.
	 */
	@Test
	final void testGetItemsStorageList() {
		assertNotEquals(null, StorageManager.getInstance().getItemsStorageList());
	}

	/**
	 * Test method for
	 * {@link com.gifork.blackboard.StorageManager#getItemStorageOpt(java.lang.String, java.lang.String)}.
	 */
	@Test
	final void testGetItemStorageOpt() {
		assert (StorageManager.getInstance().getItemStorageOpt(DataType.FLIGHT_EXTFLIGHT.name(), "0").isPresent());
	}

	/**
	 * Test method for
	 * {@link com.gifork.blackboard.StorageManager#getCloneItemStorageOpt(java.lang.String, java.lang.String)}.
	 */
	@Test
	final void testGetCloneItemStorageOpt() {
		assert (StorageManager.getInstance().getCloneItemStorageOpt(DataType.FLIGHT_EXTFLIGHT.name(), "0").isPresent());
	}

	/**
	 * Test method for {@link com.gifork.blackboard.StorageManager#getStorageSize(java.lang.String)}.
	 */
	@Test
	final void testGetStorageSize() {
		assertNotEquals(0, StorageManager.getInstance().getStorageSize(DataType.FLIGHT_EXTFLIGHT.name()));
	}

	/**
	 * Test method for
	 * {@link com.gifork.blackboard.StorageManager#register(com.gifork.auxiliary.subjectObserverEventEngine.IObserver, java.lang.String)}.
	 */
	@Test
	final void testRegister() {
		StorageManager.register(obs, DataType.FLIGHT_EXTFLIGHT.name());
		StorageManager.manageStorage(RawDataFactory.createFromJson(jsonFlight));
		while (obs.mChanged) {
			assert (true);
		}

	}

	/**
	 * Test method for
	 * {@link com.gifork.blackboard.StorageManager#notifyChange(com.gifork.commons.data.IRawData)}.
	 */
	@Test
	final void testNotifyChange() {
		StorageManager.print = true;
		StorageManager.register(obs, DataType.FLIGHT_EXTFLIGHT.name());
		StorageManager.notifyChange(RawDataFactory.createFromJson(jsonFlight));
		assertTrue(obs.mChanged);
	}

	/**
	 * @author ALBANESED
	 *
	 */
	public class InnerObserverMessage implements IObserver {

		/**
		 *
		 */
		public boolean mChanged = false;

		/** {@inheritDoc} */
		@Override
		public void update(IRawData subject) {
			assertEquals(subject.getType(), DataType.FLIGHT_EXTFLIGHT.name());
			mChanged = true;
		}

	}
}
