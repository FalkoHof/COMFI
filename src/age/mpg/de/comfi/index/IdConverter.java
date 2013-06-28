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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class IdConverter {
	
	private int maxHits = 100;
	private final String taxIdHuman = "9606";
	private final String taxIdYeast = "4932";
	
	private boolean humanHomology;
	private boolean yeastHomology;
	private List<String> querys;
	private Map<String, String>humanHomologyMap = new HashMap<String, String>();
	private Map<String, String>yeastHomologyMap = new HashMap<String, String>();

	

	public IdConverter(boolean humanHomology, boolean yeastHomology, List<String>queryList){
		this.humanHomology = humanHomology;
		this.yeastHomology = yeastHomology;
		this.querys = queryList;
		convertIds();
	}
	
	public void convertIds(){
		
		HashSet<String> hidIdsSet = new HashSet<String>();
		List<String> hidIdsList = new ArrayList<String>();
		File indexDir = new File("plugins/PCFDependencies/lucene");
	
		try {
			Directory fsDir = FSDirectory.open(indexDir);
		
			IndexReader reader = IndexReader.open(fsDir);
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer whitespaceAn = new WhitespaceAnalyzer();

			String dField = "UniprotID";

			QueryParser parser = new QueryParser(Version.LUCENE_30,dField,whitespaceAn);
			
				// loop to find the relevant groups of homologues genes
				for(String query : querys){
					Query q = parser.parse(query);
					TopDocs hits = searcher.search(q,maxHits);
					ScoreDoc[] scoreDocs = hits.scoreDocs;
				
					//get the hid ids of all relevant gene groups
					for (int n = 0; n < scoreDocs.length; ++n) {
						ScoreDoc sd = scoreDocs[n];
						int docId = sd.doc;
						Document d = searcher.doc(docId);
						String hid = d.get("HID");
						getHomologues(searcher, whitespaceAn, query, hid);
						}
					}
					System.out.println(hidIdsSet);
					hidIdsList.addAll(hidIdsSet);
					
			whitespaceAn.close();
			searcher.close();
			reader.close();
			fsDir.close();
		
		} catch (IOException e) {
		e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		}
				
			
		public void getHomologues(IndexSearcher searcher, Analyzer whitespaceAn, String query, String hid) throws ParseException, IOException{
			
			String dField = "HID";			
			QueryParser parser = new QueryParser(Version.LUCENE_30, dField, whitespaceAn);

			Query q = parser.parse(hid);
				
			TopDocs hits = searcher.search(q,maxHits);
			ScoreDoc[] scoreDocs = hits.scoreDocs;
			//get the uniprot ids of either human or yeast specific or both
			for (int n = 0; n < scoreDocs.length; ++n) {
				ScoreDoc sd = scoreDocs[n];
				int docId = sd.doc;
				Document d = searcher.doc(docId);
				String taxId = d.get("TaxID");
			
				if(humanHomology){
					if(taxId.equals(taxIdHuman)){
						String [] temp = d.get("UniprotID").split(" ");
						for(String s : temp)
							humanHomologyMap.put(s.trim().trim(),query.trim());
						}
					}
				if(yeastHomology){
					if(taxId.equals(taxIdYeast)){
						String [] temp = d.get("OrganismSpecificID").split(" ");
						for(String s : temp)
							yeastHomologyMap.put(s.trim(),query.trim());
					}
				}
							
			}
		}
	
		
	public Map<String, String> getYeastMap() {
		return yeastHomologyMap;
	}

	public Map<String, String> getHumanHomologyMap() {
		return humanHomologyMap;
	}

	public Map<String, String> getYeastHomologyMap() {
		return yeastHomologyMap;
	}

	public List<Map<String, String>> getHomologuesMaps() {
		
		List<Map<String,String>>homologuesMaps = new ArrayList<Map<String,String>>();
		
		if (humanHomology)
			homologuesMaps.add(humanHomologyMap);
		if (yeastHomology)
			homologuesMaps.add(yeastHomologyMap);
		
		return homologuesMaps;
	}
	
	
	
	
}
