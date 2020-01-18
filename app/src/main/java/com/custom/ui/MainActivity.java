package com.custom.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.custom.R;
import com.custom.bean.User;
import com.custom.db.BaseDao;
import com.custom.db.BaseDaoFactory;
import com.custom.db.BaseDaoSubFactory;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    private String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions();
    }

    public void verifyStoragePermissions() {
        try {
            int permission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                int REQUEST_EXTERNAL_STORAGE = 1;
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertData(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class, User.class);
        User user = new User();
        user.setId("NO.001");
        user.setName("杨胜文");
        user.setPassword("123456");
        user.setStatus(1);
        long result = baseDao.insert(user);
        Log.d("Ysw", "插入成功");
        Log.d("Ysw", "插入的数量 = " + result);
    }

    public void queryData(View view) {
        BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class, User.class);
        User user = new User();
        List<User> result = baseDao.query(user);
        Log.d("Ysw", "查询成功");
        Log.d("Ysw", "查询到的数量 = " + result.size());
        for (User item : result) {
            Log.d("Ysw", "查询到的数据 = " + item.toString());
        }
    }

    public void updateData(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class, User.class);
        User user = new User();
        user.setPassword("88888888");
        User where = new User();
        where.setName("杨胜文");
        where.setId("NO.001");
        long update = baseDao.update(user, where);
        Log.d("Ysw", "更新成功 = ");
        Log.d("Ysw", "更新的数量 = " + update);
    }

    public void deleteData(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class, User.class);
        User where = new User();
        where.setName("杨胜文");
        int delete = baseDao.delete(where);
        Log.d("Ysw", "删除成功 = ");
        Log.d("Ysw", "删除的数量 = " + delete);
    }

    public void userLogin(View view) {
        BaseDao subDao = BaseDaoSubFactory.getInstance().getSubDao(BaseDao.class, User.class);
        User user = new User();
        user.setStatus(1);
        user.setId("NO.002");
        user.setPassword("99999999");
        user.setName("这个是用户1");
        subDao.insert(user);
        Log.d("Ysw", "userId = " + user.getId());
        Log.d("Ysw", "用户登录成功");
    }

    public void subInsert(View view) {

    }
}