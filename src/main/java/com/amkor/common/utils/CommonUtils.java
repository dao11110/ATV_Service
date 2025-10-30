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

    public static String getString(String sString, int nTotalLength) {
        StringBuilder sStringBuilder = new StringBuilder(sString);
        while (sStringBuilder.length() < nTotalLength) sStringBuilder.append(" ");
        sString = sStringBuilder.toString();
        return sString;
    }

    public static String getString(int nNumber, int nTotalLength) {
        StringBuilder sNumber = new StringBuilder(Integer.toString(nNumber));
        while (sNumber.length() < nTotalLength) {
            sNumber.insert(0, "0");
        }
        return sNumber.toString();
    }

    public static String getString(long nNumber, int nTotalLength) {
        StringBuilder sNumber = new StringBuilder(Long.toString(nNumber));
        while (sNumber.length() < nTotalLength) {
            sNumber.insert(0, "0");
        }
        return sNumber.toString();
    }
}
