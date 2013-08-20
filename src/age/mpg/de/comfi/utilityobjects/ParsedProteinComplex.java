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
