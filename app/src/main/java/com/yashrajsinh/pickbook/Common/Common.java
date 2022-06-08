package com.yashrajsinh.pickbook.Common;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;

public class Common {

    public static boolean validate(String email, String password) {

        boolean flag = false;

        if(!TextUtils.isEmpty(email)) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if(!TextUtils.isEmpty(password)) {
                    flag = true;
                } else {
                    flag = false;
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        return flag;
    }

    public static boolean validate(String name, String email, String password, String address) {

        boolean flag = false;

        if(!TextUtils.isEmpty(name)) {
            if(!TextUtils.isEmpty(email)) {
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if(!TextUtils.isEmpty(password)) {
                        if(!TextUtils.isEmpty(address)) {
                            flag = true;
                        } else {
                            flag = false;
                        }
                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        return flag;

    }

    public static boolean validate(String bookname, String authorName, Uri imageUri) {

        boolean flag = false;

        if(!TextUtils.isEmpty(bookname)) {
            if(!TextUtils.isEmpty(authorName)) {
                if(imageUri != null) {
                    flag = true;
                } else {
                    flag = false;
                }
            }else {
                flag = false;
            }
        }else {
            flag = false;
        }

        return flag;

    }

    public static boolean validate(String name, String email, String password, String address, String phone) {
        boolean flag = false;

        if(!TextUtils.isEmpty(name)) {
            if(!TextUtils.isEmpty(email)) {
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if(!TextUtils.isEmpty(password)) {
                        if(!TextUtils.isEmpty(address)) {
                            if(!TextUtils.isEmpty(phone)) {
                                if(android.util.Patterns.PHONE.matcher(phone).matches()) {
                                    flag = true;
                                } else {
                                    flag = false;
                                }
                            } else {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        return flag;
    }

    public static boolean validate(String email) {
        boolean flag = false;
        if(!TextUtils.isEmpty(email)) {
            if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }

}
