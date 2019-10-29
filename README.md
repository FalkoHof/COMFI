# COMFI - A protein complex finder protein

![summary](../master/assets/comfi_workflow.png)

This project hosts a Cytoscape 2.8 plugin for detecting protein complexes in protein interaction networks.

It utilizes data from the following databases:

- [CORUM](http://mips.helmholtz-muenchen.de/genre/export/sites/default/corum/allComplexes.psimi.zip)
- [Cyc2008](http://wodaklab.org/cyc2008/resources/CYC2008_complex.tab)

If you use COMFI please cite:

[Garmhausen, M. et al. Virtual pathway explorer (viPEr) and pathway enrichment analysis tool (PEANuT): creating and analyzing focus networks to identify cross-talk between molecules and pathways. BMC Genomics 16, 790 (2015).](https://bmcgenomics.biomedcentral.com/articles/10.1186/s12864-015-2017-z)

COMFI is designed to work in concert with the VIPER and [PEANuT](https://github.com/FalkoHof/PEANuT) plugins.

## Installation and setup guide

### System requirements

- Internet connection
- [Java 1.5+](https://www.oracle.com/technetwork/java/index.html)
- [Cytoscape 2.8](https://cytoscape.org/)
- ~2 G of free disk space (This only required during the set up if you build your index. If you download our prepacked .zip file or finished building the index, ~70 M will suffice)

### Installation procedure

In general the installation procedure comprises 2 simple, quick steps:

1. [Download](../master/bin/COMFI.jar and install COMFI in Cytoscape
2. Set up COMFI through the “(Re)build index/download dependencies” submenu

### Installation in Cytoscape

There are three easy, different ways to install COMFI in Cytoscape:

1. Search for the COMFI plugin under the Cytoscape menu “plugins” “manage plugins” and click on install.
2. Download the COMFI.jar file from the project page and click on “Plugins” “Install plugin from file” and select the downloaded .jar file.
3. Download the COMFI.jar file from the project page and place it in the “plugins” folder in your Cytoscape installation directory.

[installationOverview](../master/assets/install.png)

If the plugin is correctly installed you should now be able to see a new “COMFI” entry in the “plugins menu”. If that is not the case try restarting Cytoscape.

[successfullInstallation](../master/assets/comfi_menuOverview.png)

### Setting up COMFI

In order to work properly COMFI needs to build an index and to download several files from different databases. Under Windows it may be required that you need to run Cytoscape as administrator to make the downloader work! Otherwise Cytoscape could crash.

The files required for the index are:

- [UniProt mapping file](ftp://ftp.ebi.ac.uk/pub/databases/uniprot/current_release/knowledgebase/idmapping/idmapping.dat.gz)
- [HomoloGene file](ftp://ftp.ncbi.nih.gov/pub/HomoloGene/current/homologene.data)
-[YeastGenome mapping file](http://downloads.yeastgenome.org/curation/chromosomal_feature/dbxref.tab)

The files that contain the protein complex information are:

- [CORUM](http://mips.helmholtz-muenchen.de/genre/export/sites/default/corum/allComplexes.psimi.zip)
- [Cyc2008](http://wodaklab.org/cyc2008/resources/CYC2008_complex.tab)

These files can either be manually downloaded und placed in a folder with the name "filesCOMFI" in the Cytoscape plugin folder (the CORUM file needs to be unzipped and renamed into corum_psimi.xml) or you can use the “(Re)build index/download dependencies” submenu in the “COMFI” entry.

When you select the "(Re)build index/download dependencies” submenu a dialogue like below should pop up. You then have basically two options:

1. [You can build your own index](#build-your-own-index)
2. [You can download a prepacked .zip file](#prepacked-.zip-file)

[downloaderMenu](../master/assets/comfi_downloader1.png)

#### Build your own index

Just click on the folder menus and consecutively download the files from box 1 & 2.
When the download is completed you will be notified of a successful download and the status icon will switch from the red cross to a green tick.
Downloading the UniProt mapping file can take quite long (~110 min) as it is quite large and the UniProt server bandwidth limited.
When you have finished downloading the files from box 1, build the index. Then proceed with downloading the files from box 2.

#### Prepacked .zip file

If this seems to time consuming or complex you can also download a prepacked .zip file of all the dependencies [here](../master/bin/filesCOMFI.zip) or via the download menu. You simply need to unzip the file into your Cytoscape plugins folder. However this prepacked file will probably not be as up to date when it comes to homology mapping as if you build your own index.

In the end the menu should look like the lower panel in the following picture, which means COMFI is ready to use!

[setUpSuccess](../master/assets/comfi_downloader2.png)

### To compile the project

1. clone the git repository to your computer
2. open the build.properties
3. set the following two variables:
..* classpath.local.location=TheFolderThatContainsThisSourceCode/lib
..* cytoscape.plugins.path=YourCytoscapeFolder/plugins/
4. make sure you have ant installed
3. run ```ant build.xml``` to compile the plugin

## User guide

### The Menu structure

The COMFI menu comprises the submenus:

1. Find complexes
2. Show results
3. (Re)build index/download dependencies

[findComplexesMenu](../master/assets/comfi_menuOverview.png)

### Find complexes

The menu entry “Find complexes” allows the user to find known protein complexes within a Cytoscape network. Human, Mouse and Yeast complexes can be found and identified directly via the [CORUM](http://mips.helmholtz-muenchen.de/genre/proj/corum) or [Cyc2008](http://wodaklab.org/cyc2008/) databases. Complexes in other organisms can be identified via homology mapping to Human or Yeast complexes via HomoloGene.

After clicking on the “Find complexes” submenu a dialogue as displayed in the figure below will pop up. The dialogue is structured into 2 sections.

1. [General options](#general-options)
2. [Network options](#network-options)

[findComplexesMenu](../master/assets/comfi_findComplexesMenu.png)

### General options

There you have the possibility so select the organism of your choice (Human, Mouse, Yeast & other) via the combo box displayed on top of the dialogue. After selecting a organism you will be provided with several other options that need to be set according to your data and preferences.
If you choose other you can select whether you want to use homology mapping to Human, Yeast or both organisms to detect potential protein complexes. Then you need to select the column containing either UniProt accession numbers (Human, Mouse & other) or CYGD ids (Yeast). If you don't have the corresponding ids try CyThesaurus to map your ids to the corresponding UniProt accession numbers or CYGD ids.
You then can select several other options via three checkboxes. There you can choose to create nested networks, layout the network after the search and remove proteins which are contained in a protein complex from a network.

### Network options

Here you can select your source and target network. The source network specifies the network which is searched for protein complexes, while the target network specifies in which network the complexes are added as nodes. If you want to add you complexes to a new empty network, simply select "create new network" in the target network combo box.

### Show results

The menu entry “Show results” allows the user to browse the results of the protein complex search.
When clicking on the “Show results” submenu a dialogue as displayed in the figure below will pop up. There you have the possibility to search the result list via the text box on the bottom or click on different table headers to sort the table according to your interest. The column "NodeID" shows the identifier of the node representing the complex in the network. The column "Homology" indicates if this complex was identified via homology mapping or not.
The checkboxes in "Select Node" can be ticked and the ticked nodes selected in the network via the "select" button.
The button "save all" can be used to export the results as a .tab file (tab delinted text file) that can be easily imported into e.g Excel.

[showResultsMenu](../master/assets/comfi_resultsMenu.png)

If you want to get some hand on experience with COMFI you can download a test network comprised out of the yeast proteome here and have a look at our walkthrough example.

### (Re)build index/download dependencies

The menu entry “(Re)build index/download dependencies” allows the user to download and update protein complex data and to build an index required for homology mapping.

To download files simply click on the button on the right, displaying a folder and an arrow.
To build the index click on the "play" icon. In order to build the index, dependencies 1-3 need to show a green tick. When the index is build it will delete unnecessary files to free up disk space. During this process the status for dependency 1-3 will change to the red cross while the status of the index will switch to a green tick. See also the COMFI Installation and setup guide.

[downloaderMenu1](../master/assets/comfi_downloader1.png)

[downloaderMenu2](../master/assets/comfi_downloader2.png)

It is recommended that from time to time you download all files and build a new index to keep your dependencies up to date, as the UniProt, HomoloGene, YeastGenome [CORUM](http://mips.helmholtz-muenchen.de/genre/proj/corum) and [Cyc2008](http://wodaklab.org/cyc2008/) content will change over time.

## Walkthrough exmaple

### Prerequisites

Before we can dive into the example you need to:

1. Install Cytoscape and COMFI [(see our installation guide)](#installation-procedure)
2. Download our [example network](../master/assets/yestTestNetwork.cys)

### Getting started

After you have started Cytoscape open the example session file via "File" --> "Open" and the select the yestTestNetwork.cys you have just downloaded.

### Using the plugin

Using the plugin consist of two simple steps:

1. Find protein complexes
2. Review the results

If you want a detailed overview on how to use the different submenus see [here](#user-guide).

### Finding protein complexes

To start searching for protein complexes in a network go to the menu "Plugins"-->"COMFI"-->"Find complexes".

Then set the following parameters:

1. Set the organism to Yeast
2. Set the CYGDId combobox to "CYGDIDs"
3. Select "Create nested networks"
4. Unselect "Layout network"
5. Select "Remove complex members from source network"
6. Select "Binary-GS.txt" as source and target network
7. Click on the "Search" button
For more information see also the [user guide](#user-guide).

[example1](../master/assets/comfi_example1.png)

### Show results

When the protein complex search has finished the result menu will pop up. You can search the results by typing into the search field or sort them by clicking on colums. See also the [user guide](#user-guide).

[example2](../master/assets/comfi_example2.png)

You can click on "Save all", save the result as a tab separated file and import it into Excel. This is done in Excel via "File"--> "open", setting the file filter to "enable all documents" and then simply selecting and opening it. Afterwards Excel will ask you in a series of dialogues which kind of delimiter this file uses. Just select tab and "Finish".

If you compare the before and after situation you will also note that the number of nodes has increased even though the nodes representing single proteins that are contained within a complex have been removed. This is caused by the possibility that proteins from a set can be combined in various manners to form complexes (e.g. A+B , A+C, B+C, A+B+C).You will also notice that the number of edges has increased. This is because every new complex node has all the connections the single proteins had (and maybe even some new connections to other complex nodes).
You will also notice the long list of new networks which are in fact the nested networks, that contain each member of the complex.

[example3](../master/assets/comfi_example3.png)

## Troubleshooting

You can also ask questions in our [google group](https://groups.google.com/forum/#!forum/comfi-cytoscape).
