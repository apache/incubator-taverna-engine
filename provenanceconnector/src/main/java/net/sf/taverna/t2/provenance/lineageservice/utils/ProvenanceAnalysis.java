/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.provenance.lineageservice.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.provenance.lineageservice.AnnotationsLoader;
import net.sf.taverna.t2.provenance.lineageservice.LineageQueryResult;
import net.sf.taverna.t2.provenance.lineageservice.LineageQueryResultRecord;
import net.sf.taverna.t2.provenance.lineageservice.LineageSQLQuery;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceQuery;
import net.sf.taverna.t2.provenance.lineageservice.utils.Arc;
import net.sf.taverna.t2.provenance.lineageservice.utils.Var;
import net.sf.taverna.t2.provenance.lineageservice.utils.VarBinding;

import org.tupeloproject.kernel.Context;
import org.tupeloproject.kernel.OperatorException;
import org.tupeloproject.kernel.UnionContext;
import org.tupeloproject.kernel.impl.MemoryContext;
import org.tupeloproject.kernel.impl.ResourceContext;
import org.tupeloproject.provenance.ProvenanceAccount;
import org.tupeloproject.provenance.ProvenanceArtifact;
import org.tupeloproject.provenance.ProvenanceGeneratedArc;
import org.tupeloproject.provenance.ProvenanceProcess;
import org.tupeloproject.provenance.ProvenanceRole;
import org.tupeloproject.provenance.ProvenanceUsedArc;
import org.tupeloproject.provenance.impl.ProvenanceContextFacade;
import org.tupeloproject.rdf.Resource;
import org.tupeloproject.rdf.Triple;
import org.tupeloproject.rdf.xml.RdfXmlWriter;

/**
 * @author paolo<p/>
 * the main class for querying the lineage DB
 * assumes a provenance DB ready to be queried
 */
public class ProvenanceAnalysis {

	private static final String IP_ANNOTATION = "index-preserving";
	private static final String OUTPUT_CONTAINER_PROCESSOR = "_OUTPUT_";
	private static final String INPUT_CONTAINER_PROCESSOR = "_INPUT_";
	private static final String OPM_TAVERNA_NAMESPACE = "http://taverna.opm.org/";
	private static final String OPM_GRAPH_FILE = "src/test/resources/provenance-testing/OPM/OPMGraph.rdf";

	private ProvenanceQuery provenanceQuery = null;
	private AnnotationsLoader al = new AnnotationsLoader();

	// paths collected by lineageQuery and to be used by naive provenance query
	private Map<String, List<List<String>>> validPaths = new HashMap<String, List<List<String>>>();

	private List<String> currentPath;

	private Map<String,List<String>> annotations = null;  // user-made annotations to processors

	//
	// Tupelo for OPM -- 4/09
	// init the provenance graph
	//
	private Context context = new UnionContext();
	private ProvenanceContextFacade graph = null;
	private ProvenanceAccount account = null;

	public ProvenanceAnalysis() {
	}
	
