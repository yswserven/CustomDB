package com.custom.db;

import android.os.Environment;

import com.custom.bean.User;

import java.io.File;

/**
 * Created by: Ysw on 2020/1/18.
 */
public enum DataBasePathEnums {
    database("");
    private String databasePath;

    DataBasePathEnums(String databasePath) {
    }

    public String getDatabasePath() {
        UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        if (userDao != null) {
            User currentUser = userDao.getCurrentUser();
            File file = null;
            if (currentUser != null) {
                file = new File(Environment.getExternalStorageDirectory(), "update/" + currentUser.getId());
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file.getAbsolutePath() + "/login.db";
            } else {
                return file.getAbsolutePath() + "/" + "tourist" + "_login.db";
            }
        }
        return null;
    }
}