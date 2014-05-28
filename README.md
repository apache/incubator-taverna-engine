# Taverna PROV support

This is a plugin for the [Taverna](http://www.taverna.org.uk/) Workbench
and Taverna Command Line which allows export of the provenance of a
workflow run according to the [W3C PROV-O
standard](http://www.w3.org/TR/prov-o/).


## Source code and license
This plugin is distributed under the [GNU Lesser General Public License
2.1](http://www.gnu.org/licenses/lgpl-2.1.html) (LGPL). The source code
for this plugin is available at https://github.com/taverna/taverna-prov


## Installation for Taverna workbench
Installation of an released version of this plugin does not require
compilation, but uses Taverna's plugin mechanism.

You need:
* [Taverna Workbench 2.5](http://www.taverna.org.uk/download/workbench/2-5/)
* Java 1.7 (now comes included with Taverna)
* Internet connectivity

Taverna-PROV is included in Taverna 2.5 and later.

To install any updates to the Taverna-PROV plugin:

1.  Start Taverna workbench

2.  In the menu, click **Advanced** -> **Updates and plugins**

3.  If Taverna does not say "An update is available" , click 
    **Find updates**

4.  For **Taverna-PROV databundle**, click **Update** if shown.

5.  Click **Close**

6. Exit and Restart Taverna workbench


## Usage in the Taverna Workbench

In order for PROV export to be possible from the Taverna Workbench,
click **Preferences** from the **File** or **Taverna** menu, then go to the 
[Preferences for Data and Provenance](http://dev.mygrid.org.uk/wiki/display24/taverna/Data+and+provenance+preferences).
Ensure that **Provenance capture** is enabled (this is the default in 2.5).

If you would like Taverna to keep the provenance data between runs
of the workbench (in order to export at a later time), then you need to
also untick to *disable* **In memory storage**; note however that this may slow
down executions of some workflows.

Open and run a workflow to export provenance for. As a quick example,
try the very simple
[helloanyone.t2flow](http://www.myexperiment.org/workflows/2649/download/hello_anyone_895936.t2flow)
from [myExperiment](http://www.myexperiment.org/workflows/2649)
(included in the `example/` folder of this source code).

To save a Taverna-PROV databundle containing the workflow run
provenance:

1.  Click **Results** perspective
2.  Run a workflow, or select an existing run on the left
3.  Click **Show workflow results** button (in case you are viewing intermediates)
4.  Click **Save all values**
5.  Ensure all ports are ticked
6.  Click **Save Provenance bundle**
7.  Browse and type the name of the bundle
8.  Click **OK**
9.  The provenance and results values is saved to the bundle with the
    extension `.bundle.zip`
10. Provide the data bundle to a service that can understand it, such
    as http://alpha2.myexperiment.org/ or http://sandbox.wf4ever-project.org/portal
11. You may also open the bundle in your operating system as it is 
    a ZIP file which contains workflow inputs, outputs, provenance and a
    copy of the workflow.



### Usage on command line

The Taverna documentation has general information about [how to use the
Taverna Command line
tool](http://dev.mygrid.org.uk/wiki/display/taverna/Command+Line+Tool).

The Taverna-PROV command line does not support all the output options of
the regular Taverna command line, output has to be saved using
`-outputdir`. The databundle will be saved in a filename which
corresponds to the output directory, but with the extension
`.bundle.zip`.

To enable PROV export for the workflow run, add the parameter
`-provenance`, which requires the parameter `-embedded` or
`-clientserver`. 

Note: `-embedded` allows only one concurrent execution at a time due to
database locking. To support multiple concurrent runs, start
`executeworkflow -startdb` separately to start the database server, and
use `executeworkflow -provenance -clientserver` for the workflow
executions.


Example:
```
stain@biggie-mint ~/src/taverna-prov/example $ executeworkflow -embedded -provbundle helloanyone.bundle.zip -inputvalue name fred helloanyone.t2flow 
Provenance bundle zip will be saved to: /home/stain/src/taverna-prov/example/helloanyone.bundle.zip

stain@biggie-mint ~/src/taverna-prov/example $ mkdir helloanyone.bundle ; cd helloanyone.bundle
stain@biggie-mint ~/src/taverna-prov/example/helloanyone.bundle $ unzip ../helloanyone.bundle.zip 
Archive:  ../helloanyone.bundle.zip
 extracting: mimetype                
   creating: inputs/
  inflating: inputs/name.txt         
   creating: outputs/
  inflating: outputs/greeting.txt    
   creating: intermediates/
   creating: intermediates/3a/
  inflating: intermediates/3a/3a82e39d-a537-40cf-91a0-2c89d4a2e62b.txt  
  inflating: workflowrun.prov.ttl    
  inflating: workflow.wfbundle       
   creating: .ro/
   creating: .ro/annotations/
  inflating: .ro/annotations/workflow.wfdesc.ttl  
  inflating: .ro/annotations/a2f03983-8836-4c36-bfb2-d713d9a1928f.ttl  
  inflating: .ro/manifest.json   
```


## Structure of exported provenance

The `.bundle.zip` file is a [RO bundle](https://w3id.org/bundle),
which species a structured ZIP file with a manifest
(`.ro/manifest.json`). You can explore the bundle by unzipping it or
browse it with a program like [7-Zip](http://7-zip.org/). 

This source includes an [example
bundle](example/helloanyone.bundle.zip) and [unzipped
bundle](example/helloanyone.bundle/) as a folder. This data bundle
has been saved after running
a simple [hello world workflow](example/helloanyone.t2flow).

The remaining text of this section describes the content of the RO
bundle, as if it was unpacked to a folder. Note that many programming
frameworks include support for working with ZIP files, and so complete
unpacking might not be necessary for your application. For Java, the
[Data bundle API](https://github.com/taverna/databundle) gives a
programmating way to inspect and generate data bundles.


### Inputs and outputs

The folders `inputs/` and `outputs/` contain files and folders
corresponding to the input and output values of the executed
workflow.  Ports with multiple values are stored as a folder with numbered
outputs, starting from `0`. Values representing errors have extension
`.err`, other values have an extension guessed by inspecting the value
structure, e.g. `.png`. External references have the extension `.url` -
these files can often be opened as "Internet shortcut" or similar,
depending on your operating system.

Example listing:

    c:\Users\stain\workspace\taverna-prov\example\helloanyone.bundle>ls
    inputs  intermediates  mimetype  outputs  workflow.wfbundle  workflowrun.prov.ttl

    c:\Users\stain\workspace\taverna-prov\example\helloanyone.bundle>ls outputs
    greeting.txt

    c:\Users\stain\workspace\taverna-prov\example\helloanyone.bundle>cat outputs/greeting.txt
    Hello, John Doe

### Workflow run provenance

The file `workflowrun.prov.ttl` contains the
[PROV-O](http://www.w3.org/TR/prov-o/) export of the workflow run
provenance (including nested workflows) in [RDF Turtle
format](http://www.w3.org/TR/turtle/). 

This log details every intermediate processor invocation in the workflow
execution, and relates them to inputs, outputs and intermediate values.

Example listing:

    c:\Users\stain\workspace\taverna-prov\example\helloanyone.bundle>cat workflowrun.prov.ttl | head -n 40 | tail -n 8

    <#taverna-prov-export>
            rdf:type                     prov:Activity ;
            prov:startedAtTime           "2013-11-22T14:01:02.436Z"^^xsd:dateTime ;
            prov:qualifiedCommunication  _:b1 ;
            prov:endedAtTime             "2013-11-22T14:01:03.223Z"^^xsd:dateTime ;
            rdfs:label                   "taverna-prov export of workflow run provenance"@en ;
            prov:wasInformedBy           <http://ns.taverna.org.uk/2011/run/385c794c-ba11-4007-a5b5-502ba8d14263/> ;

See the [provenance graph](example/helloanyone.bundle/workflowrun.prov.ttl) for a complete example. The provenance uses the vocabularies [PROV-O](http://www.w3.org/TR/prov-o/), [wfprov](https://w3id.org/ro#wfprov) and [tavernaprov](http://ns.taverna.org.uk/2012/tavernaprov/).

#### Intermediate values


Intermediate values are stored in the `intermediates/` folder and
referenced from `workflowrun.prov.ttl`

Intermediate value from the [example provenance](example/helloanyone.bundle/workflowrun.prov.ttl):

    <http://ns.taverna.org.uk/2011/data/385c794c-ba11-4007-a5b5-502ba8d14263/ref/d588f6ab-122e-4788-ab12-8b6b66a67354>
            tavernaprov:content          <intermediates/d5/d588f6ab-122e-4788-ab12-8b6b66a67354.txt> ;
            wfprov:describedByParameter  <http://ns.taverna.org.uk/2010/workflowBundle/01348671-5aaa-4cc2-84cc-477329b70b0d/workflow/Hello_Anyone/processor/Concatenate_two_strings/in/string1> ;
            wfprov:describedByParameter  <http://ns.taverna.org.uk/2010/workflowBundle/01348671-5aaa-4cc2-84cc-477329b70b0d/workflow/Hello_Anyone/processor/hello/out/value> ;
            wfprov:wasOutputFrom         <http://ns.taverna.org.uk/2011/run/385c794c-ba11-4007-a5b5-502ba8d14263/process/bbaedc02-896f-491e-88bc-8dd350fcc73b/> .

Here we see that the bundle file `intermediates/d5/d588f6ab-122e-4788-ab12-8b6b66a67354.txt` contains the output from the "hello" processor, which was also the input to the "Concatenate_two_strings" processor.  Details about processor, ports and parameters can be found in the [workflow definition](#workflow-definition).

Example listing:

    c:\Users\stain\workspace\taverna-prov\example\helloanyone.bundle>ls intermediates/d5
    d588f6ab-122e-4788-ab12-8b6b66a67354.txt
    
    c:\Users\stain\workspace\taverna-prov\example\helloanyone.bundle>cat intermediates/d5/d58*
    Hello,

Note that "small" textual values are also included as `cnt:chars` in the graph, while the referenced intermediate file within the workflow bundle is always present.

    <intermediates/d5/d588f6ab-122e-4788-ab12-8b6b66a67354.txt>
            rdf:type               cnt:ContentAsText ;
            cnt:characterEncoding  "UTF-8"^^xsd:string ;
            cnt:chars              "Hello, "^^xsd:string ;
            tavernaprov:byteCount  "7"^^xsd:long ;
            tavernaprov:sha512     "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"^^xsd:string ;
            tavernaprov:sha1       "f52ab57fa51dfa714505294444463ae5a009ae34"^^xsd:string ;
            rdf:type               tavernaprov:Content .
    

### Workflow definition

The file `workflow.wfbundle` is a copy of the executed workflow in 
[SCUFL2 workflow
bundle](http://dev.mygrid.org.uk/wiki/display/developer/Taverna+Workflow+Bundle)
format. This is the format which will be used by 
The file `workflow.wfbundle` contains the executed workflow in [Taverna
3](http://www.taverna.org.uk/developers/work-in-progress/taverna-3/). 

You can use the [SCUFL2
API](http://dev.mygrid.org.uk/wiki/display/developer/SCUFL2+API) to inspect the
workflow definition in detail, or unzip the workflow bundle to access the
complete workflow definition files as RDF/XML.

Usually it is sufficient to inspect the simpler file
`.ro/annotations/workflow.wfdesc.ttl`, which contains the abstract structure
(but not all the implementation details) of the executed
workflow, in [RDF Turtle](http://www.w3.org/TR/turtle/)
according to the [wfdesc ontology](https://w3id.org/ro/#wfdesc).

    c:\Users\stain\workspace\taverna-prov\example\helloanyone.bundle>cat .ro/annotations/workflow.wfdesc.ttl | head -n 20
    @base <http://ns.taverna.org.uk/2010/workflowBundle/01348671-5aaa-4cc2-84cc-477329b70b0d/workflow/Hello_Anyone/> .
    @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
    @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
    @prefix owl: <http://www.w3.org/2002/07/owl#> .
    @prefix prov: <http://www.w3.org/ns/prov#> .
    @prefix wfdesc: <http://purl.org/wf4ever/wfdesc#> .
    @prefix wf4ever: <http://purl.org/wf4ever/wf4ever#> .
    @prefix roterms: <http://purl.org/wf4ever/roterms#> .
    @prefix dc: <http://purl.org/dc/elements/1.1/> .
    @prefix dcterms: <http://purl.org/dc/terms/> .
    @prefix comp: <http://purl.org/DP/components#> .
    @prefix dep: <http://scape.keep.pt/vocab/dependencies#> .
    @prefix biocat: <http://biocatalogue.org/attribute/> .
    @prefix : <#> .

    <processor/Concatenate_two_strings/> a wfdesc:Process , wfdesc:Description , owl:Thing , wf4ever:BeanshellScript ;
            rdfs:label "Concatenate_two_strings" ;
            wfdesc:hasInput <processor/Concatenate_two_strings/in/string1> , <processor/Concatenate_two_strings/in/string2> ;
            wfdesc:hasOutput <processor/Concatenate_two_strings/out/output> ;
            wf4ever:script "output = string1 + string2;" .


## Querying provenance

Example [SPARQL query](http://www.w3.org/TR/sparql11-query/) from [test.sparql](example/test.sparql):

```sparql
PREFIX prov: <http://www.w3.org/ns/prov#> 
PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> 
PREFIX wfprov: <http://purl.org/wf4ever/wfprov#> 
PREFIX tavernaprov: <http://ns.taverna.org.uk/2012/tavernaprov/>
PREFIX cnt:  <http://www.w3.org/2011/content#> 
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> 
PREFIX wf4ever: <http://purl.org/wf4ever/wf4ever#> 


SELECT DISTINCT ?paramName ?name ?value ?ended ?script
WHERE {
    ?greeting tavernaprov:content <outputs/greeting.txt> .
    ?greeting prov:wasGeneratedBy ?concatenate .
    ?concatenate prov:endedAtTime ?ended ;
        wfprov:wasPartOfWorkflowRun ?run ;
	wfprov:describedByProcess ?plan .
    ?concatenate wfprov:usedInput ?input .
    ?input tavernaprov:content ?name .
    OPTIONAL { ?name cnt:chars ?value }  .
    OPTIONAL { ?plan wf4ever:script ?script } .
    ?input wfprov:describedByParameter ?param .
    ?plan wfdesc:hasInput ?param .
    OPTIONAL { ?param rdfs:label ?paramName } .  
}
```


This query will be starting with the data `?greeting` which content is
represented by the existing output file
[outputs/greeting.txt](example/helloanyone.bundle/outputs/greeting.txt), and
the remaining query tries to find which input or upstream values it has
effectively been derived from.

To do this, we find the `?concatenate` process run that generated
the greeting, and ask when it `?ended`. We also look up its `?plan`, 
which should match the [process
identifier](example/helloanyone.bundle/.ro/annotations/workflow.wfdesc.ttl#L16)
within the workflow definition.

We then look at the `?input`s used by the `?concatenate` process run (this
should give two matches as the "Concatenate string" processor takes two
arguments). We look up their `?content` (a file witin this bundle), in addition
to its textual `?value` (optionally, as this is only included in the graph for
small non-binary content).

Now we do a lookup of the
[`?script`](example/helloanyone.bundle/.ro/annotations/workflow.wfdesc.ttl#L20)
behind the defined `?plan` - this is optional because not all processors have
scripts (it might be a web service).
This information is extracted from the
[.ro/annotations/workflow.wfdesc.ttl](example/helloanyone.bundle/.ro/annotations/workflow.wfdesc.)
file which must also be parsed before querying.

Lastly we look up the pararameters which describes `?input`, filtered by
the ones bound to the processor `?plan` (thus avoiding the workflow inputs
"name"), and include their `?paramName` - each result should therefore show the 
input port name and value.


Query using [rdfproc](http://librdf.org/utils/rdfproc.html) (`apt-get install redland-utils`):

    stain@biggie-mint ~/src/taverna-prov/example/helloanyone.bundle $ rdfproc test parse workflowrun.prov.ttl turtle
    rdfproc: Parsing URI file:///home/stain/src/taverna-prov/example/helloanyone.bundle/workflowrun.prov.ttl with turtle parser

    stain@biggie-mint ~/src/taverna-prov/example/helloanyone.bundle $ rdfproc test parse .ro/annotations/workflow.wfdesc.ttl turtle
    rdfproc: Parsing URI file:///home/stain/src/taverna-prov/example/helloanyone.bundle/.ro/annotations/workflow.wfdesc.ttl with turtle parser
    
    stain@biggie-mint ~/src/taverna-prov/example/helloanyone.bundle $ rdfproc test2 query sparql - "$(cat ../test.sparql)"  
    rdfproc: Warning - URI file:///home/stain/src/taverna-prov/example/helloanyone.bundle/:1: Variable run was bound but is unused in the query
    rdfproc: Query returned bindings results:
    result: [, paramName="string2", name=<file:///home/stain/src/taverna-prov/example/helloanyone.bundle/inputs/name.txt>, value="fred"^^<http://www.w3.org/2001/XMLSchema#string>, ended="2014-05-28T11:49:43.711+01:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>, script="output = string1 + string2;"]
    result: [, paramName="string1", name=<file:///home/stain/src/taverna-prov/example/helloanyone.bundle/intermediates/3a/3a82e39d-a537-40cf-91a0-2c89d4a2e62b.txt>, value="Hello, "^^<http://www.w3.org/2001/XMLSchema#string>, ended="2014-05-28T11:49:43.711+01:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>, script="output = string1 + string2;"]
    rdfproc: Query returned 2 results
    

This shows that the `string2` input port was bound to value `"fred"` (content of `inputs/name.txt`) in the process run that generated `outputs/greeting.txt`. Next, `string` was bound to `"Hello, "` (content of `intermediates/3a/3a82..62b.txt`). The executed script was `output = string1 + string2`.



## Building
Note - you do not normally need to build from source code; unless you 
are modifying the source code, the "Taverna Prov" plugin included
in Taverna's default plugins is the preferred way of using Taverna-PROV.


You need:
* Java JDK 1.7 or newer (tested with OpenJDK 1.7.0\_03)
  * Note: Do not use the OpenJDK 1.6 (default in Ubuntu), as this is
   buggy with GUI and File operations, both used by the Taverna Workbench.
* Maven 2.2 or newer (Tested with [Maven 3.0.4](http://maven.apache.org/download.html))

To compile, run `mvn clean install`

On first run this will download various open-source third-party
libraries from Maven repositories, including required modules of Taverna 2.4. 
These JARs, and the compiled JARs from this source code, are
installed into `$HOME/.m2/repository/` [by
default](http://maven.apache.org/settings.html#Quick_Overview).
Depending on your network connection, this first run might take about 5
minutes to complete.

Example compilation:

    stain@vmint:~/src/taverna-prov$ mvn clean install
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
    [INFO] Building Taverna PROV 2.0.0-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
    (..)
    Downloading: http://www.mygrid.org.uk/maven/repository/net/sf/taverna/t2/taverna-workbench/workbench-dev/2.5.0/workbench-dev-2.5.0.pom
    Downloaded: http://www.mygrid.org.uk/maven/repository/net/sf/taverna/t2/taverna-workbench/workbench-dev/2.5.0/workbench-dev-2.5.0.pom (3 KB at 5.4 KB/sec)
    (..)
    [INFO] Installing /home/stain/src/taverna-prov/prov-taverna-cmdline/target/prov-taverna-cmdline-2.0.0-SNAPSHOT-tests.jar to /home/stain/.m2/repository/org/purl/wf4ever/provtaverna/prov-taverna-cmdline/2.0.0-SNAPSHOT/prov-taverna-cmdline-2.0.0-SNAPSHOT-tests.jar
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary:
    [INFO] 
    [INFO] Taverna PROV ...................................... SUCCESS [4.109s]
    [INFO] Taverna PROV W3 Provenance ontology Elmo bindings . SUCCESS [1.55.270s]
    [INFO] Taverna PROV export ............................... SUCCESS [45.873s]
    [INFO] Taverna PROV activity UI bindings ................. SUCCESS [1.56.572s]
    [INFO] Taverna PROV plugin ............................... SUCCESS [15.028s]
    [INFO] PROV Taverna command line ......................... SUCCESS [1:12.093s]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 5:54.000s
    [INFO] Finished at: Wed Jul 18 12:08:13 BST 2012
    [INFO] Final Memory: 72M/274M
    [INFO] ------------------------------------------------------------------------
    



Note that to work with Taverna 2's plugin system, the build is specific
for a particular Taverna version (but works for any edition). To build this
plugin for a different version of Taverna, modify the `<parent>` section of
`pom.xml` to match the Taverna release, e.g. `<version>2.4.0</version>`.

In order for Taverna to find your install of the plugin rather than
download from the official plugin site, you will need to manually edit
`plugins/plugins.xml` of the [Taverna home
directory](http://dev.mygrid.org.uk/wiki/display/tav250/Taverna+home+directory)
(run Taverna once to create the file) to include this section right
before the final `</plugins:plugins>`. The 

Note that you will get two copies of the "Save provenance" button, one for the
official version included from `workbench-plugin`, and one for the one you built.


```xml
     <plugin>
        <provider>org.purl.wf4ever</provider>
        <identifier>org.purl.wf4ever.provtaverna.prov-taverna-plugin</identifier>
        <version>2.0.0-SNAPSHOT</version>
        <name>Taverna PROV plugin</name>
        <description/>
        <enabled>true</enabled>
        <repositories>
            <repository>file:///home/johndoe/.m2/repository/</repository>
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
                <version>2.0.0-SNAPSHOT</version>
            </dependency>
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>4.8.2</version>
            </dependency>
        </profile>
        <compatibility>
            <application>
                <version>2.5.0</version>
            </application>
        </compatibility>
    </plugin>
```

1.  Modify the line:
    ```xml
        <repository>file:///home/johndoe/.m2/repository/</repository>
    ```

    to match your $HOME/.m2/repository. On Windows this path should look like:

    ```xml
        <repository>file:///C:/Users/johndoe/.m2/repository/</repository>    
    ```

2.  Take care that the `<version>` of the `<plugin>` and `prov-taverna-ui` matches
    the `<version>` specified in the `pom.xml` of this source code (typically `1.x-SNAPSHOT`).

3.  If you are building for a different Taverna, check that the `<application><version>`
    matches the Taverna version (compare with the existing plugins)

4.  Start Taverna Workbench.    

You only need to do this plugin installation once - if you later
recompile the source code without changing the version numbers, next
start of Taverna will use the newer JARs from `mvn clean install` as 
Taverna will prefer accessing `$HOME/.m2/repository`. You might however need to delete
the `repository/org/purl/wf4ever` folder of the  
[Taverna home directory](http://dev.mygrid.org.uk/wiki/display/tav250/Taverna+home+directory)
if Taverna has downloaded the SNAPSHOT versions from the myGrid repository
instead (for instance because you used the wrong `<repository>` path or
did not update `<version>`).

To make the plugin installation system-side (ie. multiple UNIX users or
to make a customized Taverna distribution), copy `plugins/`
and `repository` with overwrite onto the Taverna installation directory.
(See _Installation for Taverna Command Line_ above)


