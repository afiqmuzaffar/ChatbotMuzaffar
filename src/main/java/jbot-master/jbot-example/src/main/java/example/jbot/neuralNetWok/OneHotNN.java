package example.jbot.neuralNetWok;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;


public class OneHotNN {
  private MultiLayerNetwork model;

  public OneHotNN(int numInputs) throws Exception {
    initialModel(numInputs);
  }

  public static void main(String[] args) throws Exception {
    File charFile = new File("C:/Users/aishatulhusna/Documents/GitHub/ChatbotMuzaffar/src/main/resources/singleCharVec.txt");
    FileReader cfIn = new FileReader(charFile);
    BufferedReader bcfr = new BufferedReader(cfIn);
    LineNumberReader lnr = new LineNumberReader(bcfr);
    lnr.skip(charFile.length());
    new OneHotNN(lnr.getLineNumber());
  }

  public MultiLayerNetwork getModel() {
    return model;
  }

  public void initialModel(int numInputs) throws Exception {
    int seed = 123;
    File charFile = new File("C:/Users/aishatulhusna/Documents/GitHub/ChatbotMuzaffar/src/main/resources/answer.txt");
    FileReader cfIn = new FileReader(charFile);
    BufferedReader bcfr = new BufferedReader(cfIn);
    LineNumberReader lnr = new LineNumberReader(bcfr);
    // Position to the last line
    lnr.skip(charFile.length());
    // Get the number of result sets
    int numOutputs = lnr.getLineNumber() + 1;
    double learningRate = 0.1;
    int batchSize = 1000;
    int nEpochs = 1000;
    int numHiddenNodes = 240;
//                int numHiddenNodes = numOutputs + numOutputs/4;

    // Load the training data:
    RecordReader rr = new CSVRecordReader();
//        rr.initialize(new FileSplit(new File("/home/casey/projects/up/qa-bot-dl4j-nn/src/main/resources/classification/linear_data_train.csv")));
    rr.initialize(new FileSplit(new File("C:/Users/aishatulhusna/Documents/GitHub/ChatbotMuzaffar/src/main/resources/w2v_tr.txt")));
    DataSetIterator trainIter = new RecordReaderDataSetIterator(rr, batchSize, 0, numOutputs);

    // Load the test/evaluation data:
    RecordReader rrTest = new CSVRecordReader();
    rrTest.initialize(new FileSplit(new File("C:/Users/aishatulhusna/Documents/GitHub/ChatbotMuzaffar/src/main/resources/w2v_test.txt")));
    DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest, batchSize, 0, numOutputs);

    // build neural network
    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed).iterations(1)
        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).regularization(true)
        .l2(0.1).updater(Updater.NESTEROVS).momentum(0.9) //specify the rate of change of the learning rate.
        .weightInit(WeightInit.XAVIER).activation("relu").list()
        .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes).learningRate(learningRate).build())
        .layer(1, new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).learningRate(learningRate).build())
        .layer(2, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD).weightInit(WeightInit.XAVIER).activation("softmax").nIn(numHiddenNodes).nOut(numOutputs).build()).pretrain(false).backprop(true).build();
//                UIServer uiServer = UIServer.getInstance();
//                StatsStorage statsStorage = new InMemoryStatsStorage();
//                uiServer.attach(statsStorage);
    // Network training visualization
    MultiLayerNetwork model = new MultiLayerNetwork(conf);
    model.init();
    model.setListeners(new ScoreIterationListener(1));
//               model.setListeners(new StatsListener(statsStorage));

    // Initialize the user interface backend
    for (int n = 0; n < nEpochs; n++) {
      model.fit(trainIter);
    }

    System.out.println("Evaluate model....");
    Evaluation eval = new Evaluation(numOutputs);
    while (testIter.hasNext()) {
      DataSet t = testIter.next();
      INDArray features = t.getFeatureMatrix();
      INDArray lables = t.getLabels();
      INDArray predicted = model.output(features, false);
      System.out.println(predicted);
      eval.eval(lables, predicted);
    }
    this.model = model;
    //Print the evaluation statistics
               System.out.println(eval.stats());
  }
}
