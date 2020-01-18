package com.custom.db;

import java.util.List;

/**
 * Created by: Ysw on 2020/1/15.
 */
public interface IBaseDao<T> {
    long insert(T entity);

    long update(T entity, T where);

    int delete(T where);

    List<T> query(T where);

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);
}
