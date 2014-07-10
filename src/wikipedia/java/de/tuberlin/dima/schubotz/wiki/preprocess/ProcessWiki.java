package de.tuberlin.dima.schubotz.wiki.preprocess;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tuberlin.dima.schubotz.wiki.WikiProgram;
import eu.stratosphere.api.common.io.FileOutputFormat;
import eu.stratosphere.api.java.DataSet;
import eu.stratosphere.api.java.ExecutionEnvironment;
import eu.stratosphere.api.java.io.TextInputFormat;
import eu.stratosphere.api.java.operators.DataSource;
import eu.stratosphere.api.java.typeutils.BasicTypeInfo;
import eu.stratosphere.configuration.Configuration;
import eu.stratosphere.core.fs.FileInputSplit;
import eu.stratosphere.core.fs.FileSystem.WriteMode;
import eu.stratosphere.core.fs.Path;

public class ProcessWiki {
	private static final Log LOG = LogFactory.getLog(ProcessWiki.class);
		
	static int noSubTasks;
	static String wikiInput;
	static String wikiQueryInput;
	static String latexWikiMapOutput;
	static String numWikiOutput;
	static boolean debug;
	
	static ExecutionEnvironment env;
	
	public static String STR_SPLIT = WikiProgram.STR_SPLIT;
	public static Pattern WORD_SPLIT = WikiProgram.WORD_SPLIT;
	
	
	public static void parseArgs(String[] args) {
		noSubTasks = (args.length > 0 ? Integer.parseInt( args[0] )
			: 16);
		wikiInput = (args.length > 1 ? args[1]
			: "file:///mnt/ntcir-math/testdata/augmentedWikiDump.xml");
		wikiQueryInput = (args.length > 2 ? args[2]
			: "file:///mnt/ntcir-math/queries/wikiQuery.xml");
		latexWikiMapOutput = (args.length > 3 ? args[3]
			: "file:///mnt/ntcir-math/queries/latexWikiMap.csv");
		numWikiOutput = (args.length > 4 ? args[4]
			: "file:///mnt/ntcir-math/queries/numWiki.txt");
		debug = (args.length > 5 ? 
					(args[5].equals("debug") ? true : false)
				: false);
		
	}
	public static void main(String[] args) throws Exception {
		parseArgs(args);
		try {
			ConfigurePlan();
		} catch (IOException e) {
			LOG.fatal("IO exception", e);
		} finally {
			System.exit(1);
		}
		env.setDegreeOfParallelism(noSubTasks);
		env.execute("MathosphereWiki Process Data");
		System.exit(1);
	}
	public static ExecutionEnvironment getExecutionEnvironment() {
		return env;
	}
	public static void ConfigurePlan() throws IOException, URISyntaxException {
		env = ExecutionEnvironment.getExecutionEnvironment();
		Configuration config = new Configuration();
		
		//first page starts at 2377 bytes in
		TextInputFormat formatWiki = new TextInputFormat(new Path(wikiInput));
		formatWiki.setDelimiter("</page>"); //this will leave a null doc at the end and a useless doc at the beginning. also, each will be missing a </page>
		DataSet<String> rawWikiText = new DataSource<>( env, formatWiki, BasicTypeInfo.STRING_TYPE_INFO );
		rawWikiText.writeAsText(latexWikiMapOutput, WriteMode.OVERWRITE); 
	}
}
