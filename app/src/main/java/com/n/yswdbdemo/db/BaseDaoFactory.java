package com.n.yswdbdemo.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * Created by: Ysw on 2020/1/15.
 */
public class BaseDaoFactory {
    private String sqLiteDatabasePath;
    private SQLiteDatabase sqLiteDatabase;
    private static final BaseDaoFactory instance = new BaseDaoFactory();

    public static BaseDaoFactory getInstance() {
        return instance;
    }

    /* 定义建数据库数据的路径 @author Ysw created 2020/1/15 */
    /* 建议写到SD卡中，APP被删除了，下次安装时数据还在 @author Ysw created 2020/1/15 */

    private BaseDaoFactory() {
        boolean exist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (exist) {
            sqLiteDatabasePath = Environment.getDownloadCacheDirectory().toString();
            sqLiteDatabasePath = "data/data/com.n.yswdbdemo/Ysw.db";
        } else {
            sqLiteDatabasePath = "data/data/com.n.yswdbdemo/Ysw.db";
        }
        /* 创建database @author Ysw created 2020/1/15 */
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqLiteDatabasePath, null);
    }

    public <T> BaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseDao;
    }
}
