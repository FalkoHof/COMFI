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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import age.mpg.de.comfi.managers.TaskManagerManager;
import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.utilityobjects.FoundProteinComplex;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

public class CytoscapeAttributeSetter implements Task {
	
	private List<FoundProteinComplex> foundProteinComplexList;
	private int numFoundComplexes;
	private CyAttributes nodeAttributes;
	
	private List<CyNode> complexNodeList = new ArrayList<CyNode>();
	private List<CyEdge> cyEdgeList = new ArrayList<CyEdge>();
	private List<CyNetwork>networkList = new ArrayList<CyNetwork>();
	
	private cytoscape.task.TaskMonitor taskMonitor;
	private final String TASK_TITLE = "Writing protein complexes to network";
	private boolean interrupted = false;

	private	CyNetwork network;
	
	public 	CytoscapeAttributeSetter(){
		
		
	}

	
	@Override
	public void run() {
		//initialize variables
		foundProteinComplexList = ComplexFinderModel.getInstance().getFoundProteinComplexes();
		numFoundComplexes = foundProteinComplexList.size();
		nodeAttributes = Cytoscape.getNodeAttributes();

		setComplexFinderResults();
	}
	
	
	
	
	public void setComplexFinderResults(){
		if (!interrupted)
			createComplexNodes();
		if (!interrupted)
			createNewEdges();
		if (!interrupted)
			addToNetwork();
		if (ComplexFinderModel.getInstance().isNestedNetworks() && !interrupted)
			createNestedNetworks();
		if (ComplexFinderModel.getInstance().isRemoveOldParts() && !interrupted)
			removeNodes();
		if (ComplexFinderModel.getInstance().isDoLayout() && !interrupted)
			doLayout();
		if (interrupted)
			deleteNestedNetworks();
	}
	
	
	
	private void deleteNestedNetworks(){
		for(CyNetwork network : networkList)
			Cytoscape.destroyNetwork(network);
	}
	
	
	
	private void addToNetwork(){
		
		String networkName = ComplexFinderModel.getInstance().getTargetNetworkName();
		
		taskMonitor.setStatus("Adding complexes to the network...");
		taskMonitor.setPercentCompleted(-1);
		
		//checks if the complexes should be added to an existing network of if a new network should be created
		if (networkName.equals(ComplexFinderModel.CREATE_NEW_NETWORK)){
			network = Cytoscape.createNetwork(complexNodeList, cyEdgeList, "Found Protein Complexes");
			networkList.add(network);
		}
		else{
			network = Cytoscape.getNetwork(networkName);
			int counter = 0;
			for (CyNode node : complexNodeList){
				taskMonitor.setPercentCompleted(TaskManagerManager.getInstance().getPercentage(counter, complexNodeList.size()));
				counter++;
				network.addNode(node);
			}
			taskMonitor.setPercentCompleted(100);
		
			taskMonitor.setStatus("Adding edges to the network...");
			taskMonitor.setPercentCompleted(-1);
			counter = 0;
			for (CyEdge edge : cyEdgeList){
				taskMonitor.setPercentCompleted(TaskManagerManager.getInstance().getPercentage(counter, cyEdgeList.size()));
				counter++;
				network.addEdge(edge);
			}
		}
		taskMonitor.setPercentCompleted(100);
	}
	
		
	
	// create new nodes for found protein complexes
	private void createComplexNodes(){
		
		taskMonitor.setStatus("Adding complexes...");
		taskMonitor.setPercentCompleted(-1);
		int counter = 0;
		for (FoundProteinComplex complex: foundProteinComplexList){
			taskMonitor.setPercentCompleted(TaskManagerManager.getInstance().getPercentage(counter, numFoundComplexes));
			counter++;
			CyNode node = Cytoscape.getCyNode(complex.getCyGroupID(), true);
			complexNodeList.add(node);
			setNodeAttributes(node, complex);
		}	
		taskMonitor.setPercentCompleted(100);
	}
	
