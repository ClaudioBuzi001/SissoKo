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
package com.gifork.auxiliary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

/**
 * The Class FileLoaders.
 *
 * @author natoags02
 * @version
 */
public class FileLoaders {

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(FileLoaders.class);

	/**
	 * Load txt.
	 *
	 * @param FileName String
	 * @return String
	 */
	public static String loadTxt(final String FileName) {
		StringBuilder bufferTxt = new StringBuilder();
		File checkFile = new File(FileName);
		if (checkFile.exists()) {

			try (BufferedReader buffer_reader = new BufferedReader(new FileReader(FileName))) {
				String line = buffer_reader.readLine();
				while (line != null) {
					bufferTxt.append(line).append("\n");
					line = buffer_reader.readLine();
				}
			} catch (final IOException e) {
				logger.logError("loadTxt", e);
			}
		}

		return bufferTxt.toString();
	}

	/**
	 * Save txt.
	 *
	 * @param fileName String
	 * @param Data     String
	 * @return File
	 */
	public static File saveTxt(final String fileName, final String Data) {
		File file = null;
		if (fileName != null && Data != null) {
			file = new File(fileName);
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(Data);
			} catch (final IOException e) {
				logger.logError("saveTxt", e);
			}
		}
		return file;
	}




}
