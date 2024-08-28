package com.amkor.service.iService;

import org.springframework.stereotype.Service;

@Service
public interface IWriteService extends IService {
    default String getUserID(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "pruser";
                break;
            case "ATV":
                result = "MESPGMR";
                break;
        }
        return result;
    }

    default String getPasswd(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "prod0504";
                break;
            case "ATV":
                result = "gloryah";
                break;
        }
        return result;
    }
}
