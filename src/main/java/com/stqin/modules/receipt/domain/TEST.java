package com.stqin.modules.receipt.domain;

import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ho.yaml.Yaml;

import com.stqin.modules.receipt.PDFReceipt;

public class TEST {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		getWorkerInfo();
	}
	@SuppressWarnings("static-access")
	public static Map<String, String> getWorkerInfo() {
		try {
			Yaml yaml = new Yaml();
			URL url = PDFReceipt.class.getClassLoader().getResource("workerInfoConfig.yaml");
			if (url != null) {
				// 获取test.yaml文件中的配置数据，然后转换为obj，
				Object obj = yaml.load(new FileInputStream(url.getFile()));
				System.out.println(obj);
				// 也可以将值转换为Map
				Map map = (Map) Yaml.load(new FileInputStream(url.getFile()));
				System.out.println(map);
				List s = (List)map.get("workerInfos");
				System.out.println(s);
				// 通过map我们取值就可以了.

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
