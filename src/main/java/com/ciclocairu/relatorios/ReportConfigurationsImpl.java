package com.ciclocairu.relatorios;

public class ReportConfigurationsImpl implements ReportConfigurations {

	private String basePath;
	private String generatedReportsBasePath;
	private String imagesPath;
	
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public void setGeneratedReportsBasePath(String generatedReportsBasePath) {
		this.generatedReportsBasePath = generatedReportsBasePath;
	}


	@Override
	public String getBasePath() {
		return this.basePath;
	}



	@Override
	public String getGeneratedReportsBasePath() {
		return generatedReportsBasePath;
	}

	public void setImagesPath(String imagesPath) {
		this.imagesPath = imagesPath;
	}
	
	@Override
	public String getImagesPath() {
		return imagesPath;
	}

}
