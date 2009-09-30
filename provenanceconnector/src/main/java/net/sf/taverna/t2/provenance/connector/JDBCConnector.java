/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.taverna.t2.provenance.connector;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * A shared factory class for retrieving a connection to the database.
 * The client is responsible for closing the connection once its finished with.
 *
 * The connection needs to be configured externally using a JNDI data source named "jdbc/taverna"
 *
 * @author Stuart Owen
 */

public class JDBCConnector {

    /**
     *
     * @return a connection to the database
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection() throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, SQLException {
        InitialContext context;
        Connection connection = null;
        try {
            context = new InitialContext();
            DataSource ds = (DataSource) context.lookup("jdbc/taverna");
            connection = ds.getConnection();            
        } catch (NamingException ex) {
            throw new SQLException("Unable to retrieve database connection for name jdbc/taverna",ex);
        }

        return connection;
    }

}
