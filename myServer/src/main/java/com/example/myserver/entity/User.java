package com.example.myserver.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
//用户基本信息
import java.io.Serializable;
@Data
public class User implements Serializable {
    private String username;
    private String password;
    private String phone;
    private String email;
    private  String userid;
    private  String account;
}
    /*public User(String username,String password){
        this.username = username;
          this.password = password;

        }
    public String getAccount() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setAccount(String account) {
        this.username = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "account='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}*/
