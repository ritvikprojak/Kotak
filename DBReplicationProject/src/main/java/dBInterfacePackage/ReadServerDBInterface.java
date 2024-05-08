/*
 * Implement This Interface if Read/ Select Query are to be Fired in DB.
 * Normally Implemented on Source Server DB, for limiting actions to Read only
 */
package dBInterfacePackage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public interface ReadServerDBInterface {
	public ResultSet fetchResultSet(final String tb, final Connection con, final ResourceBundle rBProps) throws SQLException;
	public void isRSEmpty(ResultSet rs);
}
