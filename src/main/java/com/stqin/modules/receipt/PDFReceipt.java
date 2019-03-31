package com.stqin.modules.receipt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ho.yaml.Yaml;

import com.stqin.modules.receipt.domain.DetailFeeInfo;
import com.stqin.modules.receipt.domain.ReceiptInfo;

public class PDFReceipt {
	/**
	 * 发票小工具操作说明：
	 * 1.维护配置文件workerInfoConfig.yaml（参考例子）
	 * 2.把发票pdf文件放入pdfs文件夹下（目前仅支持联通/移动/电信）
	 * 3.run main方法，查看excel文件夹下生成的xls文件
	*/
	public static void main(String[] args) throws Exception {
		// 获取pdf所有人的报销金额
		List<DetailFeeInfo> templateList = new ArrayList<DetailFeeInfo>();
		// 读取CDP人员配置
		Map<String, DetailFeeInfo> infoMap = getWorkerInfo();
		// 读取发票pdf
		List<File> list = getFiles("pdfs/");
		for (File file : list) {
			try (PDDocument document = PDDocument.load(file)) {
				document.getClass();
				if (!document.isEncrypted()) {
					PDFTextStripper stripper = new PDFTextStripper();
					stripper.setSortByPosition(true);
					String pdfFileInText = stripper.getText(document);

					// 发票信息对象
					ReceiptInfo receiptInfo = new ReceiptInfo();
					String[] lines = pdfFileInText.split("\\r?\\n");
					for (String line : lines) {
						// 联通
						if (line != null && line.indexOf("中国联合网络通信有限公司重庆市分公司") >= 0) {
							receiptInfo.setCompany("中国联合网络通信有限公司重庆市分公司");
						}
						// 电信
						if (line != null && line.indexOf("中国电信股份有限公司重庆分公司") >= 0) {
							receiptInfo.setCompany("中国电信股份有限公司重庆分公司");
						}
						// 移动
						if (line != null && line.indexOf("中国移动通信集团") >= 0) {
							receiptInfo.setCompany("中国移动通信集团");
						}
					}
					// 根据通信公司设置金额等值
					if (receiptInfo.getCompany().equals("中国联合网络通信有限公司重庆市分公司")) {
						for (String line : lines) {
							// 电话号码
							if (line != null && line.indexOf("业务号码:") >= 0) {
								Matcher matcher = Pattern.compile("[1][3,4,5,7,8][0-9]{9}").matcher(line);
								if (matcher.find()) {
									receiptInfo.setPhoneNo(matcher.group());
								}
							}
							// 账期
							if (line != null && line.indexOf("账期:") >= 0) {
								Matcher matcher = Pattern.compile("[0-9]{6}").matcher(line.split("账期:")[1]);
								if (matcher.find()) {
									receiptInfo.setBillDate(matcher.group());
								}
							}
							// 金额
							if (line != null && line.indexOf("价税合计 (大写）") >= 0) {
								Matcher matcher = Pattern.compile("[0-9]+(.)[0-9]+").matcher(line);
								if (matcher.find()) {
									receiptInfo.setAmount(matcher.group());
								}
							}
						}
					}
					if (receiptInfo.getCompany().equals("中国电信股份有限公司重庆分公司")) {
						for (String line : lines) {
							// 电话号码
							if (line != null && line.indexOf("号码:") >= 0) {
								Matcher matcher = Pattern.compile("[1][3,4,5,7,8][0-9]{9}").matcher(line);
								Matcher matcher1 = Pattern.compile("[0-9]{3}-[0-9]{11}").matcher(line);
								if (matcher.find()) {
									receiptInfo.setPhoneNo(matcher.group());
								}
								if (matcher1.find()) {
									receiptInfo.setPhoneNo(matcher1.group());
								}
							}
							// 账期
							if (line != null && line.indexOf("账期:") >= 0) {
								Matcher matcher = Pattern.compile("[0-9]{6}").matcher(line.split("账期:")[1]);
								if (matcher.find()) {
									receiptInfo.setBillDate(matcher.group());
								}
							}
							// 金额
							if (line != null && line.indexOf("价税合计 (大写）") >= 0) {
								Matcher matcher = Pattern.compile("[0-9]+(.)[0-9]+").matcher(line);
								if (matcher.find()) {
									receiptInfo.setAmount(matcher.group());
								}
							}
						}
					}
					if (receiptInfo.getCompany().equals("中国移动通信集团")) {
						for (String line : lines) {
							// 电话号码
							if (line != null && line.indexOf("付费号码：") >= 0) {
								Matcher matcher = Pattern.compile("[1][3,4,5,7,8][0-9]{9}")
										.matcher(line.split("付费号码：")[1]);
								if (matcher.find()) {
									receiptInfo.setPhoneNo(matcher.group());
								}
							}
							// 账期
							if (line != null && line.indexOf("*电信服务*") >= 0) {
								Matcher matcher = Pattern.compile("[0-9]{6}").matcher(line);
								if (matcher.find()) {
									receiptInfo.setBillDate(matcher.group());
								}
							}
							// 金额
							if (line != null && line.indexOf("价税合计(大写)") >= 0) {
								Matcher matcher = Pattern.compile("[0-9]+(.)[0-9]+").matcher(line);
								if (matcher.find()) {
									receiptInfo.setAmount(matcher.group());
								}
							}
						}
					}
					// 将所有的pdf组装成一个list
					DetailFeeInfo detail = new DetailFeeInfo();
					detail.setAmount(receiptInfo.getAmount());
					detail.setMonth(receiptInfo.getBillDate());
					detail.setPhoneNo(receiptInfo.getPhoneNo());
					if (infoMap.get(receiptInfo.getPhoneNo()) != null) {
						detail.setName(infoMap.get(receiptInfo.getPhoneNo()).getName());
						detail.setWorkNo(infoMap.get(receiptInfo.getPhoneNo()).getWorkNo());
					}
					templateList.add(detail);
				}

			} catch (InvalidPasswordException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		String month = templateList.get(0).getMonth().substring(4, 5);
		@SuppressWarnings("resource")
		Workbook wb = new HSSFWorkbook(); // 定义一个新的工作簿
		Sheet sheet = wb.createSheet("CDP发票"); // 创建第一个Sheet页
		sheet.setDefaultColumnWidth(20);
		Row row0 = sheet.createRow(0); // 创建一个行
		row0.setHeightInPoints(20);// 设置这一行的高度
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 设置单元格垂直方向对其方式
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		sheet.setDefaultColumnStyle(0, cellStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, // 起始行
				0, // 结束行
				0, // 其实列
				4 // 结束列
		));
		row0.createCell(0).setCellValue("重庆研发中心移动、电信、联通通讯费明细表");

		Row row1 = sheet.createRow(1);
		row1.createCell(0).setCellValue("部门：");
		row1.createCell(1).setCellValue("航空产品研发部");
		row1.createCell(3).setCellValue("项目组：");
		row1.createCell(4).setCellValue("CDP");

		Row row2 = sheet.createRow(2);
		row2.createCell(4).setCellValue("单位：元");

		Row row3 = sheet.createRow(3);
		row3.createCell(0).setCellValue("序号");
		row3.createCell(1).setCellValue("工号");
		row3.createCell(2).setCellValue("姓名");
		row3.createCell(3).setCellValue("手机号码");
		row3.createCell(4).setCellValue(month + "月报销金额");

		double amountTotal = 0;
		for (int i = 0; i < templateList.size(); i++) {// 从第4行开始
			Row row = sheet.createRow(i + 4);
			row.createCell(0).setCellValue(i + 1);
			row.createCell(1).setCellValue(templateList.get(i).getWorkNo());
			row.createCell(2).setCellValue(templateList.get(i).getName());
			row.createCell(3).setCellValue(templateList.get(i).getPhoneNo());
			row.createCell(4).setCellValue(templateList.get(i).getAmount());
			amountTotal += Double.parseDouble(templateList.get(i).getAmount());
			// 合计金额
			if (i == templateList.size() - 1) {
				Row row5 = sheet.createRow(i + 5);
				row5.createCell(0).setCellValue("合计");
				row5.createCell(4).setCellValue(amountTotal);
			}
		}

		FileOutputStream fileOut = new FileOutputStream("excel/部门通讯费明细表" + month + "月.xls");
		wb.write(fileOut);
		fileOut.close();
	}

	public static List<File> getFiles(String path) throws Exception {
		// 目标集合fileList
		List<File> fileList = new ArrayList<File>();
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File fileIndex : files) {
				// 如果这个文件是目录，则进行递归搜索
				if (fileIndex.isDirectory()) {
					getFiles(fileIndex.getPath());
				} else {
					// 如果文件是普通文件，则将文件句柄放入集合中
					fileList.add(fileIndex);
				}
			}
		}
		return fileList;
	}

	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	public static Map<String, DetailFeeInfo> getWorkerInfo() {
		Map<String, DetailFeeInfo> workerInfoMap = new HashMap<String, DetailFeeInfo>();
		try {
			URL url = PDFReceipt.class.getClassLoader().getResource("workerInfoConfig.yaml");
			if (url != null) {
				Map map = (Map) Yaml.load(new FileInputStream(url.getFile()));
				List<Map> list = (List) map.get("workerInfos");
				for (Map m : list) {
					DetailFeeInfo detail = new DetailFeeInfo();
					detail.setWorkNo(((Integer) m.get("workerNo")).toString());
					detail.setName((String) m.get("name"));
					double phone = (double) m.get("phoneNo");
					BigDecimal phoneNo = new BigDecimal(phone);
					detail.setPhoneNo(phoneNo.toPlainString());
					workerInfoMap.put(phoneNo.toPlainString(), detail);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workerInfoMap;
	}

}
