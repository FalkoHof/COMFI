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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import age.mpg.de.comfi.databaseparser.ProteinComplexParser;
import age.mpg.de.comfi.managers.ComplexFinderManager;
import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.properties.PluginProperties;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;




public class StartDialogues extends JDialog {
	
	private int paserOptions = ComplexFinderModel.getInstance().getPaserOption();
	private String columUniprot = ComplexFinderModel.getInstance().getColumUniprot();
	private JLabel jLableComboBoxUniprot;
	private  JLabel jLableCheckBoxHomology;
	
	private static final String pluginName = PluginProperties.getInstance().getPluginName();
	private static final String titel = pluginName + " - find complexes";
	
	private String[] sourceNetworksIdArr,sourceNetworksTitleArr, targetNetworksIdArr, targetNetworksTitleArr;
	
	private int createNewNetworkIndex = -1;
	private int biggestNetworkIndex = -1;
	
	public StartDialogues (Frame owner){
		super(owner,titel,true);
		menuPanel();
	}
	
	
	public void menuPanel(){
		//Lables for GUI
		final String lableHomology = "Organisms for homology comparison:";  
		final String lableUniprot = "UniprotIDs:"; 
		final String lableYeastIds = "CYGDIds:";
		final String lableOrganism = "Organism:"; 
		final String lableTargetNetwork = "Target network:";  
		final String lableSourceNetwork = "Source network:";  
		final String lableNestedNetworks = "Create nested networks" + "\n" + "(not recommended for large networks):";
		final String lableDoLayout = "Layout network" + "\n" + "(not recommended for large networks):";
		final String lableRemoveNodes = "Remove complex members from source network:";
		 
		 
		//Strings for combobox Organisms
		final String[] organismsArray = PluginProperties.getInstance().getDefaultOrganisms();
		final int [] taxIdArray = PluginProperties.getInstance().getTaxId();
		
		//Strings for combobox select UniprotID
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		String[] comboBoxAttributes = attributes.getAttributeNames();	 

		
		// Fill comboboxes Network with the Names of open Networks
		 Set<CyNetwork> networksSet = Cytoscape.getNetworkSet();
		  
		 
		 
		 
		 String[] noNetworkPresent = {"No network loaded"};
		 
		 // if no networks are present put "No network loaded"
		 sourceNetworksIdArr = (networksSet.size() > 0) ? new String[networksSet.size()] : noNetworkPresent;
		 sourceNetworksTitleArr = (networksSet.size() > 0) ? new String[networksSet.size()] : noNetworkPresent;
		 
		 targetNetworksIdArr = (networksSet.size() > 0) ? new String[(networksSet.size()+1)] : noNetworkPresent;
		 targetNetworksTitleArr = (networksSet.size() > 0) ? new String[(networksSet.size()+1)] : noNetworkPresent;
		 
		 
		 //loop for filling arrays with network names, network identifiers and getting the index of the biggest network --> later set as default source network
		 if (networksSet.size() > 0 && networksSet != null){
			 int i = 0;
			 int maxNodeCount = 0;
			 for(CyNetwork network : networksSet){
				 sourceNetworksIdArr[i] = network.getIdentifier();
				 sourceNetworksTitleArr[i] = network.getTitle();
				 if (maxNodeCount <= network.getNodeCount()){
					 maxNodeCount = network.getNodeCount();
					 biggestNetworkIndex = i;
				 }
				 
				 targetNetworksIdArr[i] = network.getIdentifier();
				 targetNetworksTitleArr[i] = network.getTitle();
				 i++;
			 }
			// add the option to create a new network to the combobox target networks and gets the index --> later set as default 
			targetNetworksIdArr[i] = ComplexFinderModel.CREATE_NEW_NETWORK;
			targetNetworksTitleArr[i] = ComplexFinderModel.CREATE_NEW_NETWORK;
			createNewNetworkIndex = i;
		 }
		 
		
		 
		//creates  different panels for a nicer layout
	    JPanel cbxUniprotPanel = new JPanel(); 
	    JPanel cbxOrganismPanel = new JPanel();
	    JPanel cbxSourceNetworkPanel = new JPanel(); 
	    JPanel cbxTargetNetworkPanel = new JPanel(); 
	    JPanel checkboxHomologyPanel = new JPanel();	
	    JPanel checkboxSubPanel = new JPanel();
	    JPanel nestedNetworksPanel = new JPanel();
	    JPanel checkboxDoLayoutPanel = new JPanel();
	    JPanel checkboxRemoveNodesPanel = new JPanel();
	    JPanel buttonPanel = new JPanel();	
	    
	    //creates Jlables for the combo boxes
	    jLableComboBoxUniprot = new JLabel(lableUniprot, JLabel.RIGHT);
	    jLableComboBoxUniprot.setToolTipText("Select CYGD IDs for yeast");
	    jLableCheckBoxHomology = new JLabel (lableHomology);
		jLableCheckBoxHomology.setEnabled(false);
	    JLabel jLableComboBoxOrganism = new JLabel(lableOrganism, JLabel.RIGHT);  
	    JLabel jLableComboBoxSourceNetwork = new JLabel(lableSourceNetwork, JLabel.RIGHT);
	    JLabel jLableComboBoxTargetNetwork = new JLabel(lableTargetNetwork, JLabel.RIGHT);
	   
	    JLabel jLableCheckBoxNestedNetworks = new JLabel(lableNestedNetworks);
	    JLabel jLableCheckBoxDoLayout = new JLabel(lableDoLayout);
	    JLabel jLableCheckBoxRemoveNodes = new JLabel(lableRemoveNodes);
 
	    // Combobox Uniprot - needed for selecting the coloum containing the uniprot id
	    Dimension d = new Dimension();
	    d.height = 39;
	    d.width = 170;
	    
	    //create comboboxes and set a default vaule
	    JComboBox cbUniprot = new JComboBox(comboBoxAttributes);
	    cbUniprot.setToolTipText("Select yeast CYGD IDs for yeast");
	    cbUniprot.setPreferredSize(d);
	    cbUniprot.setEditable(false);
	    cbUniprot.setSelectedIndex(0);
	    ComplexFinderModel.getInstance().setColumUniprot((String) cbUniprot.getSelectedItem());
	    
	    JComboBox cbOrganism = new JComboBox(organismsArray);
	    cbOrganism.setPreferredSize(d);
	    cbOrganism.setEditable(false);	    
	    cbOrganism.setSelectedIndex(0);
	    ComplexFinderModel.getInstance().setPaserOption(cbOrganism.getSelectedIndex());
	    
	    
	    JComboBox cbSourceNetworks = new JComboBox(sourceNetworksTitleArr);
	    cbSourceNetworks.setPreferredSize(d);
	    cbSourceNetworks.setEditable(false);
	    cbSourceNetworks.setSelectedIndex((biggestNetworkIndex >= 0) ? biggestNetworkIndex : 0);
	    ComplexFinderModel.getInstance().setSourceNetworkName((String)cbSourceNetworks.getSelectedItem());
	    
		JComboBox cbTargetNetworks = new JComboBox(targetNetworksTitleArr);
		cbTargetNetworks.setPreferredSize(d);
	    cbTargetNetworks.setEditable(false);
	    cbTargetNetworks.setSelectedIndex((createNewNetworkIndex >= 0) ? createNewNetworkIndex : 0);
	    ComplexFinderModel.getInstance().setTargetNetworkName((String)cbTargetNetworks.getSelectedItem());
	    
	    //create checkbboxes
	    final JCheckBox checkbHumanHomology = new JCheckBox("Human");
	    checkbHumanHomology.setEnabled(false);
	        
	    final JCheckBox checkbYeastHomology = new JCheckBox("Yeast");
	    checkbYeastHomology.setEnabled(false);
	    
	    final JCheckBox checkbNestedNetworks = new JCheckBox();
	    checkbNestedNetworks.setSelected(ComplexFinderModel.getInstance().isNestedNetworks());
	    
	    final JCheckBox checkbDoLayout = new JCheckBox();
	    checkbDoLayout.setSelected(ComplexFinderModel.getInstance().isDoLayout());
	    final JCheckBox checkbRemoveNodes = new JCheckBox();
	    checkbRemoveNodes.setSelected(ComplexFinderModel.getInstance().isRemoveOldParts());
	    
	    //create buttons
	    JButton searchBttn = new JButton("Search");
	    JButton close = new JButton("Close");

	    
	    //add action listeners to comboboxes
	    cbUniprot.addItemListener(new ItemListener(){
	    	public void itemStateChanged(ItemEvent evt){
	    		columUniprot = (String)evt.getItem();
	    		ComplexFinderModel.getInstance().setColumUniprot(columUniprot);
	    	}});
	    
	    
	    cbOrganism.addItemListener(new ItemListener(){
	    	public void itemStateChanged(ItemEvent evt){
	    		String temp = (String)evt.getItem();
	    		for (int i = 0; i<taxIdArray.length; i++){
					if (temp.equals(organismsArray[i])){
	    				 int taxid = taxIdArray[i];
	    				 //switch that sets database parameteres and enables and disables the checkboxes depending on the combobox selection
	    				 switch (taxid){
	    				 	case ComplexFinderModel.TAX_ID_HUMAN:	jLableComboBoxUniprot.setText(lableUniprot);
	    				 											paserOptions = ProteinComplexParser.PARSE_CORUM;
	    				 											disableCheckboxes(jLableCheckBoxHomology, checkbHumanHomology, checkbYeastHomology);
	    				 											break;
	    				 	case ComplexFinderModel.TAX_ID_MOUSE:	jLableComboBoxUniprot.setText(lableUniprot);
	    				 											paserOptions = ProteinComplexParser.PARSE_CORUM;
																	disableCheckboxes(jLableCheckBoxHomology, checkbHumanHomology, checkbYeastHomology);
																	break;
	    				 	case ComplexFinderModel.TAX_ID_YEAST:	jLableComboBoxUniprot.setText(lableYeastIds);
	    				 											paserOptions = ProteinComplexParser.PARSE_CYC2008;
	    				 											disableCheckboxes(jLableCheckBoxHomology, checkbHumanHomology, checkbYeastHomology);
	    				 											break;
	    				 	default:								jLableComboBoxUniprot.setText(lableUniprot);
	    				 											paserOptions = ProteinComplexParser.PARSE_ALL;
	    				 											enableCheckboxes(jLableCheckBoxHomology, checkbHumanHomology, checkbYeastHomology);
	    				 											break;
	    				 }
	    				 ComplexFinderModel.getInstance().setTaxIDselectedOrganism(taxid);
    					 ComplexFinderModel.getInstance().setPaserOption(paserOptions);
					}
	    		}
	    	}});
	    
	    
	    cbTargetNetworks.addItemListener(new ItemListener(){
	    	public void itemStateChanged(ItemEvent evt){
	    		for (int i = 0; i < targetNetworksTitleArr.length; i++){
	    			if (targetNetworksTitleArr[i].equals((String) evt.getItem()))
	    				ComplexFinderModel.getInstance().setTargetNetworkName(targetNetworksIdArr[i]);	
	    		}
	    	}});
	    
	    cbSourceNetworks.addItemListener(new ItemListener(){
	    	public void itemStateChanged(ItemEvent evt){
	    		for (int i = 0; i < sourceNetworksTitleArr.length; i++){
	    			if (sourceNetworksTitleArr[i].equals((String) evt.getItem()))
	    	    		ComplexFinderModel.getInstance().setSourceNetworkName(sourceNetworksIdArr[i]);	
	    		}
	    	}});
	    
	   	    
	    // add listeners to check boxes    
	    checkbHumanHomology.addItemListener(new ItemListener(){ 
	    	public void itemStateChanged(ItemEvent e) {
	     		if (e.getStateChange() == ItemEvent.SELECTED)
	     			ComplexFinderModel.getInstance().setHumanHomology(true);
	     		else
	     			ComplexFinderModel.getInstance().setHumanHomology(false);
	    	}});
	    
	    
	    checkbYeastHomology.addItemListener(new ItemListener(){ 
	    	public void itemStateChanged(ItemEvent e) {
	    		if (e.getStateChange() == ItemEvent.SELECTED)
	    			ComplexFinderModel.getInstance().setYeastHomology(true);
	    		else
	    			ComplexFinderModel.getInstance().setYeastHomology(false);
	    	}});
	    
	    checkbNestedNetworks.addItemListener(new ItemListener(){ 
	    	public void itemStateChanged(ItemEvent e) {
	     		if (e.getStateChange() == ItemEvent.SELECTED)
	    			ComplexFinderModel.getInstance().setNestedNetworks(true);
	     		else
	     			ComplexFinderModel.getInstance().setNestedNetworks(false);
	    	}});

	    //add checkbox listener for remove nodes
	    checkbRemoveNodes.addItemListener(new ItemListener(){ 
	    	public void itemStateChanged(ItemEvent e) {
	     		if (e.getStateChange() == ItemEvent.SELECTED)
	    			ComplexFinderModel.getInstance().setRemoveOldParts(true);
	     		else
	    			ComplexFinderModel.getInstance().setRemoveOldParts(false);
	    	}});
	    
	    // add checkbox listener for layout
	    checkbDoLayout.addItemListener(new ItemListener(){ 
	    	public void itemStateChanged(ItemEvent e) {
	     		if (e.getStateChange() == ItemEvent.SELECTED)
	    			ComplexFinderModel.getInstance().setDoLayout(true);
	     		else
	    			ComplexFinderModel.getInstance().setDoLayout(false);
	    	}});

	    //add listener to start button
	    searchBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	//program only starts if networks are loaded
            	if (Cytoscape.getNetworkSet().size() != 0){
            		setVisible(false);
            		new ComplexFinderManager();
            		dispose();
            		new ResultDialogues(Cytoscape.getDesktop());
            	}
            	else
            		JOptionPane.showMessageDialog(null, "No network loaded!" + "\n" + "Load a network before running " + pluginName + "." , pluginName + " error", JOptionPane.ERROR_MESSAGE);		
            }});
	    
	    
	    close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	dispose();
            }});
	    
	    

	    //set layout of the different JPanels and add stuff
	    cbxUniprotPanel.setLayout(new BoxLayout(cbxUniprotPanel, BoxLayout.X_AXIS));
	    cbxUniprotPanel.add(jLableComboBoxUniprot);
	    cbxUniprotPanel.add(Box.createHorizontalGlue());
	    cbxUniprotPanel.add(cbUniprot);
	    
	    cbxOrganismPanel.setLayout(new BoxLayout(cbxOrganismPanel, BoxLayout.X_AXIS));
	    cbxOrganismPanel.add(jLableComboBoxOrganism);	
	    cbxOrganismPanel.add(Box.createHorizontalGlue());
	    cbxOrganismPanel.add(cbOrganism);   
	    
	    cbxSourceNetworkPanel.setLayout(new BoxLayout(cbxSourceNetworkPanel, BoxLayout.X_AXIS));
	    cbxSourceNetworkPanel.add(jLableComboBoxSourceNetwork);
	    cbxSourceNetworkPanel.add(Box.createHorizontalGlue());
	    cbxSourceNetworkPanel.add(cbSourceNetworks);
	    
	    cbxTargetNetworkPanel.setLayout(new BoxLayout(cbxTargetNetworkPanel, BoxLayout.X_AXIS));
	    cbxTargetNetworkPanel.add(jLableComboBoxTargetNetwork);
	    cbxTargetNetworkPanel.add(Box.createHorizontalGlue());
	    cbxTargetNetworkPanel.add(cbTargetNetworks);
 	  
	    checkboxHomologyPanel.setLayout(new BoxLayout(checkboxHomologyPanel, BoxLayout.X_AXIS));
	    checkboxHomologyPanel.add(jLableCheckBoxHomology);
	    checkboxHomologyPanel.add(Box.createHorizontalGlue());
	    
	    // add boxes to subpanel - used for layouting reasons
	    checkboxSubPanel.add(checkbHumanHomology);
	    checkboxSubPanel.add(checkbYeastHomology);
	    checkboxHomologyPanel.add(checkboxSubPanel);
	    
	    checkboxRemoveNodesPanel.setLayout(new BoxLayout(checkboxRemoveNodesPanel, BoxLayout.X_AXIS));
	    checkboxRemoveNodesPanel.add(jLableCheckBoxRemoveNodes);
	    checkboxRemoveNodesPanel.add(Box.createHorizontalGlue());
	    checkboxRemoveNodesPanel.add(checkbRemoveNodes);
	    
	    checkboxDoLayoutPanel.setLayout(new BoxLayout(checkboxDoLayoutPanel, BoxLayout.X_AXIS));
	    checkboxDoLayoutPanel.add(jLableCheckBoxDoLayout);
	    checkboxDoLayoutPanel.add(Box.createHorizontalGlue());
	    checkboxDoLayoutPanel.add(checkbDoLayout);
	    	        	    
	    nestedNetworksPanel.setLayout(new BoxLayout(nestedNetworksPanel, BoxLayout.X_AXIS));
	    nestedNetworksPanel.add(jLableCheckBoxNestedNetworks);
	    nestedNetworksPanel.add(Box.createHorizontalGlue()); 
	    nestedNetworksPanel.add(checkbNestedNetworks);
	    
	    buttonPanel.add(searchBttn);
	    buttonPanel.add(close);
	    
	    // add everything to the frame	    
	    JPanel generalOptionsPanel = new JPanel();	 
	    	    
	    generalOptionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "1 - General options:"));	    
	    generalOptionsPanel.setLayout(new GridLayout(6, 1)); 

	    generalOptionsPanel.add(cbxOrganismPanel);
	    generalOptionsPanel.add(checkboxHomologyPanel);
	    generalOptionsPanel.add(cbxUniprotPanel);
	    generalOptionsPanel.add(nestedNetworksPanel);
	    generalOptionsPanel.add(checkboxDoLayoutPanel);
	    generalOptionsPanel.add(checkboxRemoveNodesPanel);
	        
	    JPanel networkOptionsPanel = new JPanel();
	    networkOptionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "2 - Network options:"));	    
	    networkOptionsPanel.setLayout(new GridLayout(2, 1)); 
	    
	    networkOptionsPanel.add(cbxSourceNetworkPanel);
	    networkOptionsPanel.add(cbxTargetNetworkPanel);
	    
	    JPanel contentPanel = new JPanel();
	    contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contentPanel.add(generalOptionsPanel);
		contentPanel.add(Box.createVerticalGlue());
		contentPanel.add(networkOptionsPanel);
		contentPanel.add(Box.createVerticalGlue());
	    contentPanel.add(buttonPanel);
	    BoxLayout boxLayout = new BoxLayout(contentPanel, BoxLayout.Y_AXIS);
	    contentPanel.setLayout(boxLayout);
	    
	    setContentPane(contentPanel);
	    pack();
	    setResizable(true);
	    setLocationRelativeTo(null);
		setVisible(true);
	}
	
	
	//disables the homology checkboxes and resets homology to false
	private void disableCheckboxes(JLabel jLableCheckBoxHomology, JCheckBox checkbHumanHomology, JCheckBox checkbYeastHomology){
		jLableCheckBoxHomology.setEnabled(false);
		checkbYeastHomology.setEnabled(false);
		ComplexFinderModel.getInstance().setYeastHomology(false);
		checkbHumanHomology.setEnabled(false);
		ComplexFinderModel.getInstance().setHumanHomology(false);		
	}
	
	//enables the homology checkboxes
	private void enableCheckboxes(JLabel jLableCheckBoxHomology, JCheckBox checkbHumanHomology, JCheckBox checkbYeastHomology){
		jLableCheckBoxHomology.setEnabled(true);
		checkbYeastHomology.setEnabled(true);
		checkbHumanHomology.setEnabled(true);
	}
}
