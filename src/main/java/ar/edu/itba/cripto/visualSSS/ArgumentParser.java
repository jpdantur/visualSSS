package ar.edu.itba.cripto.visualSSS;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ArgumentParser {
	
	public static Option[] getOptions(String[] args) throws ParseException {
		Options options = new Options();
    	OptionGroup og = new OptionGroup();
    	og.setRequired(true);
    	og.addOption(new Option("r", "Recover image"));
    	og.addOption(new Option("d","Distribute image"));
    	options.addOptionGroup(og);
    	options.addRequiredOption("secret","secret",true,"Image bmp filename");
    	options.addRequiredOption("k", "k", true, "Minimum required shades");
    	options.addOption("n", true,"Shades to distribute image");
    	options.addOption("dir",true,"Images location");
    	CommandLineParser parser = new DefaultParser();
		CommandLine line = parser.parse(options,args);
		return line.getOptions();
	}

}
