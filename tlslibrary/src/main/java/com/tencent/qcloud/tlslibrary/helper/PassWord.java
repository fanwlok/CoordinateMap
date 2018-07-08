package com.tencent.qcloud.tlslibrary.helper;

/**
 * Created by Administrator on 2018/2/22.
 */

public class PassWord {
    public static int NumberCount(String s){
    int count = 0;
    for(int i = 0;i < s.length();i++){
        if(Character.isDigit(s.charAt(i)))
            count++;
    }
    return count;
}
    public static boolean Test(String s){
        boolean Password = false;
        for(int i = 0;i<s.length();i++){
            if(Character.isDigit(s.charAt(i))||Character.isLetter(s.charAt(i)))
                Password = true;
            else{
                Password = false;
                break;
            }
        }
        return Password;
    }
    public static boolean isequal(String password,String password1){
        return password.equals(password1);
    }
}
