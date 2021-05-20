package eu.claymc.proxysystem.database;

import java.sql.SQLException;

public interface IDatabase<C> {

    void connect();

    C getConnection() throws SQLException;


}
