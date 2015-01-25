package my.test;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.CommandRunner;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.naming.NamingException;

public class GlassFishUtils {

    private static final Logger logger =
            Logger.getLogger(GlassFishUtils.class.getName());

    public static GlassFishUtils instance = null;

    public final GlassFish glassfish;
    public final String appName;
    public final Deployer deployer;

    public GlassFishUtils(Path testHome)
            throws IOException, NamingException, GlassFishException {
        Path path;
        Path egfHome = Paths.get(testHome.toString(), "test-emb", "target", "glassfish");
        egfHome.toFile().mkdir();

        // Configure root logger
        LogManager logManager = LogManager.getLogManager();
        logManager.reset();
        Logger rootLogger = Logger.getLogger("");
        path = Paths.get(egfHome.toString(), "embedded-server.log");
        FileHandler fileHandler = new FileHandler(path.toString());
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.INFO);
        rootLogger.addHandler(fileHandler);
        rootLogger.setLevel(Level.INFO);

        // Start GlassFish
        GlassFishProperties gf = new GlassFishProperties();
        gf.setProperty("glassfish.embedded.tmpdir", egfHome.toString());
        glassfish = GlassFishRuntime.bootstrap().newGlassFish(gf);
        glassfish.start();

        glassfish.stop();
        glassfish.start();

        // Add resources
        CommandRunner runner = glassfish.getCommandRunner();
        CommandResult result;

        path = Paths.get(testHome.toString(), "test-emb", "src", "main", "resources", "password.txt");
        result = runner.run("create-file-user",
                "--groups=user_group",
                "--passwordfile=" + path.toString(),
                "user");
        File f = path.toFile();
        logger.info(result.getOutput());

        // Deploy application
        deployer = glassfish.getDeployer();
        path = Paths.get(testHome.toString(), "test-ear", "target", "test-ear.ear");
        appName = deployer.deploy(path.toFile());
        logger.info("Application name is " + appName);
    }

    public static void construct(Path testHome) {
        if (instance != null) {
            GlassFishUtils.destroy();
        }
        try {
            instance = new GlassFishUtils(testHome);
        } catch (IOException | NamingException | GlassFishException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void destroy() {
        try {
            instance.deployer.undeploy(instance.appName);
            instance.glassfish.stop();
            instance.glassfish.dispose();
            instance = null;
        } catch (GlassFishException ex) {
            throw new RuntimeException(ex);
        }
    }

}
