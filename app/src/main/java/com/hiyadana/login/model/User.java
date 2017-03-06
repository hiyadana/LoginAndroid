package com.hiyadana.login.model;

/**
 * Created by cowboy on 2017-03-02.
 */

public class User {
    private String nickname;
    private String password;
    private String email;
    private String gender;
    private int birthyear;
    private String[] extra;
    private String newPassword;
    private String createdAt;
    private String token;


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthYear(int birthyear) {
        this.birthyear = birthyear;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String[] getExtra() {
        return extra;
    }

    public void setExtra(String[] extra) {
        this.extra = extra;
    }
}
