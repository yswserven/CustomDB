package com.custom.bean;

import com.custom.annotation.DbTable;

/**
 * Created by: Ysw on 2020/1/16.
 */

@DbTable("tb_photo")
public class Photo {
    private String time;
    private String path;

    public Photo() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Photo(String time, String path) {
        this.time = time;
        this.path = path;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "time='" + time + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
