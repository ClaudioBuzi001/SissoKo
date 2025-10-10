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
package com.view.ui.controls;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;

/**
 * The Class JsonCodeArea.
 */
public class JsonCodeArea {

	/** The code area. */
	private final CodeArea m_codeArea = new CodeArea();

	/** The node. */
	private final Node m_node;

	/** The find string. */
	private String m_findString;

	/** The last find. */
	private final LastFind m_lastFind = new LastFind();

	/** The Constant JSON_CURLY. */
	private static final String JSON_CURLY = "(?<JSONCURLY>\\{|\\})";

	/** The Constant JSON_PROPERTY. */
	private static final String JSON_PROPERTY = "(?<JSONPROPERTY>\\\".*\\\")\\s*:\\s*";

	/** The Constant JSON_VALUE. */
	private static final String JSON_VALUE = "(?<JSONVALUE>\\\".*\\\")";

	/** The Constant JSON_ARRAY. */
	private static final String JSON_ARRAY = "\\[(?<JSONARRAY>.*)\\]";

	/** The Constant JSON_NUMBER. */
	private static final String JSON_NUMBER = "(?<JSONNUMBER>\\d*.?\\d*)";

	/** The Constant JSON_BOOL. */
	private static final String JSON_BOOL = "(?<JSONBOOL>true|false)";

	/** The Constant FINAL_REGEX. */
	private static final Pattern FINAL_REGEX = Pattern.compile(JSON_CURLY + "|" + JSON_PROPERTY + "|" + JSON_VALUE + "|"
			+ JSON_ARRAY + "|" + JSON_BOOL + "|" + JSON_NUMBER);

	/**
	 * Instantiates a new json code area.
	 */
	public JsonCodeArea() {
		m_codeArea.setParagraphGraphicFactory(LineNumberFactory.get(m_codeArea));
		m_codeArea.textProperty().addListener((obs, oldText, newText) -> setHighLighting(newText));
		m_node = new VirtualizedScrollPane<>(m_codeArea);
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return m_codeArea.getText();
	}

	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) {
		m_codeArea.replaceText(text);
		resetFind();
		m_codeArea.selectRange(0, 0);
		m_codeArea.requestFollowCaret();
	}

	/**
	 * Gets the node.
	 *
	 * @return the node
	 */
	public Node getNode() {
		return m_node;
	}

	/**
	 * Gets the code area.
	 *
	 * @return the code area
	 */
	public CodeArea getCodeArea() {
		return m_codeArea;
	}

	/**
	 * Bind bidirectional.
	 *
	 * @param text the text
	 */
	public void bindBidirectional(SimpleStringProperty text) {
		m_codeArea.textProperty().addListener((ob, ol, nw) -> {
			if (!text.get().equals(nw)) {
				text.set(nw);
			}
		});
		text.addListener((ob, ol, nw) -> {
			if (!m_codeArea.getText().equals(nw)) {
				setText(nw);
			}
		});
	}

	/**
	 * Reset find.
	 */
	public void resetFind() {
		m_findString = "";
		m_lastFind.reset();

		m_codeArea.setStyleSpans(0, computeHighlighting(m_codeArea.getText()));
	}

	/**
	 * Find.
	 *
	 * @param findString the find string
	 * @return true, if successful
	 */
	public boolean find(String findString) {
		if (!findString.equals(m_findString)) {
			m_findString = findString;
			m_lastFind.reset();
		}
		return findNext();
	}

	/**
	 * Find next.
	 *
	 * @return true, if successful
	 */
	public boolean findNext() {
		Pattern pattern = Pattern.compile(m_findString);
		Matcher matcher = pattern.matcher(m_codeArea.getText());
		boolean found = matcher.find(m_lastFind.Next);
		if (found) {

			setHighLighting(m_codeArea.getText());

			m_lastFind.set(matcher.start(), matcher.end());

			m_codeArea.selectRange(matcher.start(), matcher.end());
			m_codeArea.setStyle(matcher.start(), matcher.end(), Collections.singletonList("match"));

			m_codeArea.requestFollowCaret();
			findHighLight();
		} else {
			m_lastFind.reset();
			m_codeArea.selectRange(0, 0);

			setHighLighting(m_codeArea.getText());
		}
		return found;
	}

	/**
	 * Find high light.
	 */
	private void findHighLight() {
		if (m_lastFind.isActive()) {
			Pattern pattern = Pattern.compile(m_findString);
			Matcher matcher = pattern.matcher(m_codeArea.getText());
			int start = 0;
			while (matcher.find(start)) {
				m_codeArea.setStyle(matcher.start(), matcher.end(), Collections.singletonList("founded"));
				start = matcher.end();
			}
		}
	}

	/**
	 * Sets the high lighting.
	 *
	 * @param newText the new high lighting
	 */
	private void setHighLighting(String newText) {
		m_codeArea.setStyleSpans(0, computeHighlighting(newText));
		findHighLight();
	}

	/**
	 * Compute highlighting.
	 *
	 * @param text the text
	 * @return the style spans
	 */
	public StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = FINAL_REGEX.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = matcher.group("JSONPROPERTY") != null ? "json_property"
					: matcher.group("JSONVALUE") != null ? "json_value"
							: matcher.group("JSONARRAY") != null ? "json_array"
									: matcher.group("JSONCURLY") != null ? "json_curly"
											: matcher.group("JSONBOOL") != null ? "json_bool"
													: matcher.group("JSONNUMBER") != null ? "json_number" : null;
			/* never happens */
			assert styleClass != null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}

	/**
	 * The Class LastFind.
	 */
	private static class LastFind {

		/** The Start. */
		public int Start = 0;

		/** The End. */
		public int End = 0;

		/** The Next. */
		public int Next = 0;

		/**
		 * Sets the.
		 *
		 * @param start the start
		 * @param end   the end
		 */
		public void set(int start, int end) {
			Start = start;
			End = end;
			Next = end;
		}

		/**
		 * Checks if is active.
		 *
		 * @return true, if is active
		 */
		public boolean isActive() {
			return End > Start && Start > 0;
		}

		/**
		 * Reset.
		 */
		public void reset() {
			Start = 0;
			End = 0;
			Next = 0;
		}
	}

}
