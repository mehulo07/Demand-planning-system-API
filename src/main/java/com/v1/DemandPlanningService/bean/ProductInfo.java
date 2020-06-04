package com.v1.DemandPlanningService.bean;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class ProductInfo implements Serializable {

	/**
	 * @author makwameh
	 */
	private static final long serialVersionUID = 8096340022868708880L;
	
	private String catalogNo;
	@NotNull
	private String productDesc;
	private String category;
	private String contract;
	
	public String getCatalogNo() {
		return catalogNo;
	}
	public void setCatalogNo(String catalogNo) {
		this.catalogNo = catalogNo;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}

	@Override
	public String toString() {
		return "ProductInfoBean [catalogNo=" + catalogNo + ", productDesc=" + productDesc + ", category=" + category
				+ ", contract=" + contract + "]";
	}
	
}
