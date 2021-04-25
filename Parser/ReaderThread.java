
package Parser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.*;

public class ReaderThread implements Runnable{

  protected BlockingQueue<String> blockingQueue = null;
        String[] words; // Word list array

        
  public String wordFile=null;
  
  public String inputFile="inputFile.txt";
  

  public ReaderThread(BlockingQueue<String> blockingQueue,String workDir, String _inputFile, String _wordFile){
    if (_wordFile != null)
	    wordFile=_wordFile;
    inputFile=_inputFile;
	loadWords();
    this.blockingQueue = blockingQueue;
  }


public void loadWords() {
        String line = null;
        ArrayList<String> temp = new ArrayList<String>();
        if (wordFile == null)
        {
        	temp.add("error");
        	temp.add("fail");
        	temp.add("failed");
        	temp.add("faied");
        	temp.add("eror");
        	

        } else {
        try {
            FileReader fileReader = new FileReader(wordFile);
            BufferedReader buffReader = new BufferedReader(fileReader);

            while ((line = buffReader.readLine()) != null) {
                temp.add(line.trim());
            }

            buffReader.close();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        }
        words = new String[temp.size()];
        temp.toArray(words);
    }
public String autocorrect(String userWord) {

         loadWords();
         for (String  word: words)
         {
                char wordStart; // First char of word
                char userWordStart; // First char of userWord
                if (!word.isEmpty()) { // If word is NOT empty
                    wordStart = word.charAt(0);
                    userWordStart = userWord.charAt(0);

                        if (userWord.length() == word.length()) // Same length
                        {
                            if (containsAllChars(userWord, word)) // Same chars
                                return "ERROR";

                        }
                }

         }
        return userWord;
      }

 public boolean containsAllChars(String strOne, String strTwo) {
        Character[] one = strToCharArray(strOne);
        Character[] two = strToCharArray(strTwo);

        java.util.Arrays.sort(one);
        java.util.Arrays.sort(two);

        for (int i = 0; i < one.length; i++) {
            if (Search.binarySearch(two, one[i]) == -1)
                return false;
            two[i] = '0';
        }

        two = strToCharArray(strTwo);
        java.util.Arrays.sort(two);

        for (int i = 0; i < two.length; i++) {
            if (Search.binarySearch(one, two[i]) == -1)
                return false;
            one[i] = '0';
        }

        return true;
    }



  public Character[] strToCharArray(String str) {
        Character[] charArray = new Character[str.length()];
        for (int i = 0; i < str.length(); i++) {
            charArray[i] = new Character(str.charAt(i));
        }

        return charArray;
    }

public boolean isEligibleToWrite(String buffer)
{

String[] line_words = buffer.split(" ");
for (String line_word: line_words)
{
        if (autocorrect(line_word).equalsIgnoreCase("error"))
                return true;
}
return false;

}
  public void run() {
    BufferedReader br = null;
     try {
            br = new BufferedReader(new FileReader(new File(inputFile)));
            String buffer =null;
            while((buffer=br.readLine())!=null){
                if (isEligibleToWrite(buffer))
                blockingQueue.put(buffer);
            }
            blockingQueue.put("EOF");  //When end of file has been reached

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch(InterruptedException e){

        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


  }
}