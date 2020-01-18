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
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by: Ysw on 2020/1/15.
 */
public class BaseDao<T> implements IBaseDao<T> {
    private SQLiteDatabase sqLiteDatabase;
    private String tableName;
    private Class<T> entityClass;
    private HashMap<String, Field> cacheMap = new HashMap<>();

    void init(@NonNull SQLiteDatabase sqLiteDatabase, @NonNull Class<T> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;
        DbTable dbTable = entityClass.getAnnotation(DbTable.class);
        if (dbTable != null && !TextUtils.isEmpty(dbTable.value())) {
            tableName = dbTable.value();
        } else {
            tableName = entityClass.getSimpleName();
        }
        if (!sqLiteDatabase.isOpen()) return;
        String createTableSql = getCreateTableSql();
        sqLiteDatabase.execSQL(createTableSql);
    }


    /**
     * 获取创建表的数据库语句
     *
     * @author Ysw created at 2020/1/17 17:35
     */
    private String getCreateTableSql() {
        // create table if not exists tb_user(_id INTEGER,name TEXT,password TEXT,status INTEGER)
        StringBuilder buffer = new StringBuilder();
        buffer.append("create table if not exists ");
        buffer.append(tableName).append("(");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            String fieldName;
            DbField dbField = field.getAnnotation(DbField.class);
            if (dbField != null && !TextUtils.isEmpty(dbField.value())) {
                fieldName = dbField.value();
            } else {
                fieldName = field.getName();
            }
            if (fieldType == String.class) {
                buffer.append(fieldName).append(" TEXT,");
            } else if (fieldType == Integer.class) {
                buffer.append(fieldName).append(" INTEGER,");
            } else if (fieldType == Long.class) {
                buffer.append(fieldName).append(" BIGINT,");
            } else if (fieldType == Double.class) {
                buffer.append(fieldName).append(" DOUBLE,");
            } else if (fieldType == byte[].class) {
                buffer.append(fieldName).append(" BLOG,");
            } else {
                /* 不支持的类型 @author Ysw created 2020/1/15 */
                continue;
            }
            cacheMap.put(fieldName, field);
        }
        if (buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == ',') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * 将实体类中的字段名、值放入到一个map容器中
     *
     * @author Ysw created at 2020/1/17 17:55
     */
    private HashMap<String, String> getValuesMap(T entity) {
        HashMap<String, String> map = new HashMap<>();
        for (Map.Entry<String, Field> entry : cacheMap.entrySet()) {
            Field field = entry.getValue();
            try {
                Object object = field.get(entity);
                if (object == null) continue;
                String value = object.toString();
                String key = entry.getKey();
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) map.put(key, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }


    /**
     * 获取数据库插入需要的 ContentValues
     *
     * @author Ysw created at 2020/1/17 21:25
     */
    private ContentValues getContentValues(@NonNull Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }


    /**
     * 通过此内部类拿到条件语句拼写集及更新值数组
     *
     * @author Ysw created at 2020/1/17 21:39
     */
    public class Condition {
        private String whereCause;
        private String[] whereArgs;

        Condition(Map<String, String> whereCause) {
            ArrayList<String> list = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("1=1");
            for (String key : whereCause.values()) {
                String value = whereCause.get(key);
                if (value != null) {
                    stringBuilder.append(" and ").append(key).append("=?");
                    list.add(value);
                }
            }
            this.whereCause = stringBuilder.toString();
            this.whereArgs = list.toArray(new String[0]);
        }
    }


    /**
     * 通过此方法将查询到的cursor的值转换成List对象并返回给用户
     *
     * @return ArrayList
     * @author Ysw created at 2020/1/17 21:55\
     */
    private ArrayList getResult(Cursor cursor, T where) {
        ArrayList list = new ArrayList<>();
        Object item;
        while (cursor.moveToNext()) {
            try {
                item = where.getClass().newInstance();
                for (Map.Entry<String, Field> entry : cacheMap.entrySet()) {
                    //获取列明
                    String columnName = entry.getKey();
                    //获取列名在游标中的位置
                    int columnIndex = cursor.getColumnIndex(columnName);
                    //取实体类中的字段名
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
    public long insert(T entity) {
        HashMap<String, String> valuesMap = getValuesMap(entity);
        ContentValues contentValues = getContentValues(valuesMap);
        return sqLiteDatabase.insert(tableName, null, contentValues);
    }


    @Override
    public long update(T entity, T where) {
        //sqLiteDatabase.update(tableName,ContentValues,"name=?",new String[]{"value"});
        HashMap<String, String> valuesMap = getValuesMap(entity);
        ContentValues contentValues = getContentValues(valuesMap);
        HashMap<String, String> whereCause = getValuesMap(where);
        Condition condition = new Condition(whereCause);
        return sqLiteDatabase.update(tableName, contentValues, condition.whereCause, condition.whereArgs);
    }

    @Override
    public int delete(T where) {
        //sqLiteDatabase.delete(tableName,"name=?",new String[]{"value"});
        HashMap<String, String> map = getValuesMap(where);
        Condition condition = new Condition(map);
        return sqLiteDatabase.delete(tableName, condition.whereCause, condition.whereArgs);
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        HashMap<String, String> map = getValuesMap(where);
        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = sqLiteDatabase.query(tableName, null, condition.whereCause,
                condition.whereArgs, null, null, orderBy, limitString);
        return (List<T>) getResult(cursor, where);
    }
}
