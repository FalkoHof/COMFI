package age.mpg.de.comfi.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import age.mpg.de.comfi.managers.TaskManagerManager;
import age.mpg.de.comfi.model.ComplexFinderModel;
import age.mpg.de.comfi.properties.PluginProperties;

import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.util.URLUtil;

public class DependenciesDownloader implements Task {
	
	private boolean interrupted = false;
	private cytoscape.task.TaskMonitor taskMonitor;
	private CyLogger logger = CyLogger.getLogger(this.getClass());
	private boolean downloadComplete = false;
	private File file;
	private Thread runner;
	
	public static final byte DOWNLOAD_UNIPROT_MAPPING_FILE = 0, DOWNLOAD_HOMOLOGENE = 1,DOWNLOAD_YEAST_GENOME = 2, DOWNLOAD_CORUM = 3, DOWNLOAD_CYC2008 = 4;
	
	private byte parameter = -1;
	
	public DependenciesDownloader(byte parameter){
		this.parameter = parameter;
	}
	
	public DependenciesDownloader(){
		
		
	}
	
	@Override
	public void run() {
		
		try{
			File dir = new File(PluginProperties.getInstance().getDependenciesFolder());
			if (!dir.exists())
				FileUtils.forceMkdir(dir);
			
			switch(parameter){
				case DOWNLOAD_UNIPROT_MAPPING_FILE:	copyFileFromFTP(PluginProperties.getInstance().getUrlUniProt(), PluginProperties.getInstance().getDependenciesFolder() + PluginProperties.getInstance().getUniProtMappingFileName());
					break;
				case DOWNLOAD_HOMOLOGENE:			copyFileFromUrlCytoscape(PluginProperties.getInstance().getUrlHomoloGene(),PluginProperties.getInstance().getDependenciesFolder() +  PluginProperties.getInstance().getHomoloGeneFileName());
					break;
				case DOWNLOAD_YEAST_GENOME:			copyFileFromUrlCytoscape(PluginProperties.getInstance().getUrlYeastFile(),PluginProperties.getInstance().getDependenciesFolder() +  PluginProperties.getInstance().getYeastGenomeMappingFileName());
					break;
				case DOWNLOAD_CORUM:				copyFileFromUrlCytoscape(PluginProperties.getInstance().getUrlCORUM(),PluginProperties.getInstance().getDependenciesFolder() +  PluginProperties.getInstance().getCorumZipFile());
					break;
				case DOWNLOAD_CYC2008:				copyFileFromUrlCytoscape(PluginProperties.getInstance().getUrlCYC2008(),PluginProperties.getInstance().getDependenciesFolder() +  PluginProperties.getInstance().getCYC2008TabName());
					break;
				default:			throw new InvalidParameterException();	
			}
		} catch(IOException e){
			JOptionPane.showMessageDialog(null, "An error occoured while downloading.\nSee the project wiki for help on manually donwloading the files", "PathwayFinder - Download/update files", JOptionPane.ERROR_MESSAGE);
			logger.warn("An error occoured while downloading", e);
		} catch (InterruptedException e) {
			logger.warn("Thread interrupted");
			e.printStackTrace();
		}
		
	}
	

	
	private void copyFileFromUrlCytoscape(String source, String target) throws IOException{
		if (!interrupted){
			file = new File(target);		
			taskMonitor.setStatus("Downloding: " + target);
			URLUtil.download(source, file, taskMonitor);
			System.out.println(target + " downloaded from url: " + source);
			logger.info(target + " downloaded from url: " + source);
			if (parameter == DOWNLOAD_CORUM)
				unzipFile(file, target);
			JOptionPane.showMessageDialog(null, "File successfully downloaded!","PathwayFinder - Download/update files", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	
	
	private void unzipFile(File file, String filename) throws IOException{
		logger.info("Unzipping: " + file.getName());
		taskMonitor.setStatus("Unzipping: " + file.getName());
		taskMonitor.setPercentCompleted(-1);

		 ZipFile zf = new ZipFile(file);
		 Enumeration e = zf.entries();
		 
		 while (e.hasMoreElements()) {
			 ZipEntry ze = (ZipEntry) e.nextElement();
		     System.out.println("Unzipping " + ze.getName());
		     FileOutputStream fout = new FileOutputStream(PluginProperties.getInstance().getDependenciesFolder() + PluginProperties.getInstance().getCorumXmlName());
		     InputStream in = zf.getInputStream(ze);
		     for (int c = in.read(); c != -1; c = in.read())
		    	 fout.write(c);
		      
		    in.close();
		    fout.close();
		 }
			taskMonitor.setPercentCompleted(100);
	    	FileUtils.forceDelete(file);
	}

	
	
	

	//method for getin the size of the uniprot mapping file --> needed for progressbar
	private long getIdMappigFileSize(){

		FTPClient client = new FTPClient();
        long size = 0;
        logger.info("getting idmapping.dat.gz size");

		try {
        	client.connect("ftp.ebi.ac.uk");
            client.login("anonymous", "anonymous");
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            
            FTPFile[] ftpFile = client.listFiles("/pub/databases/uniprot/current_release/knowledgebase/idmapping/");
            //mapping file is the biggest in the directory so just get the size of the biggest file.
            for (FTPFile ftp: ftpFile){
            	if (size < ftp.getSize())
            		size = ftp.getSize();
            }    
                
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "An error occoured while getting the uniprot mapping file size.", "PathwayFinder - Download/update files", JOptionPane.ERROR_MESSAGE);
		        e.printStackTrace();
			}
		return size;
	}
	
	
	
	//method for copying the uniprot file from the uniprot ftp
	private void copyFileFromFTP (String source, String target) throws IOException, InterruptedException{
		taskMonitor.setStatus("Downloding: " + target);
		taskMonitor.setPercentCompleted(0);
		file  = new File(target);
		
		long fileSize = getIdMappigFileSize();
		
		
		//start a new thread that downloads the file
		runner = new Thread(new UniProtDownloader(new URL(source), file));
		runner.start();
		
		//checks how much of the file has been downloaded and sets the progressbar
		while (!downloadComplete){
			
			//breaks the while loop if the download gets canceled
			if (interrupted){
				break;
			}
			
			//if the file exists check for size and set progress bar, else wait
			if (file.exists()){
				taskMonitor.setPercentCompleted(TaskManagerManager.getInstance().getPercentage(FileUtils.sizeOf(file),fileSize));
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		taskMonitor.setPercentCompleted(100);
		if(!interrupted)
			JOptionPane.showMessageDialog(null, "File successfully downloaded!","PathwayFinder - Download/update files", JOptionPane.INFORMATION_MESSAGE);
	}	
	
	
	
	class UniProtDownloader implements Runnable{
		
		URL url;
		File targetFile;
		
		public UniProtDownloader(URL url,File targetFile){
			this.url = url;
			this.targetFile = targetFile;
		}

		@Override
		public void run() {
			try {
				FileUtils.copyURLToFile(url, file);
				downloadComplete = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	@Override
	public String getTitle() {
        return "Updating/Downloading dependencies...";
	}

	@Override
	public void halt() {
        this.interrupted = true;
        runner.interrupt();
        try {
			FileUtils.forceDelete(file);
	     } catch (IOException e) {
			e.printStackTrace();
	     }
   }

	@Override
	 public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}
}
