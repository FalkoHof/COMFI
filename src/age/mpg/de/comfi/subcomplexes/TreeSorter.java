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
package age.mpg.de.comfi.subcomplexes;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.utilityobjects.FoundProteinComplex;

public class TreeSorter implements Task {

	private List<FoundProteinComplex>complexList;
	
	//Task manager stuff
	private cytoscape.task.TaskMonitor taskMonitor;
	private final String TASK_TITLE = "Computing  protein complex heirachy";
	private boolean interrupted = false;
	

	public TreeSorter(){
	}
	
	
	public void run(){
		complexList = ComplexFinderModel.getInstance().getFoundProteinComplexes();
		sortComplexesByHeiracy();
		ComplexFinderModel.getInstance().setFoundProteinComplexes(complexList);
	}
	
	
	public void sortComplexesByHeiracy(){
		sortBySize();
		computeHeiracies();
		//printHeiracies();
	}
	
	private void sortBySize(){
		//sort and reverse the arrayList so that the complex with the most members will be at i=0
		Collections.sort(complexList);
		Collections.reverse(complexList);
	}

	
	private boolean contains(FoundProteinComplex parent, FoundProteinComplex child){
		Set<String>parentSet = new HashSet<String>(parent.getNodeIdSet());
	
		if (parentSet.containsAll(child.getNodeIdSet()))
			return true;
		else
			return false;
	}

	
	private void addSubComplex(FoundProteinComplex parent, FoundProteinComplex child){
		Set<String>parentSet = new HashSet<String>(parent.getNodeIdSet());
		//add the complex id
		parentSet.add(child.getCyGroupID());
		//set the subcomplexes in the object
		parent.setHasSubComplex(true);
		parent.setNodeIdSet(parentSet);
		parent.addSubcomplexes(child);
	}
	
	
	private void computeHeiracies(){
		//loop over the sorted complex list to identify sub/supercomplexes
		//first for loop --> parent complex
		//second loop --> child complex?
		for (int i = 0; i < complexList.size(); i++){
			//break statement for task cancelation
			if (interrupted){
				ComplexFinderModel.getInstance().setExit(true);
				break;
			}	
			
			for (int j = i+1; j < complexList.size(); j++){
				//break statement for task cancelation
				if (interrupted){
					ComplexFinderModel.getInstance().setExit(true);
					break;
				}	
				//check if the bigger complex contains all the members of the smaller complex
				//if yes, modify the object add its subcomplex(es) 
				if (contains(complexList.get(i), complexList.get(j)))
					addSubComplex(complexList.get(i), complexList.get(j));
			}
		}
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