	public ProvenanceAnalysis(ProvenanceQuery provenanceQuery) {
		this.provenanceQuery = provenanceQuery;
	}

/**
 * init Tupelo RDF provenance graph
 */
	public void initGraph() {
		MemoryContext mc = new MemoryContext();
		ResourceContext rc = new ResourceContext("http://example.org/data/","/provenanceExample/");
		context.addChild(mc);
		context.addChild(rc);

		graph = new ProvenanceContextFacade(mc);		

		// create new account to hold the causality graph
		// and give it a Resource name

		try {
			account = 
				graph.newAccount("OPM-"+
						getWFInstanceIDs().get(0), Resource.uriRef(OPM_TAVERNA_NAMESPACE+getWFInstanceIDs().get(0)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		graph.assertAccount(account);
	}


	public void setAnnotationFile(String annotationFile) {

		annotations = al.getAnnotations(annotationFile);

		if (annotations == null) {
			System.out.println("WARNING: no annotations have been loaded");
			return;
		}

		System.out.println("processor annotations for lineage refinement: ");
		for (Map.Entry<String,List<String>> entry:annotations.entrySet())  {

			System.out.println("annotations for proc "+entry.getKey());
			for (String ann: (List<String>) entry.getValue()) {
				System.out.println(ann);
			}
		}

	}


	public List<String> getWFInstanceIDs() throws SQLException { return getProvenanceQuery().getWFInstanceIDs(); }


	/**
	 * @param wfInstance lineage scope -- a specific instance
	 * @param pname for a specific processor [required]
	 * @param a specific (input or output) variable [optional]
	 * @param iteration and a specific iteration [optional]
	 * @return a lineage query ready to be executed
	 * @throws SQLException
	 */
	public LineageQueryResult fetchIntermediateResult(
			String wfInstance,
			String pname,
			String vname,
			String iteration) throws SQLException  {

		LineageSQLQuery lq = getProvenanceQuery().simpleLineageQuery(wfInstance, pname, vname, iteration);

		return getProvenanceQuery().runLineageQuery(lq);

	}



	public List<LineageQueryResult> computeLineage(
			String wfInstance,   // context
			String var,   // target var
			String proc,   // qualified with its processor name
			String path, 
			Set<String> selectedProcessors
	) throws SQLException  {

		List<LineageSQLQuery>  lqList =  searchDataflowGraph(wfInstance, var, proc, path, selectedProcessors);

		// execute queries in the LineageSQLQuery list
		// System.out.println("\n****************  executing lineage queries:  **************\n");
		return getProvenanceQuery().runLineageQueries(lqList);

	}

	/**
	 * compute lineage queries using path projections
	 * @param wfInstance the (single) instance defines the scope of a query<br/>
	 * added 2/9: collect a list of paths in the process to be used by the naive query. In practice
	 * we use this as the graph search phase that is needed by the naive query anyway
	 * @param var
	 * @param proc
	 * @param path  within var (can be empty but not null)
	 * @param selectedProcessors  only report lineage when you reach any of these processors
	 * @throws SQLException
	 */
	public List<LineageSQLQuery> searchDataflowGraph(
			String wfInstance,   // context
			String var,   // target var
			String proc,   // qualified with its processor name
			String path,  // can be empty but not null
			Set<String> selectedProcessors
	) throws SQLException  {

		List<LineageSQLQuery>  lqList =  new ArrayList<LineageSQLQuery>();

		// init paths accumulation
		//  associate an empty list of paths to each selected processor
		for (String sp:selectedProcessors) { validPaths.put(sp, new ArrayList<List<String>>()); }

		currentPath = new ArrayList<String>();

		// start with xfer or xform depending on whether initial var is output or input

		// get (var, proc) from Var  to see if it's input/output
		Map<String, String>  varQueryConstraints = new HashMap<String, String>();
		varQueryConstraints.put("W.instanceID", wfInstance);
		varQueryConstraints.put("V.pnameRef", proc);  
		varQueryConstraints.put("V.varName", var);  

		List<Var> vars = getProvenanceQuery().getVars(varQueryConstraints);

		if (vars.isEmpty())  {
			System.out.println("variable ("+var+","+proc+") not found, lineage query terminated");
			return null;
		}

		Var v = vars.get(0); 		// expect exactly one record
		// CHECK there can be multiple (pname, varname) pairs, i.e., in case of nested workflows
		// here we pick the first that turns up -- we would need to let users choose, or process all of them...

		// OPM: fetch value for this variable and assert it as an Artifact in the OPM graph

		Map<String, String> vbConstraints = new HashMap<String, String>();
		vbConstraints.put("VB.PNameRef", v.getPName());
		vbConstraints.put("VB.varNameRef", v.getVName());
		vbConstraints.put("VB.wfInstanceRef", wfInstance);
		if (path != null) { 
			vbConstraints.put("VB.iteration", "["+path+"]");
		}

		List<VarBinding> vbList = getProvenanceQuery().getVarBindings(vbConstraints); // DB


		// use only the first result (expect only one)
		// map the resulting varBinding to an Artifact
		VarBinding vb = vbList.get(0);

		Resource initialResource = Resource.uriRef(vb.getValue());
//		ProvenanceArtifact initialArtifact = graph.newArtifact(vb.getResolvedValue(), initialResource);
		ProvenanceArtifact initialArtifact = graph.newArtifact(vb.getValue(), initialResource);

		String URIFriendlyIterationVector = vb.getIteration().
		replace(',', '-').replace('[', ' ').replace(']', ' ').trim();

		String role;
		if (URIFriendlyIterationVector.length()>0) {
			role = vb.getPNameRef()+"/"+vb.getVarNameRef()+"?it="+URIFriendlyIterationVector;
		} else
			role = vb.getPNameRef()+"/"+vb.getVarNameRef();

		Resource initialRole = Resource.uriRef(OPM_TAVERNA_NAMESPACE+role);		
		ProvenanceRole initialArtifactRole = graph.newRole(role, initialRole);
		graph.assertArtifact(initialArtifact);

		if (v.isInput()) { // if vName is input, then do a xfer() step

			// rec. accumulates SQL queries into lqList
			xferStep(wfInstance, var, proc, path, selectedProcessors, lqList, initialArtifact, initialArtifactRole);

		} else { // start with xform

			// rec. accumulates SQL queries into lqList
			xformStep(wfInstance, v, proc, path, selectedProcessors, lqList, initialArtifact, initialArtifactRole);			
		}

		// print out OPM graph for diagnostics
		try {
			Set<Triple> allTriples = context.getTriples();

			RdfXmlWriter writer = new RdfXmlWriter();				
			writer.write(allTriples, new FileWriter(OPM_GRAPH_FILE));

			System.out.println("OPM graph written to "+OPM_GRAPH_FILE);

		} catch (OperatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lqList;

	}  // end searchDataflowGraph




	/**
	 * 
	 * @param wfInstance
	 * @param var
	 * @param proc
	 * @param selectedProcessors
	 * @param path
	 * @param lqList
	 * @param currentDerivedArtifactRole 
	 * @param initialArtifact 
	 * @throws SQLException 
	 */
	private void xformStep(String wfInstance, 
			Var outputVar, // we need the dnl from this output var
			String proc,
			String path,
			Set<String> selectedProcessors, 
			List<LineageSQLQuery> lqList, 
			ProvenanceArtifact currentDerivedArtifact, ProvenanceRole currentDerivedArtifactRole) throws SQLException {

		// retrieve input vars for current processor 
		Map<String, String>  varsQueryConstraints = new HashMap<String, String>();

		List<Var>  inputVars = null;

		// here we fetch the input vars for the current proc.
		// however, it may be the case that we are looking at a dataflow port (for the entire dataflow or
		// for a subdataflow) rather than a real processor. in this case 
		// we treat this as a 
		// special processor that does nothing -- so we "input var" in this case 
		// is a copy of the port, and we are ready to go for the next xfer step.
		// in this way we can seamlessly traverse the graph over intermediate I/O that are part 
		// of nested dataflows

		if (getProvenanceQuery().isDataflow(proc)) { // if we are looking at the output of an entire dataflow

			// force the "input vars" for this step to be the output var itself
			// this causes the following xfer step to trace back to the next processor _within_ proc 
			inputVars = new ArrayList<Var>();
			inputVars.add(outputVar);

		} else if (proc.equals(OUTPUT_CONTAINER_PROCESSOR)) {  // same action as prev case, but may change in the future

			inputVars = new ArrayList<Var>();
			inputVars.add(outputVar);

		} else {

			varsQueryConstraints.put("W.instanceID", wfInstance);
			varsQueryConstraints.put("pnameRef", proc);  
			varsQueryConstraints.put("inputOrOutput", "1");  

			inputVars = getProvenanceQuery().getVars(varsQueryConstraints);
		}

		///////////
		/// path projections
		///////////
		// maps each var to its projected path
		Map<Var,String> var2Path = new HashMap<Var,String>();
		Map<Var,Integer> var2delta = new HashMap<Var,Integer>();

		if (path == null) {  // nothing to split
			for (Var inputVar: inputVars)  var2Path.put(inputVar, null);
		} else {

			int minPathLength = 0;  // if input path is shorter than this we give up granularity altogether
			for (Var inputVar: inputVars) {
				int delta = inputVar.getActualNestingLevel() - inputVar.getTypeNestingLevel();
				var2delta.put(inputVar, new Integer(delta));
				minPathLength += delta;

//				System.out.println("xform() from ["+proc+"] upwards to ["+inputVar.getPName()+":"+inputVar.getVName()+"]");

			}

			String iterationVector[] = path.split(",");

			if (iterationVector.length < minPathLength) {  // no path is propagated
				for (Var inputVar: inputVars) {
					var2Path.put(inputVar, null);
				}
			} else { // compute projected paths

				String[] projectedPath; 

				int start = 0;
				for (Var inputVar: inputVars) {

					// 24/7/08 get DNL (declared nesting level) and ANL (actual nesting level) from VAR
					// TODO account for empty paths
					int projectedPathLength = var2delta.get(inputVar);  // this is delta			

					if (projectedPathLength > 0) {  // this var is involved in iteration

						projectedPath = new String[projectedPathLength];
						for (int i=0; i<projectedPathLength; i++) {					
							projectedPath[i] = iterationVector[start+i];
						}		
						start += projectedPathLength;

						StringBuffer iterationFragment = new StringBuffer();
						for (String s:projectedPath) { iterationFragment.append(s+","); }
						iterationFragment.deleteCharAt(iterationFragment.length()-1);

						var2Path.put(inputVar, iterationFragment.toString());
					} else {  // associate empty path to this var
						var2Path.put(inputVar, null);
					}
				}
			}
		}


		/////
		// accumulate this proc to current path 
		currentPath.add(proc);

		// if this is a selected processor, add a copy of the current path to the list of paths for the processor
		if (selectedProcessors.contains(proc)) { 
			List<List<String>> paths = validPaths.get(proc);

			// copy the path since the original will change
			// also remove spurious dataflow processors are this point
			List<String> pathCopy = new ArrayList<String>();
			for (String s:currentPath) {
				if (!getProvenanceQuery().isDataflow(s)) pathCopy.add(s);
			}			

			paths.add(pathCopy);			
		}


		////
		///////////
		/// generate SQL if necessary -- for all input vars, based on the current path
		/// the projected paths are required to determine the level in the collection at which 
		/// we look at the value assignment
		///////////

		Map<String, ProvenanceArtifact> var2Artifact = new HashMap<String, ProvenanceArtifact>();
		Map<String, ProvenanceRole> var2ArtifactRole = new HashMap<String, ProvenanceRole>();

		if (selectedProcessors.isEmpty() || selectedProcessors.contains(proc)) {

			LineageSQLQuery lq;

			// processor may have no inputs.
			// this is not a prob when doing path projections
			// but we still want to generate SQL if this is a selected proc
			// in this case we flag proc as outputs-only and generate SQL for the current output
			// using the current path, which becomes the element in collection		
			if (var2Path.isEmpty()) {

				lq = getProvenanceQuery().generateSQL(wfInstance, proc, path, false);  // false -> fetch output vars

			} else {
				// dnl of output var defines length of suffix to path that we are going to use for query
				// if var2Path is null this generates a trivial query for the current output var and current path CHECK
				lq = getProvenanceQuery().lineageQueryGen(wfInstance, proc, var2Path, outputVar, path);

				// if OPM is on, execute the query so we get the value we need for the Artifact node 
				LineageQueryResult inputs = getProvenanceQuery().runLineageQuery(lq);

				//
				// update OPM graph
				//

				// the value comes from the DB query
				for (LineageQueryResultRecord resultRecord: inputs.getRecords()) {

					String URIFriendlyIterationVector = resultRecord.getIteration().
					replace(',', '-').replace('[', ' ').replace(']', ' ').trim();
					
					boolean found = false;  // used to avoid duplicate process resources

					// assert proc as Process -- include iteration vector to separate different activations of the same process
					String processID;
					if (URIFriendlyIterationVector.length()>0) {
						processID = OPM_TAVERNA_NAMESPACE+proc+"?it="+URIFriendlyIterationVector;
					} else
						processID = OPM_TAVERNA_NAMESPACE+proc;
					
					Resource processResource = Resource.uriRef(processID);					
					ProvenanceProcess process = graph.newProcess(processID, processResource);
					graph.assertProcess(process);

					// add a triple to specify the iteration vector for this occurrence of Process, if it is available
					if (URIFriendlyIterationVector.length() > 0) {
//						Resource inputProcessSubject = ((RdfProvenanceProcess) process).getSubject();
						try {
							context.addTriple(processResource, Resource.uriRef(OPM_TAVERNA_NAMESPACE+"iteration"), resultRecord.getIteration());
						} catch (OperatorException e) {
							System.out.println("OPM iteration triple creation exception: "+e.getMessage());
						}
					}

					if (currentDerivedArtifact != null && currentDerivedArtifactRole != null) {
						// this process has generated the currentDerivedArtifact:

						// prevent duplicates -- add explicit option TODO
						Collection<ProvenanceGeneratedArc> generatedBy = graph.getGeneratedBy(currentDerivedArtifact);
						found = false;
						for (ProvenanceGeneratedArc arc:generatedBy) {						
							ProvenanceProcess pp = arc.getProcess();
							if (pp.getName().equals(process.getName())) { found = true; break; }						
						}

						//if (!found)
							graph.assertGeneratedBy(currentDerivedArtifact, process, currentDerivedArtifactRole, account);
					}		
					
					// map each input var in the resultRecord to an Artifact
					// create new Resource for the resultRecord
					//    use the value as URI for the Artifact, and resolvedValue as the actual value
					Resource inputResource = Resource.uriRef(resultRecord.getValue());

//					ProvenanceArtifact inputArtifact = graph.newArtifact(resultRecord.getResolvedValue(), inputResource);
					ProvenanceArtifact inputArtifact = graph.newArtifact(resultRecord.getValue(), inputResource);
					var2Artifact.put(resultRecord.getVname(), inputArtifact);

					String role;
					if (URIFriendlyIterationVector.length()>0) {
						role = resultRecord.getPname()+"/"+resultRecord.getVname()+"?it="+URIFriendlyIterationVector;
					} else
						role = resultRecord.getPname()+"/"+resultRecord.getVname();

					Resource inputRole = Resource.uriRef(OPM_TAVERNA_NAMESPACE+role);
					ProvenanceRole inputArtifactRole = graph.newRole(role, inputRole);
					var2ArtifactRole.put(resultRecord.getVname(), inputArtifactRole);

					graph.assertArtifact(inputArtifact);

					// prevent duplicates -- add explicit option TODO
					Collection<ProvenanceUsedArc> used = graph.getUsed(process);
					found = false;
					for (ProvenanceUsedArc arc:used) {						
						ProvenanceArtifact pa = arc.getArtifact();
						if (pa.getName().equals(inputArtifact.getName())) { found = true; break; }						
					}

//					if (!found) {
					// these Artifacts are used by proc:
					graph.assertUsed(process, inputArtifact, inputArtifactRole, account);
//					}

				}			

			}

			lqList.add(lq);

		}

		// recursion -- xfer path is next up
		for (Var inputVar: inputVars) {

			// fetch the Artifact corresponding to this input var
			ProvenanceArtifact currentInputArtifact = var2Artifact.get(inputVar.getVName());
			ProvenanceRole currentInputArtifactRole = var2ArtifactRole.get(inputVar.getVName());

			xferStep(wfInstance, inputVar.getVName(), inputVar.getPName(), var2Path.get(inputVar), selectedProcessors, lqList, 
					currentInputArtifact, currentInputArtifactRole);	
		}

		currentPath.remove(currentPath.size()-1);  // CHECK	

	}  // end xformStep



	private void xferStep(String wfInstanceID, 
			String var, 
			String proc,
			String path, 
			Set<String> selectedProcessors,
			List<LineageSQLQuery> lqList, ProvenanceArtifact currentOutputArtifact, ProvenanceRole currentOutputArtifactRole) throws SQLException {

		String sourceProcName = null;
		String sourceVarName  = null;

		// retrieve all Arcs ending with (var,proc) -- ideally there is exactly one
		// (because multiple incoming arcs are disallowed)
		Map<String, String>  arcsQueryConstraints = new HashMap<String, String>();

		arcsQueryConstraints.put("W.instanceID", wfInstanceID);
		arcsQueryConstraints.put("sinkVarNameRef", var);  
		arcsQueryConstraints.put("sinkPNameRef", proc);  

		List<Arc> arcs = getProvenanceQuery().getArcs(arcsQueryConstraints);

		if (arcs.size() == 0) {
//			System.out.println("no arcs going up from ["+proc+","+var+"] ... returning");
			return; // CHECK
		}

		Arc a = arcs.get(0); 

		// get source node
		sourceProcName = a.getSourcePnameRef();
		sourceVarName  = a.getSourceVarNameRef();

		//System.out.println("xfer() from ["+proc+","+var+"] to ["+sourceProcName+","+sourceVarName+"]");

		// CHECK transfer same path with only exception: when anl(sink) > anl(source)
		// in this case set path to null

		// retrieve full record for var:
		// retrieve input vars for current processor 
		Map<String, String>  varsQueryConstraints = new HashMap<String, String>();

		varsQueryConstraints.put("W.instanceID", wfInstanceID);
		varsQueryConstraints.put("pnameRef", sourceProcName);  
		varsQueryConstraints.put("varName", sourceVarName);  
		List<Var>  varList  = getProvenanceQuery().getVars(varsQueryConstraints);

		Var outputVar = varList.get(0);

		// recurse on xform
		xformStep(wfInstanceID, outputVar, sourceProcName, path, selectedProcessors, lqList, currentOutputArtifact, currentOutputArtifactRole);

	} // end xferStep2











	/**
	 * this class represents the annotation (single or sequence, to be determined) 
	 * that are produced upon visiting the graph structure and that drive the generation of 
	 * a pinpoint lineage query<br/>
	 * this is still a placeholder
	 */
	class LineageAnnotation {

		List<String> path = new ArrayList<String>();

		boolean isXform = true;

		String iteration = "";  // this is the iteration projected on a single variable. Used for propagation upwards default is no iteration --
		String iterationVector = ""; // iteration vector accounts for cross-products. Used to be matched exactly in queries. 
		int iic = 0;  // index in collection -- default is 0 
		int collectionNesting = 0;  // n indicates granularity is n levels from leaf. 
		// This quantifies loss of lineage precision when working with collections
		String collectionRef = null;
		String proc;
		String var;
		String varType = null;   // dtring, XML,... see Taverna type system

		int DNL = 0; // declared nesting level -- copied from VAR
		int ANL  = 0;  // actual nesting level -- copied from Var

		String wfInstance;  // TODO generalize to list / time interval?

		public String toString() {

			StringBuffer sb = new StringBuffer();

			if (isXform)  sb.append(" xform: ");
			else sb.append(" xfer: ");

			sb.append("<PROC/VAR/VARTYPE, IT, IIC, ITVECTOR, COLLNESTING> = "+
					proc + "/" + var + "/" + varType +
					"," + "["+iteration +"]"+
					","+ iic + 
					", ["+ iterationVector + "]"+
					","+ collectionNesting);

			return sb.toString();
		}


		public void addStep(String step) {
			path.add(step);
		}

		public void removeLastStep() {
			path.remove(path.size()-1);
		}


		/**
		 * @return the path
		 */
		public List<String> getPath() {
			return path;
		}


		/**
		 * @param path the path to set
		 */
		public void setPath(List<String> path) {
			this.path = path;
		}


		/**
		 * @return the iteration
		 */
		public String getIteration() {
			return iteration;
		}


		/**
		 * @param iteration the iteration to set
		 */
		public void setIteration(String iteration) {
			this.iteration = iteration;
		}


		/**
		 * @return the iic
		 */
		public int getIic() {
			return iic;
		}


		/**
		 * @param iic the iic to set
		 */
		public void setIic(int iic) {
			this.iic = iic;
		}


		/**
		 * @return the collectionRef
		 */
		public String getCollectionRef() {
			return collectionRef;
		}


		/**
		 * @param collectionRef the collectionRef to set
		 */
		public void setCollectionRef(String collectionRef) {
			this.collectionRef = collectionRef;
		}


		/**
		 * @return the proc
		 */
		public String getProc() {
			return proc;
		}


		/**
		 * @param proc the proc to set
		 */
		public void setProc(String proc) {
			this.proc = proc;
		}


		/**
		 * @return the var
		 */
		public String getVar() {
			return var;
		}


		/**
		 * @param var the var to set
		 */
		public void setVar(String var) {
			this.var = var;
		}


		/**
		 * @return the varType
		 */
		public String getVarType() {
			return varType;
		}


		/**
		 * @param varType the varType to set
		 */
		public void setVarType(String varType) {
			this.varType = varType;
		}


		/**
		 * @return the wfInstance
		 */
		public String getWfInstance() {
			return wfInstance;
		}


		/**
		 * @param wfInstance the wfInstance to set
		 */
		public void setWfInstance(String wfInstance) {
			this.wfInstance = wfInstance;
		}


		/**
		 * @return the isXform
		 */
		public boolean isXform() {
			return isXform;
		}


		/**
		 * @param isXform the isXform to set
		 */
		public void setXform(boolean isXform) {
			this.isXform = isXform;
		}



		/**
		 * @return the collectionNesting
		 */
		public int getCollectionNesting() {
			return collectionNesting;
		}


		/**
		 * @param collectionNesting the collectionNesting to set
		 */
		public void setCollectionNesting(int collectionNesting) {
			this.collectionNesting = collectionNesting;
		}


		/**
		 * @return the iterationVector
		 */
		public String getIterationVector() {
			return iterationVector;
		}


		/**
		 * @param iterationVector the iterationVector to set
		 */
		public void setIterationVector(String iterationVector) {
			this.iterationVector = iterationVector;
		}


		/**
		 * @return the dNL
		 */
		public int getDNL() {
			return DNL;
		}


		/**
		 * @param dnl the dNL to set
		 */
		public void setDNL(int dnl) {
			DNL = dnl;
		}


		/**
		 * @return the aNL
		 */
		public int getANL() {
			return ANL;
		}


		/**
		 * @param anl the aNL to set
		 */
		public void setANL(int anl) {
			ANL = anl;
		}
	}











	/**
	 * @return the validPaths
	 */
	public Map<String, List<List<String>>> getValidPaths() {
		return validPaths;
	}


	/**
	 * @param validPaths the validPaths to set
	 */
	public void setValidPaths(Map<String, List<List<String>>> validPaths) {
		this.validPaths = validPaths;
	}


	public void setProvenanceQuery(ProvenanceQuery provenanceQuery) {
		this.provenanceQuery = provenanceQuery;
	}


	public ProvenanceQuery getProvenanceQuery() {
		return provenanceQuery;
	}


}
