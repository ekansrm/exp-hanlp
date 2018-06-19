package app;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * @author weiming
 * 提取语料的正文部分
 */
public class CorpusContentExtraction {

  /**
   * 从XML语料提取正文部分
   */
  void extractorXML(Document doc, FileOutputStream out) {

  }

  static public void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

    // 实例化InputStreamReader时，已指定字符编码
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(
        new FileInputStream("src/main/resources/news_tensite_xml.smarty.dat"), "GBK"));
    InputSource is = new InputSource(reader);


      DocumentBuilderFactory builderFactory =  DocumentBuilderFactory.newInstance();

      DocumentBuilder builder = builderFactory.newDocumentBuilder();

      Document doc = builder.parse(is);

      doc.getDocumentElement().normalize();

      System.out.println("Root  element: "+doc.getDocumentElement().getNodeName());

      NodeList nList = doc.getElementsByTagName("book");

      for(int  i = 0 ; i<nList.getLength();i++){

        Node node = nList.item(i);

        System.out.println("Node name: "+ node.getNodeName());
        Element ele = (Element)node;

        System.out.println("----------------------------");
        if(node.getNodeType() == Element.ELEMENT_NODE){

          System.out.println("book category: "+ ele.getAttribute("category"));

          System.out.println("title name: "+ ele.getElementsByTagName("title").item(0).getTextContent());

          System.out.println("author name: "+ele.getElementsByTagName("author").item(0).getTextContent());

          System.out.println("year :"+ele.getElementsByTagName("year").item(0).getTextContent());

          System.out.println("price : "+ele.getElementsByTagName("price").item(0).getTextContent());

          System.out.println("-------------------------");


        }


      }

    }

}
