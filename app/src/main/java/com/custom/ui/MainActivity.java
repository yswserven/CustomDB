package com.custom.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.custom.R;
import com.custom.bean.User;
import com.custom.db.BaseDao;
import com.custom.db.BaseDaoFactory;

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
        BaseDaoFactory.getInstance().getBaseDao(User.class);
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
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
        baseDao.insert(new User(1, "Ysw", "123456"));
        Toast.makeText(this, "插入成功", Toast.LENGTH_LONG).show();
    }

    public void queryData(View view) {

    }

    public void updateData(View view) {

    }

    public void deleteData(View view) {

    }
}