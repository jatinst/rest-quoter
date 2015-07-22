package com.jatinst.webapp.quoter.dao;

import java.util.List;

public interface Dao<T> {

    List<T> getAll();

    T getById(String id);

}
