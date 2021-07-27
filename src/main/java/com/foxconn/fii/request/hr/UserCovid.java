package com.foxconn.fii.request.hr;

import lombok.Data;

import java.util.Map;

@Data
public class UserCovid {
    private String empId;
    private String empNameVn;
    private String empNameCn;
    private String cft;
    private String bu;
    private String deptCode;
    private String deptName;
    private String deptDesc;

    public UserCovid(Map<String, Object> itemData){
        this.empId = (String) itemData.get("emp_no");
        this.empNameVn = (String) itemData.get("name_vn");
        this.empNameCn = (String) itemData.get("name_cn");
        this.bu = (String) itemData.get("bu");
        this.cft = (String) itemData.get("cft");
        this.deptCode = (String) itemData.get("dept_code");
        this.deptName = (String) itemData.get("dept_name");
        this.deptDesc = (String) itemData.get("department");
    }
}
