package app;

import java.util.List;

/**
 * @author ekansrm
 */
public interface ISentencesHandler {
  /**
   * @param sentences
   */
  void process(List<String> sentences) throws RuntimeException;
}
