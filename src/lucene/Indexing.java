package lucene;

import java.io.BufferedReader;
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

public class Indexing {
	private String indexPath;
	private IndexWriter indexer;
	final ArrayList<String> stopWords = new ArrayList<String>(Arrays.asList("a", "acá", "ahí", "ajena", "ajenas", "ajeno", "ajenos", "al", "algo", "algún", "alguna", "algunas", "alguno", "algunos", "allá", "alli", "allí", "ambos", "ampleamos", "ante", "antes", "aquel", "aquella", "aquellas", "aquello", "aquellos", "aqui", "aquí", "arriba", "asi", "atras", "aun", "aunque", "bajo", "bastante", "bien", "cabe", "cada", "casi", "cierta", "ciertas", "cierto", "ciertos", "como", "cómo", "con", "conmigo", "conseguimos", "conseguir", "consigo", "consigue", "consiguen", "consigues", "contigo", "contra", "cual", "cuales", "cualquier", "cualquiera", "cualquieras", "cuan", "cuán", "cuando", "cuanta", "cuánta", "cuantas", "cuántas", "cuanto", "cuánto", "cuantos", "cuántos", "de", "dejar", "del", "demás", "demas", "demasiada", "demasiadas", "demasiado", "demasiados", "dentro", "desde", "donde", "dos", "el", "él", "ella", "ellas", "ello", "ellos", "empleais", "emplean", "emplear", "empleas", "empleo", "en", "encima", "entonces", "entre", "era", "eramos", "eran", "eras", "eres", "es", "esa", "esas", "ese", "eso", "esos", "esta", "estaba", "estado", "estais", "estamos", "estan", "estar", "estas", "este", "esto", "estos", "estoy", "etc", "fin", "fue", "fueron", "fui", "fuimos", "gueno", "ha", "hace", "haceis", "hacemos", "hacen", "hacer", "haces", "hacia", "hago", "hasta", "incluso", "intenta", "intentais", "intentamos", "intentan", "intentar", "intentas", "intento", "ir", "jamás", "junto", "juntos", "la", "largo", "las", "lo", "los", "mas", "más", "me", "menos", "mi", "mía", "mia", "mias", "mientras", "mio", "mío", "mios", "mis", "misma", "mismas", "mismo", "mismos", "modo", "mucha", "muchas", "muchísima", "muchísimas", "muchísimo", "muchísimos", "mucho", "muchos", "muy", "nada", "ni", "ningun", "ninguna", "ningunas", "ninguno", "ningunos", "no", "nos", "nosotras", "nosotros", "nuestra", "nuestras", "nuestro", "nuestros", "nunca", "os", "otra", "otras", "otro", "otros", "para", "parecer", "pero", "poca", "pocas", "poco", "pocos", "podeis", "podemos", "poder", "podria", "podriais", "podriamos", "podrian", "podrias", "por", "por qué", "porque", "primero", "puede", "pueden", "puedo", "pues", "que", "qué", "querer", "quien", "quién", "quienes", "quienesquiera", "quienquiera", "quiza", "quizas", "sabe", "sabeis", "sabemos", "saben", "saber", "sabes", "se", "segun", "ser", "si", "sí", "siempre", "siendo	sin", "sín", "sino", "so", "sobre", "sois", "solamente", "solo", "somos", "soy", "sr", "sra", "sres", "sta", "su", "sus", "suya", "suyas", "suyo", "suyos", "tal", "tales", "también", "tambien", "tampoco", "tan", "tanta", "tantas", "tanto", "tantos", "te", "teneis", "tenemos", "tener", "tengo", "ti", "tiempo", "tiene", "tienen", "toda", "todas", "todo", "todos", "tomar", "trabaja", "trabajais", "trabajamos", "trabajan", "trabajar", "trabajas", "trabajo", "tras", "tú", "tu", "tus", "tuya", "tuyo", "tuyos", "ultimo", "un", "una", "unas", "uno", "unos", "usa", "usais", "usamos", "usan", "usar", "usas", "uso", "usted", "ustedes", "va", "vais", "valor", "vamos", "van", "varias", "varios", "vaya", "verdad", "verdadera", "vosotras", "vosotros", "voy", "vuestra", "vuestras", "vuestro", "vuestros", "y", "ya", "yo")); //Filters both words
	final CharArraySet stopwordsSet = new CharArraySet(stopWords, true);
	
	
	public Indexing() {
		this.indexPath = "C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\PRUEBAS\\INDEX";
	}
	
	public void startIndex(Boolean doStemming){
		try {
		      Directory dir = FSDirectory.open(Paths.get(this.indexPath));
		      
		      IndexWriterConfig iwc;
		      if (doStemming) {
		    	  Map<String,Analyzer> analyzerPerField = new HashMap<>();
		    	  analyzerPerField.put("texto", new SpanishAnalyzer(stopwordsSet));
		    	  analyzerPerField.put("ref", new StandardAnalyzer());
			      analyzerPerField.put("encab", new SpanishAnalyzer(stopwordsSet));
			      analyzerPerField.put("titulo", new StandardAnalyzer());
			      PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
			      iwc = new IndexWriterConfig(wrapper);
		      }
		      else {
				  StandardAnalyzer analyzerWithoutStemming = new StandardAnalyzer();
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
		if (!doStemming) {
			//El Spanish Analyzer quita tildes, pero el Standard no, y si no se hace Stemming ese es el que se usa
			aText = deletePunctuation(bodyText);
			aText = deletePunctuation(hText);
		}		
		aText = deletePunctuation(aText);
		aText = deletePunctuation(titleText);

		//Campos utilizados en la busqueda
		doc.add(new TextField("texto", bodyText, Field.Store.NO));
		doc.add(new TextField("ref", aText, Field.Store.NO));
		doc.add(new TextField("encab", hText, Field.Store.NO));
		doc.add(new TextField("titulo", titleText, Field.Store.NO));
		
		//Campos almacenados para informacion, pero no para busqueda
		doc.add(new StringField("docStart", String.valueOf(initialIndex), Field.Store.YES));
		doc.add(new StringField("docLenght", String.valueOf(docLenght), Field.Store.YES));
		
		for (String hrefAct : hrefText) {
			doc.add(new StringField("enlace", hrefAct, Field.Store.YES));
		}
		
		try{
			this.indexer.addDocument(doc);
			System.out.println(this.indexer.getDirectory());
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
	
	private String deletePunctuation(String str) {
		//Primero remplazamos el codigo de la ñ por otro, ya que sino las quita el Normalizer
		str = str.replace('ñ', '\001');
		str = str.replace('Ñ', '\002');
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = str.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		//Regresamos las ñ
		str = str.replace('\001', 'ñ');
		str = str.replace('\002', 'Ñ');
		return str;
	}
		
}
