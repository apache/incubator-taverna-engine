package net.sf.taverna.t2.workflowmodel.utils;

import java.util.Comparator;

import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;
import net.sf.taverna.t2.workflowmodel.MergePort;
import net.sf.taverna.t2.workflowmodel.Port;
import net.sf.taverna.t2.workflowmodel.ProcessorPort;

/**
 * Compares two Datalink objects by their name. Name of the Datalink is obtained as 
 * "<source.processor>:<source.processor.output_port> -> <sink.processor>:<sink.processor.input_port>"
 * 
 * 
 * @author Alex Nenadic
 * 
 */
public class DatalinkComparator implements
		Comparator<Datalink> {

	@Override
	public int compare(Datalink link1, Datalink link2) {
		// Text for link1
		EventForwardingOutputPort source1 = link1.getSource();
		String sourceName1 = findName(source1);
		EventHandlingInputPort sink1 = link1.getSink();
		String sinkName1 = findName(sink1);
		String text1 = sourceName1 + " -> " + sinkName1;
		
		// Text for link2
		EventForwardingOutputPort source2 = link2.getSource();
		String sourceName2 = findName(source2);
		EventHandlingInputPort sink2 = link2.getSink();
		String sinkName2 = findName(sink2);
		String text2 = sourceName2 + " -> " + sinkName2;

		return text1.compareToIgnoreCase(text2);
	}
	
	public static String findName(Port port) {		
		if (port instanceof ProcessorPort) {
			String sourceProcessorName = ((ProcessorPort)port).getProcessor().getLocalName();
			return sourceProcessorName + ":" + port.getName();
		} else if (port instanceof MergePort) {
			String sourceMergeName = ((MergePort)port).getMerge().getLocalName();
			return sourceMergeName + ":" + port.getName();
			
		} else {
			return port.getName();
		}
	}
}