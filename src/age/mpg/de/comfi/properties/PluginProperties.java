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
package age.mpg.de.comfi.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PluginProperties {

    private static PluginProperties instance = new PluginProperties();
    private Properties props;

    private PluginProperties(){
        try {
            props = new Properties();
            InputStream inputStream = getClass().getResourceAsStream("plugin.properties");
            props.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    
    public String getZippedDependenciesFile(){
    	return props.getProperty("ZippedDependencies");
    }
    
    
    public String getCorumZipFile(){
    	return props.getProperty("CORUMZipXml");
    }
    
    
    public String getDependenciesFolder(){
    	return props.getProperty("dependencies");
    }
    
    
    public String getLuceneIndexName(){
    	return props.getProperty("luceneIndex");
    }
    
    public String getCYC2008TabName(){
    	return props.getProperty("CYC2008tab");
    }
    
    public String getUniProtMappingFileName(){
    	return props.getProperty("UniProt");
    }
    
    public String getHomoloGeneFileName(){
    	return props.getProperty("HomoloGene");
    }
    
    public String getYeastGenomeMappingFileName(){
    	return props.getProperty("YeastGenome");
    }
    
    
    public static PluginProperties getInstance() {
        return instance;
    }
    
    public String getPluginName(){
    	return props.getProperty("pluginname");
    }
    
    public String getCorumXmlName(){
    	return props.getProperty("CORUMxml");
    }
    

    public int[] getTaxId(){
    	String [] taxIdsString = (String[])props.getProperty("taxIds").trim().split(";");
    	int[] taxIds = new int[taxIdsString.length];
    	
    	for (int i = 0; i<taxIdsString.length; i++)
    		taxIds[i] = Integer.parseInt(taxIdsString[i]);
    	
    	return taxIds;
    }

    
    
    public String getUrlZippedDependencies(){
    	return  props.getProperty("urlZippedDependencies").trim();
    }
    
    public  String getUrlHomoloGene(){
    	return  props.getProperty("urlHomologene").trim();

    }
    
    public String getUrlCORUM(){
    	return  props.getProperty("urlCORUM").trim();
    }   
    
    public String getUrlCYC2008(){
    	return  props.getProperty("urlCYC2008").trim();
    }
        
    public String getUrlYeastFile(){
    	return  props.getProperty("urlYeastFile").trim();
    }
    
    public String getUniProtFTP(){
    	return props.getProperty("ftpSeverUniprot").trim();
    }
    
    public String getUniProtServerDir(){
    	return props.getProperty("ftpDirUniprot").trim();
    }
           
    public String[] getFilenames(){
        return (String[]) props.getProperty("filenames").trim().split(";");
    }
    
    public String [] getDefaultOrganisms(){
    	return (String []) props.getProperty("organisms").trim().split(";");
    }
    
}
