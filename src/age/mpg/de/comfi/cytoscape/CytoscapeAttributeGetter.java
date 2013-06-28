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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.utilityobjects.FoundProteinComplex;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class CytoscapeAttributeGetter {

	
	private CyAttributes nodeAttributes;
	
	public CytoscapeAttributeGetter(){
		nodeAttributes = Cytoscape.getNodeAttributes();
	}
	
	
	public void getProteinComplexes(){
			
		List<FoundProteinComplex>proteinComplexList = new ArrayList<FoundProteinComplex>();
			
		
		for (CyNode node : (List<CyNode>) Cytoscape.getCyNodesList()){
			
			List<String> complexMemberList = new ArrayList<String>();
			List<String> complexMemberNodeIdList = new ArrayList<String>();
	
			boolean hasProteinComplexAttribute = nodeAttributes.hasAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_BOOLEAN);
			boolean hasHumanHomologsAttribute = nodeAttributes.hasAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_HUMAN_HOMOLOGUES_BOOLEAN);
			boolean hasYeastHomologsAttribute = nodeAttributes.hasAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_YEAST_HOMOLOGUES_BOOLEAN);
			boolean proteinComplex = (hasProteinComplexAttribute) ? (boolean) nodeAttributes.getBooleanAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_BOOLEAN):false;
			
			String complexName = (proteinComplex) ? (String) nodeAttributes.getAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_NAME):null;
			String pubmedID = (proteinComplex) ? nodeAttributes.getStringAttribute(node.getIdentifier(),ComplexFinderModel.COLUMN_TITLE_PUBMED_ID):null;
			
			int taxId = (proteinComplex) ? nodeAttributes.getIntegerAttribute(node.getIdentifier(),ComplexFinderModel.COLUMN_TITLE_TAX_ID):null;
			
			complexMemberList = (proteinComplex) ? (List<String>) nodeAttributes.getListAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_MEMBERS):null;
			complexMemberNodeIdList = (proteinComplex) ? (List<String>) nodeAttributes.getListAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_MEMBERS_NODE_IDS):null;
			
			boolean humanHomology = (hasHumanHomologsAttribute) ? (boolean) nodeAttributes.getBooleanAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_HUMAN_HOMOLOGUES_BOOLEAN):false;
			boolean yeastHomology =  (hasYeastHomologsAttribute) ? (boolean) nodeAttributes.getBooleanAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_YEAST_HOMOLOGUES_BOOLEAN):false;
			
			
			
			if (humanHomology)
				proteinComplexList.add(new FoundProteinComplex(node.getIdentifier(), complexName, complexMemberList,complexMemberNodeIdList, taxId, true, FoundProteinComplex.HUMAN, pubmedID));
			else if (yeastHomology)
				proteinComplexList.add(new FoundProteinComplex(node.getIdentifier(), complexName, complexMemberList, complexMemberNodeIdList, taxId, true, FoundProteinComplex.YEAST, pubmedID));
			else if (proteinComplex)
				proteinComplexList.add(new FoundProteinComplex(node.getIdentifier(), complexName, complexMemberList,complexMemberNodeIdList, taxId, pubmedID));
		}
		
		if (proteinComplexList.size() >= 1)
			ComplexFinderModel.getInstance().setFinished(true);
		ComplexFinderModel.getInstance().setFoundProteinComplexes(proteinComplexList);
	}	
	
	
	public Map<String,String> getIdToNodeMap(){
		
		String columUniprotId = ComplexFinderModel.getInstance().getColumUniprot();
		Map<String, String>IdToNodeMap = new HashMap<String,String>();
		
		byte attributeType = nodeAttributes.getType(columUniprotId);
		
		//creates a set with the root graph indices of the source network
		CyNetwork network = Cytoscape.getNetwork(ComplexFinderModel.getInstance().getSourceNetworkName());
		int[]sourceNetworkIndexArray = network.getNodeIndicesArray();
		Set<Integer>sourceNetworkIndexSet = new HashSet<Integer>();

		for (int i : sourceNetworkIndexArray)
			sourceNetworkIndexSet.add(i);
		
		//creates a hashmap - <uniprotId, CyNodeId>
		for (CyNode node : (List<CyNode>) Cytoscape.getCyNodesList()){
			if (sourceNetworkIndexSet.contains(node.getRootGraphIndex())){
				if (attributeType == CyAttributes.TYPE_SIMPLE_LIST){
					List<String> templist = (List<String>) nodeAttributes.getListAttribute(node.getIdentifier(), columUniprotId);
					for (String str : templist)
						IdToNodeMap.put(str, node.getIdentifier());
				}
				else {	
					String str = (String) nodeAttributes.getAttribute(node.getIdentifier(), columUniprotId);
					IdToNodeMap.put(str, node.getIdentifier());
				}
			}
		}
		return IdToNodeMap;
	}
		
	
	
	
	
}
