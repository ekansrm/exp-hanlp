package app;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.AsciiUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;


/**
 * @author weiming
 * 提取语料的正文部分
 */
public class CorpusContentExtraction {


  /**
   * 最小篇章长度
   */
  static private Integer MIN_PARAGRAPH_SIZE = 5;

  static private Punctuator punctuator;
  static {
    punctuator = new SimplePunctuator();
  }


  static private Pattern headerPattern = Pattern.compile(
          "(^.*?\\(记者.*?\\))\\s*" +
//  "|(^.*?日(电|消息)|(^.*?讯))" +
//  "|(^.*?日)" +
          "|^中新网[^0-9]*?[0-9]+月[0-9]+[日]?[电]?\\s*(\\(记者.*?\\))*" +
          "|(^中新网[0-9]+月[0-9]+[日]?[电]?)" +
          "|((中国网|台海网|中国台湾网|中广网北京.*?)[0-9]+月[0-9]+日)" +
          "|(^【.*?】)"
  );

  static private Pattern invalidCharacterPattern = Pattern.compile("[\\uE40C\n\r]");
  static private Pattern wrongEnddingPattern = Pattern.compile("\\s$");

  static public String filterContent(String line) {
    if (line.startsWith("中国雅虎侠客平台")) {
      return null;
    }
    if (line.contains("并符合本论坛的主旨。")) {
      return null;
    }

    Matcher matcher = headerPattern.matcher(line);
    line = matcher.replaceAll("");

    Matcher invalidCharacterMatch = invalidCharacterPattern.matcher(line);
    line = invalidCharacterMatch.replaceAll("");


    return line;
  }

  static public List<String> filterSentences(List<String> sentences) {

    if(null==sentences||0==sentences.size()) {
      return Collections.emptyList();
    }

    if(sentences.size()<MIN_PARAGRAPH_SIZE) {

      return Collections.emptyList();

    }

    List<String> rv = new LinkedList<>();

    // 去除开头过短的句子
    Boolean isTrueBegin = Boolean.FALSE;
    for(String sentence: sentences) {
      if(!isTrueBegin) {
        if(sentence.length()<16) {
          continue;
        }
        isTrueBegin = Boolean.TRUE;
      }
      if(sentence.length()<4) {
        continue;
      }
      rv.add(sentence);
    }

    // 去除结尾过短的句子
    Boolean isTrueEnd = Boolean.FALSE;
    Integer len = rv.size();
    for(int i=len-1; i>=0; i--) {
      if(rv.get(i).length()<16) {
        rv.remove(i);
      } else {
        break;
      }

    }

    // 错误以空格断行的句子， 拼接起来
    len = rv.size();
    int i = 0;
    while(i<len) {

      String sentence = rv.get(i);
      if(sentence.endsWith(" ")&&(i+1)<len) {
        String next = rv.get(i+1);
        if(next.length()>23&&sentence.length()>23) {
          i += 1;
          continue;
        }
        sentence = wrongEnddingPattern.matcher(sentence).replaceAll("") + next;
        rv.set(i, sentence);
        rv.remove(i+1);
        len -=1;
        continue;
      }

      i += 1;

    }

    if(rv.size()<MIN_PARAGRAPH_SIZE) {

      return Collections.emptyList();

    }

    return rv;
  }

  static public void extractorSogouNewContentFast(FileInputStream in, ISentencesHandler handler) throws IOException {

    Pattern pattern = compile("(?<=</doc>)");
    Pattern contentPattern = compile("<content>(.*?)</content>");

    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "GBK"));


    StringBuilder contentBuilder = new StringBuilder();
    String line;
    while((line=reader.readLine())!=null) {
      contentBuilder.append(line);
      if(pattern.matcher(line).find()) {
        Matcher matcher = contentPattern.matcher(contentBuilder.toString());
        contentBuilder = new StringBuilder();
        if(matcher.find()) {
          String content = matcher.group(1);
          content = AsciiUtil.sbc2dbcCase(content);
          content = filterContent(content);
          if(content==null||content.length()==0) {
            continue;
          }
          List<String> sentences = punctuator.punctuate(content);
          sentences = filterSentences(sentences);
          handler.process(sentences);
        }
      }
    }
  }

  static class SentencesHandlerPrint implements ISentencesHandler {

    @Override
    public void process(List<String> sentences) {
      if(null==sentences||0==sentences.size()) {
        return;
      }
      System.out.println(String.join("\n", sentences));
      System.out.println();

    }

  }


  /**
   * 从XML语料提取正文部分
   */
  void extractorXML(FileInputStream in, FileOutputStream out) throws ParserConfigurationException, IOException {
    DocumentBuilderFactory builderFactory =  DocumentBuilderFactory.newInstance();

    DocumentBuilder builder = builderFactory.newDocumentBuilder();

    Document doc;

    // 实例化InputStreamReader时，已指定字符编码
    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "GBK"));


    Pattern pattern = compile("(?<=</doc>)");

    String line;
    StringBuilder contentBuilder = new StringBuilder();
    while((line=reader.readLine())!=null) {
      contentBuilder.append(line);
    }

    for(String s: pattern.split(contentBuilder.toString())) {

      try {
        System.out.println("====");
        InputStream is = new ByteArrayInputStream(s.getBytes("UTF-8"));
        doc = builder.parse(is);


        NodeList nList = doc.getElementsByTagName("content");

        for(int  i = 0 ; i<nList.getLength();i++) {
          System.out.println(nList.item(i).getTextContent());
        }

      } catch (Exception e) {
        e.printStackTrace();

      }

    }

  }

  static public void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
    FileInputStream in = new FileInputStream("src/main/resources/news_tensite_xml.smarty.dat");
    extractorSogouNewContentFast(in, new SentencesHandlerPrint());

  }

}
