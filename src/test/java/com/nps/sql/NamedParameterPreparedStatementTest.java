package com.nps.sql;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class NamedParameterPreparedStatementTest
{
    Connection connection;

    @Before
    public void openConnection() throws Exception
    {
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:mem:");

        // create table

        Statement s = connection.createStatement();
        s.execute(
            "CREATE TABLE test " +
                "(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                " value1 INT NOT NULL DEFAULT 0, " +
                " value2 INT NOT NULL DEFAULT 0)");
        s.close();
    }

    @After
    public void closeConnection() throws Exception
    {
        connection.close();
    }

    @Test
    public void test() throws Exception
    {
        // insert

        NamedParameterPreparedStatement s = NamedParameterPreparedStatement.prepare(
            connection,
            "INSERT INTO test SET value1 = :value, value2 = :value",
            Statement.RETURN_GENERATED_KEYS);

        s.setLong("value", 5);

        int rowCount = s.executeUpdate();
        assertEquals(1, rowCount);

        ResultSet rs = s.getGeneratedKeys();
        int rowId    = -1;
        if (rs.first())
        {
            rowId = rs.getInt("SCOPE_IDENTITY()");
        }
        assertNotEquals(-1, rowId);

        // update

        s = NamedParameterPreparedStatement.prepare(
            connection,
            "UPDATE test SET value2 = :value WHERE id = :id");

        s.setLong("value", 3);
        s.setLong("id", rowId);

        rowCount = s.executeUpdate();
        assertEquals(1, rowCount);

        // select

        s = NamedParameterPreparedStatement.prepare(
            connection,
            "SELECT value2 FROM test WHERE id = :id");

        s.setLong("id", rowId);

        rs = s.executeQuery();

        assertTrue(rs.first());
        assertEquals(3, rs.getInt("VALUE2"));

        // delete

        s = NamedParameterPreparedStatement.prepare(
            connection,
            "DELETE FROM test WHERE id = :id");

        s.setLong("id", rowId);

        rowCount = s.executeUpdate();
        assertEquals(1, rowCount);
    }
}
