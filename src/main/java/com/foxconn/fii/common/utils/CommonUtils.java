package com.foxconn.fii.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtils {

    public boolean checkEMailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}
