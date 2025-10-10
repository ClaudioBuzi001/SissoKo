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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.leonardo.infrastructure.plugins.interfaces.IExtension;
import com.leonardo.infrastructure.plugins.interfaces.IPluginEntryPoint;

import analyzer.AnalyzerDirectionFinder;
import analyzer.AnalyzerLabel;
import analyzer.AnalyzerMtcd;
import analyzer.AnalyzerPlot;
import analyzer.AnalyzerProbe;

/**
 * The Class GiForkPluginLabel.
 */
public class GiForkPluginLabel implements IPluginEntryPoint {

	/**
	 * Inits the.
	 */
	@Override
	public void init() {

		AnalyzerLabel label = new AnalyzerLabel();
		label.register();
		AnalyzerMtcd mtcd = new AnalyzerMtcd();
		mtcd.register();
		AnalyzerPlot plotAnalyzer = new AnalyzerPlot();
		plotAnalyzer.register();
		AnalyzerProbe probeAnalyzer = new AnalyzerProbe();
		probeAnalyzer.register();
		AnalyzerDirectionFinder directionFinderAnalyzer = new AnalyzerDirectionFinder();
		directionFinderAnalyzer.register();
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
		return "GiForkPluginLabel";
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	@Override
	public String getTitle() {
		return "GiForkPluginLabel";
	}

}
