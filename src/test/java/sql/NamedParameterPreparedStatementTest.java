package sql;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;

public class NamedParameterPreparedStatementTest {

    Connection connection;

    @Before
    public void openConnection() {

        // TODO: create and connect to in-memory SQL db
    }

//    @Test
    public void test() throws Exception {

        // insert

        NamedParameterPreparedStatement s = NamedParameterPreparedStatement.prepare(
            connection,
            "INSERT INTO test SET value = :value",
            new String[] { "id" });

        s.setLong("value", 5);

        int rowCount = s.executeUpdate();
        assertEquals(1, rowCount);

        ResultSet rs = s.getGeneratedKeys();
        int rowId = rs.getInt("id");

        // update

        s = NamedParameterPreparedStatement.prepare(
            connection,
            "UPDATE test SET value = :value WHERE id = :id");

        s.setLong("value", 3);
        s.setLong("id", rowId);

        rowCount = s.executeUpdate();
        assertEquals(1, rowCount);

        // select

        s = NamedParameterPreparedStatement.prepare(
            connection,
            "SELECT value FROM test WHERE id = :id");

        s.setLong("id", rowId);

        rs = s.executeQuery();
        assertEquals(3, rs.getInt("value"));

        // delete

        s = NamedParameterPreparedStatement.prepare(
            connection,
            "DELETE FROM test WHERE id = :id");

        s.setLong("id", rowId);

        rowCount = s.executeUpdate();
        assertEquals(1, rowCount);
    }
}
