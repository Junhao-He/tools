package com.znv.javabean;

public class VehicleEvent {
	private String type;
	private VehicleEventData data;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public VehicleEventData getData() {
		return data;
	}
	public void setData(VehicleEventData data) {
		this.data = data;
	}
}
