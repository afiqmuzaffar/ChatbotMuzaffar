package example.jbot;

import example.jbot.neuralNetWok.CampusQA;
import example.jbot.neuralNetWok.QA;
import example.jbot.neuralNetWok.Sent2Vec;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "example.jbot", "example.jbot.neuralNetWok"})
public class JBotApplication extends CampusQA implements QA {
    private MultiLayerNetwork model;
    private Sent2Vec sent2Vec;
    private LinkedList<String> aswList;
    private int numInputs;

    public String getAnswer(String text) {

        String val = sent2Vec.getMatrixString(text);
        String[] split = val.split(",", -1);
        List<Writable> ret = new ArrayList<Writable>();
        for (String s : split) {
            ret.add(new Text(s));
        }// Get the result set
        INDArray featureVector = Nd4j.create(numInputs);
        int featureCount = 0;
        for (int j = 0; j < ret.size(); j++) {
            Writable current = ret.get(j);
            double value = current.toDouble();
            featureVector.putScalar(featureCount++, value);
        }
//                System.out.println(featureVector);
        INDArray predicted = model.output(featureVector, false);
        INDArray binaryGuesses = predicted.gt(0.5);
        System.out.println(predicted);
        if (binaryGuesses.maxNumber().doubleValue() == 1) {
            for (int i = 0; i < aswList.size(); i++) {
                if (binaryGuesses.getDouble(i) == 1) return aswList.get(i);
            }
            return "Index Error";
        }
        else if (featureVector.maxNumber().doubleValue() != 0) {
            return "\"" + text +" \"  The description is not very specific, please describe in detail~";
        }
        else return "Sorry, not include yet~";
    }
    public JBotApplication() throws Exception {

    }

    /**
     * Entry point of the application. Run this method to start the sample bots,
     * but don't forget to add the correct tokens in application.properties file.
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(JBotApplication.class, args);
    }
}
