package dBInterfacePackage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public interface DBInterface {

	public void loadDriver()throws ClassNotFoundException ;
	public Connection loadConnection(final String env,final ResourceBundle rBProps) throws SQLException, ClassNotFoundException;
	public boolean getAutoCommit(Connection con) throws SQLException;
	public void offAutoCommit(Connection con) throws SQLException;
	public void commit(Connection con) throws SQLException;
	public void unloadConnection(Connection con) throws SQLException;
}
