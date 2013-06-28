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
package age.mpg.de.comfi.cytoscape;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.utilityobjects.FoundProteinComplex;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;

public class CytoscapeNodeSelector {
	
	private List<FoundProteinComplex> proteinComplexList;
	private Set<CyNode>nodesSet;
	
	public CytoscapeNodeSelector(){
		proteinComplexList = ComplexFinderModel.getInstance().getFoundProteinComplexes();
		selectNodes();
	}

	public void selectNodes(){
		getNodesToSelect();
		setSelectedNodes();
	}
	
	public void getNodesToSelect(){
		nodesSet = new HashSet<CyNode>();
		for (FoundProteinComplex complex : proteinComplexList){
			if (complex.isSelect()){
				nodesSet.add(Cytoscape.getCyNode(complex.getCyGroupID()));
				complex.setSelect(false);
			}
			
		}
	}
	
	public void setSelectedNodes(){
		//CyNetwork network = Cytoscape.getNetwork(ComplexFinderModel.getInstance().getTargetNetworkName());
		CyNetwork network = Cytoscape.getCurrentNetwork();
		Set<CyNode>nodesSelectSet = new HashSet<CyNode>();

		Set<CyNode> selectedNodes = (Set<CyNode>) network.getSelectedNodes();
		if (selectedNodes.size() > 0 && selectedNodes!= null)
			network.unselectAllNodes();
		for (CyNode node: nodesSet)
			if (network.containsNode(node))
				nodesSelectSet.add(node);
		
		
		if (nodesSelectSet.size() > 0){
			network.setSelectedNodeState(nodesSelectSet, true);
			CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
			view.updateView();
		}
	}
	
}
