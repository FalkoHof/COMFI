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


/* Class for one parsed Homologene Entry/Line
 * 
 * The String array passed to the constructor contains
 * 1) HID (HomoloGene group id)
 * 2) Taxonomy ID
 * 3) Gene ID
 * 4) Gene Symbol
 * 5) Protein gi
 * 6) Protein accession 
 */
	
public class HomoloGeneEntry {
	
	private int HID;
	private int taxId;
	private int geneId;
	private String geneSymbol;
	private int proteinGi;
	private String proteinAcc;	
	private String organismId;
	private String uniprotID;
	
	public HomoloGeneEntry(String[] oneEntry){
		
		HID = Integer.parseInt(oneEntry[0]);
		taxId = Integer.parseInt(oneEntry[1]);
		geneId = Integer.parseInt(oneEntry[2]);
		geneSymbol = oneEntry[3];
		proteinGi =Integer.parseInt(oneEntry[4]);
		proteinAcc = oneEntry[5];
	}


	public int getHID() {
		return HID;
	}


	public void setHID(int HID) {
		this.HID = HID;
	}


	public int getTaxId() {
		return taxId;
	}


	public void setTaxId(int taxId) {
		this.taxId = taxId;
	}


	public int getGeneId() {
		return geneId;
	}


	public void setGeneId(int geneId) {
		this.geneId = geneId;
	}


	public String getGeneSymbol() {
		return geneSymbol;
	}


	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}


	public int getProteinGi() {
		return proteinGi;
	}


	public void setProteinGi(int proteinGi) {
		this.proteinGi = proteinGi;
	}


	public String getProteinAcc() {
		return proteinAcc;
	}


	public void setProteinAcc(String proteinAcc) {
		this.proteinAcc = proteinAcc;
	}	
	
	public String getOrganismId() {
		return organismId;
	}


	public void setOrganismId(String organismId) {
		this.organismId = organismId;
	}


	public String getUniprotID() {
		return uniprotID;
	}


	public void setUniprotID(String uniprotID) {
		this.uniprotID = uniprotID;
	}
}
