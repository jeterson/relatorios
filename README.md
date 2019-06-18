# How To
###### Crie uma classe de configuração no seu aplicativo para poder iniciar o uso do relatório.
```

import com.ciclocairu.relatorios.Config;
import com.ciclocairu.relatorios.ReportConfigurations;
import com.ciclocairu.relatorios.services.ReportService;


@Configuration
public class ReportConfig {

	public static final String separator = System.getProperty("file.separator");
	
	@Bean
	public ReportConfigurations initialConfigurations() {
		return Config.create();
	}
	
	@Bean
	public ReportService createReportService() {
		return new ReportService();
	}
	
}

```

## Configurando a classe propriedades do relatório pra poder usar.
A classe deve implementar a interface `ReportProperties`

```
@Component
public class AdiantamentoRelatoriosProperties implements ReportProperties {

	private String fileReportName;
	private List<ReportParameters> parameters = new ArrayList<ReportParameters>();
	private Connection connection;
	
	@Override
	public String getFolderReportsName() {
		return "adiantamento";
	}

	@Override
	public String getFileReportName() {
		return fileReportName;
	}
	
	public void setFileReportName(String fileReportName) {
		this.fileReportName = fileReportName;
	}

	@Override
	public List<ReportParameters> getReportParams() {
		return parameters;
	}
	
	public void setParameters(List<ReportParameters> parameters) {
		this.parameters = parameters;
	}

	@Override
	public Connection getReportConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}

```


## Gerando o Relatório
###### após o relatório gerado, o caminho dele será retornado pelo método.

```

	@Autowired
	private AdiantamentoRelatoriosProperties adiantamentoRelatoriosProperties;
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;


public String gerarRelatorioRequisicao(List<ReportParameters> parametros, HttpServletResponse response) {
		try {
			adiantamentoRelatoriosProperties.setFileReportName("requisicao");
			adiantamentoRelatoriosProperties.setConnection(jdbcTemplate.getDataSource().getConnection());
			adiantamentoRelatoriosProperties.setParameters(parametros);
			String filePath = reportService.generateReport(adiantamentoRelatoriosProperties, response);
			
			return filePath;
		} catch (GenerateReportException | SQLException | IOException e) {
			LOG.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
```

## Diretório dos relatórios
Por padrão, o caminho usado pra salvar relatório é em `System.getProperty("user.home") + separator + "reports"`;
Voce pode alterar isso criando uma classe que implement `ReportConfigurations`

```
[...]

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

```

###### Agora adicionei ao seu @Bean

```
@Bean
	public ReportConfigurations initialConfigurations() {
		ReportConfigurationsImpl config = new ReportConfigurationsImpl();
    
    //configurando diretórios
    config.setBasePath(path);
		config.setGeneratedReportsBasePath(generatedBasePath);
		config.setImagesPath(config.getBasePath() + separator + "images");
    return config;
	}
```
