package ar.edu.itba.cripto.visualSSS;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	try {
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
			Option[] recognized = line.getOptions();
			for (Option o: recognized) {
				System.out.println(Arrays.toString(o.getValues()));
			}
    	} catch(ParseException e) {
    		System.out.println("Invalid Arguments");
    	}
    	
    }
}
