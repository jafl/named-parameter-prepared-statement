package com.nps.sql;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NamedParameterParserTest
{
    Map<String, List<Integer>> indexMap = new HashMap<>();

    @Test
    public void testParsing_oneParameter()
    {
        String parsedSql = NamedParameterParser.parse(
            "update foo set bar = :baz",
            indexMap);

        assertEquals("update foo set bar = ?", parsedSql);
        assertEquals(1, indexMap.size());
        assertEquals(1, indexMap.get("baz").size());
        assertEquals(1, indexMap.get("baz").get(0).intValue());
    }

    @Test
    public void testParsing_twoParameters()
    {
        String parsedSql = NamedParameterParser.parse(
            "update foo set bar = :baz, zap = :shug",
            indexMap);

        assertEquals("update foo set bar = ?, zap = ?", parsedSql);
        assertEquals(2, indexMap.size());
        assertEquals(1, indexMap.get("baz").size());
        assertEquals(1, indexMap.get("baz").get(0).intValue());
        assertEquals(1, indexMap.get("shug").size());
        assertEquals(2, indexMap.get("shug").get(0).intValue());
    }

    @Test
    public void testParsing_repeatedParameters()
    {
        String parsedSql = NamedParameterParser.parse(
            "update foo set bar = :baz, zap = :shug, foo = :baz",
            indexMap);

        assertEquals("update foo set bar = ?, zap = ?, foo = ?", parsedSql);
        assertEquals(2, indexMap.size());
        assertEquals(2, indexMap.get("baz").size());
        assertEquals(1, indexMap.get("baz").get(0).intValue());
        assertEquals(3, indexMap.get("baz").get(1).intValue());
        assertEquals(1, indexMap.get("shug").size());
        assertEquals(2, indexMap.get("shug").get(0).intValue());
    }

    @Test
    public void testParsing_quotes()
    {
        String parsedSql = NamedParameterParser.parse(
            "update `f'oo` set `bar` = :baz, zap = ':s\\'h\"ug', foo = \":b'a\\\"z\"",
            indexMap);

        assertEquals("update `f'oo` set `bar` = ?, zap = ':s\\'h\"ug', foo = \":b'a\\\"z\"", parsedSql);
        assertEquals(1, indexMap.size());
        assertEquals(1, indexMap.get("baz").size());
        assertEquals(1, indexMap.get("baz").get(0).intValue());
    }
}
