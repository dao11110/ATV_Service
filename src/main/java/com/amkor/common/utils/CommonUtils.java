package com.amkor.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtils {

    public boolean checkEMailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public <T> boolean ArrayContains(T[] arr, T value) {
        for (T obj : arr) {
            if (obj.equals(value)) {
                return true;
            }
        }

        return false;
    }
}
