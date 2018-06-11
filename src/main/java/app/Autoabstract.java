package app;

import com.hankcs.hanlp.HanLP;

import java.util.List;

/**
 * @author weiming
 */
public class Autoabstract {

  static public void main(String[] args) {
    String document = "算法可大致分为基本算法、数据结构的算法、数论算法、计算几何的算法、图的算法、动态规划以及数值分析、加密算法、排序算法、检索算法、随机化算法、并行算法、厄米变形模型、随机森林算法。\n" +
      "算法可以宽泛的分为三类，\n" +
      "一，有限的确定性算法，这类算法在有限的一段时间内终止。他们可能要花很长时间来执行指定的任务，但仍将在一定的时间内终止。这类算法得出的结果常取决于输入值。\n" +
      "二，有限的非确定算法，这类算法在有限的时间内终止。然而，对于一个（或一些）给定的数值，算法的结果并不是唯一的或确定的。\n" +
      "三，无限的算法，是那些由于没有定义终止定义条件，或定义的条件无法由输入的数据满足而不终止运行的算法。通常，无限算法的产生是由于未能确定的定义终止条件。";
    List<String> sentenceList = HanLP.extractSummary(document, 3);
    System.out.println(sentenceList);

    String doc2 = "2017年，加州大学研究者发现，毛发之间会彼此“对话”。这种“对话”机制是为了防止动物身上出现秃斑，帮助动物更好地生存。\n" +
      "原理是这样的：皮肤上有许多毛囊，每个毛囊所处的阶段不同。毛发从毛囊脱落后，毛囊需要花一段时间才能长出新的毛发。为了保证毛发数量刚刚好，毛囊之间需要通过释放化学信号达成合作。不然的话，皮肤上就会出现秃斑。\n" +
      "有些信号会促进毛发生长，例如老鼠腹部的毛发能够防止热量损失，保护腹部不被地面划伤。而有些信号则会起抑制作用，让毛发变得稀疏，例如人类的耳朵部位就没什么毛发，这是为了不影响听力。\n" +
      "那么为什么有些部位的毛发会不受控制地生长呢？可能正是因为毛囊之间的沟通出现了障碍，就像是不同频道之间出现串音。\n" +
      "了解毛囊间的“对话”机制，或许能够帮助我们在未来治疗秃头。不过在治疗方法出来之前，男性与多余战争之间的体毛将不会停止。";
     sentenceList = HanLP.extractSummary(doc2, 4);
    System.out.println(sentenceList);
  }
}
