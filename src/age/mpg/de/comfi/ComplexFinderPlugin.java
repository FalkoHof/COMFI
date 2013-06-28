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
package age.mpg.de.comfi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;


import age.mpg.de.comfi.gui.IndexDialogues;
import age.mpg.de.comfi.gui.ResultDialogues;
import age.mpg.de.comfi.gui.StartDialogues;
import age.mpg.de.comfi.properties.PluginProperties;

import cytoscape.Cytoscape;

public class ComplexFinderPlugin {
	
	public ComplexFinderPlugin(){		
		
		JMenu pluginMenu = new JMenu(PluginProperties.getInstance().getPluginName());
		
		// create 2 submenu entries
		JMenuItem startMenuItem = new JMenuItem("Find complexes");			
		JMenuItem resultsMenuItem = new JMenuItem("Show results");
		JMenuItem indexMenuItem = new JMenuItem("(Re)build index/download dependencies");

		//add the 2 submenus
		pluginMenu.add(startMenuItem);
		pluginMenu.add(resultsMenuItem);
		pluginMenu.add(indexMenuItem);
		
		//add listener for data import
		startMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new StartDialogues(Cytoscape.getDesktop());
			}});
		
		//add listener for pathway statistics
		resultsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ResultDialogues(Cytoscape.getDesktop());
			}});
		
		indexMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new IndexDialogues(Cytoscape.getDesktop());
			}});
		
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(pluginMenu);
	}
}
