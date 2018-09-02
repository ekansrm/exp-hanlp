package app;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author ekansrm
 */
public class CorpusDependencyParse {

  static class SentencesHandlerDependency implements ISentencesHandler {

    private FileOutputStream out;
    private FileOutputStream vocabOut;
    private Integer count;

    public SentencesHandlerDependency(FileOutputStream out, FileOutputStream vocabOut) {
      this.out = out;
      this.vocabOut = vocabOut;
      this.count = 0;

    }

    @Override
    public void process(List<String> sentences) throws RuntimeException{
      count += 1;
      System.out.println("Processing: " + count);
      if(null==sentences||0==sentences.size()) {
        return;
      }
      Set<String> vocab = new HashSet<>(65536);
      try {
        for(String sentence: sentences) {
          sentence = sentence.replace("|", "");
          Map<CoNLLWord, List<CoNLLWord>> wordTree = DependencyParser.parse(sentence);
          vocab.addAll(DependencyParser.vocab(wordTree));
          String line = StringUtils.join(DependencyParser.flat(wordTree), "|");
          out.write(line.getBytes());
          out.write('\n');
        }
        out.write('\n');
        vocabOut.write(StringUtils.join(vocab, "\n").getBytes());
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }

    }

  }

  static public void main(String[] args) throws IOException {
    FileInputStream in = new FileInputStream("src/main/resources/news_tensite_xml.smarty.dat");
    FileOutputStream out = new FileOutputStream("src/main/resources/data1.dat");
    FileOutputStream vocabOut = new FileOutputStream("src/main/resources/vocab.dat");
    CorpusContentExtraction.extractorSogouNewContentFast(in, new SentencesHandlerDependency(out, vocabOut));

  }
}
