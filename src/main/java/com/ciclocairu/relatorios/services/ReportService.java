package com.ciclocairu.relatorios.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ciclocairu.relatorios.GenerateReportException;
import com.ciclocairu.relatorios.ReportConfigurations;
import com.ciclocairu.relatorios.enums.ParamType;
import com.ciclocairu.relatorios.model.ReportParameters;
import com.ciclocairu.relatorios.model.ReportProperties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

@Service

public class ReportService {

	public static final String separator = System.getProperty("file.separator");
	private static final Logger LOG = LoggerFactory.getLogger(ReportService.class);

	@Autowired
	private ReportConfigurations config;


	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	public String generateReport(ReportProperties properties) {
		return generateReport(properties, null, null);
	}

	public String generateReport(ReportProperties properties, List<?> data) {
		return generateReport(properties, data, null);
	}

	public String generateReport(ReportProperties properties, HttpServletResponse response) {
		return generateReport(properties, null, response);
	}

	public String generateReport(ReportProperties properties, List<?> data, HttpServletResponse response) {

		Connection conn = properties.getReportConnection();
		try {
			Map<String, Object> params = toParamsMap(properties.getReportParams());

			String path = config.getBasePath() 
					+ separator + properties.getFolderReportsName() 
					+ separator + properties.getFileReportName() 
					+ config.getTypeFileInput();

			String pathCompiledJasper = config.getGeneratedReportsBasePath() 
					+ separator + properties.getFolderReportsName();


			createIfNotExists(pathCompiledJasper);
			createIfNotExists(config.getBasePath() 
					+ separator + properties.getFolderReportsName());

			File file = new File(path);
			if(!file.exists()) {
				throw new IOException("Arquivo de relatório não existe em " + path);
			}
			//carregar relatorio em memoria
			InputStream reportStream = new FileInputStream(file);

			//salva relatorio compilado no disco
			JasperReport jasper =  JasperCompileManager.compileReport(reportStream);
			JRSaver.saveObject(jasper, pathCompiledJasper + separator + config.generatedJasperFileName());
			params.put("CONTEXT", config.getBasePath());
			params.put("imagesPath", config.getImagesPath());

			//gera relatório
			JasperPrint jasperPrint;
			if(data == null)
				jasperPrint= JasperFillManager.fillReport(jasper, params, conn);
			else
				jasperPrint = JasperFillManager.fillReport(jasper, params, new JRBeanCollectionDataSource(data));


			//Converte em PDF
			String generatedpath = exportPdf(properties, jasperPrint);

			// coloca o pdf na resposta da requisição, se ela for passada
			if(response != null) {


				File output = new File(generatedpath);

				try(InputStream is = new FileInputStream(output);OutputStream out = response.getOutputStream()){
					response.reset();
					response.setContentType("application/pdf");
					response.setContentLength((int) output.length());		    	
					response.addHeader("Access-Control-Allow-Origin", "*");
					response.addHeader("Content-Disposition", "inline; filename="+properties.getFileReportName()+".pdf");
					IOUtils.copy(is, out);
					out.flush();


				}finally {

					if(config.deleteAfterDownload())
						file.delete();


				}
			}
			return generatedpath;
		}catch (JRException | IOException e) {
			throw new GenerateReportException(e.getMessage(), e.getCause());
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				LOG.error(e.getMessage());
			}
		}



	}

	private String exportPdf(ReportProperties properties, JasperPrint jasperPrint) throws JRException {
		JRPdfExporter exporter = new JRPdfExporter();	
		String reportName = properties.getFileReportName() + System.currentTimeMillis() + config.getTypeFileOutput();
		String pathGenerated = config.getGeneratedReportsBasePath() 
				+ separator + properties.getFolderReportsName() 
				+ separator + reportName;

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(
				new SimpleOutputStreamExporterOutput(pathGenerated));

		SimplePdfReportConfiguration reportConfig
		= new SimplePdfReportConfiguration();
		//reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);

		SimplePdfExporterConfiguration exportConfig
		= new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("Ciclo Cairu Report App");				

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);

		exporter.exportReport();

		return pathGenerated;
	}

	private Map<String, Object> toParamsMap(List<ReportParameters> parameters) {
		Map<String, Object> params = new HashMap<String, Object>();

		for(ReportParameters p : parameters) {
			setToMapTarget(p, params);
		}

		return params;
	}

	private Date toDate(Object date) {
		try {
			Date dt = dateFormat.parse(String.valueOf(date));
			return dt;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isDate(Object date) {
		try {
			toDate(String.valueOf(date));
			return true;
		}catch(Exception e){
			return false;
		}
	}

	private Integer toInt(Object value) {
		try {
			Integer v = Integer.parseInt(String.valueOf(value));
			return v;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isInt(Object value) {
		try {
			toInt(value);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	private Double toDecimal(Object value) {
		try {
			Double v = Double.parseDouble(String.valueOf(value));
			return v;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isDecimal(Object value) {
		try {
			toDecimal(value);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	public String toStr(Object value) {
		return String.valueOf(value);
	}

	private void setToMapTarget(ReportParameters param, Map<String, Object> target) {
		if(param.getType() == ParamType.DATE) {

			if(param.getReportType() == ParamType.DATE) {
				if(isDate(param.getValue().toString()))
					target.put(param.getName(), toDate(param.getValue()));
				else
					target.put(param.getName(), null);
			}else {
				if(isDate(param.getValue().toString()))
					target.put(param.getName(), toStr(param.getValue()));
				else
					target.put(param.getName(), null);

			}
		}else if(param.getType() == ParamType.INTEGER) {

			if(isInt(param.getValue())) {
				target.put(param.getName(), toInt(param.getValue()));
			}else {
				target.put(param.getName(), null);
			}
		}else if(param.getType() == ParamType.DECIMAL) {
			if(isDecimal(param.getValue())) {
				target.put(param.getName(), toDecimal(param.getValue()));
			}else {
				target.put(param.getName(), null);
			}
		}else if(param.getType() == ParamType.STRING) {
			target.put(param.getName(), toStr(param.getValue()));
		}
	}

	private void createIfNotExists(String dir) {
		LOG.info("Criando diretório " + dir);
		File file = new File(dir);
		if(!file.exists()) {
			LOG.info(file.mkdirs() ? "Diretório criado" : "Diretório não foi criado");
		}else {
			LOG.info("Diretório já existe");
		}
	}
}
