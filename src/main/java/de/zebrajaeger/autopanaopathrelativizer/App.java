package de.zebrajaeger.autopanaopathrelativizer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class App {
    public static void main(String[] args) throws ParseException {
        log.info("START");

        if (args.length == 0) {
            args = new String[]{"-u"};
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
            final Relativizer relativizer = new Relativizer(FileNameTransformer.DEFAULT);
            relativizer.relativizeAllArgs(cmd.getArgList());
        }
    }
}
