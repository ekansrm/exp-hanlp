package app;

import java.util.List;

/**
 * 断句器接口
 * @author ekansrm
 */
public interface Punctuator {
    List<String> punctuate(String content);
}
