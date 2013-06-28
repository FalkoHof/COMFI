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
package age.mpg.de.comfi.index;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;


import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.properties.PluginProperties;
import age.mpg.de.comfi.utilityobjects.HomoloGeneEntry;

import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class IndexDepedenciesParser implements Task{
	
	//filenames
	private final String DIRECTORY = PluginProperties.getInstance().getDependenciesFolder();
	private final String HOMOLOGENEFILE = PluginProperties.getInstance().getHomoloGeneFileName();
	private final String UNIPROTIDFILE = PluginProperties.getInstance().getUniProtMappingFileName();
	private final String YEASTIDFILE = PluginProperties.getInstance().getYeastGenomeMappingFileName();
	
	Map<String, Integer> AccMap;
	List<HomoloGeneEntry> homoloGeneList = new ArrayList<HomoloGeneEntry>();

	//TaskManager and Cytoscae Stuff
	private final String TASK_TITLE = "Parsing files for indexing...";
	private boolean interrupted = false;
	private cytoscape.task.TaskMonitor taskMonitor;
	private CyLogger logger = CyLogger.getLogger(this.getClass());

	
	
	public IndexDepedenciesParser(){
		
	}
	
	
	private BufferedReader openBufferedGzipReader(String filename) throws IOException{
		
		InputStream fileStream = new FileInputStream(filename);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
		BufferedReader buffered = new BufferedReader(decoder);
        return buffered;

		
	}
	
	
	
	private void parseDependencies() throws IOException{
		
		parseHomologene();
		parseUniprotIDs();
		parseYeastIDs();
		ComplexFinderModel.getInstance().setHomoloGeneList(homoloGeneList);
	}
	
	private void parseHomologene() throws IOException{		
		BufferedReader reader = new BufferedReader(new FileReader(DIRECTORY + HOMOLOGENEFILE));
		taskMonitor.setStatus("Parsing HomoloGene, File: " + HOMOLOGENEFILE);
		taskMonitor.setPercentCompleted(-1);

		Set<Integer> taxIDSet = new HashSet<Integer>();

		String line = null;
		while((line = reader.readLine()) != null){
			if(interrupted){
		    	logger.warning("Parsing homoloGene canceled");
				break;
			}
			String [] tokens = line.split("\t");
			taxIDSet.add(Integer.parseInt(tokens[1]));
			HomoloGeneEntry entry = new HomoloGeneEntry(tokens);
			homoloGeneList.add(entry);
		}
		reader.close();
		
		taskMonitor.setPercentCompleted(100);
		logger.info("Number of Entries: "+ homoloGeneList.size());
		System.out.println("Number of Entries: "+ homoloGeneList.size());

		AccMap = new HashMap<String,Integer>();
		for (int i = 0; i< homoloGeneList.size(); i++)
			AccMap.put((homoloGeneList.get(i).getProteinAcc()), i);
		}
	
	
	
	private void parseUniprotIDs() throws IOException{
		//parse uniprot conversion file
		//BufferedReader reader = new BufferedReader(new FileReader(DIRECTORY + UNIPROTIDFILE));
		BufferedReader reader = openBufferedGzipReader(DIRECTORY + UNIPROTIDFILE);
		
		logger.info("Parsing UniprotIDs....");
		System.out.println("Parsing UniprotIDs....");
		
		taskMonitor.setStatus("Parsing UniProtIDs, File: " + UNIPROTIDFILE);
		taskMonitor.setPercentCompleted(-1);

		String line = null;
		while((line = reader.readLine()) != null){
			if(interrupted){
		    	logger.warning("Parsing uniprotfile canceled");
				ComplexFinderModel.getInstance().setExit(true);
				break;
			}
			
			//if line contains refseq and not nt it is a line containing a uniprot - refseq map
			if(line.contains("RefSeq") && !line.contains("NT")){
				String [] tokens = line.split("\t");
				String uniprotID = tokens[0];
				String refseqID = tokens[2];
				// if the refseq id is present in homologene get the index of the correspoding entry
				if(AccMap.containsKey(refseqID)){
					int index = (int)(AccMap.get(refseqID));
					// if there is no uniprot id for this entry registerd set it				
					if(homoloGeneList.get(index).getUniprotID() == null)
						homoloGeneList.get(index).setUniprotID(uniprotID);
					//if there is already a uniport id set for this entry append the other one
					else if( homoloGeneList.get(index).getUniprotID() != null)
						homoloGeneList.get(index).setUniprotID(homoloGeneList.get(index).getUniprotID() + " " + uniprotID);
					
				}	
			}
		}
		reader.close();
		
		
		for(HomoloGeneEntry oneEntry : homoloGeneList){
			if(oneEntry.getUniprotID() == null)
				oneEntry.setUniprotID("None");
		}
		logger.info("Parsing UniprotIDs... - Done");
		System.out.println("Done");
		taskMonitor.setPercentCompleted(100);

	}
	
	private void parseYeastIDs() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(DIRECTORY + YEASTIDFILE));
				
		System.out.println("Parsing YeastIDs....");
		
		taskMonitor.setStatus("Parsing YeastIDs, File: " + YEASTIDFILE);
		taskMonitor.setPercentCompleted(-1);
		String line = null;
		while((line = reader.readLine()) != null){
			if(interrupted){
		    	logger.warning("Parsing YeastGenome file canceled");
				ComplexFinderModel.getInstance().setExit(true);
				break;
			}
			
			if(line.contains("RefSeq protein")){
				String [] tokens = line.split("\t");
				String refseqID = tokens[0];
				String yeastId = tokens[3];
				if(AccMap.containsKey(refseqID)){
					int index = (int)(AccMap.get(refseqID));
					// if there is no yeast orf id for this entry registerd set it				
					if(homoloGeneList.get(index).getOrganismId() == null)
						homoloGeneList.get(index).setOrganismId(yeastId);
					//if there is already a yeast orf id id set for this entry append the other one
					else if( homoloGeneList.get(index).getOrganismId() != null)
						homoloGeneList.get(index).setOrganismId(homoloGeneList.get(index).getOrganismId() + " " + yeastId);
				}	
			}
		}
		reader.close();
		
		for(HomoloGeneEntry oneEntry : homoloGeneList){
			if(oneEntry.getOrganismId() == null)
				oneEntry.setOrganismId("None");
		}
		
		taskMonitor.setPercentCompleted(100);
		System.out.println("Done");

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



	@Override
	public void run() {
		try {
			parseDependencies();
		} catch (IOException e) {
			logger.warning("Error while parsing files for indexing", e);
			e.printStackTrace();
		}
		
	}
	
	
}
