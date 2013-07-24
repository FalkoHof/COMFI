/*
 * Copyright (C) 2012-2013 Falko Hofmann Max Planck Institute for Biology
 * of Ageing, Cologne (MPI-age)
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package age.mpg.de.comfi.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import age.mpg.de.comfi.downloader.DependenciesDownloader;
import age.mpg.de.comfi.gui.icons.IconLoader;
import age.mpg.de.comfi.index.IndexBuilder;
import age.mpg.de.comfi.index.IndexDepedenciesParser;
import age.mpg.de.comfi.managers.TaskManagerManager;
import age.mpg.de.comfi.properties.PluginProperties;

import cytoscape.logger.CyLogger;


public class IndexDialogues extends JDialog {
	
	private boolean homologeneCheck = false, uniprotCheck = false, yeastgenomeCheck = false, luceneCheck = false, corumCheck = false, cyc2008Check = false;
	private File uniprotFile, homoloGeneFile, yeastGenomeFile ,corumFile, cyc2008File, luceneIndexFile;
	private CyLogger logger = CyLogger.getLogger(this.getClass());

	//private final String download = "Download";
	private  JLabel uniprotLabel, homologeneLabel,yeastGenomeLabel,corumLabel ,cyc2008Label,luceneLabel, luceneStatusLabel;
	private ImageIcon download = IconLoader.getInstance().getDownloadIcon();
	private static final String titel = PluginProperties.getInstance().getPluginName() + " - index builder/downloader";
	
	
	public IndexDialogues(Frame owner){
		super(owner,titel,true);
		createIndexDialogue();
	}
	
	
	
	//methods that checks which files/dependencies are present and sets and icon according to the value
	public void checkDependencies(){
		String folder = PluginProperties.getInstance().getDependenciesFolder();
		String corum = PluginProperties.getInstance().getCorumXmlName();
		String cyc2008 = PluginProperties.getInstance().getCYC2008TabName();
		String lucene = PluginProperties.getInstance().getLuceneIndexName();
		String homologene = PluginProperties.getInstance().getHomoloGeneFileName();
		String uniprot = PluginProperties.getInstance().getUniProtMappingFileName();
		String yeastgenome = PluginProperties.getInstance().getYeastGenomeMappingFileName();
		
		uniprotFile = new File(folder + uniprot);
		homoloGeneFile = new File(folder + homologene);
		yeastGenomeFile = new File(folder + yeastgenome);
		corumFile = new File(folder + corum);
		cyc2008File = new File(folder + cyc2008);
		luceneIndexFile = new File(folder + lucene);
		
		if (uniprotFile.exists() && uniprotFile.canRead()){
			uniprotCheck = true;
			uniprotLabel.setIcon(IconLoader.getInstance().getTickIcon());
		}
		else{
			uniprotCheck = false;
			uniprotLabel.setIcon(IconLoader.getInstance().getCrossIcon());
		}
		if (homoloGeneFile.exists() && homoloGeneFile.canRead()){
			homologeneCheck = true;
			homologeneLabel.setIcon(IconLoader.getInstance().getTickIcon());
		}
		else{
			homologeneCheck = false;
			homologeneLabel.setIcon(IconLoader.getInstance().getCrossIcon());
		}
		
		if (yeastGenomeFile.exists() && yeastGenomeFile.canRead()){
			yeastgenomeCheck = true;	
			yeastGenomeLabel.setIcon(IconLoader.getInstance().getTickIcon());
		}
		else{
			yeastgenomeCheck = false;
			yeastGenomeLabel.setIcon(IconLoader.getInstance().getCrossIcon());
		}
		if (corumFile.exists() && corumFile.canRead()){
			corumCheck = true;
			corumLabel.setIcon(IconLoader.getInstance().getTickIcon());
		}
		else{
			corumCheck = false;
			corumLabel.setIcon(IconLoader.getInstance().getCrossIcon());
		}
		if (cyc2008File.exists() && cyc2008File.canRead()){
			cyc2008Check = true;
			cyc2008Label.setIcon(IconLoader.getInstance().getTickIcon());
		}
		else{
			cyc2008Check = false;
			cyc2008Label.setIcon(IconLoader.getInstance().getCrossIcon());
		}
		if (luceneIndexFile.exists() && luceneIndexFile.canRead()){
			luceneCheck = true;
			luceneStatusLabel.setIcon(IconLoader.getInstance().getTickIcon());
		}
		else{
			luceneCheck = false;
			luceneStatusLabel.setIcon(IconLoader.getInstance().getCrossIcon());
		}
		
		
	}
	

	
	//method returning a JPanel containing the download buttons and listeners required for downloading the files necessary to build the index
	private JPanel indexPanel(){

		GridLayout indexFilesPanelLayout = new GridLayout(4,2);
		indexFilesPanelLayout.setHgap(50);
		indexFilesPanelLayout.setVgap(10);
		JPanel indexFilesPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();		
		indexFilesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Index - Download files 1-3 to build the index:"));
		
		//lables for the gui
		uniprotLabel = new JLabel("1. - UniProt mapping file", JLabel.LEFT);
		uniprotLabel.setToolTipText("Download can take around 1 hour");
		homologeneLabel = new JLabel("2. - HomoloGene file", JLabel.LEFT);
		yeastGenomeLabel = new JLabel("3. - YeastGenome id mapping file", JLabel.LEFT);
		luceneStatusLabel = new JLabel("4. - Status index");
		luceneLabel = new JLabel("(Re)build index (needed for homology mapping)", JLabel.LEFT);
		
		
		// download buttons for the gui
		JButton uniprotDownloadBttn = new JButton(download);
		JButton homologeneDownloadBttn = new JButton(download);
		JButton yeastgeneomeDownloadBttn = new JButton(download);
		JButton buildBttn = new JButton(IconLoader.getInstance().getPlayIcon());
			
		uniprotDownloadBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DependenciesDownloader downloader = new DependenciesDownloader(DependenciesDownloader.DOWNLOAD_UNIPROT_MAPPING_FILE);
				TaskManagerManager.getInstance().invokeTask(downloader);
				checkDependencies();
			}});
		
		homologeneDownloadBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DependenciesDownloader downloader = new DependenciesDownloader(DependenciesDownloader.DOWNLOAD_HOMOLOGENE);
				TaskManagerManager.getInstance().invokeTask(downloader);
				checkDependencies();
			}});

		yeastgeneomeDownloadBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DependenciesDownloader downloader = new DependenciesDownloader(DependenciesDownloader.DOWNLOAD_YEAST_GENOME);
				TaskManagerManager.getInstance().invokeTask(downloader);
				checkDependencies();
			}});
		
		buildBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (uniprotCheck && homologeneCheck && yeastgenomeCheck){
					System.out.println("Building index");
					TaskManagerManager.getInstance().queueTask(new IndexDepedenciesParser());
					TaskManagerManager.getInstance().queueTask(new IndexBuilder());
					logger.info("building index");
					JOptionPane.showMessageDialog(null, "The index will now be build.\nThis may take a few minutes...","Complex Finder - (re)indexing", JOptionPane.INFORMATION_MESSAGE);				
					TaskManagerManager.getInstance().manageQueuedTasks();
					JOptionPane.showMessageDialog(null, "Index successfully build! \nUnecessary files have been deleted.","Complex Finder - (re)indexing", JOptionPane.INFORMATION_MESSAGE);
					logger.info("Index successfully build");
					checkDependencies();
				}
				else{
					String message = "The following files are mising:\n";
					if (!uniprotCheck)
						message = message + "UniProt mapping file: " + uniprotFile.getPath() + "\n";
					if (!homologeneCheck)
						message = message + "HomoloGene file: " + homoloGeneFile.getPath() + "\n";
					if (!yeastgenomeCheck)
						message = message + "YeastGenome mapping file: " + yeastGenomeFile.getPath() + "\n";
					JOptionPane.showMessageDialog(null, message,"Complex Finder - (re)indexing", JOptionPane.ERROR_MESSAGE);				
				}
			}});
		
		//layout of the dialogue
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0.8;
		c.gridx = 0;
		c.gridy = 0;
		indexFilesPanel.add(uniprotLabel,c);
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0;
		c.gridx = 1;
		indexFilesPanel.add(uniprotDownloadBttn,c);
		c.anchor = GridBagConstraints.WEST;
		c.gridy = 1;
		c.gridx = 0;
		indexFilesPanel.add(homologeneLabel,c);
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		indexFilesPanel.add(homologeneDownloadBttn,c);
		c.anchor = GridBagConstraints.WEST;
		c.gridy = 2;
		c.gridx = 0;
		indexFilesPanel.add(yeastGenomeLabel,c);
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		indexFilesPanel.add(yeastgeneomeDownloadBttn,c);
		c.anchor = GridBagConstraints.WEST;
		c.gridy = 3;
		c.gridx = 0;
		indexFilesPanel.add(luceneStatusLabel,c);
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		indexFilesPanel.add(new JPanel(),c);	
		c.anchor = GridBagConstraints.WEST;
		c.gridy = 4;
		c.gridx = 0;
		indexFilesPanel.add(luceneLabel,c);	
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		indexFilesPanel.add(buildBttn,c);
			
		return indexFilesPanel;
	}
	
	
	//method returning a JPanel containing the Database download buttons/lables
	private JPanel databasePanel(){
		
		GridLayout databaseFilesPanelLayout = new GridLayout(3,2);
		databaseFilesPanelLayout.setHgap(50);
		databaseFilesPanelLayout.setVgap(10);
		
		JPanel databaseFilesPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		databaseFilesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Databases - Place and unzip these files in your Cytoscape plugins/PCFDependencies folder:"));

		corumLabel = new JLabel("1. - CORUM PSI-MI file", JLabel.LEFT);
		cyc2008Label = new JLabel("2. - CYC2008 tab file", JLabel.LEFT);
		JLabel databasesInfoLabel = new JLabel("* database files needed for complex identification");

		
		JButton corumDownloadBttn = new JButton(download);
		JButton cyc2008DownloadBttn = new JButton(download);
		corumDownloadBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DependenciesDownloader downloader = new DependenciesDownloader(DependenciesDownloader.DOWNLOAD_CORUM);
				TaskManagerManager.getInstance().invokeTask(downloader);
				checkDependencies();
			}});
		
		cyc2008DownloadBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DependenciesDownloader downloader = new DependenciesDownloader(DependenciesDownloader.DOWNLOAD_CYC2008);
				TaskManagerManager.getInstance().invokeTask(downloader);
				checkDependencies();
			}});
		
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		databaseFilesPanel.add(corumLabel,c);
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.gridy = 0;
		databaseFilesPanel.add(corumDownloadBttn,c);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 1;
		databaseFilesPanel.add(cyc2008Label,c);
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.gridy = 1;
		databaseFilesPanel.add(cyc2008DownloadBttn,c);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 2;
		databaseFilesPanel.add(databasesInfoLabel,c);
		c.gridx = 1;
		c.gridy = 2;
		databaseFilesPanel.add(new JPanel(),c);

		return databaseFilesPanel;
	}
	
	
	
	//method returning a JPanel for downloading the precompiled index and databases as .zip
	private JPanel zipFolderPanel(){
		
		GridLayout zippedFolderPanelLayout = new GridLayout(1,2);
		zippedFolderPanelLayout.setHgap(50);
		zippedFolderPanelLayout.setVgap(10);
		//JPanel zippedFolderPanel = new JPanel(zippedFolderPanelLayout);
		JPanel zippedFolderPanel = new JPanel(new GridBagLayout());
		zippedFolderPanel.setPreferredSize(new Dimension(650, 70));
		GridBagConstraints c = new GridBagConstraints();

		zippedFolderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Index and databases - Unzip to your Cytoscape plugins/PCFDependencies folder:"));
		
		JLabel preBuildLabel = new JLabel("<HTML>- packed archive with prebuild index and databases <br>&nbsp&nbsp  (likley less up to date but faster download)</HTML>");
		JButton prebuildBttn = new JButton(download);
		
		prebuildBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openWebpage(PluginProperties.getInstance().getUrlZippedDependencies());
			}});
		
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		zippedFolderPanel.add(preBuildLabel,c);
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		zippedFolderPanel.add(prebuildBttn, c);
		
		return zippedFolderPanel;
	}
	
	
	
	//method that returns a JPanel with the refresh button an listener
	private JPanel refreshButtonPanel(){

		JPanel bttnPanel = new JPanel();
		JButton updateBttn = new JButton("refresh", IconLoader.getInstance().getRefreshIcon());
		updateBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("checking dependencies");
				checkDependencies();
		}});
		
		bttnPanel.add(updateBttn);
		return bttnPanel;
	}
	
	
	
	//method called to show the actual dialogue
	private void createIndexDialogue(){
				
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		BoxLayout overallLayout = new BoxLayout(contentPanel,BoxLayout.Y_AXIS);
		contentPanel.add(indexPanel());
		contentPanel.add(databasePanel());
		contentPanel.add(zipFolderPanel());
		contentPanel.add(refreshButtonPanel());
		
		contentPanel.setLayout(overallLayout);
		
		checkDependencies();
		
		setContentPane(contentPanel);
		pack();
		setLocationRelativeTo(null);
	    setResizable(false);
		setVisible(true);
		setModal(true);	
	}
	
	
	
	// method that opens a link in the default browser
	private void openWebpageInBrowser(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	    	try {
	    		desktop.browse(uri);
	    		logger.info("openening " + uri.toString()); 
			} catch (IOException e) {
	    		logger.warn("openening " + uri.toString() + "failed", e); 
				e.printStackTrace();
			}
	    }
	}

	private void openWebpage(String str) {
	    try {
	    	URL url = new URL(str);	    	
	        openWebpageInBrowser(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    } catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
