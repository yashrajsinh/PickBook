package com.yashrajsinh.pickbook.Model;

public class ClientUsers {

    private String name, email, password, address, phone;
    private String uId;

    public ClientUsers() {}

    public ClientUsers(String name, String email, String password, String address, String uId, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.uId = uId;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public String getuId() {
        return uId;
    }

    public String getPhone() {
        return phone;
    }
}
