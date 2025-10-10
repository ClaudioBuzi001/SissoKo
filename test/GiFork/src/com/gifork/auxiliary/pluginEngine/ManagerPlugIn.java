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
package com.gifork.auxiliary.pluginEngine;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;
import com.leonardo.infrastructure.plugins.framework.ClasspathHacker;
import com.leonardo.infrastructure.plugins.framework.SimplePluginManager;
import com.leonardo.infrastructure.plugins.interfaces.IExtension;
import com.leonardo.infrastructure.plugins.interfaces.IPluginEntryPoint;
import com.leonardo.infrastructure.plugins.interfaces.IPluginManager;

/**
 * The Class ManagerPlugIn.
 */
public class ManagerPlugIn {
	
	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(ManagerPlugIn.class);
	
	/**
	 * The Class SingletonLoader.
	 */
	private static class SingletonLoader {
		
		/** The Constant instance. */
		private static final ManagerPlugIn instance = new ManagerPlugIn();
	}
	
	/**
	 * Restituisce la istanza singleton.
	 *
	 * @return single instance of ManagerPlugIn
	 */
	public static ManagerPlugIn getInstance() {
		return SingletonLoader.instance;
	}
	

	/**
	 * Inits the.
	 */
	public static void init() {
		IPluginManager pluginManager = SimplePluginManager.getInstance();
		pluginManager.load(jarName -> tryToLoad(jarName));
		pluginManager.init();
		
		final String loggerCaller = "init()";
		logger.logInfo(loggerCaller, "Plugins:");
		
		
		for (IPluginEntryPoint plug : pluginManager.getPlugins()) {
			
			
			logger.logInfo(loggerCaller, plug.getName() + " - " + plug.getDescription());
			
			logger.logInfo(loggerCaller, "   Extensions");
			
			
			
			
			if (plug.getExtensions() != null) {
				for (IExtension pext : plug.getExtensions()) {
					
					logger.logInfo(loggerCaller, "      " + pext.getName() + " -> " + pext.getDescription());
					
					
				}
			}
			
			logger.logInfo(loggerCaller, "   End of Extensions");
			
			
		}
		
		logger.logInfo(loggerCaller, "End of Plugins");		
		
		
	}

	/**
	 * Try to load.
	 *
	 * @param jarName the jar name
	 */
	private static void tryToLoad(String jarName) {
		final String loggerCaller = "tryToLoad()";
		try {
			
			File f = new File(jarName);
			if (f.exists()) {
				ClasspathHacker.addFile(f);
				logger.logInfo(loggerCaller, "Loaded:" + f.getCanonicalPath());
			} else {
				
				f = new File(f.getName());
				if (f.exists()) {
					ClasspathHacker.addFile(f);
					logger.logInfo(loggerCaller, "Loaded:" + f.getCanonicalPath());
				} else {
					
					f = new File(SimplePluginManager.PLUGINS_FOLDER + "/" + f.getName());
					if (f.exists()) {
						ClasspathHacker.addFile(f);
						logger.logInfo(loggerCaller, "Loaded:" + f.getCanonicalPath());
					} else {
						
						Optional<File> fileJar = ClasspathHacker.addFirstMatchFile("", f);
						if (fileJar.isPresent()) {
							logger.logInfo(loggerCaller, "Loaded:" + fileJar.get().getCanonicalPath());
						} else {
							fileJar = ClasspathHacker.addFirstMatchFile("../", f);
							if (fileJar.isPresent()) {
								logger.logInfo(loggerCaller, "Loaded:" + fileJar.get().getCanonicalPath());
							} else {
								logger.logInfo(loggerCaller, "File '" + jarName + "' non caricato");
							}
						}
					}
				}
			}
		} catch (IOException e) {
			logger.logError(loggerCaller, e);
		}

	}

}
