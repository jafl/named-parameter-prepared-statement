package sql;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
* Query parser used by {@link NamedParameterPreparedStatement}.
*
* @see http://www.javaworld.com/article/2077706/core-java/named-parameters-for-preparedstatement.html
*/
/* package */ class NamedParameterParser {

    /**
    * Parses a query with named parameters to generate an indexed query and
    * the parameter-index mapping.
    *
    * @param sql query to parse
    * @param indexMap output map of parameter names to parameter indices
    * @return the parsed query
    */
    public static String parse(String sql, Map<String, List<Integer>> indexMap) {

        indexMap.clear();

        int length = sql.length();
        StringBuffer parsedSql = new StringBuffer(length);

        int index = 1;      // probably the only situation where an index starts with 1
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inBackQuote = false;

        for (int i=0; i<length; i++) {

            char c = sql.charAt(i);
            if (c == '\\') {
                parsedSql.append(c);
                i++;
                c = sql.charAt(i);

            } else if (c == '\'' && !inDoubleQuote && !inBackQuote) {
                inSingleQuote = !inSingleQuote;

            } else if (c == '"' && !inSingleQuote && !inBackQuote) {
                inDoubleQuote = !inDoubleQuote;

            } else if (c == '`' && !inSingleQuote && !inDoubleQuote) {
                inBackQuote = !inBackQuote;

            } else if (!inSingleQuote && !inDoubleQuote && !inBackQuote &&
                    c == ':' && i+1 < length &&
                    Character.isJavaIdentifierStart(sql.charAt(i+1))) {

                int j = i + 2;
                while (j < length && Character.isJavaIdentifierPart(sql.charAt(j))) {
                    j++;
                }
                String name = sql.substring(i+1, j);

                c = '?';                // replace the parameter with a question mark
                i += name.length();     // skip past the end of the parameter

                List<Integer> indexList = indexMap.get(name);
                if (indexList == null) {
                    indexList = new ArrayList<Integer>();
                    indexMap.put(name, indexList);
                }
                indexList.add(index);
                index++;
            }

            parsedSql.append(c);
        }

        return parsedSql.toString();
    }
}
