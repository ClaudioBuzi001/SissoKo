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
package common;

/**
 * The Class Alias.
 */
public class Alias {

	/** The alias data. */
	private String aliasData;

	/** The alias key. */
	private String aliasKey;

	/**
	 * Instantiates a new alias.
	 *
	 * @param key  the key
	 * @param data the data
	 */
	public Alias(final String key, final String data) {
		this.aliasData = data;
		this.aliasKey = key;
	}

	/**
	 * Gets the alias data.
	 *
	 * @return the alias data
	 */
	public String getAliasData() {
		return aliasData;
	}

	/**
	 * Sets the alias data.
	 *
	 * @param aliasData the new alias data
	 */
	public void setAliasData(final String aliasData) {
		this.aliasData = aliasData;
	}

	/**
	 * Gets the alias key.
	 *
	 * @return the alias key
	 */
	public String getAliasKey() {
		return aliasKey;
	}

	/**
	 * Sets the alias key.
	 *
	 * @param aliasKey the new alias key
	 */
	public void setAliasKey(final String aliasKey) {
		this.aliasKey = aliasKey;
	}

}
