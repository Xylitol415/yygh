package com.atguigu.cmn.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.model.acl.User;

import java.util.ArrayList;
import java.util.List;

public class TestWrite {
    public static void main(String[] args) {
        String fileName = "C:\\excel\\01.xlsx";

        List<UserData> list = new ArrayList<>();

        for(int i = 1; i <= 10; i++) {
            UserData userData = new UserData();
            userData.setUid(i);
            userData.setUname("Lucy" + i);
            list.add(userData);
        }

        EasyExcel.write(fileName, UserData.class).sheet("用户信息")
                .doWrite(list);
    }
}
