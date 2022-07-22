package com.lncn.remotejdbc;

import com.lncn.remotejdbc.constant.Constants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @Classname Driver
 * @Description TODO
 * @Date 2022/6/28 17:50
 * @Created by byco
 */
public class Driver implements java.sql.Driver {

    static {
        try {
            java.sql.DriverManager.registerDriver(new Driver());
        } catch (SQLException E) {
            throw new RuntimeException("Can't register driver!");
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        var url = "jdbc:rsql://localhost:9000";

        Class.forName("com.lncn.remotejdbc.Driver");
        Properties properties = new Properties();
        properties.put("fetchSize", "4000");
        //properties.put("logLevel","debug");
        try (var con = DriverManager.getConnection(url, properties)
             ; var st = con.createStatement()
        ) {
            try (var rs = st.executeQuery("SELECT * FROM financialpostingline limit 50")) {
                int c = rs.getMetaData().getColumnCount();
                System.out.println("getColumnCount " + c);
                while (rs.next()) {
                    for (int i = 1; i <= c; i++) {
                        System.out.print(rs.getString(i));
                        System.out.print(", ");
                    }
                    System.out.println();
                }
            }

        }
    }

    /**
     * Attempts to make a database connection to the given URL.
     * The driver should return "null" if it realizes it is the wrong kind
     * of driver to connect to the given URL.  This will be common, as when
     * the JDBC driver manager is asked to connect to a given URL it passes
     * the URL to each loaded driver in turn.
     *
     * <P>The driver should throw an <code>SQLException</code> if it is the right
     * driver to connect to the given URL but has trouble connecting to
     * the database.
     *
     * <P>The {@code Properties} argument can be used to pass
     * arbitrary string tag/value pairs as connection arguments.
     * Normally at least "user" and "password" properties should be
     * included in the {@code Properties} object.
     * <p>
     * <B>Note:</B> If a property is specified as part of the {@code url} and
     * is also specified in the {@code Properties} object, it is
     * implementation-defined as to which value will take precedence. For
     * maximum portability, an application should only specify a property once.
     *
     * @param url  the URL of the database to which to connect
     * @param info a list of arbitrary string tag/value pairs as
     *             connection arguments. Normally at least a "user" and
     *             "password" property should be included.
     * @return a <code>Connection</code> object that represents a
     * connection to the URL
     * @throws SQLException if a database access error occurs or the url is
     *                      {@code null}
     */
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return new RemoteConnectionImpl(url, info);
    }

    /**
     * Retrieves whether the driver thinks that it can open a connection
     * to the given URL.  Typically drivers will return <code>true</code> if they
     * understand the sub-protocol specified in the URL and <code>false</code> if
     * they do not.
     *
     * @param url the URL of the database
     * @return <code>true</code> if this driver understands the given URL;
     * <code>false</code> otherwise
     * @throws SQLException if a database access error occurs or the url is
     *                      {@code null}
     */
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.toLowerCase().startsWith(Constants.URL_PREFIX);
    }

    /**
     * Gets information about the possible properties for this driver.
     * <p>
     * The <code>getPropertyInfo</code> method is intended to allow a generic
     * GUI tool to discover what properties it should prompt
     * a human for in order to get
     * enough information to connect to a database.  Note that depending on
     * the values the human has supplied so far, additional values may become
     * necessary, so it may be necessary to iterate though several calls
     * to the <code>getPropertyInfo</code> method.
     *
     * @param url  the URL of the database to which to connect
     * @param info a proposed list of tag/value pairs that will be sent on
     *             connect open
     * @return an array of <code>DriverPropertyInfo</code> objects describing
     * possible properties.  This array may be an empty array if
     * no properties are required.
     * @throws SQLException if a database access error occurs
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    /**
     * Retrieves the driver's major version number. Initially this should be 1.
     *
     * @return this driver's major version number
     */
    @Override
    public int getMajorVersion() {
        return 0;
    }

    /**
     * Gets the driver's minor version number. Initially this should be 0.
     *
     * @return this driver's minor version number
     */
    @Override
    public int getMinorVersion() {
        return 0;
    }

    /**
     * Reports whether this driver is a genuine JDBC
     * Compliant driver.
     * A driver may only report <code>true</code> here if it passes the JDBC
     * compliance tests; otherwise it is required to return <code>false</code>.
     * <p>
     * JDBC compliance requires full support for the JDBC API and full support
     * for SQL 92 Entry Level.  It is expected that JDBC compliant drivers will
     * be available for all the major commercial databases.
     * <p>
     * This method is not intended to encourage the development of non-JDBC
     * compliant drivers, but is a recognition of the fact that some vendors
     * are interested in using the JDBC API and framework for lightweight
     * databases that do not support full database functionality, or for
     * special databases such as document information retrieval where a SQL
     * implementation may not be feasible.
     *
     * @return <code>true</code> if this driver is JDBC Compliant; <code>false</code>
     * otherwise
     */
    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    /**
     * Return the parent Logger of all the Loggers used by this driver. This
     * should be the Logger farthest from the root Logger that is
     * still an ancestor of all of the Loggers used by this driver. Configuring
     * this Logger will affect all of the log messages generated by the driver.
     * In the worst case, this may be the root Logger.
     *
     * @return the parent Logger for this driver
     * @throws SQLFeatureNotSupportedException if the driver does not use
     *                                         {@code java.util.logging}.
     * @since 1.7
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
