package com.stqin.modules.receipt.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TEST {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(Pattern.matches("[0-9]{11}", "名              称     ： 中国联合网络通信有限公司重庆市分公司 业务号码:13290045577; 账期:201902;"));
		
		System.out.println(Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$").matcher("名              称     ： 中国联合网络通信有限公司重庆市分公司 业务号码:13290045577; 账期:201902;").matches());
	
		String str = "价税合计 (大写） 壹佰肆拾元壹角 （小写） ￥ 140.10";
//		String str1 = str.split("账期:")[1];
        String regex = "[0-9]+(.)[0-9]+";
        Pattern p = Pattern.compile(regex); 
        Matcher matcher = p.matcher(str);
        if (matcher.find()) {
			System.out.println(matcher.group());
		}
	}

}
