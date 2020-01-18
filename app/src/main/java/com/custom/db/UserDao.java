package com.custom.db;

import com.custom.bean.User;

import java.util.List;

/**
 * Created by: Ysw on 2020/1/18.
 */
public class UserDao extends BaseDao<User> {
    @Override
    public long insert(User entity) {
        List<User> list = query(new User());
        User where;
        for (User user : list) {
            where = new User();
            where.setId(user.getId());
            user.setStatus(0);
            update(user, where);
        }
        entity.setStatus(1);
        return super.insert(entity);
    }


    /**
     * 获取当前的用户
     *
     * @author Ysw created at 2020/1/18 1:56
     */
    public User getCurrentUser() {
        User user = new User();
        user.setStatus(1);
        List<User> list = query(user);
        for (User u : list) {
            Integer status = u.getStatus();
            if (status != null && status == 1) {
                return u;
            }
        }
        return null;
    }
}
