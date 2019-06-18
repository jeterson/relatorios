package com.ciclocairu.relatorios.model;

import com.ciclocairu.relatorios.enums.ParamType;

public class ReportParameters {

	public String name;
	public Object value;
	public String type;
	public String reportType;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	public Object getValue() {
		return value;
	}
	
	public ParamType getReportType() {
		return ParamType.toEnum(reportType);
	}
	
	public void setType(ParamType type) {
		this.type = type.toString();
	}
	
	public void setReportType(ParamType reportType) {
		this.reportType = reportType.toString();
	}
	
	public ParamType getType() {
		return ParamType.toEnum(type);
	}
}
