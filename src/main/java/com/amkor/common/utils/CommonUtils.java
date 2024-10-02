package com.amkor.common.utils;

import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

@UtilityClass
public class CommonUtils {

    public boolean checkEMailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public <T> boolean ArrayContains(@NotNull T[] arr, @NotNull T value) {
        return Arrays.asList(arr).contains(value);
    }
}
