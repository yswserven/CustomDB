package com.custom.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Ysw on 2020/1/15.
 */
public class BaseDaoFactory {
    private SQLiteDatabase sqLiteDatabase;
    private static final BaseDaoFactory instance = new BaseDaoFactory();
    protected Map<String, BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    public static BaseDaoFactory getInstance() {
        return instance;
    }

    protected BaseDaoFactory() {
        boolean exist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        String sqLiteDatabasePath;
        if (exist) {
            sqLiteDatabasePath = "data/data/com.custom/user.db";
        } else {
            sqLiteDatabasePath = "data/data/com.custom/user.db";
        }
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqLiteDatabasePath, null);
    }

    public synchronized <T extends BaseDao<M>, M> T getBaseDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (map.get(daoClass.getSimpleName()) != null) {
            baseDao = map.get(daoClass.getSimpleName());
            return (T) baseDao;
        }
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
            map.put(daoClass.getSimpleName(), baseDao);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}
