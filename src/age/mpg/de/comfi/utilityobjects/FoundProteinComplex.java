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
package age.mpg.de.comfi.utilityobjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FoundProteinComplex implements Comparable {
	
		
	private String proteinComplexName;
	private String organism = "";
	private String cyGroupID;
	private String pubmedID;
	
	private List<String>complexMembers;

	private Set<String>nodeIdSet;

	//booleans indicating homology 
	private boolean humanHomologue = false;
	private boolean yeastHomologue = false;
	
	private boolean homologue = false;
	private boolean select = false;
	
	
	private int numberOfMembers = 0;
	private int taxID = 0;
	
	//static finals for the allowed organisms
	private final int TAX_ID_HUMAN = 9606;	 
	private final int TAX_ID_YEAST = 4932;
	private final int TAX_ID_MOUSE = 10090;	

	//static finals for the allowed organisms
	public static final String HUMAN = "Human";
	public static final String YEAST = "Yeast";
	public static final String MOUSE = "Mouse";
	
	//boolean for establishing a complex heirachy---> complexes can have super and sub complexes
	//default value for isRootComplex is true --> is later set to false if not true;
	private boolean hasSubComplex = false;
		
	private Set<FoundProteinComplex>subComplexes = new HashSet<FoundProteinComplex>();
	//set for nested networks/heirachy --> holds the ids for subcomlexes and the residual proteins not in a subcomplex
	private Set<String>nodeIdSetWithSubcomplexes = new HashSet<String>();
	
	public FoundProteinComplex(String cyGroupID, String proteinComplexName, List<String> complexMembers,List<String> nodeIdList, int taxID, boolean homologue, String organism, String pubmedID){
		this.cyGroupID = cyGroupID;
		this.proteinComplexName = proteinComplexName;
		this.complexMembers = complexMembers;
		this.nodeIdSet = new HashSet<String>(nodeIdList);
		this.nodeIdSetWithSubcomplexes = new HashSet<String>(nodeIdList);
		this.taxID = taxID;
		this.homologue = homologue;
		this.organism = organism;
		this.pubmedID = pubmedID;
		setNumberofMembers();
		setHomologues();
	}

	
	
	public FoundProteinComplex(String cyGroupID, String proteinComplexName, List<String> complexMembers, List<String> nodeIdList, int taxID, String pubmedID){
		this.cyGroupID = cyGroupID;
		this.proteinComplexName = proteinComplexName;
		this.complexMembers = complexMembers;
		this.nodeIdSet = new HashSet<String>(nodeIdList);
		this.nodeIdSetWithSubcomplexes = new HashSet<String>(nodeIdList);
		this.taxID = taxID;
		this.pubmedID = pubmedID;
		setNumberofMembers();
		setOrganism();	
	}
	
	
	public void setHomologues(){
		if(homologue){
			if (organism.equals(HUMAN))
				humanHomologue = true;
			if (organism.equals(YEAST))	
				yeastHomologue = true;
		}
	}

	
	private void setNumberofMembers(){
		if (complexMembers != null)
			numberOfMembers = complexMembers.size();
	}
	
	
	private void setOrganism(){
		if (taxID == TAX_ID_HUMAN)
			organism ="Human";
		if (taxID == TAX_ID_YEAST)
			organism = "Yeast";
		if (taxID == TAX_ID_MOUSE)
			organism = "Mouse";
	}


	public String getProteinComplexName() {
		return proteinComplexName;
	}


	public void setProteinComplexName(String proteinComplexName) {
		this.proteinComplexName = proteinComplexName;
	}


	public String getOrganism() {
		return organism;
	}


	public void setOrganism(String organism) {
		this.organism = organism;
	}


	public List<String> getComplexMembers() {
		return complexMembers;
	}


	public void setComplexMembers(List<String> complexMembers) {
		this.complexMembers = complexMembers;
	}


	public Set<String> getNodeIdSet() {
		return nodeIdSet;
	}

	
	public List<String> getNodeIdList() {
		return new ArrayList<String>(nodeIdSet);
	}
	

	public void setNodeIdSet(Set<String> nodeIdList) {
		this.nodeIdSet = new HashSet<String>(nodeIdList);
	}


	public boolean isHumanHomologue() {
		return humanHomologue;
	}


	public void setHumanHomologue(boolean humanHomologue) {
		this.humanHomologue = humanHomologue;
	}


	public boolean isYeastHomologue() {
		return yeastHomologue;
	}


	public void setYeastHomologue(boolean yeastHomologue) {
		this.yeastHomologue = yeastHomologue;
	}


	public boolean isHomologue() {
		return homologue;
	}


	public void setHomologue(boolean homologue) {
		this.homologue = homologue;
	}


	public int getNumberOfMembers() {
		return numberOfMembers;
	}


	public void setNumberOfMembers(int numberOfMembers) {
		this.numberOfMembers = numberOfMembers;
	}

	public int getTaxID() {
		return taxID;
	}

	public void setTaxID(int taxID) {
		this.taxID = taxID;
	}

	public String getCyGroupID() {
		return cyGroupID;
	}

	public void setCyGroupID(String cyGroupID) {
		this.cyGroupID = cyGroupID;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public String getPubmedID() {
		return pubmedID;
	}

	public Set<FoundProteinComplex> getSubComplexes() {
		return subComplexes;
	}

	public List<String> getSubComplexesIds() {
		List<String> subComplexIdList = new ArrayList<String>();	
		for (FoundProteinComplex complex : subComplexes)
			subComplexIdList.add(complex.getCyGroupID());
		return subComplexIdList;
	}
		
	
	public void addSubcomplexes(FoundProteinComplex complex){
		subComplexes.add(complex);	
		for (String id : complex.getNodeIdList())
			if (nodeIdSetWithSubcomplexes.contains(id))
				nodeIdSetWithSubcomplexes.remove(id);
		nodeIdSetWithSubcomplexes.add(complex.getCyGroupID());
	}
	public boolean hasSubComplex() {
		return hasSubComplex;
	}

	public void setHasSubComplex(boolean hasSubComplex) {
		this.hasSubComplex = hasSubComplex;
	}


	public Set<String> getNodeIdSetWithSubcomplexes() {
		return nodeIdSetWithSubcomplexes;
	}

	public List<String> getNodeIdListWithSubcomplexes() {
		return new ArrayList<String>(nodeIdSetWithSubcomplexes);
	}

	public void setNodeIdSetWithSubcomplexes(Set<String> nodeIdSetForHeirachy) {
		this.nodeIdSetWithSubcomplexes = nodeIdSetForHeirachy;
	}
	
	//method that determines how to compare these objects for sorting
	@Override
	public int compareTo(Object obj) {
  	
        if (!(obj instanceof FoundProteinComplex))
			throw new IllegalArgumentException();

        FoundProteinComplex complex;
		complex = (FoundProteinComplex) obj;

        int compareValue = -2;
		int a = complex.getNumberOfMembers();
		int b = numberOfMembers;
			
		if (a == b)
			compareValue = 0;
		if (a < b)
			compareValue = 1;
		if ( a> b)
			compareValue = -1;
				
		return compareValue;
	}
}
