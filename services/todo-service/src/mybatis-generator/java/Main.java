import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

void main() throws Exception {

    var cfg = Path.of("services/todo-service/MyBatisGeneratorConfig.xml");
    var configFile = cfg.toFile();
    if (!configFile.exists()) {
        IO.println("Config file not found: " + configFile.getAbsolutePath());
        return;
    }

    var warnings = new ArrayList<String>();
    var cp = new ConfigurationParser(warnings);
    var config = cp.parseConfiguration(configFile);

    var callback = new DefaultShellCallback(true);
    var myBatisGenerator = new MyBatisGenerator(config, callback, warnings);

    myBatisGenerator.generate(null);

    for (var warning : warnings) {
        IO.println("Warning: " + warning);
    }

    IO.println("MyBatis generator done.");
}