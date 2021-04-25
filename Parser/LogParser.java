
package Parser;

import java.util.Properties;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;



public class LogParser {

  public static void main(String[] args) {

    BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1024);
    
   // Properties props=System.getProperties();
    
    String workDir=System.getProperty("WorkDir");
    if (workDir == null)
    	workDir=Paths.get("").toAbsolutePath().toString();
    
    if (workDir.endsWith("/")) {
        workDir = workDir.substring(0, workDir.length() - 1);
    }
    
    System.out.println("Workirectory given .. " + workDir);
    Path path = Paths.get(workDir);
    boolean isDir = Files.isDirectory(path);
    if (!isDir)
    {
    	System.out.println("WorkDirectory do not exist ...exiting");
    	System.exit(1);
    }

    String inputFile=System.getProperty("InputFile");
    if (inputFile == null)
    	inputFile="inputFile.txt";
    
    inputFile=workDir+ "\\" + inputFile;
    
    System.out.println("InputFile given ..  " + inputFile );
    File tempFile=new File(inputFile);
    if (!tempFile.exists() || !tempFile.canRead())
    {
    	System.out.println("Input file does not exists or not readable ");
    	System.exit(1);
    }
    	
    
    
    String outputFile=System.getProperty("OutputFile");
    if (outputFile == null)
    	outputFile="error.json";
    
    outputFile=workDir+ "\\" + outputFile;

    System.out.println("OutputFile given .. " + outputFile);
    
    
    String wordFile=System.getProperty("AlertWordFile");
    if ( wordFile != null)
    {
    	wordFile=workDir+ "\\" + wordFile;
        System.out.println("AlertWordFile given .. " + wordFile);

    	File tempAlertFile=new File(wordFile);
        if (!tempAlertFile.exists() || !tempAlertFile.canRead())
        {
        	System.out.println("Alert file does not exists or not readable ");
        	System.exit(1);
        }
    } else {
    	System.out.println ("AlertWordFile not given, taking following default words");
    	System.out.println("error");
    	System.out.println("failed");
    	System.out.println("eror");
    	System.out.println("faled");

    	
    }
    
    System.out.println("AlertWordFile given =" + wordFile);
    
    	
    	
    ReaderThread reader = new ReaderThread(queue,workDir,inputFile,wordFile);
    WriterThread writer = new WriterThread(queue,workDir,outputFile);

    new Thread(reader).start();
    new Thread(writer).start();

  }

 }
