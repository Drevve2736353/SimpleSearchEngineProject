import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *   A graphical interface to the information retrieval system.
 */
public class SearchGUI extends JFrame {
	public SearchGUI() {
	}

	/**  The indexer creating the search index. */
	Indexer indexer;

	/**  Directories that should be indexed. */
	LinkedList<String> dirNames = new LinkedList<String>();

	/**  Indexers to be retrieved from disk. */
	LinkedList<String> indexFiles = new LinkedList<String>();

	/** Maximum number of indexes an we can read from disks */
	public static final int MAX_NUMBER_OF_INDEX_FILES = 10;

	/**  The query type (either intersection, phrase, or ranked). */
	int queryType = Index.RANKED_QUERY;

	/**  The index type (either hashed or mega). */
	int indexType = Index.HASHED_INDEX;

	/**  Lock to prevent simultaneous access to the index. */
	Object indexLock = new Object();

	/** File containing link graph for PageRank */
	public String linksFile;

	/*
	 *    GUI resources
	 */
	public JTextField queryWindow = new JTextField("", 28);
	public JTextArea resultWindow = new JTextArea("", 23, 28);
	private JScrollPane resultPane = new JScrollPane(resultWindow);
	private Font queryFont = new Font("Arial", Font.BOLD, 20);
	private Font resultFont = new Font("Arial", Font.BOLD, 14);
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenu optionsMenu = new JMenu("Search options");
	JMenuItem saveItem = new JMenuItem("Save index and exit");
	JMenuItem quitItem = new JMenuItem("Quit");
	JRadioButtonMenuItem intersectionItem = new JRadioButtonMenuItem("Intersection query");
	JRadioButtonMenuItem unionItem = new JRadioButtonMenuItem("Union query");
	JRadioButtonMenuItem phraseItem = new JRadioButtonMenuItem("Phrase query");
	JRadioButtonMenuItem rankedItem = new JRadioButtonMenuItem("Ranked retrieval");
	ButtonGroup queries = new ButtonGroup();
	Insets insets = new Insets(2, 3, 2, 3);


 void createGUI() {
 
		setSize(600, 650);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout(2, 2));
		getContentPane().add(p, BorderLayout.CENTER);

	
		menuBar.add(fileMenu);
		menuBar.add(optionsMenu);
		fileMenu.add(saveItem);
		fileMenu.add(quitItem);
		optionsMenu.add(intersectionItem);
		optionsMenu.add(unionItem);
		optionsMenu.add(phraseItem);
		optionsMenu.add(rankedItem);
		queries.add(intersectionItem);
		queries.add(unionItem);
		queries.add(phraseItem);
		queries.add(rankedItem);
		rankedItem.setSelected(true);
		getContentPane().add(menuBar, BorderLayout.PAGE_START);

	
		queryWindow.setFont(queryFont);
		queryWindow.setMargin(insets);
		p.add(queryWindow, BorderLayout.PAGE_START);

		
		resultWindow.setFont(resultFont);
		resultWindow.setEditable(false);
		resultWindow.setMargin(insets);
		p.add(resultPane, BorderLayout.CENTER);

	
		setVisible(true);

		Action search = new AbstractAction() {
			
			private static final long serialVersionUID = 1L; 

				public void actionPerformed(ActionEvent e) {
					
					String searchstring = SimpleTokenizer.normalize(queryWindow.getText());
					StringTokenizer tok = new StringTokenizer(searchstring);
					LinkedList<String> searchterms = new LinkedList<String>();
					while (tok.hasMoreTokens()) {
						searchterms.add(tok.nextToken());
	}
					
};

		queryWindow.registerKeyboardAction(search,
											"",
											KeyStroke.getKeyStroke("ENTER"),
											JComponent.WHEN_FOCUSED); 
		
		Action saveAndQuit = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					resultWindow.setText("\n  Saving index...");
					indexer.index.cleanup();
					System.exit(0);
				}
			};
		saveItem.addActionListener(saveAndQuit);
		
		
		Action quit = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			};
		quitItem.addActionListener(quit);

		
		Action setIntersectionQuery = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					queryType = Index.INTERSECTION_QUERY;
				}
			};
		intersectionItem.addActionListener(setIntersectionQuery);

		Action setUnionQuery = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					queryType = Index.UNION_QUERY;
				}
			};
		unionItem.addActionListener(setUnionQuery);
				
		Action setPhraseQuery = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					queryType = Index.PHRASE_QUERY;
				}
			};
		phraseItem.addActionListener(setPhraseQuery);
				
		Action setRankedQuery = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					queryType = Index.RANKED_QUERY;
				}
			};
		rankedItem.addActionListener(setRankedQuery);

	}
 

	private void index() {
		synchronized (indexLock) {
			resultWindow.setText("Indexing, please wait...");
			for (int i=0; i<dirNames.size(); i++) {
				File dokDir = new File(dirNames.get(i));
				indexer.processFiles(dokDir);
			}
			resultWindow.setText("Done!");
		}
	};


	public static void main(String[] args) {
		SearchGUI s = new SearchGUI();
		s.createGUI();
		s.decodeArgs(args);
		s.index();
	}

}