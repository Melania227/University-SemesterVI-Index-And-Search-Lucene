package lucene;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import myOwnAnalyzer.myOwnAnalyzer;

public class Indexing {
	private String indexPath;
	private IndexWriter indexer;
	private Directory dir;
	private CharArraySet stopwordsSet;
	
	public Indexing(String urlStopwords) {
 		this.stopwordsSet = getStopwords(urlStopwords);
	}
	
	public void startIndex(Boolean doStemming){
		try {
		      this.dir = FSDirectory.open(Paths.get(this.indexPath));
		      
		      //Nota: MyOwnAnalyzer es el StandardAnalyzer pero con modificaciones propias del problema que estamos intentando resolver para la tarea
		      IndexWriterConfig iwc;
		      if (doStemming) {
		    	  Map<String,Analyzer> analyzerPerField = new HashMap<>();
		    	  analyzerPerField.put("texto", new SpanishAnalyzer(stopwordsSet));
		    	  analyzerPerField.put("ref", new myOwnAnalyzer(stopwordsSet));
			      analyzerPerField.put("encab", new SpanishAnalyzer(stopwordsSet));
			      analyzerPerField.put("titulo", new myOwnAnalyzer(stopwordsSet));
			      PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new myOwnAnalyzer(stopwordsSet), analyzerPerField);
			      iwc = new IndexWriterConfig(wrapper);
		      }
		      else {
		    	  myOwnAnalyzer analyzerWithoutStemming = new myOwnAnalyzer(stopwordsSet);
				  iwc = new IndexWriterConfig(analyzerWithoutStemming);
		      }
		      
		      iwc.setOpenMode(OpenMode.CREATE);

		      IndexWriter writer = new IndexWriter(dir, iwc);
		      this.indexer = writer;
		} 
		catch (IOException e) {
	      System.out.println(" caught a " + e.getClass() +
	       "\n with message: " + e.getMessage());
		}
	}
	
	public void addDocument(Long initialIndex, Long docLenght, Boolean doStemming, String bodyText, String aText, String hText, String titleText, ArrayList<String> hrefText) {
		Document doc = new Document();

		//Campos utilizados en la busqueda
		doc.add(new TextField("texto", bodyText, Field.Store.NO));
		doc.add(new TextField("ref", aText, Field.Store.NO));
		doc.add(new TextField("encab", hText, Field.Store.NO));
		doc.add(new TextField("titulo", titleText, Field.Store.YES));
		
		//Campos almacenados para informacion, pero no para busqueda
		doc.add(new StringField("docStart", String.valueOf(initialIndex), Field.Store.YES));
		doc.add(new StringField("docLenght", String.valueOf(docLenght), Field.Store.YES));
		
		for (String hrefAct : hrefText) {
			doc.add(new StringField("enlace", hrefAct, Field.Store.YES));
		}
		
		try{
			this.indexer.addDocument(doc);
		}
		catch (IOException e) {
		    System.out.println(" caught a " + e.getClass() +
		     "\n with message: " + e.getMessage());
		}
	}

	public String getIndexPath() {
		return indexPath;
	}
	
	
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}
	
	public IndexWriter getIndexWriter() {
		return this.indexer;
	}
	
	private CharArraySet getStopwords (String url) {
		ArrayList<String> stopWords = new ArrayList<String>();
		
		try (BufferedReader brRafReader = new BufferedReader(new FileReader(url))){
			String line;
			while ((line = brRafReader.readLine()) != null) {
				stopWords.add(line);
			}
			CharArraySet stopwordsSet = new CharArraySet(stopWords, true);
			return stopwordsSet;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
		
}