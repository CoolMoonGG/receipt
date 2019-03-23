package com.stqin.modules.receipt.domain;

public class ReceiptInfo {
	/** 电话公司 */
	private String company;
	/** 电话号码 */
	private String phoneNo;
	/** 发票金额 */
	private String amount;
	/** 账期 */
	private String billDate;

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

}
