package com.fanweilin.coordinatemap.DataModel;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public class Register {


    private String message;
    private String username;
    private String password;
    private Integer id;
    private Integer vip;
    private int code ;//200 成功，400未登录
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }



    public Integer getVip() {
        return vip;
    }

    public void setVip(Integer vip) {
        this.vip = vip;
    }
}
