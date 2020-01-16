package com.custom.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.custom.annotation.DbField;
import com.custom.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by: Ysw on 2020/1/15.
 */
public class BaseDao<T> implements IBaseDao<T> {

    /* 持有数据库操作的引用 @author Ysw created 2020/1/15 */
    private SQLiteDatabase sqLiteDatabase;

    /* 表名 @author Ysw created 2020/1/15 */
    private String tableName;

    /* 持有操作数据库所对应得java类型 @author Ysw created 2020/1/15 */
    private Class<T> entityClass;

    /* 是否做过初始化操作 @author Ysw created 2020/1/15 */
    private boolean isInit = false;

    /* 定义一个缓存空间的hashMap @author Ysw created 2020/1/15 */
    private HashMap<String, Field> cacheMap;


    public boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;
        if (!isInit) {
            /* 获取表名 @author Ysw created 2020/1/15 */
            if (entityClass.getAnnotation(DbTable.class) == null) {
                tableName = entityClass.getSimpleName();
            } else {
                String value = entityClass.getAnnotation(DbTable.class).value();
                tableName = TextUtils.isEmpty(value) ? entityClass.getSimpleName() : value;
            }
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }
            String createTableSql = getCreateTableSql();
            sqLiteDatabase.execSQL(createTableSql);
            cacheMap = new HashMap<>();
            initCacheMap();
            isInit = true;
        }
        return isInit;

    }

    private void initCacheMap() {
        //取到所有的列名
        String sql = "select * from " + tableName + " limit 1, 0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();

        //取所有的成员变量
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            //进行成员变量操作权限获取
            field.setAccessible(true);
        }

        //对列名和变量进行映射--字段名和成员变量进行映射(成员变量有可能添加注解名字不一样)
        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : fields) {
                String fieldName = null;
                if (field.getAnnotation(DbField.class) != null &&
                        !TextUtils.isEmpty(field.getAnnotation(DbField.class).value())) {
                    fieldName = field.getAnnotation(DbField.class).value();
                } else {
                    fieldName = field.getName();
                }
                if (columnName.equals(fieldName)) {
                    columnField = field;
                    break;
                }
            }
            if (columnField != null) {
                cacheMap.put(columnName, columnField);
            }
        }
    }

    private String getCreateTableSql() {
        //create table if not exists tb_user(_id integer,name varchar2(20),password varchar2(20))
        // create table if not exists tb_user(_id INTEGER,name TEXT,password TEXT)
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName + "(");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            /* 获取成员变量的类型 @author Ysw created 2020/1/15 */
            Class type = field.getType();
            if (field.getAnnotation(DbField.class) != null &&
                    !TextUtils.isEmpty(field.getAnnotation(DbField.class).value())) {
                if (type == String.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BLOG,");
                } else {
                    /* 不支持的类型 @author Ysw created 2020/1/15 */
                    continue;
                }
            } else {
                if (type == String.class) {
                    stringBuffer.append(field.getName() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getName() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getName() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getName() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getName() + " BLOG,");
                } else {
                    /* 不支持的类型 @author Ysw created 2020/1/15 */
                    continue;
                }
            }
        }
        if (stringBuffer.length() > 0 && stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }


    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }

    private Map<String, String> getValues(T entity) {
        HashMap<String, String> map = new HashMap<>();
        Iterator<Field> filedIterator = cacheMap.values().iterator();
        while (filedIterator.hasNext()) {
            Field field = filedIterator.next();
            field.setAccessible(true);
            try {
                Object object = field.get(entity);
                if (object == null) {
                    continue;
                }
                String value = object.toString();
                String key = null;
                if (field.getAnnotation(DbField.class) != null &&
                        !TextUtils.isEmpty(field.getAnnotation(DbField.class).value())) {
                    key = field.getAnnotation(DbField.class).value();
                } else {
                    key = field.getName();
                }
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private class Condition {
        private String whereCause;
        private String[] whereArgs;

        public Condition(Map<String, String> whereCause) {
            ArrayList list = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("1=1");

            Set<String> keys = whereCause.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = whereCause.get(key);
                if (value != null) {
                    stringBuilder.append(" and " + key + "=?");
                    list.add(value);
                }
            }
            this.whereCause = stringBuilder.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }

    @Override
    public long insert(T entity) {
        Map<String, String> map = getValues(entity);
        ContentValues contentValues = getContentValues(map);
        return sqLiteDatabase.insert(tableName, null, contentValues);
    }

    @Override
    public long update(T entity, T where) {
//        sqLiteDatabase.update(tableName,ContentValues,"name=?",new String[]{"test"});
        Map<String, String> values = getValues(entity);
        ContentValues contentValues = getContentValues(values);
        Map whereCause = getValues(where);
        Condition condition = new Condition(whereCause);
        return sqLiteDatabase.update(tableName, contentValues, condition.whereCause, condition.whereArgs);
    }

    @Override
    public int delete(T where) {
//        sqLiteDatabase.delete(tableName, "name=?", new String[]{});
        Map<String, String> map = getValues(where);
        Condition condition = new Condition(map);
        return sqLiteDatabase.delete(tableName, condition.whereCause, condition.whereArgs);
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
//        sqLiteDatabase.query(tableName, null, "id=?", new String[]{}, null, null, "orderby", "1,5");
        Map<String, String> map = getValues(where);
        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = sqLiteDatabase.query(tableName, null, condition.whereCause,
                condition.whereArgs, null, null, orderBy, limitString);
        List<T> result = getResult(cursor, where);
        return result;
    }

    private List<T> getResult(Cursor cursor, T where) {
        ArrayList list = new ArrayList();
        Object item = null;
        while (cursor.moveToNext()) {
            try {
                item = where.getClass().newInstance();
                Iterator<Map.Entry<String, Field>> iterator = cacheMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> entry = iterator.next();
                    //取列名
                    String columnName = entry.getKey();
                    //获取列名在游标中的位置
                    int columnIndex = cursor.getColumnIndex(columnName);

                    Field field = entry.getValue();
                    Class<?> type = field.getType();
                    if (columnIndex != -1) {
                        if (type == String.class) {
                            field.set(item, cursor.getString(columnIndex));
                        } else if (type == Double.class) {
                            field.set(item, cursor.getDouble(columnIndex));
                        } else if (type == Integer.class) {
                            field.set(item, cursor.getInt(columnIndex));
                        } else if (type == Long.class) {
                            field.set(item, cursor.getLong(columnIndex));
                        } else if (type == Byte[].class) {
                            field.set(item, cursor.getBlob(columnIndex));
                        } else {
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    @Override
    public List<T> query(String sql) {
        return null;
    }
}
