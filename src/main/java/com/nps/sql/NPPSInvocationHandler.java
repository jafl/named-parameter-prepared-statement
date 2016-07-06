package com.nps.sql;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.sql.PreparedStatement;

/* package */ class NPPSInvocationHandler implements InvocationHandler
{
    /**
     * Standard PreparedStatement used to implement the PreparedStatement
     * interface.
     */
    private PreparedStatement statement;

    /**
     * Maps each parameter name to a list of integers: the parameter
     * indices. This enables the same value to be used in multiple
     * locations in the statement.
     */
    private Map<String, List<Integer>> indexMap;

    /**
     * @param statement PreparedStatement
     * @param indexMap map of parameter names to indices
     */
    public NPPSInvocationHandler(PreparedStatement statement, Map<String, List<Integer>> indexMap)
    {
        this.statement = statement;
        this.indexMap = indexMap;
    }

    /**
     * @param proxy the proxy object
     * @param method method being invoked
     * @param args arguments passed to the method
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        boolean isSetter =
                args != null &&
                args.length > 1 &&
                "set".equals(method.getName().substring(0,3));

        if (isSetter && args[0] instanceof String)
        {
            Class[] paramTypes = method.getParameterTypes();
            paramTypes[0]      = int.class;
            Method statementMethod = statement.getClass().getMethod(
                method.getName(), paramTypes);

            Object result = null;
            for (Integer i : getIndices((String) args[0]))
            {
                args[0] = i;
                result  = statementMethod.invoke(statement, args);
            }

            return result;
        }
        else if (isSetter && args[0] instanceof Integer)
        {
            throw new RuntimeException("Cannot set value by index on a NamedParameterPreparedStatement");
        }
        else
        {
            return method.invoke(statement, args);
        }
    }

    /**
     * Returns the indices for a parameter.
     *
     * @param name parameter name
     * @return parameter indices
     * @throws IllegalArgumentException if the parameter does not exist
     */
    private List<Integer> getIndices(String name)
    {
        List<Integer> indices = indexMap.get(name);
        if (indices == null)
        {
            throw new IllegalArgumentException("Parameter not found: " + name);
        }
        return indices;
    }
}
