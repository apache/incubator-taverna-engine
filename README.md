Taverna PROV support
====================

This is a plugin for the [Taverna](http://www.taverna.org.uk/) Workbench
and Taverna Command Line which allows export of the provenance of a
workflow run according to the proposed [PROV-O
standard](http://www.w3.org/TR/prov-o/).



Source code and license
-----------------------
This plugin is distributed under the [GNU Lesser General Public License
2.1](http://www.gnu.org/licenses/lgpl-2.1.html) (LGPL). The source code
for this plugin is available at https://github.com/wf4ever/taverna-prov


Installation for Taverna workbench
----------------------------------
Installation of an released version of this plugin does not require
compilation, but uses Taverna's plugin mechanism.

You need:
* [Taverna Workbench 2.4.0](http://www.taverna.org.uk/download/workbench/2-4/)
* [Patch update for Taverna 2.4.1](http://markmail.org/thread/rd2zpaq27eiqiean)
* Internet connectivity

To install:

1.  Start Taverna workbench

2.  In the menu, click **Advanced** -> **Updates and plugins**

3.  If Taverna does not say "An update is available" , click **Find
    updates**

4.  To install the required 2.4.1 patches, click **Update** for each of:
    * _External tool service_
    * _Service catalogue_
    * _Services_
    * _Workbench_

    Note: Click **OK** in the warning message about restart; you do not need
    to restart Taverna for each of these as we'll do that in the end

5.  Click **Find new plugins**, then **Add update site** and fill in:
    * Site name: Taverna PROV
    * Site URL: `http://wf4ever.github.com/taverna-prov/`

6.  Click **OK**

7.  Under _Taverna PROV_, tick to select _Taverna PROV plugin 1.x_

8.  Click **Install**

9.  Click **Close**

10. Exit and Restart Taverna workbench


Usage in workbench
------------------
In order for PROV export to be possible from the Taverna Workbench,
click **Preferences** from the **File** or **Taverna** menu, then go to the 
[Preferences for Data and Provenance](http://dev.mygrid.org.uk/wiki/display24/taverna/Data+and+provenance+preferences).
Ensure that **Provenance capture** is enabled. (This is the default in 2.4).

If you would like Taverna to keep the provenance data between runs
of the workbench (in order to export at a later time), then you need to
also untick to *disable* **In memory storage**; note however that this may slow
down executions of some workflows.

Open and run a workflow to export provenance for. As a quick example,
try the very simple
[helloanyone.t2flow](http://www.myexperiment.org/workflows/2649/download/hello_anyone_895936.t2flow)
from [myExperiment](http://www.myexperiment.org/workflows/2649)
(included in the `example/` folder of this source code).

Exporting PROV from Taverna is done similar to the [export of OPM and
Janus provenance](http://dev.mygrid.org.uk/wiki/display/taverna24/Provenance+export+to+OPM+and+Janus).

The difference is that you need to click **Save provenance (PROV)**, and
in the file dialogue give the name of what will become
a _folder_, rather than a single file.

In short:

1.  Click **Results** perspective
2.  Select a run on the left
3.  Click **Show workflow results** button (in case you are viewing intermediates)
4.  Click **Save all values**
5.  Ensure all ports are ticked
6.  Click **Save Provenance (PROV)**
7.  Browse and type the name of a folder for the results (which will be created)
8.  Click **OK**
9.  The provenance and results values should have been saved to the
    requested folder.


Installation for Taverna Command line tool
------------------------------------------
Installation for the [Taverna command line tool](http://www.taverna.org.uk/download/command-line-tool/2-4/)
is slightly manual as it has no GUI for installing plugins.

These instructions assumes a Linux environment, but the plugin should
work also on Windows or OS X. Note that the [Taverna home
directory](http://dev.mygrid.org.uk/wiki/display/taverna/Taverna+home+directory)
has a different locations on those operating systems, replace
`$HOME/.taverna-cmd-2.4.0` for the equivalent path. (Note that the
command line tool has a separate home directory from the workbench)


1.  To install, extract the **separate download** [Taverna command line
    tool 2.4](http://www.taverna.org.uk/download/command-line-tool/2-4/)
    to a folder of your choice. 
    
2.  (Optionally, Linux): Make execution shortcut in $HOME/bin

    ```
    stain@ralph-ubuntu:~/software/taverna-commandline-2.4.0$ chmod 755 executeworkflow.sh 

    stain@ralph-ubuntu:~/software/taverna-commandline-2.4.0$ mkdir ~/bin 
    mkdir: cannot create directory `/home/stain/bin': File exists

    stain@ralph-ubuntu:~/software/taverna-commandline-2.4.0$ cd ~/bin

    stain@ralph-ubuntu:~/bin$ ln -s ~/software/taverna-commandline-2.4.0/executeworkflow.sh executeworkflow

    stain@ralph-ubuntu:~/bin$ . ~/.profile

    stain@ralph-ubuntu:~/bin$ type executeworkflow
    executeworkflow is hashed (/home/stain/bin/executeworkflow)
    ```

3.  Edit the file `plugins/plugin.xml` of the unpacked installation, and
    add this section right before `</plugins:plugin>` at the end of the
    file:

    ```xml
    <plugin>
        <provider>org.purl.wf4ever</provider>
        <identifier>org.purl.wf4ever.provtaverna.prov-taverna-plugin</identifier>
        <version>1.5</version>
        <name>Taverna PROV plugin</name>
        <description/>
        <enabled>true</enabled>
        <repositories>
            <repository>http://wf4ever.github.com/taverna-prov/</repository>
            <repository>http://repo.aduna-software.org/maven2/releases/</repository>
            <repository>http://uk.maven.org/maven2/</repository>
        </repositories>
        <profile>
            <dependency>
                <groupId>org.purl.wf4ever.provtaverna</groupId>
                <artifactId>prov-taverna-cmdline</artifactId>
                <version>1.5</version>
            </dependency>
        </profile>
        <compatibility>
            <application>
                <version>2.4.0</version>
            </application>
        </compatibility>
    </plugin>
    ```

    You should replace `<version>1.5</version>` with whatever is the
    latest version [listed on the taverna-prov plugin site](http://wf4ever.github.com/taverna-prov/pluginlist.xml).

4.  Start the command line tool without parameters to force downloading
    of plugins (this might take a few minutes the first time):

    ```
    stain@ahtissuntu:~/software/taverna-commandline-2.4.0$ sh executeworkflow.sh 
    usage: executeworkflow [options] [workflow]
     -clientserver                           Connect as a client to a derby
                                             server instance.
     -cmdir <directory path>                 Absolute path to a directory
    (..)
    ```

    Note that Taverna will copy the content of this file to
    `$HOME/.taverna-cmd-2.4.0/plugins/plugins.xml` only on first start, so
    if you do further edits, you will need to delete the file in
    `$HOME/.taverna-cmd-2.4.0/plugins/`.

5.  If you get an error such as:

    ```
    WARN  2012-07-18 16:44:36,323 (net.sf.taverna.raven.repository.impl.LocalRepository:85) - Could not find artifact org.purl.wf4ever.provtaverna:prov-taverna-cmdline:1.5
    net.sf.taverna.raven.repository.ArtifactNotFoundException: Could not find artifact org.purl.wf4ever.provtaverna:prov-taverna-cmdline:1.5: Can't find artifact for: org.purl.wf4ever.provtaverna:prov-taverna-cmdline:1.5
        at net.sf.taverna.raven.repository.impl.LocalRepository.fetch(LocalRepository.java:820)
    ```

    then check if you need to [configure Java for using proxies](http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies.html) by modifying the `java` command line in `executeworkflow.sh` or `executeworkflow.bat`.
  
6.  Modify the script `executeworkflow.sh` so that the line with `raven.launcher.app.main` says:
    ```
    -Draven.launcher.app.main=org.purl.wf4ever.provtaverna.cmdline.ProvCommandLineLauncher \
    ```
    or for `executeworkflow.bat`:
    ```
    set ARGS=%ARGS% -Draven.launcher.app.main=org.purl.wf4ever.provtaverna.cmdline.ProvCommandLineLauncher
    
    ```

7.  Execute without parameters to ensure the modified launcher is
    working:

    ```
    stain@ahtissuntu:~/software/taverna-commandline-2.4.0$ sh executeworkflow.sh 
    usage: executeworkflow [options] [workflow]
     -clientserver                           Connect as a client to a derby
                                             server instance.
     -cmdir <directory path>                 Absolute path to a directory
    (..)
    ```

8.  If you get this error:
    ```
    Could not find class: org.purl.wf4ever.provtaverna.cmdline.ProvCommandLineLauncher
    java.lang.ClassNotFoundException: Could not find org.purl.wf4ever.provtaverna.cmdline.ProvCommandLineLauncher
    ```
    Then check that org.purl.wf4ever is listed in
    `$HOME/.taverna-cmd-2.4.0/plugins/plugins.xml` - if not, then delete
    that file so that it is restored from the installation directory.

8.  (Optional) Copy the downloaded repository content to the
    installation folder:
    
    ```
    stain@ralph-ubuntu:~/software/taverna-commandline-2.4.0$ cp -r ~/.taverna-cmd-2.4.0/repository .
    
    ```

    This is useful if several users will run the same Taverna
    installation (such as in a Taverna Server installation), or if you
    are going to repackage the installation, as those users would not
    need to wait for the initial download of the plugin libraries.


Usage on command line
---------------------
The Taverna documentation has general information about [how to use the
Taverna Command line
tool](http://dev.mygrid.org.uk/wiki/display/taverna/Command+Line+Tool).

The Taverna-PROV command line does not support all the output options of
the regular Taverna command line, output has to be saved using
`-outputdir`, as the PROV export refers to files in the output
directory.

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
stain@ralph-ubuntu:~/src/taverna-prov/example$ executeworkflow -embedded \
  -provenance -outputdir hello -inputvalue name fred helloanyone.t2flow 

Outputs will be saved to the directory: /home/stain/src/taverna-prov/example/hello

stain@ralph-ubuntu:~/src/taverna-prov/example$ ls hello
greeting  workflowrun.prov.ttl

```


Structure of exported provenance
--------------------------------

The folder selected will contain each of the selected input and output
ports. Ports with multiple values are stored as a folder with numbered
outputs, starting from `0`. Values representing errors have extension `.err`.

In you will find the file `workflowrun.prov.ttl` which contains the PROV-O export of the
workflow run (including nested workflows) in [RDF Turtle format](http://www.w3.org/TR/turtle/).

Example listing:

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

```sparql
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
```

Note: Future versions of the PROV-O export will use custom subproperties of
`prov:alternateOf` to proper distinguish values at workflow level,
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



TODO
----
* Include workflow definition

See also [outstanding issues](https://jira.man.poznan.pl/jira/browse/WFE-513).


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

On first run this will download various open-source third-party
libraries from Maven repositories, including required modules of Taverna 2.4. 
These JARs, and the compiled JARs from this source code, are
installed into `$HOME/.m2/repository/` [by
default](http://maven.apache.org/settings.html#Quick_Overview).
Depending on your network connection, this first run might take about 5
minutes to complete.

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
    [INFO] Building Taverna PROV 1.6-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
    (..)
    Downloading: http://www.mygrid.org.uk/maven/repository/net/sf/taverna/t2/taverna-workbench/workbench-dev/2.4.0/workbench-dev-2.4.0.pom
    Downloaded: http://www.mygrid.org.uk/maven/repository/net/sf/taverna/t2/taverna-workbench/workbench-dev/2.4.0/workbench-dev-2.4.0.pom (3 KB at 5.4 KB/sec)
    (..)
    [INFO] Installing /home/stain/src/taverna-prov/prov-taverna-cmdline/target/prov-taverna-cmdline-1.6-SNAPSHOT-tests.jar to /home/stain/.m2/repository/org/purl/wf4ever/provtaverna/prov-taverna-cmdline/1.6-SNAPSHOT/prov-taverna-cmdline-1.6-SNAPSHOT-tests.jar
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
    

Note that to work with Taverna's plugin system, the build is specific
for a particular Taverna version. To build this plugin for a different
version of Taverna, modify the `<properties>` section of `pom.xml` to
match the [Maven module
versions](http://dev.mygrid.org.uk/wiki/display/developer/Maven+module+version+numbers)
for the specific Taverna release.

In order for Taverna to find your install of the plugin rather than
download from the official plugin site, you will need to manually edit
`plugins/plugins.xml` of the [Taverna home
directory](http://dev.mygrid.org.uk/wiki/display/taverna24/Taverna+home+directory)
(run Taverna once to create the file) to include this section right
before the final `</plugins:plugins>`.

NOTE: If you have already installed the official plugin, first *Remove*
it from within Taverna. To check, look for `org.purl.wf4ever.provtaverna`
in `plugins/plugins.xml`.


```xml
     <plugin>
        <provider>org.purl.wf4ever</provider>
        <identifier>org.purl.wf4ever.provtaverna.prov-taverna-plugin</identifier>
        <version>1.6-SNAPSHOT</version>
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
                <version>1.6-SNAPSHOT</version>
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
the `repository/org/purl/wf4ever` folder of the  [Taverna home directory](http://dev.mygrid.org.uk/wiki/display/taverna24/Taverna+home+directory) if 
Taverna has downloaded the SNAPSHOT versions from the myGrid repository
instead (for instance because you used the wrong `<repository>` path or
did not update `<version>`).

To make the plugin installation system-side (ie. multiple UNIX users or
to make a customized Taverna distribution), copy `plugins/`
and `repository` with overwrite onto the Taverna installation directory.
(See _Installation for Taverna Command Line_ above)


