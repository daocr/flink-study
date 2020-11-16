package com.huilong.kafka.entity;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author daocr
 * @date 2020/11/16
 */
public class MockUser implements Serializable {

    private Integer id;
    private String name;
    private String email;
    private String password;

    public Integer getId() {
        return id;
    }

    public MockUser setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MockUser setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public MockUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public MockUser setPassword(String password) {
        this.password = password;
        return this;
    }
}
