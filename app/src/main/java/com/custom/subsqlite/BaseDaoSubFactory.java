package com.custom.subsqlite;

import android.database.sqlite.SQLiteDatabase;

import com.custom.db.BaseDao;
import com.custom.db.BaseDaoFactory;

/**
 * Created by: Ysw on 2020/1/16.
 */
public class BaseDaoSubFactory extends BaseDaoFactory {
    private static final BaseDaoSubFactory instance = new BaseDaoSubFactory();
    //定义一个用于实现分库的对象
    private SQLiteDatabase subSqliteDatabase;

    public static BaseDaoSubFactory getInstance() {
        return instance;
    }

    private BaseDaoSubFactory() {

    }

    public synchronized <T extends BaseDao<M>, M> T getSubDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (map.get(PrivateDataBaseEnums.database.getValue()) != null) {
            return (T) map.get(PrivateDataBaseEnums.database.getValue());
        }
        subSqliteDatabase = SQLiteDatabase.openOrCreateDatabase(PrivateDataBaseEnums.database.getValue(), null);
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(subSqliteDatabase, entityClass);
            map.put(PrivateDataBaseEnums.database.getValue(), baseDao);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}
