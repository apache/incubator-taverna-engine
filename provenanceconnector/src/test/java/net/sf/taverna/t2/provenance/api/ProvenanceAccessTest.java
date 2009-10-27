/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.taverna.t2.provenance.api;

import java.io.File;
import java.sql.Connection;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stuart Owen
 */
public class ProvenanceAccessTest {

    

    /**
     * Test of initDataSource method, of class ProvenanceAccess.
     */
    @Test    
    public void testInitDataSource() throws Exception {
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        File tempDir = File.createTempFile("install", "dir");
        tempDir.delete();
        tempDir.mkdir();
        String jdbcUrl= "jdbc:derby:"+tempDir.toString()+"/database;create=true;upgrade=true";
        ProvenanceAccess.initDataSource(driver, jdbcUrl, null, null, 10 , 20, 20);

        InitialContext context = new InitialContext();
        DataSource ds = (DataSource)context.lookup("jdbc/taverna");
        assertNotNull(ds);
        Connection con = ds.getConnection();
        assertNotNull(con);
    }

    @Test    
    public void testInitDataSource2() throws Exception {
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        File tempDir = File.createTempFile("install", "dir");
        tempDir.delete();
        tempDir.mkdir();
        String jdbcUrl= "jdbc:derby:"+tempDir.toString()+"/database;create=true;upgrade=true";
        ProvenanceAccess.initDataSource(driver, jdbcUrl);

        InitialContext context = new InitialContext();
        DataSource ds = (DataSource)context.lookup("jdbc/taverna");
        assertNotNull(ds);
        Connection con = ds.getConnection();
        assertNotNull(con);
    }

    @Test    
    public void testInitDefaultReferenceService() throws Exception {
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        File tempDir = File.createTempFile("install", "dir");
        tempDir.delete();
        tempDir.mkdir();
        String jdbcUrl= "jdbc:derby:"+tempDir.toString()+"/database;create=true;upgrade=true";
        ProvenanceAccess.initDataSource(driver, jdbcUrl);

        ProvenanceAccess provenanceAccess = new ProvenanceAccess(ProvenanceConnectorType.DERBY);
        
        provenanceAccess.initDefaultReferenceService();
        assertNotNull(provenanceAccess.provenanceConnector.getInvocationContext());
        assertNotNull(provenanceAccess.provenanceConnector.getInvocationContext().getReferenceService());
        assertEquals(provenanceAccess.provenanceConnector.getReferenceService(),provenanceAccess.provenanceConnector.getInvocationContext().getReferenceService());
    }

    

}