package com.ciclocairu.relatorios;

public interface ReportConfigurations {

	String getBasePath();
	String getGeneratedReportsBasePath();
	
	String getImagesPath();
	
	default String getTypeFileOutput() {
		return ".pdf";
	}
	
	default String getTypeFileInput() {
		return ".jrxml";
	}
	
	default String generatedJasperFileName() {
		return "report.jasper";
	}
	
	
}
