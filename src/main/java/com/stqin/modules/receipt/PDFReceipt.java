package com.stqin.modules.receipt;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

import com.stqin.modules.receipt.domain.ReceiptInfo;

public class PDFReceipt {
	public static void main(String[] args) {

		try (PDDocument document = PDDocument.load(new File("pdfs/1.pdf"))) {

			document.getClass();

			if (!document.isEncrypted()) {
				PDFTextStripper stripper = new PDFTextStripper();
				stripper.setSortByPosition(true);
				String pdfFileInText = stripper.getText(document);

				//发票信息对象
				ReceiptInfo receiptInfo = new ReceiptInfo();
				String[] lines = pdfFileInText.split("\\r?\\n");
				for (String line : lines) {
					//联通
					if(line != null && line.indexOf("中国联合网络通信有限公司重庆市分公司") >= 0) {
						receiptInfo.setCompany("中国联合网络通信有限公司重庆市分公司");
					}
					//TODO
				}
				//根据通信公司设置金额等值
				if(receiptInfo.getCompany().equals("中国联合网络通信有限公司重庆市分公司")) {
					for(String line : lines) {
						//电话号码
						if(line != null && line.indexOf("业务号码:") >= 0) {
							Matcher matcher = Pattern.compile("[1][3,4,5,7,8][0-9]{9}").matcher(line); 
					        if (matcher.find()) {
					        	receiptInfo.setPhoneNo(matcher.group());
							}
						}
						//账期
						if(line != null && line.indexOf("账期:") >= 0) {
							Matcher matcher = Pattern.compile("[0-9]{6}").matcher(line.split("账期:")[1]); 
					        if (matcher.find()) {
					        	receiptInfo.setBillDate(matcher.group());
							}
						}
						//金额
						if(line != null && line.indexOf("价税合计 (大写）") >= 0) {
							Matcher matcher = Pattern.compile("[0-9]+(.)[0-9]+").matcher(line); 
					        if (matcher.find()) {
					        	receiptInfo.setAmount(matcher.group());
							}
						}
					}
					System.out.println(receiptInfo);
				}

			}

		} catch (InvalidPasswordException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
