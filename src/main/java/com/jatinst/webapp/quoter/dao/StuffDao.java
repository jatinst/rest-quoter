package com.jatinst.webapp.quoter.dao;

import java.util.Arrays;
import java.util.List;

public class StuffDao implements Dao<String> {

    @Override
    public List<String> getAll() {
        String[] stuffList = { "a", "b", "c", "d" };
        return Arrays.asList(stuffList);
    }

    @Override
    public String getById(String id) {
        return "foo";
    }

}
