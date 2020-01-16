package com.custom.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.custom.R;
import com.custom.bean.Photo;
import com.custom.bean.User;
import com.custom.db.BaseDao;
import com.custom.db.BaseDaoFactory;
import com.custom.db.BaseDaoImpl;
import com.custom.subsqlite.BaseDaoSubFactory;
import com.custom.subsqlite.PhotoDao;
import com.custom.subsqlite.UserDao;

import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    private int REQUEST_EXTERNAL_STORAGE = 1;
    private String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private BaseDao userDao;

    private int i = 0;
    private int n = 0;

    public MainActivity() {
        userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
    }

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
        baseDao.insert(new User("1", "杨胜文", "11111111", 0));
        Toast.makeText(this, "插入成功", Toast.LENGTH_LONG).show();
    }

    public void queryData(View view) {
//        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDaoImpl.class, User.class);
//        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        BaseDao<Photo> baseDao = BaseDaoFactory.getInstance().getBaseDao(PhotoDao.class, Photo.class);

        User user = new User();
        Photo photo = new Photo();
//        user.setName("杨胜文");
        List<Photo> result = baseDao.query(photo);
        for (Photo item : result) {
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

    public void userLogin(View view) {
        User user = new User();
        user.setName("杨胜文");
        user.setPassword("123456");
        user.setId("N00" + ++i);
        userDao.insert(user);
        Log.d("Ysw", "userId = " + user.getId());
        Toast.makeText(this, "用户登录成功", Toast.LENGTH_SHORT).show();
    }

    public void subInsert(View view) {
        Photo photo = new Photo();
        photo.setPath("/data/data/" + ++n + "/my.jpg");
        photo.setTime(new Date().toString());
        PhotoDao photoDao = BaseDaoSubFactory.getInstance().getSubDao(PhotoDao.class, Photo.class);
        photoDao.insert(photo);
        Log.d("Ysw", "path = " + photo.getPath());
        Toast.makeText(this, "图片插入成功", Toast.LENGTH_SHORT).show();

    }
}