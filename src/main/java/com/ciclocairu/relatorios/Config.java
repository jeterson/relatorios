package com.ciclocairu.relatorios;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

	public static final String separator = System.getProperty("file.separator");
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);
	
	public static ReportConfigurations create() {
		LOG.info("Iniciando configuraçoes iniciais dos relatórios");
		/*String path = separator + separator + "10.69.1.13" 
					+ separator + "g$" + separator + "relatorios";*/
		ReportConfigurationsImpl config = new ReportConfigurationsImpl();
		
		String path = System.getProperty("user.home") + separator + "reports";
		String generatedBasePath = path + separator + "generated";
		
		config.setBasePath(path);
		config.setGeneratedReportsBasePath(generatedBasePath);
		config.setImagesPath(config.getBasePath() + separator + "images");
		
		createIfNotExists(config.getBasePath());
		createIfNotExists(config.getImagesPath());
		createIfNotExists(config.getGeneratedReportsBasePath());
		
		
		
		return config;
	}
	
	
	
	
	private static void createIfNotExists(String dir) {
		LOG.info("Criando diretório " + dir);
		File file = new File(dir);
		if(!file.exists()) {
			LOG.info(file.mkdirs() ? "Diretório criado" : "Diretório não foi criado");
		}else {
			LOG.info("Diretório já existe");
		}
	}
}
