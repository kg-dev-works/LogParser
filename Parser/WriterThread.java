package Parser;

import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.*;
import java.text.SimpleDateFormat;
  import java.lang.reflect.Field;
 import java.util.LinkedHashMap;
 import org.json.JSONObject;
 import org.json.JSONArray;


 
 

 
public class WriterThread implements Runnable{


  Queue<Errors>  errorQueue;
  protected BlockingQueue<String> blockingQueue = null;

  public String outputFile="error.json";
  
  public WriterThread(BlockingQueue<String> blockingQueue,String workDir, String _outputFile){
    this.blockingQueue = blockingQueue;
    outputFile =_outputFile;
    errorQueue            = new LinkedList<>();
  }

  private static void makeJSONObjUnordered(JSONObject jsonObject) {
	    try {
	            Field changeMap = jsonObject.getClass().getDeclaredField("map");
	            changeMap.setAccessible(true);
	            changeMap.set(jsonObject, new LinkedHashMap<>());
	            changeMap.setAccessible(false);
	        } catch (IllegalAccessException | NoSuchFieldException e) {
	            e.printStackTrace();
	        }
	}


  public long getSecondsFromBuffer(String buffer)
  {
	  String[] line_words = buffer.split("-");

      String[] msg1 = line_words[2].split(" ");


      String dateString = line_words[0] + "-" + line_words[1] + "-" + msg1[0] + " " +msg1[1];
      long millis=0;

      try {
   	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   	   Date date = sdf.parse(dateString);
   	   millis = date.getTime();
      } catch (java.text.ParseException excpt) {
   	     excpt.printStackTrace();
      }

     return millis;
 
  }
  
  public String  getDateStringFromBuffer(String buffer)
  {
	  String[] line_words = buffer.split("-");

      String[] msg1 = line_words[2].split(" ");


      String dateString = line_words[0] + "-" + line_words[1] + "-" + msg1[0] + " " +msg1[1];
      

     return dateString;
 
  }
  public String getClassFromBuffer(String buffer)
  {
	  String[] line_words = buffer.split("-");

      String[] msg = line_words[2].split(" ");
      return msg[2];   

  }
  
  public String getErrorMsgFromBuffer(String buffer)
  {
	  String[] line_words = buffer.split("-");

      return line_words[3];


  }
  
  @Override
  public void run() {
    PrintWriter writer = null;
    try {
    	 writer = new PrintWriter(outputFile);

  
        
        while(true){
            String buffer = blockingQueue.take();
            if(buffer.equals("EOF")){
            	System.out.println("Program Finished, ERROR JSON file is available at "+ outputFile);
                break;
            }


           Errors eObj = new Errors();
           String dateString=getDateStringFromBuffer(buffer);
           long millis=getSecondsFromBuffer(buffer);
           String  className=getClassFromBuffer(buffer);
           String errorMsg=getErrorMsgFromBuffer(buffer);
           

           eObj.setDate(millis);
           eObj.setClassName(className);
           eObj.setErrorMsg(errorMsg);
           
           while (errorQueue.peek() != null)
           {
                long diff = millis - errorQueue.peek().getDate();       
                if (diff > 5000)
                	errorQueue.remove();
                else
                    break;
           }
           
           int errorCount=errorQueue.size()+1;
           
          JSONObject jobj = new JSONObject();
          makeJSONObjUnordered(jobj);
           
           
           jobj.put("errorCount", errorCount);
           JSONObject errObjects =new JSONObject();
           makeJSONObjUnordered(errObjects);

           errObjects.put("timeStamp", dateString);
           errObjects.put("className", className);
           errObjects.put("message", errorMsg);  
           JSONArray prevErrorArr=new JSONArray();
           
           
           for (Iterator<Errors> i = errorQueue.iterator(); i.hasNext();) {
        	   JSONObject prevErrorObj = new JSONObject();
               makeJSONObjUnordered(prevErrorObj);

        	   Errors item = i.next();
        	   prevErrorObj.put("timeStamp", item.getDateString());
        	   prevErrorObj.put("className", item.getClassName());
        	   prevErrorObj.put("message", item.getErrorMsg());  
        	   prevErrorArr.put (prevErrorObj);
           }
           
           if (errorQueue.size() > 0)
           errObjects.put("PreviousLogs", prevErrorArr);
           jobj.put("errors:", errObjects);
           
           errorQueue.add(eObj);

          

        
            
           
           
           writer.println(jobj.toString());
        }


    } catch (Exception e) {
        e.printStackTrace();
    }finally{
    	writer.flush();
        writer.close();
    }

  }

public class Errors {

private long _date;
private String _className;
private String _errorMsg;

public long getDate () { return _date;}
public String getErrorMsg () { return _errorMsg;}
public String getClassName () { return _className;}
public String getDateString () {
           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date(getDate());
           return df.format(currentDate);
}


public void setDate ( long d) { _date = d; }
public void setClassName ( String  s) { _className = s; }
public void setErrorMsg ( String  s) { _errorMsg = s; }

public String toString() {
           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date(getDate());

            return  df.format(currentDate) + " " + getClassName() + " ----" + getErrorMsg();

}

}

}