package com.ciclocairu.relatorios.enums;

public enum ParamType {

	DATE, STRING, INTEGER, DECIMAL;
	
	public static ParamType toEnum(String type) {
		type = type.toUpperCase();
		for(ParamType p : ParamType.values()) {
			if(p.toString().contentEquals(type)) {
				return p;
			}
		}
		return null;
	}
}
