package com.n.yswdbdemo.bean;

import com.n.yswdbdemo.annotation.DbField;
import com.n.yswdbdemo.annotation.DbTable;

/**
 * Created by: Ysw on 2020/1/15.
 */
@DbTable("tb_user")
public class User {
    @DbField("_id")
    private Integer id;
    private String name;
    private String password;

    public User(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}
