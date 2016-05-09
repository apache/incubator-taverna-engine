Taverna Workflow system Execution Engine data reference management implementation.

Implements certain SPIs to provide a core functionality for the reference management framework. This includes ExternalReference implementations to reference data held in local files and URLs along with the appropriate translate / publish logic. Implementations in this package are tied only to the API, although certain metadata files (such as Hibernate mappings) are also included, where appropriate. (These will be silently ignored if using a backing store that is not Hibernate-based.)

This code was previously hosted at 
http://taverna.googlecode.com/svn/taverna/engine/net.sf.taverna.t2.core.reference-core-extensions - 
for historical references (e.g. for Taverna 2.x), see
[taverna-svn on GitHub](https://github.com/taverna/taverna-svn/tree/master/taverna/engine/net.sf.taverna.t2.core.reference-core-extensions)
and the `old/` git tags in this repository, e.g. 
[old/reference-core-extension-1.5](https://github.com/apache/incubator-taverna-engine/tree/old/reference-core-extensions-1.5)
