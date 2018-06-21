package app;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.AsciiUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;


/**
 * @author weiming
 * 提取语料的正文部分
 */
public class CorpusContentExtraction {

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

  static Pattern headPattern = compile(
  "(^.*?\\(记者.*?\\))\\s*" +
//  "|(^.*?日(电|消息)|(^.*?讯))" +
//  "|(^.*?日)" +
  "|^中新网[^0-9]*?[0-9]+月[0-9]+[日]?[电]?\\s*(\\(记者.*?\\))*" +
  "|(^中新网[0-9]+月[0-9]+[日]?[电]?)" +
  "|((中国网|台海网|中国台湾网|中广网北京.*?)[0-9]+月[0-9]+日)" +
  "|(^【.*?】)"
);

  static public String removeNoValue(String line) {
    if (line.startsWith("中国雅虎侠客平台")) {
      return null;
    }
    if (line.contains("并符合本论坛的主旨。")) {
      return null;
    }
    Matcher matcher = headPattern.matcher(line);
    return matcher.replaceAll("");
  }

  static public void extractorSogouNewContentFast(FileInputStream in, FileOutputStream out) throws IOException {

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
          content = removeNoValue(content);
          if(content==null||content.length()==0) {
            continue;
          }
          System.out.println(content);
        }
      }
    }






  }

  static public void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
      FileInputStream in = new FileInputStream("src/main/resources/news_tensite_xml.smarty.dat");
      extractorSogouNewContentFast(in, null);

    }

}
