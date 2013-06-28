package age.mpg.de.comfi.utilityobjects;


import java.util.ArrayList;
import java.util.List;

public class ParsedProteinComplex {

	private int id;
	private String fullName;
	private int taxId;
	private List<String>interactors = new ArrayList<String>();
	private String pubmedId;
	
	public ParsedProteinComplex(){
	
	}
		
	public ParsedProteinComplex(int id, int taxId, String fullName, List<String> interactors, String pubmedId){
		this.id = id;
		this.taxId = taxId;
		this.fullName = fullName;
		this.interactors = interactors;		
		this.pubmedId = pubmedId;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	public int getTaxId() {
		return taxId;
	}


	public void setTaxId(int taxId) {
		this.taxId = taxId;
	}


	public List<String> getInteractors() {
		return interactors;
	}


	public void setInteractors(List<String> interactors) {
		this.interactors = interactors;
	}

	public String getPubmedId() {
		return pubmedId;
	}
	
}
