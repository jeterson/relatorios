package com.ciclocairu.relatorios;

public class GenerateReportException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GenerateReportException(String msg) {
		super(msg);
	}
	
	public GenerateReportException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
