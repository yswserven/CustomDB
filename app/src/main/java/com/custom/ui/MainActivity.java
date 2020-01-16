package com.custom.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.custom.R;
import com.custom.bean.User;
import com.custom.db.BaseDao;
import com.custom.db.BaseDaoFactory;
import com.custom.db.BaseDaoImpl;

import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    private int REQUEST_EXTERNAL_STORAGE = 1;
    private String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions();

    }

    public void createTable(View view) {
        BaseDaoFactory.getInstance().getBaseDao(BaseDaoImpl.class, User.class);
        Toast.makeText(this, "数据库创建成功", Toast.LENGTH_LONG).show();
    }

    public void verifyStoragePermissions() {
        try {
            int permission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertData(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDaoImpl.class, User.class);
        baseDao.insert(new User(1, "杨胜文", "11111111"));
        baseDao.insert(new User(1, "吴士胜", "BBBBBBBBBBBB"));
        baseDao.insert(new User(1, "涂良坛", "AAAAAAAAAAA"));
        baseDao.insert(new User(1, "华星宇", "22222222"));
        baseDao.insert(new User(1, "曾凡军", "33333333"));
        baseDao.insert(new User(1, "小龙虾", "44444444"));
        baseDao.insert(new User(1, "蔡楠林", "55555555"));
        baseDao.insert(new User(1, "吴茂聪", "66666666"));
        baseDao.insert(new User(1, "Wss", "CCCCCCCCCC"));
        baseDao.insert(new User(1, "Tlt", "fffffffffff"));
        baseDao.insert(new User(1, "Ysw", "77777777"));
        baseDao.insert(new User(1, "Serven", "88888888"));
        Toast.makeText(this, "插入成功", Toast.LENGTH_LONG).show();
    }

    public void queryData(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDaoImpl.class, User.class);

        User user = new User();
//        user.setName("杨胜文");
        List<User> result = baseDao.query(user);
        for (User item : result) {
            Log.d("Ysw", item.toString());
        }
        Log.d("Ysw", "result = " + result.size());
    }

    public void updateData(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDaoImpl.class, User.class);
        User user = new User();
        user.setPassword(String.valueOf(new Random().nextInt(10)));
        User where = new User();
        where.setName("杨胜文");
        baseDao.update(user, where);
    }

    public void deleteData(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDaoImpl.class, User.class);
        User where = new User();
        where.setName("Tlt");
        baseDao.delete(where);
    }
}