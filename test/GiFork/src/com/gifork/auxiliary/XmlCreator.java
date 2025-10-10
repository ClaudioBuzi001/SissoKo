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

import java.util.HashMap;

/**
 * The Class XmlCreator.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class XmlCreator
{
	
	/** The Constant list. */
	public static final String list 			= "list";
	
	/** The Constant timeLines. */
	public static final String timeLines 		= "timeLines";
	
	/** The Constant object_timeline. */
	public static final String object_timeline 	= "object_timeline";
	
	/** The Constant service. */
	public static final String service 			= "service";
	
	/** The Constant object. */
	public static final String object 			= "object";

	/** The xml type. */
	private String xmlType = "";
	
	/** The tag type. */
	private String tagType ="v";
	
	/** The safe and clean xml check map. */
	HashMap<String , String> safeAndCleanXmlCheckMap = new HashMap<>();
	
	/**
	 * Instantiates a new xml creator.
	 */
	public XmlCreator()
	{
		safeAndCleanXmlCheckMap.clear();
	}
	
	/**
	 * Xml header.
	 *
	 * @param oper String
	 * @param type  String
	 * @return String
	 */
	public String xmlHeader(String oper , String type)
	{
		StringBuilder xmlheader = new StringBuilder();
		this.xmlType = type;
		safeAndCleanXmlCheckMap.clear();
		if (type.equals(list) )
		{
			xmlheader.append("<msg><list oper=\"").append(oper).append("\"  >");
		}
		else if (type.equals(timeLines) )
		{
			xmlheader.append("<msg><timeLines oper=\"").append(oper).append("\" >");
		}
		else if (type.equals(object_timeline) )
		{
			xmlheader.append("<msg><object_timeline oper=\"").append(oper).append("\" >");
		}
		else if (type.equals(service) )
		{
			xmlheader.append("<msg><service type=\"").append(oper).append("\" >");
		}
		else if (type.equals(object) )
		{
			xmlheader.append("<msg><object oper=\"").append(oper).append("\" type=\"TRACK\">");
		}
		else
		{
			xmlheader.append("<msg><object oper=\"").append(oper).append("\" type=\"").append(type).append("\">");
			this.xmlType = "object";

		}
		return xmlheader.toString();
	}

	/**
	 * Xml footer.
	 *
	 * @param type String
	 * @return String
	 */
	public String xmlFooter( String type)
	{
		String xmlheader ;
		if (type.equals("list") )
		{
			xmlheader	 = "</list></msg>\n";
		}
		else if (type.equals("timeLines") )
		{
			xmlheader	 = "</timeLines></msg>\n";
		}
		else if (type.equals("object_timeline") )
		{
			xmlheader	 = "</object_timeline></msg>\n";
		}
		else if (type.equals("service") )
		{
			xmlheader	 = "</service></msg>\n";
		}
		else
		{
			xmlheader	 = "</object></msg>\n";
		}
		safeAndCleanXmlCheckMap.clear();
		return xmlheader;
	}

	/**
	 * Xml footer.
	 *
	 * @return String
	 */
	public String xmlFooter( )
	{
		StringBuilder xmlheader	 = new StringBuilder();
		xmlheader.append("</").append(this.xmlType).append("></msg>\n");
		safeAndCleanXmlCheckMap.clear();
		return xmlheader.toString();
	}

	/**
	 * Xml line.
	 *
	 * @param _key String
	 * @param _data String
	 * @param _attributes HashMap<String, String>
	 * @return String
	 */
	public String xmlLine(String _key , String _data , HashMap<String, String> _attributes)
	{
		StringBuilder  xmlline = new StringBuilder();

			setTagType("v");
			if (this.xmlType.equals("list") || this.xmlType.equals("timeLines"))
			{
				setTagType("l");
			}
			else if (this.xmlType.equals("service"))
			{
				setTagType("p");
			}

			xmlline.append("<").append(getTagType()).append(" key=\"").append(_key).append("\" data=\"").append(_data).append("\" ");

			if (_attributes != null)
			{
				for (HashMap.Entry<String, String> entry : _attributes.entrySet())
				{
						    xmlline.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\" " );
				}
			}

			xmlline.append("/>");

		return xmlline.toString();
	}


	/**
	 * Xml line.
	 *
	 * @param _key String
	 * @param _data String
	 * @return String
	 */
	public String xmlLine(String _key , String _data )
	{
			return xmlLine(_key , _data , null);
	}
	
	/**
	 * Gets the tag type.
	 *
	 * @return String
	 */
	public String getTagType()
	{
		return this.tagType;
	}
	
	/**
	 * Sets the tag type.
	 *
	 * @param tagType String
	 */
	public void setTagType(String tagType)
	{
		this.tagType = tagType;
	}
}
