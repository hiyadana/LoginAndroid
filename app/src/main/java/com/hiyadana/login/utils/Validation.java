package com.hiyadana.login.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Created by cowboy on 2017-03-02.
 */

public class Validation {
    public static boolean validateField(String name) {
        if(TextUtils.isEmpty(name))
            return false;
        else
            return true;
    }

    public static boolean validateEmail(String email) {
        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return false;
        else
            return true;
    }

    public static boolean validatePassword(String pw1, String pw2) {
        if(validateField(pw1) && validateField(pw2))
            if(pw1.equals(pw2))
                return true;
            else
                return false;
        else
            return false;
    }
}
