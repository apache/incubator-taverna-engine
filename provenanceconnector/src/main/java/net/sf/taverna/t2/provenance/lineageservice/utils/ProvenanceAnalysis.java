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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.provenance.lineageservice.AnnotationsLoader;
import net.sf.taverna.t2.provenance.lineageservice.LineageQueryResult;
import net.sf.taverna.t2.provenance.lineageservice.LineageQueryResultRecord;
import net.sf.taverna.t2.provenance.lineageservice.LineageSQLQuery;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceQuery;

import org.apache.log4j.Logger;
import org.tupeloproject.kernel.Context;
import org.tupeloproject.provenance.ProvenanceAccount;
import org.tupeloproject.provenance.ProvenanceArtifact;
import org.tupeloproject.provenance.ProvenanceRole;
import org.tupeloproject.provenance.impl.ProvenanceContextFacade;

/**
 * @author paolo<p/>
 * the main class for querying the lineage DB
 * assumes a provenance DB ready to be queried
 */
public class ProvenanceAnalysis {
	
	private static Logger logger = Logger.getLogger(ProvenanceAnalysis.class);

	private static final String IP_ANNOTATION = "index-preserving";
	private static final String OUTPUT_CONTAINER_PROCESSOR = "_OUTPUT_";
	private static final String INPUT_CONTAINER_PROCESSOR = "_INPUT_";
	private static final Object ALL_PATHS_KEYWORD = "ALL";

	private ProvenanceQuery pq = null;
	private AnnotationsLoader al = new AnnotationsLoader();  // singleton

	// paths collected by lineageQuery and to be used by naive provenance query
	private Map<String, List<List<String>>> validPaths = new HashMap<String, List<List<String>>>();

	private List<String> currentPath;
	private Map<String,List<String>> annotations = null;  // user-made annotations to processors

	// Tupelo for OPM -- 4/09
//	private Context context = null;
//	private ProvenanceAccount account = null;

	private boolean ready = false; // set to true as soon as init succeeds. this means pa is ready to answer queries

	private boolean returnOutputs = false; // by default only return input bindings

	private OPMManager aOPMManager = new OPMManager();

	public ProvenanceAnalysis() { ; }

	public ProvenanceAnalysis(ProvenanceQuery pq) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		this.pq = pq;

