package com.znv.javabean;

public class OutResult {
	private Integer releaseAuth;
	private Integer releaseResult;
	private float  totalCost;
    private float  realCost;
    private String  chargeIndex;
    private String  tollerId;
	public Integer getReleaseAuth() {
		return releaseAuth;
	}
	public void setReleaseAuth(Integer releaseAuth) {
		this.releaseAuth = releaseAuth;
	}
	public Integer getReleaseResult() {
		return releaseResult;
	}
	public void setReleaseResult(Integer releaseResult) {
		this.releaseResult = releaseResult;
	}
	public float getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}
	public float getRealCost() {
		return realCost;
	}
	public void setRealCost(float realCost) {
		this.realCost = realCost;
	}
	public String getChargeIndex() {
		return chargeIndex;
	}
	public void setChargeIndex(String chargeIndex) {
		this.chargeIndex = chargeIndex;
	}
	public String getTollerId() {
		return tollerId;
	}
	public void setTollerId(String tollerId) {
		this.tollerId = tollerId;
	}
	
}
