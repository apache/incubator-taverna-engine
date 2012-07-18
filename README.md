Taverna PROV support
====================

This is a plugin for the Taverna Workbench and Taverna Command Line
which allows export of the provenance of a workflow run
according to the proposed [PROV-O standard](http://www.w3.org/TR/prov-o/).


Installation
------------
Installation of an released version of this plugin does not require
compilation, but uses Taverna's plugin mechanism.

You need:
* [Taverna Workbench 2.4.0](http://www.taverna.org.uk/download/workbench/2-4/)
* [Patch update for Taverna 2.4.1](http://markmail.org/thread/rd2zpaq27eiqiean)
* Internet connectivity

To install:
1. Start Taverna workbench
2. In the menu, click **Advanced** -> **Updates and plugins**
3. If Taverna does not say "An update is available" , click **Find
   updates**
4. To install the required 2.4.1 patches, click **Update** for each of:
    * _External tool service_
    * _Service catalogue_
    * _Services_
    * _Workbench_
    Note: Click **OK** in the warning message about restart; you do not need
    to restart Taverna for each of these as we'll do that in the end
5. Click **Find new plugins**, then **Add update site** and fill in:
    * Site name: Taverna PROV
    * Site URL: `http://wf4ever.github.com/taverna-prov/`
6. Click **OK**
7. Under _Taverna PROV_, tick to select _Taverna PROV plugin 1.x_
8. Click **Install**
9. Click **Close**
10. Exit and Restart Taverna workbench


Usage in workbench
------------------
In order for PROV export to be possible from the Taverna Workbench,
click **Preferences** from the **File** or **Taverna** menu, then go to the 
[Preferences for Data and Provenance](http://dev.mygrid.org.uk/wiki/display24/taverna/Data+and+provenance+preferences).
Ensure that **Provenance capture** is enabled. (This is the default in 2.4).

If you would like Taverna to keep the provenance data between runs
of the workbench (in order to export at a later time), then you need to
also tick to *disable* **In memory storage**; note that this may slow
down executions of some workflows.

Open and run a workflow to export provenance for. As a quick example,
try the very simple
[helloanyone.t2flow](http://www.myexperiment.org/workflows/2649/download/hello_anyone_895936.t2flow)
from [myExperiment](http://www.myexperiment.org/workflows/2649)
(included in `examples/` folder of this source code).

Exporting PROV from Taverna is done similar to the [export of OPM and
Janus provenance](http://dev.mygrid.org.uk/wiki/display/taverna24/Provenance+export+to+OPM+and+Janus).

The difference is that you need to click **Save provenance (PROV)**, and
in the file dialogue give the name of what will become
a _folder_, rather than a single file.

In short:
1. Click **Results** perspective
2. Select a run on the left
3. Click **Show workflow results** button (in case you are viewing
.  intermediates)
4. Click **Save all values**
5. Ensure all boxes are ticked
6. Click **Save Provenance (PROV)**
7. Browse and type the name of a folder for the results (which will be created)
8. Click **OK**

Browse the folder using Explorer/Finder etc. You should find a set of
files for the input and output ports of the workflow, in addition to a
file `workflowrun.prov.ttl` which contains the PROV-O export of the
complete workflow run in [RDF Turtle format](http://www.w3.org/TR/turtle/):

    stain@ahtissuntu:~/src/taverna-prov/example/helloanyone$ ls
    greeting.txt  name.txt  workflowrun.prov.ttl

    stain@ahtissuntu:~/src/taverna-prov/example/helloanyone$ cat greeting.txt ; echo
    Hello, World!

    stain@ahtissuntu:~/src/taverna-prov/example/helloanyone$ head workflowrun.prov.ttl 
    # @base <file:/home/stain/src/taverna-prov/example/helloanyone/> .
    @prefix scufl2: <http://ns.taverna.org.uk/2010/scufl2#> .
    @prefix prov: <http://www.w3.org/ns/prov#> .
    @prefix owl: <http://www.w3.org/2002/07/owl#> .

    <http://ns.taverna.org.uk/2011/run/5fec72ee-aa48-495b-905c-588203c8c948/> a prov:Activity ;
        prov:influenced _:node172d1lgacx2 .

    _:node172d1lgacx2 a prov:Activity ;
        prov:startedAtTime "2012-07-18T13:40:40.582+01:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;


Querying
--------

Example [SPARQL query](http://www.w3.org/TR/sparql11-query/):

    stain@ahtissuntu:~/src/taverna-prov/example/helloanyone$ cat test.sparql 
    PREFIX prov: <http://www.w3.org/ns/prov#> 

    SELECT ?name ?plan ?ended
    WHERE {
    ?greeting prov:alternateOf <greeting.txt> .
    ?output prov:alternateOf ?greeting .
    ?output prov:wasGeneratedBy ?concatenate .
    ?concatenate prov:endedAtTime ?ended ;
        prov:qualifiedAssociation [
            prov:hadPlan ?plan
    ] .
    ?concatenate prov:used ?string2 .
    ?string2 prov:alternateOf ?name .
    }

Note: Future versions of the PROV-O export will use custom subproperties of
prov:alternateOf to proper distinguish values at workflow level,
processor level and file level.

Query using [rdfproc](http://librdf.org/utils/rdfproc.html) (`apt-get install redland-utils`):
    stain@ahtissuntu:~/src/taverna-prov/example/helloanyone$ rdfproc test parse workflowrun.prov.ttl turtle
    rdfproc: Parsing URI file:///home/stain/src/taverna-prov/example/helloanyone/workflowrun.prov.ttl with turtle parser

    stain@ahtissuntu:~/src/taverna-prov/example/helloanyone$ rdfproc test query sparql - "$(cat test.sparql)" 
    rdfproc: Query returned bindings results:
    result: [name=<file:///home/stain/src/taverna-prov/example/helloanyone/name.txt>, 
             plan=<http://ns.taverna.org.uk/2010/workflowBundle/01348671-5aaa-4cc2-84cc-477329b70b0d/workflow/Hello_Anyone/processor/hello/>, 
             ended="2012-07-18T12:40:20.543Z"^^<http://www.w3.org/2001/XMLSchema#dateTime>]
    rdfproc: Query returned 1 results



Troubleshooting
---------------

If the *Save Provenance (PROV)* button is not listed, then the plugin
was most likely not installed correctly. 

Check that:
* You installed all the 2.4.1 updates (duplicate icons in toolbar indicates no)
* You restarted Taverna after installation
* Taverna can access the Internet; check [proxy settings](http://dev.mygrid.org.uk/wiki/display/taverna24/HTTP+proxy+preferences)
* The plugin and its dependences downloaded correctly
  * Check the `logs/` in the [Taverna home
    directory](http://dev.mygrid.org.uk/wiki/display/taverna24/Taverna+home+directory)
    for any error messages relating to PROV or Sesame
  * To force a new download of plugin, delete `repository/` from the
    above folder (*not* the installation folder) and restart Taverna
  * To start clean and reinstall the plugin, delete everything from the
    [Taverna home directory](http://dev.mygrid.org.uk/wiki/display/taverna24/Taverna+home+directory)
    and restart Taverna
* Check your Java version with `java -version`
* Reinstall Taverna
* Contact myGrid for support <support@mygrid.org.uk>



Installation for command line tool
----------------------------------
_To be done_


Building
--------
Note - you do not normally need to build from source code, installation
from the above plugin site is the preferred way to install this plugin.

You need:
* Java JDK 1.6 or newer (Tested with OpenJDK 1.7.0\_03)
  * Note: Do not use the OpenJDK 1.6 (default in Ubuntu), as this is
   buggy with GUI and File operations, both used by Taverna workbench.
* Maven 2.2 or newer (Tested with [Maven 3.0.4](http://maven.apache.org/download.html))

To compile, run `mvn clean install`

On first run this will download various third-party libraries from Maven
repositories, including required modules of Taverna 2.4. These JARs, and the
compiled JARs from this source code, are installed into
$HOME/.m2/repository/ by default.  

Example compilation:

    stain@ahtissuntu:~/src/taverna-prov$ mvn clean install
    [INFO] Scanning for projects...
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Build Order:
    [INFO] 
    [INFO] Taverna PROV
    [INFO] Taverna PROV W3 Provenance ontology Elmo bindings
    [INFO] Taverna PROV export
    [INFO] Taverna PROV activity UI bindings
    [INFO] Taverna PROV plugin
    [INFO] PROV Taverna command line
    [INFO]                                                                         
    [INFO] ------------------------------------------------------------------------
    [INFO] Building Taverna PROV 1.4-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
    (..)
    Downloading: http://www.mygrid.org.uk/maven/repository/net/sf/taverna/t2/taverna-workbench/workbench-dev/2.4.0/workbench-dev-2.4.0.pom
    Downloaded: http://www.mygrid.org.uk/maven/repository/net/sf/taverna/t2/taverna-workbench/workbench-dev/2.4.0/workbench-dev-2.4.0.pom (3 KB at 5.4 KB/sec)
    (..)
    [INFO] Installing /home/stain/src/taverna-prov/prov-taverna-cmdline/target/prov-taverna-cmdline-1.4-SNAPSHOT-tests.jar to /home/stain/.m2/repository/org/purl/wf4ever/provtaverna/prov-taverna-cmdline/1.4-SNAPSHOT/prov-taverna-cmdline-1.4-SNAPSHOT-tests.jar
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary:
    [INFO] 
    [INFO] Taverna PROV ...................................... SUCCESS [4.109s]
    [INFO] Taverna PROV W3 Provenance ontology Elmo bindings . SUCCESS [1:45.270s]
    [INFO] Taverna PROV export ............................... SUCCESS [45.873s]
    [INFO] Taverna PROV activity UI bindings ................. SUCCESS [1:46.572s]
    [INFO] Taverna PROV plugin ............................... SUCCESS [15.028s]
    [INFO] PROV Taverna command line ......................... SUCCESS [1:12.093s]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 5:54.000s
    [INFO] Finished at: Wed Jul 18 12:08:13 BST 2012
    [INFO] Final Memory: 72M/274M
    [INFO] ------------------------------------------------------------------------
    

On first run this will download various third-party libraries from Maven
repositories, including required modules of Taverna 2.4. Depending on
your bandwith, this might take about 5 minutes in total.  These JARs,
and the compiled JARs from this source code, are installed into
`$HOME/.m2/repository/` by default.  

Note that to work with Taverna's plugin system, the build is specific
for a particular Taverna version. To build this plugin for a different
version of Taverna, modify the `<properties>` section of `pom.xml` to
match the [Maven module
versions](http://dev.mygrid.org.uk/wiki/display/developer/Maven+module+version+numbers)
for the specific Taverna release.

In order for Taverna to find your install of the plugin rather than
download from the official plugin site, you will need to manually edit
*plugins/plugins.xml* of the [Taverna home
directory](http://dev.mygrid.org.uk/wiki/display/taverna24/Taverna+home+directory)
(run Taverna once to create the file) to include this section right
before the final </plugins:plugins>.

```xml
     <plugin>
        <provider>org.purl.wf4ever</provider>
        <identifier>org.purl.wf4ever.provtaverna.prov-taverna-plugin</identifier>
        <version>1.3</version>
        <name>Taverna PROV plugin</name>
        <description/>
        <enabled>true</enabled>
        <repositories>
            <repository>file:///home/fred/.m2/repository/</repository>
            <repository>http://www.mygrid.org.uk/maven/repository/</repository>
            <repository>http://uk.maven.org/maven2/</repository>
            <repository>http://repo.aduna-software.org/maven2/releases/</repository>
            <repository>http://repo.aduna-software.org/maven2/snapshots/</repository>
            <repository>http://repo.aduna-software.org/maven2/ext/</repository>
            <repository>http://repository.aduna-software.org/maven2/</repository>
            <repository>http://repository.aduna-software.org/maven2-snapshots/</repository>
            <repository>http://people.apache.org/repo/m2-snapshot-repository/</repository>
            <repository>http://nexus.codehaus.org/snapshots/</repository>
            <repository>http://repository.jboss.org/maven2/</repository>
        </repositories>
        <profile>
            <dependency>
                <groupId>org.purl.wf4ever.provtaverna</groupId>
                <artifactId>prov-taverna-ui</artifactId>
                <version>1.4-SNAPSHOT</version>
            </dependency>
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>4.8.2</version>
            </dependency>
        </profile>
        <compatibility>
            <application>
                <version>2.4.0</version>
            </application>
        </compatibility>
    </plugin>
```

Modify the line:
    <repository>file:///home/fred/.m2/repository/</repository>

to match your $HOME/.m2/repository. On Windows this path should look like:

    <repository>file:///C:/Users/fred/.m2/repository/</repository>    

NOTE: You must remove the official installation of this plugin if this
is already present in `plugins.xml`, look for
`org.purl.wf4ever.provtaverna`.

Take care that the `<version>` of the `<plugin>` and `prov-taverna-ui` matches
the `<version>` specified in the `pom.xml` of this source code (typically `1.x-SNAPSHOT`).

If you are building for a different Taverna, check that the `<application><version>`
matches the Taverna version (compare with the existing plugins)

