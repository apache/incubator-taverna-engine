package org.purl.wf4ever.provtaverna.ui;

import java.io.IOException;
import java.io.OutputStream;

import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleUtil;
import org.openrdf.rio.turtle.TurtleWriter;

public class TurtleWriterWithBase extends TurtleWriter {
		private java.net.URI replaceBaseURI;
		private java.net.URI newBaseURI = null;

		public TurtleWriterWithBase(OutputStream out, java.net.URI replaceBaseURI) {
			this(out, replaceBaseURI, null);
		}

		public TurtleWriterWithBase(OutputStream out, java.net.URI replaceBaseURI, java.net.URI newBaseURI) {
			super(out);
			setReplaceBaseURI(replaceBaseURI);
			setNewBaseURI(newBaseURI);
		}

		@Override
		public void startRDF() throws RDFHandlerException {
			super.startRDF();
			java.net.URI base = newBaseURI != null ? newBaseURI : replaceBaseURI;
			try {
				if (newBaseURI == null) {					
					// We'll include original base as a comment
					writer.write("# ");
				}
				writer.write("@base ");
				writer.write("<");
				writer.write(base.toASCIIString());
				writer.write(">");
				writer.write(" .") ;
				writer.writeEOL();
			} catch (IOException e) {
				throw new RDFHandlerException(e);
			}
	
		}
		
		protected void writeURI(URI uri)
			throws IOException
		{
//				String uriString = uri.toString();			
			java.net.URI netURI = java.net.URI.create(uri.toString());
			String uriString = getReplaceBaseURI().relativize(netURI).toASCIIString();
			
			// Try to find a prefix for the URI's namespace
			String prefix = null;

			int splitIdx = TurtleUtil.findURISplitIndex(uriString);
			if (splitIdx > 0) {
				String namespace = uriString.substring(0, splitIdx);
				prefix = namespaceTable.get(namespace);
			}

			if (prefix != null) {
				// Namespace is mapped to a prefix; write abbreviated URI
				writer.write(prefix);
				writer.write(":");
				writer.write(uriString.substring(splitIdx));
			}
			else {
				// Write full URI
				writer.write("<");
				writer.write(TurtleUtil.encodeURIString(uriString));
				writer.write(">");
			}
		}

		public java.net.URI getReplaceBaseURI() {
			return replaceBaseURI;
		}

		public void setReplaceBaseURI(java.net.URI replaceBaseURI) {
			if (replaceBaseURI == null) {
				throw new NullPointerException("replaceBaseURI can't be null");
			}
			this.replaceBaseURI = replaceBaseURI;
		}

		public java.net.URI getNewBaseURI() {
			return newBaseURI;
		}

		public void setNewBaseURI(java.net.URI newBaseURI) {
			this.newBaseURI = newBaseURI;
		}

	}