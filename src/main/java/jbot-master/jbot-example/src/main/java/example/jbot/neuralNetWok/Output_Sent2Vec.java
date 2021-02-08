package example.jbot.neuralNetWok;
import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
public class Output_Sent2Vec {
  public static void main(String[] args) throws Exception {
    //Please set up the training-question sequence.txt file in advance
    outputCharVec();
    File charFile = new File("C:/Users/zeke1/Documents/GitHub/ChatbotMuzaffar/src/main/resources/singleCharVec.txt");
    File inFileTe = new File("C:/Users/zeke1/Documents/GitHub/ChatbotMuzaffar/src/main/resources/training.txt");
    File outFileTe = new File("C:/Users/zeke1/Documents/GitHub/ChatbotMuzaffar/src/main/resources/w2v_test.txt");
    File inFileTr = new File("C:/Users/zeke1/Documents/GitHub/ChatbotMuzaffar/src/main/resources/validation.txt");
    File outFileTr = new File("C:/Users/zeke1/Documents/GitHub/ChatbotMuzaffar/src/main/resources/w2v_tr.txt");
    File inFileVa = new File("C:/Users/zeke1/Documents/GitHub/ChatbotMuzaffar/src/main/resources/test.txt");
    File outFileVa = new File("C:/Users/zeke1/Documents/GitHub/ChatbotMuzaffar/src/main/resources/w2v_val.txt");
    outputVec(charFile, inFileTr, outFileTr);
    outputVec(charFile, inFileVa, outFileVa);
    outputVec(charFile, inFileTe, outFileTe);
  }
  private static void outputCharVec() throws Exception {
    HashSet<Character> set = new HashSet<Character>();
    File file = new File("C:/Users/zeke1/Documents/GitHub/ChatbotMuzaffar/src/main/resources/validation.txt");
    File charFile = new File("C:/Users/zeke1/Documents/GitHub/ChatbotMuzaffar/src/main/resources/singleCharVec.txt");
    FileReader fIn = new FileReader(file);
    BufferedReader bfr = new BufferedReader(fIn);
    FileWriter cfOut = new FileWriter(charFile);
    BufferedWriter bcfw = new BufferedWriter(cfOut);
    String str;
    while ((str = bfr.readLine()) != null) {
      char[] x = str.toCharArray();
      for (char xx : x) {
        set.add(xx);
      }
    }
    set.remove('0');
    set.remove('1');
    set.remove('2');
    set.remove('3');
    set.remove('4');
    set.remove('5');
    set.remove('6');
    set.remove('7');
    set.remove('8');
    set.remove('9');
    set.remove(' ');
    for (char xx : set) {
      bcfw.write(xx + "\n");
    }
    bcfw.flush();
    bfr.close();
    bcfw.close();
    FileReader cfIn = new FileReader(charFile);
    BufferedReader bcfr = new BufferedReader(cfIn);
    LineNumberReader lnr = new LineNumberReader(bcfr);
    lnr.skip(charFile.length());
    System.out.println(lnr.getLineNumber());
    bcfr.close();
  }
  private static void outputVec(File charFile, File in, File out) throws Exception {
    LinkedList<Character> list = new LinkedList<Character>();
    FileReader fReader = new FileReader(charFile);
    BufferedReader bfr = new BufferedReader(fReader);
    FileReader fReaderQ = new FileReader(in);
    BufferedReader bfrQ = new BufferedReader(fReaderQ);
    FileWriter fWriter = new FileWriter(out);
    BufferedWriter bfw = new BufferedWriter(fWriter);
    String str;
    while ((str = bfr.readLine()) != null) {
      char[] s = str.toCharArray();
      list.add(s[0]);
    }
    while ((str = bfrQ.readLine()) != null) {
      //Map each line of text to a word vector
      int answer = Integer.parseInt(str.substring(str.length() - 2, str.length()).trim()) - 1;
      str = str.substring(0, str.length() - 2);
      int[] vector = new int[list.size()];
      char[] s = str.toCharArray();
      for (char c : s) {
        int index = list.indexOf(c);
        if (index != -1) vector[index] = 1;
      }
      str = answer + "";
      for (int n : vector) {
        str += "," + n;
      }
      bfw.write(str + "\n");
    }
    bfw.flush();
    bfw.close();
    bfr.close();
    bfrQ.close();
  }
}