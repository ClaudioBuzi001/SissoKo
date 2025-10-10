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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import com.gifork.auxiliary.FileLoaders;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;
import com.gifork.commons.log.LoggerFactory;
import com.gifork.data_exchange.model.RawStorage;
import com.leonardo.infrastructure.ArgumentManager;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.log.ILogger;

import application.pluginService.ServiceExecuter.ServiceExecuter;
import application.pluginService.services.ServiceConfiguration;

/**
 * The Class GiForkMain.
 */
public class GiForkMain extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2686587917183167060L;

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateMainLogger(GiForkMain.class);

	/** The frame. */
	private static GiForkMain frame;

	/** The content pane. */
	private JPanel contentPane;

	/** The b BK source list. */
	private JList<String> bBKSourceList;

	/** The Json editor. */
	private JEditorPane JsonEditor;

	/** The blackboard list model. */
	private DefaultListModel<String> blackboardListModel = new DefaultListModel<>();

	/** The b BK source list model. */
	private DefaultListModel<String> bBKSourceListModel = new DefaultListModel<>();

	/** The blackboard list. */
	private JList<String> blackboardList;

	/** The highelight text. */
	Highlighter highelightText = null;

	/**
	 * Launch the application.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		final String loggerCaller = "main()";
		ArgumentManager.init(args);
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			LOGGER.logError("Eccezione: " + thread.getName(), throwable);
		});
		EventQueue.invokeLater(() -> {
			try {
				if (args.length == 0) {
					LOGGER.logError(loggerCaller, "No configuration folder provided");
					return;
				}
				
				// Canonicalization of the input
			    Path configPath = Paths.get(args[0]).toRealPath(LinkOption.NOFOLLOW_LINKS);
			    String canonicalConfigPath = configPath.toString();
				
				frame = new GiForkMain(canonicalConfigPath);
				frame.setVisible(false);

				ManagerBlackboard.initRMIEndpoint();
			} catch (Exception e) {
				LOGGER.logError(loggerCaller + ".run()", e);
			}
		});
	}

	/**
	 * Create the frame.
	 *
	 * @param configFolder the config folder
	 */
	public GiForkMain(String configFolder) {

		ServiceExecuter.getInstance().RegisterService(new ServiceConfiguration());

		String XML = FileLoaders.loadTxt(configFolder + "/GI-FORK/GiForkConfig/configuration.xml");
		ManagerBlackboard.addXML(XML);
		setAlwaysOnTop(true);
		frame = this;
		setTitle("GI-FORK STATUS PANEL");
		setPreferredSize(new Dimension(1000, 0));
		setBounds(100, 100, 999, 589);
		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(1000, 10));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel buttonsPanel = new JPanel();
		contentPane.add(buttonsPanel, BorderLayout.NORTH);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(arg0 -> scanningBlackBoard());
		buttonsPanel.add(btnRefresh);

		JButton btnDumpSelectedBb = new JButton("DUMP SELECTED BB TO FILE");
		btnDumpSelectedBb.addActionListener(arg0 -> dumpToFileSelectedBB());
		buttonsPanel.add(btnDumpSelectedBb);

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(2, 15));
		buttonsPanel.add(separator);

		JLabel lblTxtfilter = new JLabel("txtFilter:");
		buttonsPanel.add(lblTxtfilter);

		JLabel lblClassfilter = new JLabel("fileFilter:");
		buttonsPanel.add(lblClassfilter);

		JSplitPane splitPane_1 = new JSplitPane();
		contentPane.add(splitPane_1, BorderLayout.CENTER);

		JPanel bKSourcePanel = new JPanel();
		splitPane_1.setRightComponent(bKSourcePanel);
		bKSourcePanel.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		bKSourcePanel.add(splitPane, BorderLayout.CENTER);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JPanel panel_1 = new JPanel();
		panel_1.setMinimumSize(new Dimension(10, 200));
		splitPane.setLeftComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblItems = new JLabel("ITEMS");
		panel_1.add(lblItems, BorderLayout.NORTH);
		lblItems.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblItems.setHorizontalAlignment(SwingConstants.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);

		bBKSourceList = new JList<>();
		scrollPane.setViewportView(bBKSourceList);
		bBKSourceList.setModel(this.bBKSourceListModel);
		bBKSourceList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				BKSourceSelected();
			}
		});

		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setMinimumSize(new Dimension(0, 0));
		scrollPane_1.setLocation(new Point(40, 0));
		panel.add(scrollPane_1, BorderLayout.CENTER);
		scrollPane_1.setPreferredSize(new Dimension(450, 0));

		JsonEditor = new JEditorPane();
		JsonEditor.setMinimumSize(new Dimension(6, 100));
		scrollPane_1.setViewportView(JsonEditor);
		highelightText = this.JsonEditor.getHighlighter();

		JPanel panel_2 = new JPanel();
		scrollPane_1.setColumnHeaderView(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		textSearch = new JTextField();
		textSearch.addActionListener(e -> search());
		panel_2.add(textSearch);
		textSearch.setColumns(10);

		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(e -> search());
		panel_2.add(btnSearch, BorderLayout.EAST);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setMinimumSize(new Dimension(0, 0));
		scrollPane_2.setPreferredSize(new Dimension(250, 0));
		panel.add(scrollPane_2, BorderLayout.EAST);

		auxEditorPane = new JEditorPane();
		scrollPane_2.setViewportView(auxEditorPane);
		auxEditorPane.setMinimumSize(new Dimension(6, 100));

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		scrollPane_2.setColumnHeaderView(panel_3);

		JLabel lblAuxiliaryData = new JLabel("AUXILIARY DATA ");
		panel_3.add(lblAuxiliaryData);

		JPanel blackBoardPanel = new JPanel();
		splitPane_1.setLeftComponent(blackBoardPanel);
		blackBoardPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblBlackboard = new JLabel("BLACKBOARD");
		blackBoardPanel.add(lblBlackboard, BorderLayout.NORTH);
		lblBlackboard.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblBlackboard.setHorizontalAlignment(SwingConstants.CENTER);

		JScrollPane scrollPane_3 = new JScrollPane();
		blackBoardPanel.add(scrollPane_3, BorderLayout.CENTER);
		blackboardList = new JList<>();
		blackboardList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				blackboardSelected();
			}
		});
		blackboardList.setModel(this.blackboardListModel);
		scrollPane_3.setViewportView(blackboardList);

		setVisible(false);
	}

	/**
	 * Search.
	 */
	protected void search() {
		String jsonStr = this.JsonEditor.getText();
		highelightText.removeAllHighlights();
		String toSearch = this.getTextSearch().getText();
		String safeRegex = Pattern.quote(toSearch);

		Pattern pattern = Pattern.compile(safeRegex);
		Matcher matcher = pattern.matcher(jsonStr);

		boolean matchFound = matcher.matches();
		if (!matchFound) {
			while (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				Rectangle viewRect;
				try {
					viewRect = this.JsonEditor.modelToView(start);
					this.JsonEditor.scrollRectToVisible(viewRect);
				} catch (BadLocationException e) {

					LOGGER.logError("Error: ", e);
				}
				try {
					highelightText.addHighlight(start, end, DefaultHighlighter.DefaultPainter);
				} catch (BadLocationException e1) {
					LOGGER.logError("Error: ", e1);
				}
			}
		}
	}

	/**
	 * Dump to file selected BB.
	 */
	protected void dumpToFileSelectedBB() {
		List<String> bbNameList = this.blackboardList.getSelectedValuesList();

		for (Object bbNameObj : bbNameList) {
			String bbName = (String) bbNameObj;
			if (bbName != null && !bbName.isEmpty()) {
				bbName = (String) bbName.subSequence(0, bbName.indexOf(" ("));
				String BB_DUMPED = "BB_DUMPED";
				String BBPAth = Strings.concat(BB_DUMPED, "/", bbName);

				File BB_DUMPED_DIR = new File(BB_DUMPED);
				File BBPAth_DIR = new File(BBPAth);

				BB_DUMPED_DIR.mkdir();
				BBPAth_DIR.mkdir();

				if (bbName != null) {
					var storage = StorageManager.getItemsStorage(bbName);
					String basePath = Strings.concat(BBPAth, "/");
					for (var entry : storage.entrySet()) {
						String kkey = entry.getKey();
						IRawData json = entry.getValue();
						String fileName = Strings.concat(basePath, kkey, ".json");
						FileLoaders.saveTxt(fileName, json.toString());
					}
				}
			}
		}
	}

	/**
	 * Sort.
	 *
	 * @param model the model
	 */
	private static void sort(DefaultListModel<String> model) {
		for (int i = 0; i < model.size(); i++) {
			for (int j = i + 1; j < model.size(); j++) {
				String appo = model.get(i);
				String appo2 = model.get(j);
				if (appo.compareTo(appo2) > 0) {
					model.set(j, appo);
					model.set(i, appo2);
				}
			}
		}
	}

	/**
	 * Scanning black board.
	 */
	private void scanningBlackBoard() {
		Map<String, RawStorage> blackBoardList = StorageManager.getInstance().getContainerStorage();
		this.blackboardListModel.removeAllElements();
		this.bBKSourceListModel.removeAllElements();
		this.JsonEditor.setText("");
		this.auxEditorPane.setText("");
		for (String key : blackBoardList.keySet()) {
			String str = Strings.concat(key, " (",
					String.valueOf(StorageManager.getInstance().getStorage(key).getSize()), ")");
			this.blackboardListModel.addElement(str);
		}
		sort(this.blackboardListModel);

		this.blackboardList.setSelectedIndex(0);
		this.bBKSourceList.setSelectedIndex(0);
		BKSourceSelected();

		setTitle(this.memoryUsage());
	}

	/**
	 * BK source selected.
	 */
	private void BKSourceSelected() {
		String bbName = this.blackboardList.getSelectedValue();
		if (!bbName.isEmpty()) {
			String kbNAme = this.bBKSourceList.getSelectedValue();
			bbName = (String) bbName.subSequence(0, bbName.indexOf(" ("));
			if (kbNAme == null) {
				return;
			}

			Optional<IRawData> item = BlackBoardUtility.getDataOpt(bbName, kbNAme);
			item.ifPresent(it -> {
				String Formatted = "";
				String FormattedAux = "";
				Formatted = it.toJsonString(4);
				IRawDataElement jsonAux = it.getAuxiliaryData();
				FormattedAux = jsonAux.toJsonString(4);
				this.JsonEditor.setText(Formatted);
				this.auxEditorPane.setText(FormattedAux);
			});

		}
	}

	/**
	 * Blackboard selected.
	 */
	private void blackboardSelected() {
		String bbName = this.blackboardList.getSelectedValue();
		bbName = (String) bbName.subSequence(0, bbName.indexOf(" ("));

		this.bBKSourceListModel.removeAllElements();
		this.JsonEditor.setText("");
		this.auxEditorPane.setText("");

		bBKSourceListModel.addAll(StorageManager.getItemsStorage(bbName).keySet());

		sort(this.bBKSourceListModel);

		this.bBKSourceList.setSelectedIndex(0);
		BKSourceSelected();
	}

	/**
	 * Gets the list.
	 *
	 * @return the list
	 */
	public JList<String> getList() {
		return bBKSourceList;
	}

	/**
	 * Gets the editor pane.
	 *
	 * @return the editor pane
	 */
	public JEditorPane getEditorPane() {
		return JsonEditor;
	}

	/**
	 * Gets the blackboard list.
	 *
	 * @return the blackboard list
	 */
	public JList<String> getBlackboardList() {
		return blackboardList;
	}

	/** The Constant MEGABYTE. */
	private static final long MEGABYTE = 1024L * 1024L;

	/** The text search. */
	private JTextField textSearch;

	/** The aux editor pane. */
	private JEditorPane auxEditorPane;

	/**
	 * Bytes to megabytes.
	 *
	 * @param bytes the bytes
	 * @return the long
	 */
	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	/**
	 * Memory usage.
	 *
	 * @return the string
	 */
	public String memoryUsage() {
		String memoryInfo = "";

		Runtime runtime = Runtime.getRuntime();

		long memory = runtime.totalMemory() - runtime.freeMemory();

		System.out.println("Tot. Memory is bytes		: " + runtime.totalMemory() / 1000 + " MB");
		System.out.println("Used memory is bytes		: " + memory / 1000 + " KB");
		System.out.println("Used memory is megabytes	: " + bytesToMegabytes(memory) + " MB");

		memoryInfo = "Tot. Memory is bytes		: " + runtime.totalMemory() / 1000 + " MB - Used memory is bytes: "
				+ memory / 1024 + " KB - " + "Used memory is megabytes: " + bytesToMegabytes(memory) + " MB";
		return memoryInfo;
	}

	/**
	 * Gets the text search.
	 *
	 * @return the text search
	 */
	public JTextField getTextSearch() {
		return textSearch;
	}

	/**
	 * Gets the aux editor pane.
	 *
	 * @return the aux editor pane
	 */
	public JEditorPane getAuxEditorPane() {
		return auxEditorPane;
	}
}
