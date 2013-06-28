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
package age.mpg.de.comfi.databaseparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import age.mpg.de.comfi.managers.TaskManagerManager;
import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.properties.PluginProperties;
import age.mpg.de.comfi.utilityobjects.ParsedProteinComplex;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import psidev.psi.mi.xml.PsimiXmlReader;
import psidev.psi.mi.xml.PsimiXmlReaderException;
import psidev.psi.mi.xml.PsimiXmlVersion;
import psidev.psi.mi.xml.model.Entry;
import psidev.psi.mi.xml.model.EntrySet;
import psidev.psi.mi.xml.model.Interaction;
import psidev.psi.mi.xml.model.Participant;

public class ProteinComplexParser implements Task {
	
	private List<ParsedProteinComplex> proteinComplexList = new ArrayList<ParsedProteinComplex>();
    private boolean interrupted = false;
	private cytoscape.task.TaskMonitor taskMonitor;
    private final String TASK_TITLE = "Reading protein complex databases";
    
    
    public static final int PARSE_ALL = 0;
	public static final int PARSE_CORUM = 1;
	public static final int PARSE_CYC2008 = 2;
	private File dependenciesDir = new File(PluginProperties.getInstance().getDependenciesFolder());
	private int option = ComplexFinderModel.getInstance().getPaserOption();
	private int taxIdSelectedOrganism = ComplexFinderModel.getInstance().getTaxIDselectedOrganism();
	
	public ProteinComplexParser(){
		
	}
	
	@Override
	public void run() {
	
		taskMonitor.setStatus("Parsing Databases...");
		taskMonitor.setPercentCompleted(-1);
		System.out.println("selected option: " + option );
		switch (option){
			case PARSE_ALL:		parseAll();
								break;
			case PARSE_CORUM: 	parseCORUM();
								break;
			case PARSE_CYC2008:	parseCYC2008();
								break;
			default:			parseAll();
								break;
		}
		ComplexFinderModel.getInstance().setProteinComplexList(proteinComplexList);
		System.out.println("Complexes parsed: " + proteinComplexList.size());
		taskMonitor.setPercentCompleted(100);
	}
	
	
	
	public void parseCORUM(){
		psiMiXmlParser(new File(PluginProperties.getInstance().getDependenciesFolder(), PluginProperties.getInstance().getCorumXmlName()));	
	}
	
	
	public void parseCYC2008(){
		tabParser(new File(PluginProperties.getInstance().getDependenciesFolder(), PluginProperties.getInstance().getCYC2008TabName()));
	}
	
	
	public void parseAll(){
		String[] filenames =  PluginProperties.getInstance().getFilenames();
		int counter = 0;
		//check which parser is the appropriate one to for the file
		for (String filename : filenames){
			taskMonitor.setPercentCompleted(TaskManagerManager.getInstance().getPercentage(counter, filenames.length));
			counter++;
			File file = new File( PluginProperties.getInstance().getDependenciesFolder() + filename);
			if (filename.contains(".xml"))
				psiMiXmlParser(file);
			else if (filename.contains(".tab"))
				tabParser(file);
			else
				System.out.println("File not recognized...");
		}
	}


	public void psiMiXmlParser(File file){	

		PsimiXmlVersion psiMiVersion = PsimiXmlVersion.VERSION_25_UNDEFINED;
		
		try {
			taskMonitor.setStatus("Parsing :" + file.toString());
			taskMonitor.setPercentCompleted(-1);
			PsimiXmlReader reader = new PsimiXmlReader(psiMiVersion);
			EntrySet set = reader.read(file);
			int counter = 0;
			// Iterate through the entries
			for (Entry entry : set.getEntries()) {
				
				if (interrupted){
					ComplexFinderModel.getInstance().setExit(true);
					break;
				}
				taskMonitor.setPercentCompleted(TaskManagerManager.getInstance().getPercentage(counter,set.getEntries().size()));
				counter++;

				// Iterate through the Interactions - one Interaction --> one protein complex
				for (Interaction oneInteraction: entry.getInteractions()){
					
					int taxId = oneInteraction.getExperiments().iterator().next().getHostOrganisms().iterator().next().getNcbiTaxId();
					
					String fullName = oneInteraction.getNames().getFullName();
					String pubmedID = oneInteraction.getXref().getPrimaryRef().getId();
					List<String> interactors  = new ArrayList<String>();

					for (Participant participant : oneInteraction.getParticipants())
						interactors.add(participant.getInteractor().getNames().getShortLabel());
				
					
					
					ParsedProteinComplex complex = new ParsedProteinComplex(oneInteraction.getId(), taxId, fullName, interactors, pubmedID);
					proteinComplexList.add(complex);
				}
			}		
		} 
		catch (PsimiXmlReaderException e) {
			System.out.print("Parsing XML failed due to a " + e.getClass().getName() + ":");
			e.printStackTrace();
		}
	}


	public void tabParser(File file){

		ParsedProteinComplex complex = new ParsedProteinComplex();
		List<String> interactors = new ArrayList<String>();
		int counter = 0;
		// add to model stuff
		int taxId = 4932;

		try {
			taskMonitor.setStatus("Parsing " + file.toString());
			taskMonitor.setPercentCompleted(-1);

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine(); // used to skip the first line, containing headers
			line = reader.readLine();
		
			while(line != null && !interrupted){
				String[] temp = line.split("\t");
				String interactor = temp[0].trim();
				String fullName = temp[2].trim();
		
				if (complex.getFullName() == null)
					complex.setFullName(fullName);
			
				if (complex.getFullName().equals(fullName))
					interactors.add(interactor);
				else{
					complex.setInteractors(interactors);
					complex.setId(counter);
					complex.setTaxId(taxId);
					counter++;
					proteinComplexList.add(complex);
					complex = new ParsedProteinComplex();
					interactors = new ArrayList<String>();
					interactors.add(interactor);
				}
				line = reader.readLine();
			}
			complex.setInteractors(interactors);
			complex.setId(counter);
			counter++;
			proteinComplexList.add(complex);
			taskMonitor.setPercentCompleted(100);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	
	public List<ParsedProteinComplex> getProteinComplexList() {
		return proteinComplexList;
	}


	public void setProteinComplexList(List<ParsedProteinComplex> proteinComplexList) {
		this.proteinComplexList = proteinComplexList;
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