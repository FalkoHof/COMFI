package age.mpg.de.comfi.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import age.mpg.de.comfi.managers.TaskManagerManager;
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
	private Long fileSize = -1L;
	private boolean sizeCheck = false;
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
				case DOWNLOAD_UNIPROT_MAPPING_FILE:	copyFileFromFTP(PluginProperties.getInstance().getUniProtFTP(), PluginProperties.getInstance().getUniProtServerDir(), PluginProperties.getInstance().getUniProtMappingFileName(), PluginProperties.getInstance().getDependenciesFolder() + PluginProperties.getInstance().getUniProtMappingFileName());
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
	
	//method for copying the uniprot file from the uniprot ftp
	private void copyFileFromFTP (String ftpName, String dir, String fileName, String target) throws IOException, InterruptedException{
		taskMonitor.setStatus("Downloding: " + target);
		taskMonitor.setPercentCompleted(0);
		file  = new File(target);
		
		//start a new thread that downloads the file
		runner = new Thread(new UniProtDownloader(ftpName, dir, fileName, file));
		runner.start();
		
		
		//waits till the file size is retrived
		while (!sizeCheck){
			Thread.sleep(2000);
		}
		
		
		//checks how much of the file has been downloaded and sets the progressbar
		while (!downloadComplete){
			
			//breaks the while loop if the download gets canceled
			if (interrupted){
				break;
			}
			
			//if the file exists check for size and set progress bar, else wait
			if (file.exists()){
				System.out.println("Downloading... \t" + FileUtils.sizeOf(file) + "/" + fileSize);
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
		
		String ftpName;
		String dirName;
		String fileName;
		File targetFile;
		
		public UniProtDownloader(String ftpName, String dirName, String fileName,File targetFile){
			this.ftpName = ftpName;
			this.dirName = dirName;
			this.fileName = fileName;	
			this.targetFile = targetFile;
		}

		@Override
		public void run() {

			FTPClient client = new FTPClient();
	        logger.info("getting idmapping.dat.gz size");

			try {
				long start = System.currentTimeMillis();
				System.out.println("Connecting to ftp: " + ftpName);
	        	client.connect(ftpName);
	            client.login("anonymous", "anonymous");
	            System.out.println("Client reply code: " + client.getReplyCode());
	            client.setFileType(FTPClient.BINARY_FILE_TYPE);
	            client.setBufferSize(16384);
	            FTPFile[] ftpFile = client.listFiles(dirName);
	            //mapping file is the biggest in the directory so just get the size of the biggest file.
	            for (FTPFile ftp: ftpFile){
	            	if (fileSize < ftp.getSize()){
	            		fileSize = ftp.getSize();
	            	}
	            }
	            sizeCheck = true;
	            
	            FileOutputStream fos = new FileOutputStream(targetFile);
	            System.out.println("Starting retrieving.... URL: " + dirName +fileName);
	            client.retrieveFile(dirName +fileName, fos);
				long end = System.currentTimeMillis();
				long time = end - start;
				System.out.println("Download time (min): " + ((time/1000.0)/60.0));
				fos.close(); 
	            client.logout();
	            client.disconnect();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "An error occoured while getting the uniprot mapping file size.", "PathwayFinder - Download/update files", JOptionPane.ERROR_MESSAGE);
			        e.printStackTrace();
				}
			downloadComplete = true;
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
