package com.ciclocairu.relatorios.model;

import java.sql.Connection;
import java.util.List;

public interface ReportProperties {

	public String getFolderReportsName();
	
	public String getFileReportName();
	
	public List<ReportParameters> getReportParams();
	
	public Connection getReportConnection();
}
