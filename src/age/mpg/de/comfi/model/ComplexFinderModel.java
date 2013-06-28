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
package age.mpg.de.comfi.model;

import java.io.File;
import java.util.List;

import age.mpg.de.comfi.utilityobjects.FoundProteinComplex;
import age.mpg.de.comfi.utilityobjects.HomoloGeneEntry;
import age.mpg.de.comfi.utilityobjects.ParsedProteinComplex;


public class ComplexFinderModel {	//Model class - Holding variables/values for setting program parameters

    
	private static ComplexFinderModel instance = new ComplexFinderModel();

	
	//cytoscape network parameters/variables
	private String columUniprot = "canonicalName"; 
	private String sourceNetworkName = "";
	private String targetNetworkName = "";
	private int taxIDselectedOrganism = 0;
	
	public static final int TAX_ID_HUMAN = 9606;	 
	public static final int TAX_ID_YEAST = 4932;	
	public static final int TAX_ID_MOUSE = 10090;
	
	//headers for CyAttributes that are set and loaded
	public static final String CREATE_NEW_NETWORK = "create new network";
	public static final String COLUMN_TITLE_COMPLEX_NAME = "Full Name";
	public static final String COLUMN_TITLE_COMPLEX_BOOLEAN = "Protein Complex";
	public static final String COLUMN_TITLE_COMPLEX_MEMBERS = "Complex Members";
	public static final String COLUMN_TITLE_HUMAN_HOMOLOGUES = "Human Homologues";
	public static final String COLUMN_TITLE_HUMAN_HOMOLOGUES_BOOLEAN = "Is Human Homologue";
	public static final String COLUMN_TITLE_YEAST_HOMOLOGUES = "Yeast Homologues";
	public static final String COLUMN_TITLE_YEAST_HOMOLOGUES_BOOLEAN = "Is Yeast Homologue";
	public static final String COLUMN_TITLE_SOURCE_ORGANISM = "Source Organism";
	public static final String COLUMN_TITLE_TAX_ID = "Taxid";
	public static final String COLUMN_TITLE_COMPLEX_MEMBERS_NODE_IDS = "Complex Members Node IDs";
	public static final String COLUMN_TITLE_IS_IN_COMPLEX = "Is in Complex";
	public static final String COLUMN_TITLE_IS_IN_COMPLEX_LIST = "List of Complexes";
	public static final String COLUMN_TITLE_PUBMED_ID = "Pubmed ID";

	
	
	
	//general parameters
	private boolean nestedNetworks = true; 
	private boolean saveOutput = false;
	private boolean removeOldParts = false;
	private boolean doLayout = true;
	private boolean exit = false;
	
	
	private boolean finished = false;
	
	//parameters for homology
	private boolean yeastHomology = false;
	private boolean humanHomology = false; 
	
	
	//parser parameters
	private int paserOption = 1;
	
		
	//file for export
	private File outputFile  = new File("ComplexFinder_output.tab");
	
	//List with all parsed protein complexes
	private List<ParsedProteinComplex> proteinComplexList;
	//List with all found protein complexes
	private List<FoundProteinComplex> foundProteinComplexes;
	
			
	//List with homologene entries for lucene index building
	private List<HomoloGeneEntry> HomoloGeneList;
	
	
    public ComplexFinderModel (){
    	
    	
       	
    }
    
    public static ComplexFinderModel getInstance() {
		return instance;
	}

	public String getColumUniprot() {
		return columUniprot;
	}

	public void setColumUniprot(String columUniprot) {
		this.columUniprot = columUniprot;
	}



	public boolean isYeastHomology() {
		return yeastHomology;
	}

	public void setYeastHomology(boolean yeastHomology) {
		this.yeastHomology = yeastHomology;
	}

	public boolean isHumanHomology() {
		return humanHomology;
	}

	public void setHumanHomology(boolean humanHomology) {
		this.humanHomology = humanHomology;
	}

	public boolean isNestedNetworks() {
		return nestedNetworks;
	}

	public void setNestedNetworks(boolean nestedNetworks) {
		this.nestedNetworks = nestedNetworks;
	}

	public boolean isSaveOutput() {
		return saveOutput;
	}

	public void setSaveOutput(boolean saveOutput) {
		this.saveOutput = saveOutput;
	}

	public List<ParsedProteinComplex> getProteinComplexList() {
		return proteinComplexList;
	}

	public void setProteinComplexList(List<ParsedProteinComplex> proteinComplexList) {
		this.proteinComplexList = proteinComplexList;
	}


	public int getPaserOption() {
		return paserOption;
	}

	public void setPaserOption(int paserOption) {
		this.paserOption = paserOption;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public boolean isRemoveOldParts() {
		return removeOldParts;
	}

	public void setRemoveOldParts(boolean removeOldParts) {
		this.removeOldParts = removeOldParts;
	}

	public int getTaxIdHuman() {
		return TAX_ID_HUMAN;
	}

	public int getTaxIdYeast() {
		return TAX_ID_YEAST;
	}

	public boolean isDoLayout() {
		return doLayout;
	}

	public void setDoLayout(boolean doLayout) {
		this.doLayout = doLayout;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public List<FoundProteinComplex> getFoundProteinComplexes() {
		return foundProteinComplexes;
	}

	public void setFoundProteinComplexes(List<FoundProteinComplex> foundProteinComplexes) {
		this.foundProteinComplexes = foundProteinComplexes;
	}

	
	public String getTargetNetworkName() {
		return targetNetworkName;
	}

	public void setTargetNetworkName(String selectedNetworkName) {
		this.targetNetworkName = selectedNetworkName;
	}

	public boolean isExit() {
		return exit;
	}

	public void setExit(boolean exit) {
		this.exit = exit;
	}

	public String getSourceNetworkName() {
		return sourceNetworkName;
	}

	public void setSourceNetworkName(String sourceNetworkName) {
		this.sourceNetworkName = sourceNetworkName;
	}

	public int getTaxIDselectedOrganism() {
		return taxIDselectedOrganism;
	}

	public void setTaxIDselectedOrganism(int taxIDselectedOrganism) {
		this.taxIDselectedOrganism = taxIDselectedOrganism;
	}

	public List<HomoloGeneEntry> getHomoloGeneList() {
		return HomoloGeneList;
	}

	public void setHomoloGeneList(List<HomoloGeneEntry> homoloGeneList) {
		HomoloGeneList = homoloGeneList;
	}
	
}
