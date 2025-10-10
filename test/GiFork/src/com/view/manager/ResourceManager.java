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
package com.view.manager;

import java.io.File;
import java.util.HashMap;

import com.leonardo.infrastructure.PathUtils;

import javafx.scene.image.Image;

/**
 * The Class ResourceManager.
 */
public final class ResourceManager {
	
	
	

	/**
	 * The Enum FilesPath.
	 */
	public enum FilesPath {
		
		/** The Config. */
		Config("config.ini");

		/** The tostring. */
		private final String m_tostring;

		/**
		 * Instantiates a new files path.
		 *
		 * @param value the value
		 */
		FilesPath(String value) {
			m_tostring = value;
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return m_tostring;
		}

	}

	/**
	 * The Enum FoldersPath.
	 */
	public enum FoldersPath {

		/** The Icons. */
		Icons("icons"),

		/** The Scenes. */
		Scenes("scenes");

		/** The tostring. */
		private final String m_tostring;

		/**
		 * Instantiates a new folders path.
		 *
		 * @param value the value
		 */
		FoldersPath(String value) {
			m_tostring = value;
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return m_tostring;
		}
	}

	/**
	 * The Enum IconsFile.
	 */
	public enum IconsFile {
		
		/** The Ico app. */
		IcoApp("appicon.png"), 
 /** The Ico un know icon. */
 
		IcoUnKnowIcon("application_xp_terminal.png"), 
 /** The Ico link to generic document. */
 
		IcoLinkToGenericDocument("IMG_GENERIC_FILE.png"), 
 /** The Ico link to XML document. */
 
		IcoLinkToXMLDocument("page_white_code.png"), 
 /** The Ico link to JSON document. */
 
		IcoLinkToJSONDocument("tag_orange.png"), 
 /** The Ico link to folder. */
 
		IcoLinkToFolder("IMG_PINK_CLOSE_FOLDER.png"), 
 /** The Ico node separator. */
 
		IcoNodeSeparator("TVSeparator.png"), 
 /** The Ico node folder. */
 
		IcoNodeFolder("IMG_GRAY_CLOSE_FOLDER.png"), 
 /** The Ico link to web. */
 
		IcoLinkToWeb("world_link.png");

		/** The tostring. */
		private final String m_tostring;

		/**
		 * Instantiates a new icons file.
		 *
		 * @param value the value
		 */
		IconsFile(String value) {
			m_tostring = value;
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return m_tostring;
		}

	}

	
	
	/** The Constant m_resourceList. */
	
	private static final HashMap<String, Object> m_resourceList = new HashMap<>();
	
	/** The resources path. */
	private static String m_resources_path = "";
	
	
	/**
	 * Instantiates a new resource manager.
	 */
	
	private ResourceManager() {
	}

	
	
	/**
	 * Gets the resources path.
	 *
	 * @return the resources path
	 */
	
	public static String getResourcesPath() {
		return m_resources_path;
	}
	
	
	

	/**
	 * Gets the image resource.
	 *
	 * @param ico the ico
	 * @return the image resource
	 */
	public static Image getImageResource(IconsFile ico) {
		String resourceName = "/icons/" + ico.toString();
		Image image = (Image) m_resourceList.get(resourceName);
		if (image == null) {
			image = new Image("file:"+m_resources_path+File.separator+resourceName);
			m_resourceList.put(resourceName, image);
		}
		return image;
	}
	
	/**
	 * Sets the resources path.
	 *
	 * @param resources the new resources path
	 */
	public static void setResourcesPath(String resources) {
		m_resources_path = resources.replace("\\", "/");
	}
	
	/**
	 * Gets the image resource.
	 *
	 * @param file the file
	 * @return the image resource
	 */
	public static Image getImageResource(File file) {
		
		String resourceName = "/icons/" +  file.getName();
		Image image = (Image) m_resourceList.get(resourceName);
		if (image == null) {
			image = new Image("file:"+m_resources_path+File.separator+resourceName);
			m_resourceList.put(resourceName, image);
		}
		return image;
	}
	
	/**
	 * Gets the icon of item.
	 *
	 * @param item the item
	 * @return the icon of item
	 */
	public static Image getIconOfItem(File item) {
		String ext = PathUtils.getExtension(item).toLowerCase();
		Image retImage = null;
		switch (ext) {
		case "txt":
			retImage = getImageResource(IconsFile.IcoLinkToGenericDocument);
			break;
		case "xml":
			retImage = getImageResource(IconsFile.IcoLinkToXMLDocument);
			break;
			
		case "json":
			retImage = getImageResource(IconsFile.IcoLinkToJSONDocument);
			break;

		default:
			retImage = getImageResource(IconsFile.IcoUnKnowIcon);
			break;
		}

		return retImage;
	}
}
