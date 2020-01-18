package com.custom.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by: Ysw on 2020/1/18.
 */
public class BaseDaoSubFactory extends BaseDaoFactory {
    private static final BaseDaoSubFactory instance = new BaseDaoSubFactory();

    public static BaseDaoSubFactory getInstance() {
        return instance;
    }

    private BaseDaoSubFactory() {
    }

    public synchronized <T extends BaseDao<T>, M> T getSubDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (map.get(DataBasePathEnums.database.getDatabasePath()) != null) {
            return (T) map.get(DataBasePathEnums.database.getDatabasePath());
        }
        SQLiteDatabase subSqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(DataBasePathEnums.database.getDatabasePath(),
                null);
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(subSqLiteDatabase, entityClass);
            map.put(DataBasePathEnums.database.getDatabasePath(), baseDao);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}