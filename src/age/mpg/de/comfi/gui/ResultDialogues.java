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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;


import age.mpg.de.comfi.cytoscape.CytoscapeAttributeGetter;
import age.mpg.de.comfi.cytoscape.CytoscapeNodeSelector;
import age.mpg.de.comfi.gui.tablemodel.TableModelComplexes;
import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.output.GenerateOutput;
import age.mpg.de.comfi.properties.PluginProperties;
import age.mpg.de.comfi.utilityobjects.FoundProteinComplex;

import cytoscape.Cytoscape;

public class ResultDialogues extends JDialog{

	
	private JTextField filterText;
	private TableRowSorter<TableModelComplexes> sorter;
	private File outputFile  = ComplexFinderModel.getInstance().getOutputFile();
	
	
	private static final String pluginName = PluginProperties.getInstance().getPluginName();
	private static final String titel = pluginName + " - found complexes";
	
	
	
	public ResultDialogues(Frame owner) {
		
		super(owner,titel,false);

		CytoscapeAttributeGetter cyGet = new CytoscapeAttributeGetter();
		if (ComplexFinderModel.getInstance().isFinished())
			showResults();
		else if (ComplexFinderModel.getInstance().isExit())
			showCancelError();
		else
			showRunError();
	}

	
	
	
	//method that needs to be called to show the result dialogue
	public void showResults(){
		
		List<FoundProteinComplex> foundComplexesList = ComplexFinderModel.getInstance().getFoundProteinComplexes();
		
		//create table model and table sorter
		TableModelComplexes tableModel = new TableModelComplexes(foundComplexesList);		
		sorter = new TableRowSorter<TableModelComplexes>(tableModel);		
		sorter.setSortable(0, true);
		sorter.setSortable(1, true);
		sorter.setSortable(2, true);
		sorter.setSortable(3, true);
		sorter.setSortable(4, true);
		sorter.setSortable(5, true);

		
		//create JTable and enable sorting
		JTable table = new JTable(tableModel);
		table.setAutoCreateRowSorter(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setPreferredWidth(20);
		table.getColumnModel().getColumn(2).setPreferredWidth(20);
		table.getColumnModel().getColumn(3).setPreferredWidth(20);
		table.getColumnModel().getColumn(4).setPreferredWidth(20);
		table.getColumnModel().getColumn(5).setPreferredWidth(70);
		table.getColumnModel().getColumn(6).setPreferredWidth(30);

		table.setRowSorter(sorter);

		
		//create text field for search filter an register listener
		filterText = new JTextField();
		filterText.setPreferredSize(new Dimension(700,39));
		filterText.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				newFilter();
			}
	        public void insertUpdate(DocumentEvent e) {
	        	newFilter();
	        }
	        public void removeUpdate(DocumentEvent e) {
	        	newFilter();
	       }});

		
		//create save button and register listener
		JButton saveBtn = new JButton("Save all");
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputFile = chooseFile();
				new GenerateOutput(outputFile);
			}});
		
		//create import button and register listener
		JButton importBtn = new JButton("Select");
		importBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CytoscapeNodeSelector();
			}});

		//create a JScrollPane and set the	
		JScrollPane listScroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listScroller.setPreferredSize((new Dimension(900, 300)));
		listScroller.setBorder(BorderFactory.createTitledBorder("Found Complexes:"));
		listScroller.setBackground(this.getBackground());		
		
		//create lable for search field
		JLabel searchLable = new JLabel("Search", SwingConstants.TRAILING);
		searchLable.setLabelFor(filterText);
		
		//create Panels for nicer look
		JPanel panelList = new JPanel();
	    JPanel searchField = new JPanel();
		JPanel panelButtons = new JPanel();
		
		//add listScroller to panel and set layout
		panelList.setLayout(new BoxLayout(panelList, BoxLayout.PAGE_AXIS));
		panelList.add(Box.createRigidArea(new Dimension(0,5)));
		panelList.add(listScroller);
		panelList.add(searchField);
		panelList.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		//add search field to the corresponding field panel
		searchField.add(searchLable);
		searchField.add(filterText);
		
		//add buttons to the corresponding panel
		panelButtons.add(saveBtn);		
		panelButtons.add(importBtn);

		//add panels to the content pane		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		contentPanel.add(panelList, BorderLayout.CENTER);
		contentPanel.add(panelButtons, BorderLayout.SOUTH);
		
		setContentPane(contentPanel);
		setResizable(true);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);		
	}
	
	
	//needed for the save output file chooser
	public File chooseFile(){ 
		File outputFile =  new File("complexfinder_output.tab");
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(Cytoscape.getDesktop());
        if (returnVal == JFileChooser.APPROVE_OPTION)
        	outputFile = fc.getSelectedFile();
        return outputFile;
	}
	
	//needed for filtering and updating the table
	 private void newFilter() { 
		 RowFilter<TableModelComplexes, Object> rf = null;
	      //If current expression doesn't parse, don't update.
		 try {
			 String input = "(?i)" + filterText.getText();	//(?i) regex - makes input case insensitive
	         rf = RowFilter.regexFilter(input, 0);	
		 } catch (java.util.regex.PatternSyntaxException e) {
			 return;
		 }
		 sorter.setRowFilter(rf);
	}
	
	
	 public void showCancelError(){
			JOptionPane.showMessageDialog(null, "The Complex search was canceled.\nPlease run the \"Find complexes\" option again.", pluginName + " error", JOptionPane.ERROR_MESSAGE);
	}
		
	 
	 
	public void showRunError(){
		JOptionPane.showMessageDialog(null, "No annotated complexes present.\nRun " + pluginName + "--> \"Find complexes\" first.", pluginName + " error", JOptionPane.ERROR_MESSAGE);
	}
	
}
