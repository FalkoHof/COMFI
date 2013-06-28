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
package age.mpg.de.comfi.managers;


import age.mpg.de.comfi.ComplexFinder;
import age.mpg.de.comfi.cytoscape.CytoscapeAttributeSetter;
import age.mpg.de.comfi.databaseparser.ProteinComplexParser;
import age.mpg.de.comfi.model.ComplexFinderModel;


public class ComplexFinderManager {

	
	public ComplexFinderManager(){
		startManager();
	}
		
	public void startManager(){
		
		ComplexFinderModel.getInstance().setExit(false);
		long startTime = System.currentTimeMillis();
		
		// queues the tasks to be executed		
		TaskManagerManager.getInstance().queueTask(new ProteinComplexParser());
		TaskManagerManager.getInstance().queueTask(new ComplexFinder());
		TaskManagerManager.getInstance().queueTask(new CytoscapeAttributeSetter());
		TaskManagerManager.getInstance().manageQueuedTasks();
		
		
		ComplexFinderModel.getInstance().setFinished(true);
		
		
		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		System.out.println("The search took: " + "\t" +  runTime + "ms" + "\t" +  (runTime/1000) + "sec" + "\t" +  (runTime/(1000*60.00)) + "min");
	}
		
}
