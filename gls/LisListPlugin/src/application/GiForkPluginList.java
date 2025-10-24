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
package application;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.json.XML;

import com.gifork.auxiliary.FileLoaders;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;
import com.leonardo.infrastructure.plugins.interfaces.IExtension;
import com.leonardo.infrastructure.plugins.interfaces.IPluginEntryPoint;

import analyzer.AnalyzerDiagnosticMessage;
import analyzer.AnalyzerExternalOrderACK;
import analyzer.AnalyzerList;
import analyzer.AnalyzerMTCDCallsignColor;
import application.pluginService.ServiceExecuter.ServiceExecuter;
import auxliary.PluginListConstants;
import services.ServiceATSUList;
import services.ServiceAckColor;
import services.ServiceAlarmList;
import services.ServiceAliasList;
import services.ServiceCPDLCList;
import services.ServiceCalculateFgr;
import services.ServiceCoordList;
import services.ServiceFlightPlanOrder;
import services.ServiceProbeList;
import services.ServiceQNHList;
import services.ServiceSCLListUtility;
import services.ServiceSNETList;
import services.ServiceTCTList;

/**
 * The Class GiForkPluginList.
 *
 * @author ggiampietro/pcardoni
 * @version $Revision$
 */
public class GiForkPluginList implements IPluginEntryPoint {

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateLogger(GiForkPluginList.class);

	/**
	 * Inits the.
	 */
	@Override
	public void init() {

		final String configuredListDir = "list";
		final var configuredListArray = loadConfiguredList(
				com.view.manager.ResourceManager.getResourcesPath() + File.separator + "Plugins" + File.separator
						+ PluginListConstants.PLUGIN_LIST_CFG + configuredListDir);

		final AnalyzerList newAnalyzer = new AnalyzerList(configuredListArray);
		final AnalyzerMTCDCallsignColor analyzerMtcd = new AnalyzerMTCDCallsignColor();
		final AnalyzerExternalOrderACK externalOrderACK = new AnalyzerExternalOrderACK();

		final AnalyzerDiagnosticMessage svrMessage = new AnalyzerDiagnosticMessage();

		ServiceExecuter.getInstance().RegisterService(new ServiceCalculateFgr());
		ServiceExecuter.getInstance().RegisterService(new ServiceSCLListUtility());
		ServiceExecuter.getInstance().RegisterService(new ServiceCPDLCList());
		ServiceExecuter.getInstance().RegisterService(new ServiceAliasList());
		ServiceExecuter.getInstance().RegisterService(new ServiceFlightPlanOrder());
		ServiceExecuter.getInstance().RegisterService(new ServiceATSUList());
		ServiceExecuter.getInstance().RegisterService(new ServiceQNHList());
		ServiceExecuter.getInstance().RegisterService(new ServiceTCTList());
		ServiceExecuter.getInstance().RegisterService(new ServiceAlarmList());
		ServiceExecuter.getInstance().RegisterService(new ServiceProbeList());
		ServiceExecuter.getInstance().RegisterService(new ServiceAckColor());
		ServiceExecuter.getInstance().RegisterService(new ServiceCoordList());
		ServiceExecuter.getInstance().RegisterService(new ServiceSNETList());

	}

	/**
	 * Load configured list.
	 *
	 * @param string String
	 * @return the map
	 */
	private static Map<String, Map<String, IRawData>> loadConfiguredList(final String string) {
		final String loggerCaller = "loadConfiguredList()";
		final Map<String, Map<String, IRawData>> listMap = new HashMap<>();
		final File listDir = new File(string);
		if (listDir.exists()) {
			final File[] fileList = listDir.listFiles();

			for (File value : fileList) {

				Map<String, IRawData> dataTypeListArray;
				if (value.isDirectory()) {
					final File[] dataTypeFileList = value.listFiles();
					final String dirName = value.getName();
					dataTypeListArray = listMap.get(dirName);

					if (dataTypeListArray == null) {
						dataTypeListArray = new HashMap<>();
						listMap.put(dirName, dataTypeListArray);
					}

					for (File file : dataTypeFileList) {

						final String fileName = file.getAbsolutePath();
						if (fileName.endsWith(".xml")) {
							final String xml = FileLoaders.loadTxt(fileName);
							final JSONObject ob = XML.toJSONObject(xml).getJSONObject("msg").getJSONObject("list");
							final IRawData xmlJSONObj = RawDataFactory.createFromJson(ob.toString());
							final String listName = file.getName().replace(".xml", "");
							dataTypeListArray.put(listName, xmlJSONObj);
						}
					}
				} else {
					dataTypeListArray = listMap.get("NO_DATA_TYPE");
					if (dataTypeListArray == null) {
						dataTypeListArray = new HashMap<>();
						listMap.put("NO_DATA_TYPE", dataTypeListArray);
					}
					final String fileName = value.getAbsolutePath();
					if (fileName.endsWith(".xml")) {
						final String xml = FileLoaders.loadTxt(fileName);
						final IRawData xmlJSONObj = RawDataFactory.createFromXML(xml);
						final String listName = value.getName();
						dataTypeListArray.put(listName, xmlJSONObj);
					}
				}

			}
		} else {
			LOGGER.logDebug(loggerCaller, "Configuration dir Not Found ");
		}
		return listMap;
	}

	/**
	 * Gets the dependencies.
	 *
	 * @return the dependencies
	 */
	@Override
	public Set<String> getDependencies() {
		return new HashSet<>();
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Override
	public String getDescription() {

		return "";
	}

	/**
	 * Gets the extensions.
	 *
	 * @return the extensions
	 */
	@Override
	public List<IExtension> getExtensions() {

		return new ArrayList<>();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {

		return "GiForkPluginList";
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	@Override
	public String getTitle() {

		return "GiForkPluginList";
	}

}
