import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.core.io.ClassPathResource;

void main() throws Exception {
    var configFile = new ClassPathResource("generatorConfig.xml").getFile();

    var warnings = new ArrayList<String>();
    var config = new ConfigurationParser(warnings).parseConfiguration(configFile);

    var callback = new DefaultShellCallback(true);
    var myBatisGenerator = new MyBatisGenerator(config, callback, warnings);

    myBatisGenerator.generate(null);

    for (var warning : warnings) {
        IO.println("Warning: " + warning);
    }
}