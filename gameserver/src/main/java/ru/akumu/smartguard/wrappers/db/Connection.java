package ru.akumu.smartguard.wrappers.db;

import ru.akumu.smartguard.core.wrappers.db.IConnection;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Akumu
 * @date 27.03.2016 12:07
 */
public class Connection extends IConnection {
    private java.sql.Connection con;

    public Connection(java.sql.Connection con) {
        if (con == null)
            throw new InvalidParameterException();

        this.con = con;
    }

    @Override
    public PreparedStatement prepareStatement(String s) throws SQLException {
        return con.prepareStatement(s);
    }

    @Override
    public void close() {
        try {
            con.close();
        } catch (SQLException e) {

        }
    }
}
