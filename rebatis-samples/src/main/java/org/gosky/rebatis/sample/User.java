package org.gosky.rebatis.sample;


import io.vertx.codegen.annotations.DataObject;

import javax.persistence.Table;

/**
 * @Auther: guozhong
 * @Date: 2019-04-01 11:21
 * @Description:
 */
@Table
@DataObject
public class User {

    private Integer id;
    private String name;
    private Integer age;
    private Integer sex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }
}
