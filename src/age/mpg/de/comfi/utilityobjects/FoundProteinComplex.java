package age.mpg.de.comfi.utilityobjects;

import java.util.List;

public class FoundProteinComplex {
	
		
	private String proteinComplexName;
	private String organism = "";
	private String cyGroupID;
	private String pubmedID;
	
	private List<String>complexMembers;
	private List<String>nodeIdList;

	
	private boolean humanHomologue = false;
	private boolean yeastHomologue = false;
	
	private boolean homologue = false;
	private boolean select = false;
	
	private int numberOfMembers = 0;
	private int taxID = 0;
	
	private final int TAX_ID_HUMAN = 9606;	 
	private final int TAX_ID_YEAST = 4932;
	private final int TAX_ID_MOUSE = 10090;	

	
	public static final String HUMAN = "Human";
	public static final String YEAST = "Yeast";
	public static final String MOUSE = "Mouse";
	
	

	public FoundProteinComplex(String cyGroupID, String proteinComplexName, List<String> complexMembers,List<String> nodeIdList, int taxID, boolean homologue, String organism, String pubmedID){
		this.cyGroupID = cyGroupID;
		this.proteinComplexName = proteinComplexName;
		this.complexMembers = complexMembers;
		this.nodeIdList = nodeIdList;
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
		this.nodeIdList = nodeIdList;
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


	public List<String> getNodeIdList() {
		return nodeIdList;
	}


	public void setNodeIdList(List<String> nodeIdList) {
		this.nodeIdList = nodeIdList;
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
	
}
