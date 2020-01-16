package com.custom.subsqlite;

import com.custom.bean.User;
import com.custom.db.BaseDaoFactory;

import java.io.File;

/**
 * Created by: Ysw on 2020/1/16.
 */
public enum PrivateDataBaseEnums {
    database("");
    private String value;

    PrivateDataBaseEnums(String value) {

    }

    public String getValue() {
        UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        if (userDao != null) {
            User currentUser = userDao.getCurrentUser();
            if (currentUser != null) {
                File file = new File("/data/data/com.custom");
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file.getAbsolutePath() + "/" + currentUser.getId() + "_login.db";
            }
        }
        return null;
    }
}
