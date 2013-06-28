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
package age.mpg.de.comfi.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.utilityobjects.FoundProteinComplex;

public class GenerateOutput {

	private File outputFile;
	
	public GenerateOutput(File outputFile) {
		this.outputFile = outputFile;
		writeOutputFile(generateOutputString());
	}

	
	

	public String generateOutputString(){
		
		List<FoundProteinComplex> resultList = ComplexFinderModel.getInstance().getFoundProteinComplexes();

		String outputStr = "# found protein complexes:" + "\t" + resultList.size() + "\n";
		outputStr = outputStr + "Complex Name" + "\t" + "# Members" + "\t" + "Complex Members" + "\t" + "CyNode IDs" + "\t" + "Human Homologue" + "\t" + "Yeast Homologue" + "\n";
		
		for (FoundProteinComplex oneComplex : resultList){
			outputStr = outputStr + oneComplex.getProteinComplexName() + "\t" + oneComplex.getNumberOfMembers() + "\t";
			
			
			for (String member : oneComplex.getComplexMembers())
				outputStr = outputStr + member + ",";
			
			outputStr = outputStr + "\t";
			

			for (String member : oneComplex.getNodeIdList())
				outputStr = outputStr + member + ",";
			
			outputStr = outputStr + "\t";
			
			
			if (oneComplex.isHumanHomologue())
				outputStr = outputStr + "Yes";

			outputStr = outputStr + "\t";
			
			
			if (oneComplex.isYeastHomologue())
				outputStr = outputStr + "Yes";
			
			outputStr = outputStr + "\t";
			
			outputStr = outputStr + "\n";
			
		}
		return outputStr;
	}
	
	
	
	public void writeOutputFile(String outputStr) {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(outputFile);
			fileWriter.write(outputStr);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
