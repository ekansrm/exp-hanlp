package app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author weiming
 *
 */
public class DependencyParser {
  static private Gson gson = new GsonBuilder().create();
  static private NeuralNetworkDependencyParser parser = new NeuralNetworkDependencyParser();

  static private CoNLLWord root = null;
  static private Map<CoNLLWord, List<CoNLLWord>> wordTree = new HashMap<>(30);
  static private Queue<CoNLLWord> traversalQueue = new ConcurrentLinkedQueue<>();
  static private List<CoNLLWord> traversalQueuePending = new LinkedList<>();

  static public Map<CoNLLWord, List<CoNLLWord>> parse(String sentence) {
    parser.enableDeprelTranslator(false);
    CoNLLSentence coNLLSentence = parser.parse(sentence);
    wordTree.clear();
    for(CoNLLWord coNLLWord: coNLLSentence.getWordArray()) {
      if("HED".equals(coNLLWord.DEPREL)) {
        root = coNLLWord;
        continue;
      }
      CoNLLWord head = coNLLWord.HEAD;
      if(!wordTree.containsKey(head)) {
        wordTree.put(head, new LinkedList<>());
      }
      List<CoNLLWord> headWordChildrens = wordTree.get(head);
      headWordChildrens.add(coNLLWord);
    }

    return wordTree;

  }

  static public List<String> flat(Map<CoNLLWord, List<CoNLLWord>> wordTree) {
    List<String> rv = new LinkedList<>();
    traversalQueue.clear();
    traversalQueue.add(root);
    while (!traversalQueue.isEmpty()) {

      traversalQueuePending.clear();
      while(!traversalQueue.isEmpty()) {
        CoNLLWord e = traversalQueue.poll();
        if(e==null||!wordTree.containsKey(e)) {
          continue;
        }
        List<CoNLLWord> childrens = wordTree.get(e);
        for(CoNLLWord child: childrens) {
          rv.add(child.DEPREL);
          rv.add(child.LEMMA);
          rv.add(e.LEMMA);
          traversalQueuePending.add(child);
        }
      }
      traversalQueue.addAll(traversalQueuePending);
    }
    return rv;
  }

  static public void writeLine(List<String> flat) {
    System.out.println(String.join(",", flat));
  }

  static public void main(String[] args) {

    String sentence = "徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。";
    writeLine(flat(parse(sentence)));
  }


}
