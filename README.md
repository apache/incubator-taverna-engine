<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->


## Taverna Project Retired

> tl;dr: The Taverna code base is **no longer maintained** 
> and is provided here for archival purposes.

From 2014 till 2020 this code base was maintained by the 
[Apache Incubator](https://incubator.apache.org/) project _Apache Taverna (incubating)_
(see [web archive](https://web.archive.org/web/20200312133332/https://taverna.incubator.apache.org/)
and [podling status](https://incubator.apache.org/projects/taverna.html)).

In 2020 the Taverna community 
[voted](https://lists.apache.org/thread.html/r559e0dd047103414fbf48a6ce1bac2e17e67504c546300f2751c067c%40%3Cdev.taverna.apache.org%3E)
to **retire** Taverna as a project and withdraw the code base from the Apache Software Foundation. 

This code base remains available under the Apache License 2.0 
(see _License_ below), but is now simply called 
_Taverna_ rather than ~~Apache Taverna (incubating)~~.

While the code base is no longer actively maintained, 
Pull Requests are welcome to the 
[GitHub organization taverna](http://github.com/taverna/), 
which may infrequently be considered by remaining 
volunteer caretakers.


### Previous releases

Releases 2015-2018 during incubation at Apache Software Foundation
are available from the ASF Download Archive <https://archive.apache.org/dist/incubator/taverna/>

Releases 2014 from the University of Manchester are on BitBucket <https://bitbucket.org/taverna/>

Releases 2009-2013 from myGrid are on LaunchPad <https://launchpad.net/taverna/>

Releases 2003-2009 are on SourceForge <https://sourceforge.net/projects/taverna/files/taverna/>

Binary JARs for Taverna are available from 
Maven Central <https://repo.maven.apache.org/maven2/org/apache/taverna/>
or the myGrid Maven repository <https://repository.mygrid.org.uk/>





# Taverna Engine

Workflow engine for
[Taverna](https://web.archive.org/web/*/https://taverna.incubator.apache.org/).

The engine executes a Taverna workflow, defined using
[Taverna Language](https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/language/).

Note that the engine does not include the
[activity implementations](https://github.com/apache/incubator-taverna-common-activities/)
that actually perform work (e.g. calling a REST service). To
use the engine, use the
[Taverna Command Line](https://github.com/apache/incubator-taverna-commandline/)
or [Taverna Server](https://github.com/apache/incubator-taverna-server/).

All Taverna Engine modules are also valid [OSGi](http://www.osgi.org/) bundles,
providing OSGi [Spring services](#spring-services).


## License

* (c) 2007-2014 University of Manchester
* (c) 2014-2018 Apache Software Foundation

This product includes software developed at The [Apache Software
Foundation](http://www.apache.org/).

Licensed under the
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), see the [LICENSE](LICENSE) file for details.

The [NOTICE](NOTICE) file contains any additional attributions as well as
details about embedded third-party libraries and source code.


# Contribute

Any contributions received are assumed to be covered by the [Apache License
2.0](https://www.apache.org/licenses/LICENSE-2.0).




## Prerequisites

* Java 1.8 or newer (tested with OpenJDK 1.8)
* [Apache Maven](https://maven.apache.org/download.html) 3.2.5 or newer (older
  versions probably also work)

This code relies on other
[Taverna modules](https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/code/),
which Maven shuold automatically
download from
[Apache's Maven repository](https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/maven/);
however you might want to compile these yourself in the below order:

* [taverna-language](https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/language/)
* [taverna-osgi](https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/osgi/)

Please see the `<properties>` of this [pom.xml](pom.xml) to find the
correct versions to build.

# Building

To build, use

    mvn clean install

This will build each module and run their tests.


## Building on Windows

If you are building on Windows, ensure you unpack this source code
to a folder with a [short path name](http://stackoverflow.com/questions/1880321/why-does-the-260-character-path-length-limit-exist-in-windows)
lenght, e.g. `C:\src` - as
Windows has a [limitation on the total path length](https://msdn.microsoft.com/en-us/library/aa365247%28VS.85%29.aspx#maxpath)
which might otherwise
prevent this code from building successfully.


## Skipping tests

To skip the tests (these can be time-consuming), use:

    mvn clean install -DskipTests


If you are modifying this source code independent of the
Taverna project, you may not want to run the
[Rat Maven plugin](https://creadur.apache.org/rat/apache-rat-plugin/)
that enforces Apache headers in every source file - to disable it, try:

    mvn clean install -Drat.skip=true

# Modules

The Taverna Engine modules are generally
split into `-api` and `-impl`. The `-api` modules contain
Java interfaces, abstract classes, and minimal dependencies while `-impl`
modules contain the corresponding implementation(s).

Thus, the [taverna-common-activities](https://github.com/apache/incubator-taverna-common-activities/)
should only need to depend on the `-api` modules, while the `-impl` are added by the
packaging of the
[taverna-commandline-product](https://github.com/apache/incubator-taverna-commandline/tree/master/taverna-commandline-product).

* [taverna-capability-api](taverna-capability-api/) Taverna Platform Capability API
* [taverna-capability-impl](taverna-capability-impl/) Taverna Platform Capability impl
* [taverna-credential-manager-api](taverna-credential-manager-api/) Taverna Credential Manager API
* [taverna-credential-manager-impl](taverna-credential-manager-impl/) Taverna Credential Manager impl
* [taverna-database-configuration-api](taverna-database-configuration-api/) Taverna Database Configuration API
* [taverna-database-configuration-impl](taverna-database-configuration-impl/) Taverna Database Configuration impl
* [taverna-execution-api](taverna-execution-api/) Taverna Platform Execution Service API
* [taverna-execution-impl](taverna-execution-impl/) Taverna Platform Execution Service impl
* [taverna-execution-local](taverna-execution-local/) Taverna Platform Local Execution Service
* [taverna-reference-api](taverna-reference-api/) Taverna Reference Manager API
* [taverna-reference-impl](taverna-reference-impl/) Taverna Reference Manager impl
* [taverna-reference-types](taverna-reference-types/) Taverna Engine Reference Types
* [taverna-report-api](taverna-report-api/) Taverna Platform Report Service
* [taverna-run-api](taverna-run-api/) Taverna Platform Run Service API
* [taverna-run-impl](taverna-run-impl/) Taverna Platform Run Service impl
* [taverna-services-api](taverna-services-api/) Taverna Services API
* [taverna-services-impl](taverna-services-impl/) Taverna Platform Services impl
* [taverna-workflowmodel-api](taverna-workflowmodel-api/) Taverna Workflow Model API
* [taverna-workflowmodel-impl](taverna-workflowmodel-impl/) Taverna Workflow Model impl
* [taverna-workflowmodel-extensions](taverna-workflowmodel-extensions/) Taverna Workflow Model Extension Points

These modules include utilities used by the above, or for test purposes:

* [taverna-observer](taverna-observer/) Taverna Observer pattern
* [taverna-activity-test-utils](taverna-activity-test-utils/) Taverna Activity test utils
* [taverna-reference-testhelpers](taverna-reference-testhelpers/) Taverna Reference Test Helpers

These modules include structural workflow activities:

* [taverna-dataflow-activity](taverna-dataflow-activity/) Taverna Dataflow Activity
* [taverna-stringconstant-activity](taverna-stringconstant-activity/) Taverna StringConstant Activity

_See the separate release of
[taverna-common-activities](taverna.incubator.apache.org/download/common-activities/)
for activities that invoke Beanshell, WSDL, REST and command line tool services._


These modules are experimental:

* [taverna-execution-remote](taverna-execution-remote/) Taverna Platform Remote Execution Service
* [taverna-execution-hadoop](taverna-execution-hadoop/) Taverna Hadoop Workflow Execution Service

These modules are not yet fully updated to Taverna 3 (contributions welcome!):

* [taverna-activity-archetype](taverna-activity-archetype/) Taverna Activity archetype
* [taverna-prov](taverna-prov/) Taverna PROV support ()
* [taverna-provenanceconnector](taverna-provenanceconnector/) Taverna Provenance Connector
* [taverna-prov-owl-bindings](taverna-prov-owl-bindings/) Taverna PROV OWL bindings


See the [taverna-engine JavaDoc](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/)
for more details.

## Which module does what?

This section shows how different Taverna Engine module are involved when running
a Taverna workflow, e.g. from the
[Taverna Command Line](https://github.com/apache/incubator-taverna-commandline/).

There are two layers of Taverna Engine:

* Taverna Platform provides an outer API for creating and managing workflow runs, potentially remotely.
* Taverna Core (aka `workflowmodel`, formerly `t2.core`) is the orchestrating part of the Taverna Engine. Its [Activity](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/workflowmodel/processor/activity/Activity.html) is implemented by multiple plugins depending on the activity type.

### Preparations

* Load a SCUFL2 [WorkflowBundle](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/container/WorkflowBundle.html)
 from a `.wfbundle` or `.t2flow` file
 using [WorkflowBundleIO](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/io/WorkflowBundleIO.html)
 from [Taverna Language](https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/language/).
 + (Optional) Choose a [Workflow](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/container/WorkflowBundle.html#getWorkflows--) to bind
 workflows (usually the
 [main workflow](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/container/WorkflowBundle.html#getMainWorkflow--)).
 + (Optional) Choose a [Profile](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/container/WorkflowBundle.html#getProfiles--) to bind
 workflow implementations. (There is normally only one profile.)
* Create or load a [Bundle](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/robundle/Bundle.html)
 with the expected [inputs](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/core/Workflow.html#getInputPorts--) of the
 [main workflow](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/container/WorkflowBundle.html#getMainWorkflow--) using
 [DataBundles](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/databundle/DataBundles.html)
from [Taverna Language](https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/language/).

### Running a workflow

* You must be running within OSGi to support multiple Taverna activities
 and their dependencies, e.g. by using the
 [OsgiLauncher](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-osgi/org/apache/taverna/osgilauncher/OsgiLauncher.html) from [Taverna OSGi](https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/osgi/).
 See the [TavernaStarter example](https://github.com/taverna-extras/taverna-starter/blob/master/src/main/java/org/apache/taverna/osgistarter/TavernaStarter.java)
 for details.
* (Optional) Populate service credentials (username/password)
 to be used by the workflow into the
 [CredentialManager](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/security/credentialmanager/CredentialManager.html) from
 [taverna-credential-manager-api](taverna-credential-manager-api/).
* Use the [RunService](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/run/api/RunService.html) from the [taverna-run-api](taverna-run-api) (discovered as a [Spring service](#spring-services)).
* Use [runService.getExecutionEnvironments()](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/run/api/RunService.html#getExecutionEnvironments-org.apache.taverna.scufl2.api.container.WorkflowBundle-) to find an executer for your workflow bundle or profile.
  + The RunService selects an appropriate
  [ExecutionService](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/execution/api/ExecutionService.html)
    service ([taverna-execution-api](taverna-execution-api)), checking the [Profile](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/container/WorkflowBundle.html#getProfiles--) against the installed [activity types](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/execution/api/ExecutionEnvironment.html#getActivityTypes--) from [common-activities](https://github.com/apache/incubator-taverna-common-activities/) and plugins; discovered by the  [taverna-capability-api](taverna-capability-api).
  + In the Taverna Command Line there is typically only one match, a  [LocalExecutionEnvironment](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/execution/impl/local/LocalExecutionEnvironment.html), representing the  [LocalExecutionService](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/execution/impl/local/LocalExecutionService.html) from [taverna-execution-local](taverna-execution-local).
  + The experimental module [taverna-execution-remote](taverna-execution-remote) aims to execute a workflow on a remote [Taverna Server](http://github.com/apache/incubator-taverna-server/) - e.g. for running workflows remotely from Taverna Workbench.
  + The prototype module [taverna-execution-hadoop](taverna-execution-hadoop) is an attempt to execute compatible Taverna workflows as an [Apache Hadoop](http://hadoop.apache.org/) map-reduce workflow. (This code needs to be updated for newer Hadoop releases.) For details, see the [SCAPE report on feasibility of parallelising preservation processes](http://scape-project.eu/wp-content/uploads/2013/04/SCAPE_D6.1_TUB_V1.2.pdf).
* [Create an execution job](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/execution/api/ExecutionService.html#createExecution-org.apache.taverna.platform.execution.api.ExecutionEnvironment-org.apache.taverna.scufl2.api.container.WorkflowBundle-org.apache.taverna.scufl2.api.core.Workflow-org.apache.taverna.scufl2.api.profiles.Profile-org.apache.taverna.robundle.Bundle-), and provide the `ExecutionEnvironment`, the `WorkflowBundle`, its `Workflow`, `Profile` and data `Bundle` with inputs. A workflow run id `String` is returned for subsequent calls.
  + Typically this creates a [LocalExecution](taverna-execution-local/src/main/java/org/apache/taverna/platform/execution/impl/local/LocalExecution.java#L105) instance ([taverna-execution-local](taverna-execution-local)), which converts the
   abstract SCUFL2 [Workflow](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/core/Workflow.html) to an executable
   [Dataflow](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/workflowmodel/Dataflow.html) ([taverna-workflowmodel-api](taverna-workflowmodel-api)) which has corresponding [Processor](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/workflowmodel/Processor.html)s and
   [Activity](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/workflowmodel/processor/activity/Activity.html) implementations from the installed plugins.
   + A [WorkflowInstanceFacade](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/facade/WorkflowInstanceFacade.html) is prepared - the engine's top-level representation of a workflow run.
   + A [LocalExecutionManager](https://github.com/apache/incubator-taverna-engine/blob/master/taverna-execution-local/src/main/java/org/apache/taverna/platform/execution/impl/local/LocalExecutionMonitor.java) is added as a bridge between the Taverna Core engine and the Platform.
* Start the run using [runService.start()](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/run/api/RunService.html#start-java.lang.String-).
  * Input values from the data [Bundle](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/robundle/Bundle.html) are
  [converted](https://github.com/apache/incubator-taverna-engine/blob/master/taverna-execution-local/src/main/java/org/apache/taverna/platform/execution/impl/local/T2ReferenceConverter.java#L34) to be registered with a
[ReferenceService](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/reference/ReferenceService.html) ([taverna-reference-api](taverna-reference-api)) - as Taverna workflows pass [T2Reference](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/reference/T2Reference.html)s.
  + Values are represented as either a [InlineStringReference](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/reference/impl/external/object/InlineStringReference.html) or [InlineByteArrayReference](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/reference/impl/external/object/InlineByteArrayReference.html), or could be external [HttpReference](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/reference/impl/external/http/HttpReference.html)s or [FileReference](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/reference/impl/external/file/FileReference.html)s ([taverna-reference-types](taverna-reference-types)). These types are recognized by the `Activity` implementations, allowing pass-by-reference
  where supported, with conversion to strings and bytes if needed.
  + Lists, values and references are stored  [in memory](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/reference/impl/InMemoryReferenceSetDao.html).
  + This was previously backed by [Hibernate](https://github.com/apache/incubator-taverna-engine/blob/old/core-pre-incubator-20141219/reference-impl/src/main/java/net/sf/taverna/t2/reference/impl/HibernateReferenceSetDao.java) stored in [Apache Derby](http://db.apache.org/derby/) - this code was removed due to license incompatibility.
  + TODO: Add a different [ReferenceSetDao](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/reference/ReferenceSetDao.html) implementation
    that is backed directly by the platform's [Bundle](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/robundle/Bundle.html)
    (effectively file-based).
* Monitor the [state of the run](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/run/api/RunService.html#getState-java.lang.String-) or
[get](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/run/api/RunService.html#getWorkflowReport-java.lang.String-) the [WorkflowReport](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/report/WorkflowReport.html) for detailed progress ([taverna-report-api](taverna-report-api)), such as
the [number of jobs completed](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/report/ProcessorReport.html#getJobsCompleted--) for a given [Processor](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/core/Processor.html).
* Once the workflow is [COMPLETED](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/report/State.html#COMPLETED)  (and even while it is [RUNNING](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/report/State.html#RUNNING)), you can
retrieve the  [bundle](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/run/api/RunService.html#getDataBundle-java.lang.String-) containing the output values and intermediate values.
  + To relate intermediate values, you will need to inspect either the
   [WorkflowReport](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/report/WorkflowReport.html),
  [ProcessorReport](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/report/ProcessorReport.html) or [ActivityReport](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/report/ActivityReport.html)s
  to [get the invocations](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/report/StatusReport.html#getInvocations--), which provide the [inputs](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/report/Invocation.html#getInputs--)
  and [outputs](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-engine/org/apache/taverna/platform/report/Invocation.html#getOutputs--) as
  [Path](http://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)s within the bundle.


## Spring services

The OSGi services should be
discoverable as [Spring](https://spring.io/) services,
e.g., by adding the following code to
your `META-INF/spring/update-context-osgi.xml`:

```xml

  <beans:beans xmlns="http://www.springframework.org/schema/osgi" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:beans="http://www.springframework.org/schema/beans"
    xsi:schemaLocation="
      http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
      http://www.springframework.org/schema/osgi http://www.springframework.org/schema/osgi/spring-osgi.xsd">

  <reference id="executionService" interface="org.apache.taverna.platform.execution.api.ExecutionService"/>
  <reference id="runService" interface="org.apache.taverna.platform.run.api.RunService"/>
  <reference id="credentialManager" interface="org.apache.taverna.security.credentialmanager.CredentialManager" />
  <reference id="databaseConfiguration" interface="org.apache.taverna.configuration.database.DatabaseConfiguration" />
  <reference id="databaseManager" interface="org.apache.taverna.configuration.database.DatabaseManager" />

  </beans:beans>
```

You might want to combine these with the
[Taverna OSGi services](https://github.com/apache/incubator-taverna-osgi/#spring-services).


# Export restrictions

This distribution includes cryptographic software.
The country in which you currently reside may have restrictions
on the import, possession, use, and/or re-export to another country,
of encryption software. BEFORE using any encryption software,
please check your country's laws, regulations and policies
concerning the import, possession, or use, and re-export of
encryption software, to see if this is permitted.
See <http://www.wassenaar.org/> for more information.

The U.S. Government Department of Commerce, Bureau of Industry and Security (BIS),
has classified this software as Export Commodity Control Number (ECCN) 5D002.C.1,
which includes information security software using or performing
cryptographic functions with asymmetric algorithms.
The form and manner of this Apache Software Foundation distribution makes
it eligible for export under the License Exception
ENC Technology Software Unrestricted (TSU) exception
(see the BIS Export Administration Regulations, Section 740.13)
for both object code and source code.

The following provides more details on the included cryptographic software:

* [taverna-credential-manager-impl](taverna-credential-manager-impl)
  manages an encrypted keystore for username/passwords and
  client/server SSL certificates. It
  is designed to be used with
  [Java Secure Socket Extension](https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html) (JSSE),
  [Java Cryptography Extension](http://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html) (JCE), and depends on the
  [BouncyCastle](https://www.bouncycastle.org/) bcprov encryption library.
  The [JCE Unlimited Strength Jurisdiction Policy](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
  may need to be installed separately to use keystore passwords with 7 or more characters.
* Taverna Engine depends on
  [Taverna Language](https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/language/),
  [Taverna OSGi](https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/osgi/) and
  [Apache Jena](http://jena.apache.org/), which depend on
  [Apache HttpComponents](https://hc.apache.org/) Client, which can
  initiate encrypted `https://` connections using
  [Java Secure Socket Extension](https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html)
  (JSSE).
* [taverna-database-configuration-impl](taverna-database-configuration-impl)  and
  [taverna-reference-impl](taverna-reference-impl) depend on [Apache Derby](http://db.apache.org/derby/),
  which uses the
  [Java Cryptography Extension](http://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html) (JCE)
  API.
