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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import age.mpg.de.comfi.cytoscape.CytoscapeAttributeGetter;
import age.mpg.de.comfi.index.IdConverter;
import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.utilityobjects.FoundProteinComplex;
import age.mpg.de.comfi.utilityobjects.ParsedProteinComplex;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;


public class ComplexFinder implements Task{
	
	
	private boolean yeastHomology = ComplexFinderModel.getInstance().isYeastHomology();
	private boolean humanHomology = ComplexFinderModel.getInstance().isHumanHomology(); 
	
	//Task manager stuff
	private cytoscape.task.TaskMonitor taskMonitor;
	private final String TASK_TITLE = "Searching for protein complexes";
	private boolean interrupted = false;

	
	//List with all parsed protein complexes
	private List<ParsedProteinComplex> proteinComplexList;
	
	private List<FoundProteinComplex> foundComplexList = new ArrayList<FoundProteinComplex>();
	
	private Map<String,String> idToNodeMap;
	private Map<String,String> humanHomologyMap;
	private Map<String,String> yeastHomologyMap;
	
	//constructor
	public ComplexFinder(){

	}
	
	
	@Override
	public void run() {
		
		proteinComplexList = ComplexFinderModel.getInstance().getProteinComplexList();

		taskMonitor.setStatus("Searching for complexes...");
		taskMonitor.setPercentCompleted(-1);
		
		CytoscapeAttributeGetter cyGet = new CytoscapeAttributeGetter();
		idToNodeMap = cyGet.getIdToNodeMap();
		
		if(humanHomology || yeastHomology){
			List<String>queryList = new ArrayList<String>(idToNodeMap.keySet());
			IdConverter converter = new IdConverter(humanHomology,yeastHomology,queryList);
			humanHomologyMap = ((humanHomology) ? (Map<String, String>) converter.getHumanHomologyMap() : null);
			yeastHomologyMap = ((yeastHomology) ? (Map<String, String>) converter.getYeastHomologyMap() : null);
		}
		
		findComplexes();
	}
		
	
	public void findComplexes(){

		for (ParsedProteinComplex oneComplex : proteinComplexList){

			List<String>cyNodeComplexList = new ArrayList<String>();
			List<String>humanHomologueComplexList = new ArrayList<String>();
			List<String>yeastHomologueComplexList = new ArrayList<String>();
			
			//breakes the loop if task is cancelled 
			if (interrupted){
				ComplexFinderModel.getInstance().setExit(true);
				break;
			}
			//check if which complex members are present in the loaded network
			for (String oneMember :	oneComplex.getInteractors()){
				if (humanHomology){
					if (humanHomologyMap.containsKey(oneMember))
						humanHomologueComplexList.add(idToNodeMap.get(humanHomologyMap.get(oneMember)));
				}
				if (yeastHomology){
					if (yeastHomologyMap.containsKey(oneMember))
						yeastHomologueComplexList.add(idToNodeMap.get(yeastHomologyMap.get(oneMember)));
				}
				if (!humanHomology && !yeastHomology){
					if (idToNodeMap.containsKey(oneMember))
						cyNodeComplexList.add(idToNodeMap.get(oneMember));
				}
			}			
			
			// if all are present create a corresponding protein complex object
			if (cyNodeComplexList.size() == oneComplex.getInteractors().size())
				foundComplexList.add(new FoundProteinComplex("Cpx" + oneComplex.getId(), oneComplex.getFullName(), oneComplex.getInteractors(), cyNodeComplexList, oneComplex.getTaxId(), oneComplex.getPubmedId()));		
		
			if (humanHomologueComplexList.size() == oneComplex.getInteractors().size())
				foundComplexList.add(new FoundProteinComplex("Cpx" + oneComplex.getId(), oneComplex.getFullName(), oneComplex.getInteractors(), humanHomologueComplexList, oneComplex.getTaxId(),true, FoundProteinComplex.HUMAN, oneComplex.getPubmedId()));
			
			if (yeastHomologueComplexList.size() == oneComplex.getInteractors().size())
				foundComplexList.add(new FoundProteinComplex("Cpx" + oneComplex.getId(), oneComplex.getFullName(), oneComplex.getInteractors(), yeastHomologueComplexList, oneComplex.getTaxId(),true, FoundProteinComplex.YEAST, oneComplex.getPubmedId()));
			
		}	
		ComplexFinderModel.getInstance().setFoundProteinComplexes(foundComplexList);
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
