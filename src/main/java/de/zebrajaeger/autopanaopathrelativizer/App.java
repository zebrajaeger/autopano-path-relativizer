package de.zebrajaeger.autopanaopathrelativizer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;

@Slf4j
public class App {
    public static void main(String[] args) throws ParseException {
        log.info("START");
//        new Relativizer(FileNameTransformer.DEFAULT).relativize(new File("./test.pano"));
//        return;

        final Relativizer relativizer = new Relativizer(FileNameTransformer.DEFAULT);
        if (args.length == 0) {
            relativizer.relativize(new File(System.getProperty("user.dir")));
            return;
        }

        Options options = new Options();
        options.addOption("u", "user-interface", false, "Show GUI");
        options.addOption("r", "recursive", false, "Parse directories recursive");
        options.addOption("h", "help", false, "Print help");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Autopano Path Relativizer", options);
            return;
        }

        if (cmd.hasOption("u")) {
            new Ui().setVisible(true);
        } else {
            relativizer.relativizeAllArgs(cmd.getArgList());
        }
    }
}
