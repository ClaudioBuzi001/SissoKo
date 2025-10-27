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

import analyzer.AnalyzerSDA;
import com.leonardo.infrastructure.plugins.interfaces.IExtension;
import com.leonardo.infrastructure.plugins.interfaces.IPluginEntryPoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Class GiForkPluginSDA.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class GiForkPluginSDA implements IPluginEntryPoint{

	/**
	 * Inits the.
	 */
	@Override
	public void init()
	{
		 final AnalyzerSDA analyzerSDA = new AnalyzerSDA();
		 analyzerSDA.register();
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
		
		return "GiForkPluginSDA";
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	@Override
	public String getTitle() {
		
		return "GiForkPluginSDA";
	}


}
