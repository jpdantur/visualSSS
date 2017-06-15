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
	    	Option[] recognized = ArgumentParser.getOptions(args);
			for (Option o: recognized) {
				System.out.println(o.getOpt()+" : "+Arrays.toString(o.getValues()));
			}
    	} catch(ParseException e) {
    		System.out.println("Invalid Arguments");
    	}
    	
    }
}
