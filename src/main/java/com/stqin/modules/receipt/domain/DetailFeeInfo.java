package com.stqin.modules.receipt.domain;

public class DetailFeeInfo {
	/** 工号 */
	private String workNo;
	/** 姓名 */
	private String name;
	/** 手机号码 */
	private String phoneNo;
	/** 报销金额 */
	private String amount;
	/** 报销月份 */
	private String month;

	public String getWorkNo() {
		return workNo;
	}

	public void setWorkNo(String workNo) {
		this.workNo = workNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

}