		setReady(tryInit());
	}


	private boolean tryInit() throws SQLException {

		if (getWFInstanceIDs() != null && getWFInstanceIDs().size()>0) {
			initGraph();
			return true;
		} else 
			return false;		
	}

	/**
	 * Call to create the opm graph and annotation loader. 
	 * this may fail due to queries being issued before DB is populated, minimally with wfInstanceID 
	 */
	public void initGraph() {

		// OPM management

		try {
			aOPMManager.createAccount(getWFInstanceIDs().get(0));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}


	public void setAnnotationFile(String annotationFile) {

		annotations = al.getAnnotations(annotationFile);

		if (annotations == null) {
			logger.warn("no annotations have been loaded");
			return;
		}

		logger.info("processor annotations for lineage refinement: ");
		for (Map.Entry<String,List<String>> entry:annotations.entrySet())  {

			logger.info("annotations for proc "+entry.getKey());
			for (String ann: (List<String>) entry.getValue()) {
				logger.info(ann);
			}
		}

	}


	public List<String> getWFInstanceIDs() throws SQLException { return getPq().getWFInstanceIDs(); }

	/**
	 * @param wfInstance lineage scope -- a specific instance
	 * @param pname for a specific processor [required]
	 * @param a specific (input or output) variable [optional]
	 * @param iteration and a specific iteration [optional]
	 * @return a lineage query ready to be executed, or null if we cannot return an answer because we are not ready
	 * (for instance the DB is not yet populated) 
	 * @throws SQLException
	 */
	public LineageQueryResult fetchIntermediateResult(
			String wfInstance,
			String pname,
			String vname,
			String iteration) throws SQLException  {

		if (!isReady()) {
			setReady(tryInit());
			if (!isReady())  return null;
		}

		LineageSQLQuery lq = getPq().simpleLineageQuery(wfInstance, pname, vname, iteration);

		return getPq().runLineageQuery(lq);
	}


	/**
	 * facade for computeLineage: if path == ALL then it retrieves all VBs for (proc,var) ignoring path
	 * (i.e., all values within the collection bound to var) and invokes computeLineageSingleBinding() on each path</br>
	 * if path is specified, however, this just passes the request to computeLineageSingleBinding. in this case the result map 
	 * only contains one entry
	 * @param wfInstance
	 * @param var
	 * @param proc
	 * @param path
	 * @param selectedProcessors
	 * @return a map <pre>{ path -> List<LineageQueryResult> }</pre>, one entry for each path
	 * @throws SQLException
	 */
	public Map<String, List<LineageQueryResult>> computeLineage (
			String wfInstance,   // context
			String var,   // target var
			String proc,   // qualified with its processor name
			String path,   // possibly empty when no collections or no granular lineage required
			Set<String> selectedProcessors
	) throws SQLException  {

		if (!isReady()) {
			setReady(tryInit());
			if (!isReady())  return null;
		}

		// are we returning all outputs in addition to the inputs?
		System.out.println("return outputs: "+isReturnOutputs());

		Map<String, List<LineageQueryResult>> results = new HashMap<String, List<LineageQueryResult>>();

		// run a query for each variable in the entire workflow graph
		if (path.equals(ALL_PATHS_KEYWORD)) {

			Map<String, String> vbConstraints = new HashMap<String, String>();
			vbConstraints.put("VB.PNameRef", proc);
			vbConstraints.put("VB.varNameRef", var);
			vbConstraints.put("VB.wfInstanceRef", wfInstance);

			List<VarBinding> vbList = getPq().getVarBindings(vbConstraints); // DB

			for (VarBinding vb:vbList) {

				// path is of the form [x,y..]  we need it as x,y... 
				path = vb.getIteration().substring(1, vb.getIteration().length()-1);

				List<LineageQueryResult> result = computeLineageSingleBinding(
						wfInstance, var, proc, path, selectedProcessors);
				results.put(vb.getIteration(), result);
			}
		}  else {
			results.put(path, computeLineageSingleBinding(
					wfInstance, var, proc, path, selectedProcessors));
		}
		return results;		
	}


	/**
	 * main lineage query method. queries the provenance DB 
	 * with a single originating proc/var/path and a set of selected Processors
	 * @param wfInstance
	 * @param var
	 * @param proc
	 * @param path
	 * @param selectedProcessors
	 * @return a list of bindings. each binding involves an input var for one of the selectedProcessors. Note 
	 * each var can contribute multiple bindings, i.e., when all elements in a collection bound to the var are retrieved.
	 * Note also that bindings for input vars are returned as well, when the query is configured with returnOutputs = true
	 * {@link ProvenanceAnalysis#isReturnOutputs() }
	 * @throws SQLException
	 */
	public List<LineageQueryResult> computeLineageSingleBinding(
			String wfInstance,   // context
			String var,   // target var
			String proc,   // qualified with its processor name
			String path,   // possibly empty when no collections or no granular lineage required
			Set<String> selectedProcessors
	) throws SQLException  {

//		Map<String, LineageSQLQuery>  varName2lqList =  new HashMap<String, LineageSQLQuery>();

//		System.out.println("timing starts...");
		long start = System.currentTimeMillis();

		List<LineageSQLQuery>  lqList =  searchDataflowGraph(wfInstance, var, proc, path, selectedProcessors);
		long stop = System.currentTimeMillis();

		long gst = stop-start;

		// execute queries in the LineageSQLQuery list
		// System.out.println("\n****************  executing lineage queries:  **************\n");
		start = System.currentTimeMillis();

		List<LineageQueryResult> results =  getPq().runLineageQueries(lqList);
		stop = System.currentTimeMillis();

		long qrt = stop-start;
//		System.out.println("search time: "+gst+"ms\nlineage query response time: "+qrt+" ms");
//		System.out.println("total exec time "+(gst+qrt)+"ms");

		return results;
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

		// init paths accumulation. here "path" is a path in the graph, not within a collection!
		//  associate an empty list of paths to each selected processor
		for (String sp:selectedProcessors) { validPaths.put(sp, new ArrayList<List<String>>()); }

		currentPath = new ArrayList<String>();

		// start with xfer or xform depending on whether initial var is output or input

		// get (var, proc) from Var  to see if it's input/output
		Map<String, String>  varQueryConstraints = new HashMap<String, String>();
		varQueryConstraints.put("W.instanceID", wfInstance);
		varQueryConstraints.put("V.pnameRef", proc);  
		varQueryConstraints.put("V.varName", var);  

		List<Var> vars = getPq().getVars(varQueryConstraints);

		if (vars.isEmpty())  {
			logger.info("variable ("+var+","+proc+") not found, lineage query terminated");
			return null;
		}

		Var v = vars.get(0); 		// expect exactly one record
		// CHECK there can be multiple (pname, varname) pairs, i.e., in case of nested workflows
		// here we pick the first that turns up -- we would need to let users choose, or process all of them...

//		begin OPM fumbling

		// fetch value for this variable and assert it as an Artifact in the OPM graph
		Map<String, String> vbConstraints = new HashMap<String, String>();
		vbConstraints.put("VB.PNameRef", v.getPName());
		vbConstraints.put("VB.varNameRef", v.getVName());
		vbConstraints.put("VB.wfInstanceRef", wfInstance);
		if (path != null) { 

			// account for x,y,.. format as well as [x,y,...]  depending on where the request is coming from
			// TODO this is just irritating must be removed
			if (path.startsWith("[")) 
				vbConstraints.put("VB.iteration", path);
			else
				vbConstraints.put("VB.iteration", "["+path+"]");
		}

		List<VarBinding> vbList = getPq().getVarBindings(vbConstraints); // DB

		// use only the first result (expect only one) -- in this method we assume path is not null

		// map the resulting varBinding to an Artifact
		if (vbList == null) {
			logger.info("no entry corresponding to conditions: proc="+
					v.getPName()+" var = "+v.getVName()+" iteration = "+path);
			return lqList;
		}

		VarBinding vb = vbList.get(0);

		String URIFriendlyIterationVector = vb.getIteration().
		replace(',', '-').replace('[', ' ').replace(']', ' ').trim();

		String role;
		if (URIFriendlyIterationVector.length()>0) {
			role = vb.getPNameRef()+"/"+vb.getVarNameRef()+"?it="+URIFriendlyIterationVector;
		} else
			role = vb.getPNameRef()+"/"+vb.getVarNameRef();

		aOPMManager.addArtifact(vb.getValue());
		aOPMManager.createRole(role);
//		end OPM fumbling

		if (v.isInput() || getPq().isDataflow(proc)) { // if vName is input, then do a xfer() step

			// rec. accumulates SQL queries into lqList
			xferStep(wfInstance, var, proc, path, 
					selectedProcessors, lqList, aOPMManager.getCurrentArtifact(), aOPMManager.getCurrentRole());

		} else { // start with xform

			// rec. accumulates SQL queries into lqList
			xformStep(wfInstance, v, proc, path, selectedProcessors, lqList);			
		}

		aOPMManager.writeGraph();

		return lqList;

	}  // end searchDataflowGraph




	/**
	 * accounts for an inverse transformation from one output to all inputs of a processor
	 * @param wfInstance
	 * @param var  the output var
	 * @param proc  the processor
	 * @param selectedProcessors  the processors for which we are interested in producing lineage 
	 * @param path iteration vector within a VarBinding collection
	 * @param lqList  partial list of spot lineage queries, to be added to
	 * @throws SQLException 
	 */
	private void xformStep(String wfInstance, 
			Var outputVar, // we need the dnl from this output var
			String proc,
			String path,
			Set<String> selectedProcessors, 
			List<LineageSQLQuery> lqList 
	) throws SQLException {

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

		if (getPq().isDataflow(proc)) { // if we are looking at the output of an entire dataflow

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

			inputVars = getPq().getVars(varsQueryConstraints);
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

		// accumulate this proc to current path 
		currentPath.add(proc);

		// if this is a selected processor, add a copy of the current path to the list of paths for the processor
		if (selectedProcessors.contains(proc)) { 
			List<List<String>> paths = validPaths.get(proc);

			// copy the path since the original will change
			// also remove spurious dataflow processors are this point
			List<String> pathCopy = new ArrayList<String>();
			for (String s:currentPath) {
				if (!getPq().isDataflow(s)) pathCopy.add(s);
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

			// CHECK do the same even if we do have inputs, but returnOutputs is set to true
			if (var2Path.isEmpty()) {

				lq = getPq().generateSQL(wfInstance, proc, path, true);  // true -> fetch output vars

			} else {

				// dnl of output var defines length of suffix to path that we are going to use for query
				// if var2Path is null this generates a trivial query for the current output var and current path CHECK
				
				// note: if returnOutputs is true then this returns outputs ** in addition to ** inputs
				lq = getPq().lineageQueryGen(wfInstance, proc, var2Path, outputVar, path, isReturnOutputs());

				// if OPM is on, execute the query so we get the value we need for the Artifact node 
				LineageQueryResult inputs = getPq().runLineageQuery(lq);

//
//				update OPM graph
//
				
				for (LineageQueryResultRecord resultRecord: inputs.getRecords()) {

					String URIFriendlyIterationVector = resultRecord.getIteration().
					replace(',', '-').replace('[', ' ').replace(']', ' ').trim();

					boolean found = false;  // used to avoid duplicate process resources

					// assert proc as Process -- include iteration vector to separate different activations of the same process					
					aOPMManager.addProcess(proc, resultRecord.getIteration(), URIFriendlyIterationVector);

					if (aOPMManager.getCurrentArtifact() != null && aOPMManager.getCurrentRole() != null) {

						aOPMManager.assertGeneratedBy(
								aOPMManager.getCurrentArtifact(), 
								aOPMManager.getCurrentProcess(), 
								aOPMManager.getCurrentRole(), 
								aOPMManager.getCurrentAccount(),
								true);   // true -> prevent duplicates CHECK						


						// map each input var in the resultRecord to an Artifact
						// create new Resource for the resultRecord
						//    use the value as URI for the Artifact, and resolvedValue as the actual value

						aOPMManager.addArtifact(resultRecord.getValue());
						var2Artifact.put(resultRecord.getVname(), aOPMManager.getCurrentArtifact());

						String role;
						if (URIFriendlyIterationVector.length()>0) {
							role = resultRecord.getPname()+"/"+resultRecord.getVname()+"?it="+URIFriendlyIterationVector;
						} else
							role = resultRecord.getPname()+"/"+resultRecord.getVname();

						aOPMManager.createRole(role);					
						var2ArtifactRole.put(resultRecord.getVname(), aOPMManager.getCurrentRole());

						aOPMManager.assertUsed(
								aOPMManager.getCurrentArtifact(), 
								aOPMManager.getCurrentProcess(), 
								aOPMManager.getCurrentRole(), 
								aOPMManager.getCurrentAccount(),
								true);   // true -> prevent duplicates CHECK
					}
				}	
				
//
//				end OPM update		
//
				
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
			List<LineageSQLQuery> lqList,
			ProvenanceArtifact currentOutputArtifact,
			ProvenanceRole currentOutputArtifactRole) throws SQLException {

		String sourceProcName = null;
		String sourceVarName  = null;

		// retrieve all Arcs ending with (var,proc) -- ideally there is exactly one
		// (because multiple incoming arcs are disallowed)
		Map<String, String>  arcsQueryConstraints = new HashMap<String, String>();

		arcsQueryConstraints.put("W.instanceID", wfInstanceID);
		arcsQueryConstraints.put("sinkVarNameRef", var);  
		arcsQueryConstraints.put("sinkPNameRef", proc);  

		List<Arc> arcs = getPq().getArcs(arcsQueryConstraints);

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
		List<Var>  varList  = getPq().getVars(varsQueryConstraints);

		Var outputVar = varList.get(0);

		// recurse on xform
		xformStep(wfInstanceID, outputVar, sourceProcName, path, selectedProcessors, lqList);

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


	public void setPq(ProvenanceQuery pq) {
		this.pq = pq;
	}


	public ProvenanceQuery getPq() {
		return pq;
	}

	/**
	 * @return the ready
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * @param ready the ready to set
	 */
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	/**
	 * @return the returnOutputs
	 */
	public boolean isReturnOutputs() {
		return returnOutputs;
	}

	/**
	 * @param returnOutputs the returnOutputs to set
	 */
	public void setReturnOutputs(boolean returnOutputs) {
		this.returnOutputs = returnOutputs;
	}


}
