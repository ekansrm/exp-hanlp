package app;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 基于简单规则的断句器
 * @author ekansrm
 */
public class SimplePunctuator implements Punctuator{


    static private Set<Character> DEFAULT_PUNCTUATION = new HashSet<>(Arrays.asList(
              '。'
            , ' '
            , '?', '？'
            , '!', '！'
            , '…'
    ));
    static private List<Character> DEFAULT_QUOTATION_MARK = Arrays.asList(
            '(', ')', '（', '）'
            ,'\'', '\'', '‘', '’'
            , '"', '"', '“', '”'
            , '{', '}', '｛', '｝'
            , '<', '>', '《', '》'
            , '[', ']', '【', '】'
    );

    private Set<Character> punctuations;
    private List<Character> quotationMark;
    private Map<Character, Character> quotationMarkPairs;
    private Stack<Character> quotationMarkStack;
    private Integer splitCount;
    private Character lastChar;
    private Pattern whitespacePattern;

    public SimplePunctuator() {
        init();
    }

    private void init() {
        if(null==punctuations) {
            punctuations = new HashSet<>(DEFAULT_PUNCTUATION);
        }
        if(null==quotationMark) {
            quotationMark = new LinkedList<>(DEFAULT_QUOTATION_MARK);
        }
        initQuotationMarkPairs();

        quotationMarkStack = new Stack<>();
        lastChar = null;
        splitCount = null;

        Set<Character> mark = new HashSet<>(punctuations);
        mark.add(',');
        mark.add('，');
        mark.add('、');

        String punctuationsRegex = "[" + StringUtils.join(mark, "") + "]";
        whitespacePattern = Pattern.compile(String.format("(\\s+(?=%s))|((?<=%s)\\s+)", punctuationsRegex, punctuationsRegex));


    }
    private void initQuotationMarkPairs() {

        quotationMarkPairs = new HashMap<>(20);

        if(quotationMark.size()%2!=0) {
            throw new IllegalArgumentException("quotation marks must come in pair!");
        }

        Iterator<Character> iterator = quotationMark.iterator();
        while (iterator.hasNext()) {
            quotationMarkPairs.put(iterator.next(), iterator.next());
        }


    }



    private Boolean isEndOfTheSentence(Character c) {

        if(!quotationMarkStack.empty()&&c.equals(quotationMarkPairs.get(quotationMarkStack.peek()))) {
            quotationMarkStack.pop();
            return punctuations.contains(lastChar);
        }

        if(quotationMarkPairs.containsKey(c)) {
            quotationMarkStack.push(c);
            return false;
        }

        return punctuations.contains(c) && quotationMarkStack.empty();

    }



    @Override
    public List<String> punctuate(String content) {
        List<String> rv = new LinkedList<>();

        StringBuilder sb;
        String sentence;

        quotationMarkStack.clear();
        splitCount = 0;
        sb = new StringBuilder();

        content = whitespacePattern.matcher(content).replaceAll("");

        for(Character c: content.toCharArray()) {

            sb.append(c);
            if(isEndOfTheSentence(c)) {
                rv.add(sb.toString());
                sb = new StringBuilder();
                quotationMarkStack.clear();
                splitCount ++;
            }
            lastChar = c;

        }
        sentence = sb.toString();
        sentence = sentence.trim();

        if(sentence.length()>0) {
            rv.add(sentence);
        }
        return rv;
    }
}