	// sets Node Attributes in cytoscape
	private void setNodeAttributes(CyNode node, FoundProteinComplex complex) {
				
		nodeAttributes.setAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_BOOLEAN, true);
		nodeAttributes.setListAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_MEMBERS, complex.getComplexMembers());
		nodeAttributes.setAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_NAME, complex.getProteinComplexName());
		nodeAttributes.setAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_SOURCE_ORGANISM, complex.getOrganism());
		nodeAttributes.setAttribute(node.getIdentifier(),ComplexFinderModel.COLUMN_TITLE_TAX_ID, complex.getTaxID());
		
		if (complex.getPubmedID() != null)
			nodeAttributes.setAttribute(node.getIdentifier(),ComplexFinderModel.COLUMN_TITLE_PUBMED_ID, complex.getPubmedID());
		
		if (complex.isHomologue()){
			if (complex.isHumanHomologue()){
				nodeAttributes.setAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_HUMAN_HOMOLOGUES_BOOLEAN, true);
				nodeAttributes.setListAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_MEMBERS_NODE_IDS, (List<String>) complex.getNodeIdList());
			}
			if (complex.isYeastHomologue()){
				nodeAttributes.setAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_YEAST_HOMOLOGUES_BOOLEAN, true);
				nodeAttributes.setListAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_MEMBERS_NODE_IDS, (List<String>) complex.getNodeIdList());
			}
		}
		else
			nodeAttributes.setListAttribute(node.getIdentifier(), ComplexFinderModel.COLUMN_TITLE_COMPLEX_MEMBERS_NODE_IDS, (List<String>) complex.getNodeIdList());
		
		for (String nodeId : complex.getNodeIdList()){
			List<String> isInComplexes = new ArrayList<String>();					
			isInComplexes.add(complex.getCyGroupID());
			nodeAttributes.setAttribute(nodeId,ComplexFinderModel.COLUMN_TITLE_IS_IN_COMPLEX, true);
			
			if (nodeAttributes.hasAttribute(nodeId, ComplexFinderModel.COLUMN_TITLE_IS_IN_COMPLEX_LIST))
				isInComplexes.addAll( (List<String>) nodeAttributes.getListAttribute(nodeId, ComplexFinderModel.COLUMN_TITLE_IS_IN_COMPLEX_LIST));
			
			nodeAttributes.setListAttribute(nodeId, ComplexFinderModel.COLUMN_TITLE_IS_IN_COMPLEX_LIST, isInComplexes);
		}	
	}
	
	
	
	private void createNewEdges(){
		
		taskMonitor.setStatus("Calculating new edges...");
		taskMonitor.setPercentCompleted(-1);
		
		int counter = 0;
		int numOfEdges = Cytoscape.getCyEdgesList().size();
		for (CyEdge edge : (List<CyEdge>) Cytoscape.getCyEdgesList()){
			
			if (interrupted){
				ComplexFinderModel.getInstance().setExit(true);
				break;
			}
			
			taskMonitor.setPercentCompleted(TaskManagerManager.getInstance().getPercentage(counter, numOfEdges));
			counter++;
			
			String source = edge.getSource().getIdentifier();
			String target = edge.getTarget().getIdentifier();

			List<String> sourceComplexList = new ArrayList<String>();
			List<String> targetComplexList = new ArrayList<String>();
			
			boolean sourceInComplex = false; 
			boolean targetInComplex = false; 
			
			CyEdge newEdge = null;
			
			// get node complex information
			if (nodeAttributes.hasAttribute(source, ComplexFinderModel.COLUMN_TITLE_IS_IN_COMPLEX)){
				sourceInComplex = (boolean) nodeAttributes.getBooleanAttribute(source, ComplexFinderModel.COLUMN_TITLE_IS_IN_COMPLEX);
				if (sourceInComplex)
					sourceComplexList = (List<String>) nodeAttributes.getListAttribute(source,ComplexFinderModel.COLUMN_TITLE_IS_IN_COMPLEX_LIST);
			}
			
			if (nodeAttributes.hasAttribute(target, ComplexFinderModel.COLUMN_TITLE_IS_IN_COMPLEX)){
				targetInComplex = (boolean) nodeAttributes.getBooleanAttribute(target, ComplexFinderModel.COLUMN_TITLE_IS_IN_COMPLEX);
				if (targetInComplex)
					targetComplexList = (List<String>) nodeAttributes.getListAttribute(target,ComplexFinderModel.COLUMN_TITLE_IS_IN_COMPLEX_LIST);
			}
				
			
			// make the correct new edges 
			if (sourceInComplex && targetInComplex){
				for (String sourceComplex : sourceComplexList){					
					for (String targetComplex : targetComplexList){
						newEdge = Cytoscape.getCyEdge(Cytoscape.getCyNode(sourceComplex), Cytoscape.getCyNode(targetComplex), Semantics.INTERACTION, "INTERACTS_WITH_COMPLEX", true, edge.isDirected());
						cyEdgeList.add(newEdge);
					}
				}
			}
			else if (sourceInComplex && !targetInComplex){
				for (String sourceComplex : sourceComplexList){
					newEdge = Cytoscape.getCyEdge(Cytoscape.getCyNode(sourceComplex), Cytoscape.getCyNode(target), Semantics.INTERACTION, "INTERACTS_WITH_COMPLEX", true, edge.isDirected());
					cyEdgeList.add(newEdge);
				}
			}
			else if (!sourceInComplex && targetInComplex){
				for (String targetComplex : targetComplexList){
					newEdge = Cytoscape.getCyEdge(Cytoscape.getCyNode(source), Cytoscape.getCyNode(targetComplex), Semantics.INTERACTION, "INTERACTS_WITH_COMPLEX", true, edge.isDirected());
					cyEdgeList.add(newEdge);
				}
			}		
		}
		taskMonitor.setPercentCompleted(100);
	}

	
	
	private void createNestedNetworks(){
		
		taskMonitor.setStatus("Setting nested networks...");
		taskMonitor.setPercentCompleted(-1);

		int counter = 0;
		for (FoundProteinComplex complex : foundProteinComplexList){
			
			if (interrupted){
				ComplexFinderModel.getInstance().setExit(true);
				break;
			}
			
			taskMonitor.setPercentCompleted(TaskManagerManager.getInstance().getPercentage(counter, numFoundComplexes));
			counter++;
			
			List<String> nodeIdList = new ArrayList<String>();
			
			nodeIdList = complex.getNodeIdList();
			
			Set<CyNode>nodeSet = new HashSet<CyNode>();

			for (String id : nodeIdList)
				nodeSet.add(Cytoscape.getCyNode(id));
			
			int index = 0;
			int[] nodeRootGraphIndexArray = new int[nodeSet.size()];
			for (CyNode node : nodeSet){
				nodeRootGraphIndexArray[index] = node.getRootGraphIndex();
				index++;
			}
			
			int[] connectingEdges = Cytoscape.getRootGraph().getConnectingEdgeIndicesArray(nodeRootGraphIndexArray);
			
			CyNode groupNode = Cytoscape.getCyNode(complex.getCyGroupID());
			CyNetwork nestedNetwork = Cytoscape.createNetwork(nodeRootGraphIndexArray, connectingEdges, complex.getProteinComplexName(), network, false);
			groupNode.setNestedNetwork(nestedNetwork);
			networkList.add(nestedNetwork);
			
			
		}
		taskMonitor.setPercentCompleted(100);	
	}
	
	
	
	private void doLayout(){
		
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();
		VisualStyle vs = catalog.getVisualStyle("default");
		manager.setVisualStyle(vs);
		
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
		CyNetworkView viewer = Cytoscape.createNetworkView(network, network.getTitle(), layout, vs);

		layout.doLayout(viewer);
		
	}
	
	

	private void removeNodes(){
		
		
		taskMonitor.setStatus("Removing old nodes and edges...");
		taskMonitor.setPercentCompleted(-1);
		int counter = 0;
		for (FoundProteinComplex complex : foundProteinComplexList){
			
			if (interrupted){
				ComplexFinderModel.getInstance().setExit(true);
				break;
			}
			
			taskMonitor.setPercentCompleted(TaskManagerManager.getInstance().getPercentage(counter, numFoundComplexes));
			counter++;
			List<String> nodeIdList = new ArrayList<String>();
			nodeIdList = complex.getNodeIdList();
	
	
			// gets the root graph index of the protein complex members
			int[]nodeRootGraphIndexArray = new int[nodeIdList.size()];
			for (int i =0; i<nodeIdList.size(); i++)
				nodeRootGraphIndexArray[i] = Cytoscape.getCyNode(nodeIdList.get(i)).getRootGraphIndex();
			
			// gets the root graph index of the edges adjacent to the complex members
			List<Integer> adjacentEdgeList = new ArrayList<Integer>();
			for (String nodeID : nodeIdList){
				
				int nodeIndex = Cytoscape.getCyNode(nodeID).getRootGraphIndex();
				int[] adjacentEdgesArray = Cytoscape.getRootGraph().getAdjacentEdgeIndicesArray(nodeIndex, true, true, true);

				for (int i : adjacentEdgesArray)
					adjacentEdgeList.add(i);
			}			
		
			for (int i : nodeRootGraphIndexArray)
				network.removeNode(i, true);
			for (int i : adjacentEdgeList)
				network.removeEdge(i, true);
		}
		taskMonitor.setPercentCompleted(100);
	}



	@Override
	public String getTitle() {
		return TASK_TITLE;
	}


	@Override
	public void halt() {	
	this.interrupted = true;
	
	}


	@Override
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}
	
}
