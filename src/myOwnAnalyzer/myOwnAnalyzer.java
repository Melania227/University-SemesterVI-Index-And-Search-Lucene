package myOwnAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class myOwnAnalyzer extends Analyzer{
	private CharArraySet stopwords;
	
	public myOwnAnalyzer(CharArraySet stopwords) {
		super();
		this.stopwords = stopwords;
	}
	
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
	    final Tokenizer source = new StandardTokenizer();
	    TokenStream result = new LowerCaseFilter(source);
	    result = new StopFilter(result, stopwords);
	    result = new MyOwnASCIIFoldingFilter(result);
	    return new TokenStreamComponents(source,result);
	}
	
}
