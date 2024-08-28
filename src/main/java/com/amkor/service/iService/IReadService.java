package com.amkor.service.iService;

import org.springframework.stereotype.Service;

@Service
public interface IReadService extends IService {
    default String getUserID(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "pruser";
                break;
            case "ATV":
                result = "pruser";
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
                result = "prod0504";
                break;
        }
        return result;
    }
}
