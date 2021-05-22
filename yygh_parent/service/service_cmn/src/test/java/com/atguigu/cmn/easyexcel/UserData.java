package com.atguigu.cmn.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserData {

    @ExcelProperty(value = "用户编号", index = 0)
    public int uid;

    @ExcelProperty(value = "用户名", index = 1)
    public String uname;
}
