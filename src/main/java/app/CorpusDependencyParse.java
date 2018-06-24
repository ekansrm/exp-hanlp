package app;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * @author ekansrm
 */
public class CorpusDependencyParse {

  static class SentencesHandlerDependency implements ISentencesHandler {

    private FileOutputStream out;
    private Integer count;

    public SentencesHandlerDependency(FileOutputStream out) {
      this.out = out;
      this.count = 0;

    }

    @Override
    public void process(List<String> sentences) throws RuntimeException{
      count += 1;
      System.out.println("Processing: " + count);
      if(null==sentences||0==sentences.size()) {
        return;
      }
      try {
        for(String sentence: sentences) {
          String line = StringUtils.join(DependencyParser.flat(DependencyParser.parse(sentence)), "/");
          out.write(line.getBytes());
          out.write('\n');
        }
        out.write('\n');
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }

    }

  }

  static public void main(String[] args) throws IOException {
    FileInputStream in = new FileInputStream("src/main/resources/news_tensite_xml.smarty.dat");
    FileOutputStream out = new FileOutputStream("src/main/resources/data1.dat");
    CorpusContentExtraction.extractorSogouNewContentFast(in, new SentencesHandlerDependency(out));

  }
}
