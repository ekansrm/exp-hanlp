package demo;

import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

/**
 * @author weiming
 */
public class DemoCRFSegment {
  static public void main(String[] args) {
    Segment segment = new CRFSegment();
    segment.enablePartOfSpeechTagging(true);
    List<Term> termList = segment.seg("你看过穆赫兰道吗");
    System.out.println(termList);
    for (Term term : termList)
    {
      if (term.nature == null)
      {
        System.out.println("识别到新词：" + term.word);
      }
    }
  }
}
