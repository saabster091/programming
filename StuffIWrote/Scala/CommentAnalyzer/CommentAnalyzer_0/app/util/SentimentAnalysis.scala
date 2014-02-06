package util

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.rnn.RNNCoreAnnotations
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.util.CoreMap
import java.util.Properties
import scala.collection.JavaConverters._

/**
 * Ethan Petuchowski
 * 1/23/14
 */
object SentimentAnalysis {
  /**
   * it gets an average over the sentences, weighted by sentence-length
   * for no good reason
   */
  def analyzeText(text: String) : Double = {
    println(text)
    val props = new Properties()
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
    val pipeline = new StanfordCoreNLP(props)
    var avgSentiment = 0.0
    val length = text.length.asInstanceOf[Double]
    assert(text != null && length > 0, "can't analyze this crap")
    val annotation = pipeline.process(text)
    for (sentence: CoreMap <- annotation.get(classOf[CoreAnnotations.SentencesAnnotation]).asScala) {
      val tree = sentence.get(classOf[SentimentCoreAnnotations.AnnotatedTree])
      val sentiment = RNNCoreAnnotations.getPredictedClass(tree)
      val partText = sentence.toString
      avgSentiment += sentiment * partText.length.asInstanceOf[Double] / length
    }
    avgSentiment
  }
}