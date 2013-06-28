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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import age.mpg.de.comfi.managers.TaskManagerManager;
import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.properties.PluginProperties;
import age.mpg.de.comfi.utilityobjects.HomoloGeneEntry;

import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class IndexBuilder implements Task {
	
	private List<HomoloGeneEntry> homoloGeneList;
	private cytoscape.task.TaskMonitor taskMonitor;
	private final String TASK_TITLE = "Indexing HomoloGene..";
	private boolean interrupted = false;
	private CyLogger logger = CyLogger.getLogger(this.getClass());
	private static final String pluginName = PluginProperties.getInstance().getPluginName();
	
	
	public IndexBuilder () {
		
	}
	
	
	private void indexFiles() throws CorruptIndexException, LockObtainFailedException, IOException{

	    int counter = 0;
		
		File indexDir = new File(PluginProperties.getInstance().getDependenciesFolder(), PluginProperties.getInstance().getLuceneIndexName());

		Directory fsDir = FSDirectory.open(indexDir);
		
	    IndexWriter indexWriter = new IndexWriter(fsDir, new WhitespaceAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
	    

		Field hid = new Field("HID","", Store.YES,Index.NOT_ANALYZED);
		Field taxID = new Field ("TaxID","", Store.YES,Index.NOT_ANALYZED);
		Field geneID = new Field("GeneID","", Store.YES,Index.NOT_ANALYZED);
		Field geneSymbol = new Field("GeneSymbol","", Store.YES,Index.NOT_ANALYZED);
		Field proteinGi = new Field("ProteinGi", "", Store.YES,Index.NOT_ANALYZED);
		Field proteinAcc = new Field("ProteinAccession", "", Store.YES, Index.NOT_ANALYZED);
		Field uniprotId = new Field("UniprotID", "", Store.YES, Index.ANALYZED);
		Field OrganismSpecificID = new Field("OrganismSpecificID", "", Store.YES, Index.ANALYZED);

		System.out.println("Indexing Entries...");
		
		for(HomoloGeneEntry oneEntry : homoloGeneList){
			
			if(interrupted){
				indexWriter.commit();
		    	indexWriter.optimize();
		    	indexWriter.close();
		    	fsDir.close();
		    	indexDir.delete();
		    	logger.warning("indexing canceled - incomplete index has been deleted");
				ComplexFinderModel.getInstance().setExit(true);
				break;
			}
			
			int percentage = TaskManagerManager.getInstance().getPercentage(counter, homoloGeneList.size());
			taskMonitor.setPercentCompleted(percentage);
			
			
			counter++;
			Document doc = new Document(); 
		
			hid.setValue(Integer.toString(oneEntry.getHID()));
			taxID.setValue(Integer.toString(oneEntry.getTaxId()));
			geneID.setValue(Integer.toString(oneEntry.getGeneId()));
			geneSymbol.setValue(oneEntry.getGeneSymbol());
			proteinGi.setValue(Integer.toString(oneEntry.getProteinGi()));
			proteinAcc.setValue(oneEntry.getProteinAcc());
			uniprotId.setValue(oneEntry.getUniprotID());
			OrganismSpecificID.setValue(oneEntry.getOrganismId());
			
			
			doc.add(hid);
			doc.add(taxID);
			doc.add(geneID);
			doc.add(geneSymbol);
			doc.add(proteinGi);
			doc.add(proteinAcc);
			doc.add(uniprotId);
			doc.add(OrganismSpecificID);
			indexWriter.addDocument(doc);
			
			if (counter % 5000 == 0){
				System.out.println(counter + " Commiting..");
				indexWriter.commit();
			}
		}
    	indexWriter.commit();
    	indexWriter.optimize();
    	indexWriter.close();
    	fsDir.close();
	}

	
	private void deleteUnecessaryFiles() throws IOException{
		
		List<File>fileList = new ArrayList<File>();
		
		fileList.add(new File(PluginProperties.getInstance().getDependenciesFolder(), PluginProperties.getInstance().getUniProtMappingFileName()));
		fileList.add(new File(PluginProperties.getInstance().getDependenciesFolder(), PluginProperties.getInstance().getHomoloGeneFileName()));
		fileList.add(new File(PluginProperties.getInstance().getDependenciesFolder(), PluginProperties.getInstance().getYeastGenomeMappingFileName()));

		for (File file: fileList)
			FileUtils.forceDelete(file);
	}
	

	@Override
	public void run() {
		homoloGeneList = ComplexFinderModel.getInstance().getHomoloGeneList();
		try {
			indexFiles();
			deleteUnecessaryFiles();			
		} catch (CorruptIndexException e) {
			logger.warn("Error while indexing...", e);
			JOptionPane.showMessageDialog(null, "Failed to build index.\nMake sure all needed files are in the PCFDepencencies folder", pluginName + " - (re)indexing", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			logger.warn("Error while indexing...", e);
			JOptionPane.showMessageDialog(null, "Failed to build index.\nMake sure all needed files are in the PCFDepencencies folder", pluginName + " - (re)indexing", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			logger.warn("Error during the index or file deletion process...", e);
			JOptionPane.showMessageDialog(null, "Failed to build index.\nMake sure all needed files are in the PCFDepencencies folder", pluginName + " - (re)indexing", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
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
