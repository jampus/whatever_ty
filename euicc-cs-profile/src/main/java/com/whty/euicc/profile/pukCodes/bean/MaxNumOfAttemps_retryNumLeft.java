package com.whty.euicc.profile.pukCodes.bean;

import com.whty.euicc.profile.parent.JavaBean;
/**
 * PukCodes子元素
 * 结构类型:简单类型
 * @author Administrator
 *
 */
public class MaxNumOfAttemps_retryNumLeft extends JavaBean{
	private String tag = "82";

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "MaxNumOfAttemps_retryNumLeft [tag=" + tag + ", length="
				+ length + ", number=" + number + ", value=" + value + "]";
	}
	
	
	
	
}
