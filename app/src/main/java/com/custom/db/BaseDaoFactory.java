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
    private String sqLiteDatabasePath;
    private SQLiteDatabase sqLiteDatabase;
    private static final BaseDaoFactory instance = new BaseDaoFactory();
    //设计一个数据库连接池
    protected Map<String, BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    public static BaseDaoFactory getInstance() {
        return instance;
    }

    /* 定义建数据库数据的路径 @author Ysw created 2020/1/15 */
    /* 建议写到SD卡中，APP被删除了，下次安装时数据还在 @author Ysw created 2020/1/15 */

    protected BaseDaoFactory() {
        boolean exist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (exist) {
            /* SDK的目录有问题 @author Ysw created 2020/1/15 */
            //sqLiteDatabasePath = Environment.getDownloadCacheDirectory().toString();
            sqLiteDatabasePath = "data/data/com.custom/Ysw.db";
        } else {
            sqLiteDatabasePath = "data/data/com.custom/Ysw.db";
        }
        /* 创建database @author Ysw created 2020/1/15 */
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
