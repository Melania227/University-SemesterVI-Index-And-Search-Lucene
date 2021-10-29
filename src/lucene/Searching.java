package lucene;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.FileHandler;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import myOwnAnalyzer.myOwnAnalyzer;

import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;


public class Searching {
	
	final static ArrayList<String> stopWords = new ArrayList<String>(Arrays.asList("a", "acá", "ahí", "ajena", "ajenas", "ajeno", "ajenos", "al", "algo", "algún", "alguna", "algunas", "alguno", "algunos", "allá", "alli", "allí", "ambos", "ampleamos", "ante", "antes", "aquel", "aquella", "aquellas", "aquello", "aquellos", "aqui", "aquí", "arriba", "asi", "atras", "aun", "aunque", "bajo", "bastante", "bien", "cabe", "cada", "casi", "cierta", "ciertas", "cierto", "ciertos", "como", "cómo", "con", "conmigo", "conseguimos", "conseguir", "consigo", "consigue", "consiguen", "consigues", "contigo", "contra", "cual", "cuales", "cualquier", "cualquiera", "cualquieras", "cuan", "cuán", "cuando", "cuanta", "cuánta", "cuantas", "cuántas", "cuanto", "cuánto", "cuantos", "cuántos", "de", "dejar", "del", "demás", "demas", "demasiada", "demasiadas", "demasiado", "demasiados", "dentro", "desde", "donde", "dos", "el", "él", "ella", "ellas", "ello", "ellos", "empleais", "emplean", "emplear", "empleas", "empleo", "en", "encima", "entonces", "entre", "era", "eramos", "eran", "eras", "eres", "es", "esa", "esas", "ese", "eso", "esos", "esta", "estaba", "estado", "estais", "estamos", "estan", "estar", "estas", "este", "esto", "estos", "estoy", "etc", "fin", "fue", "fueron", "fui", "fuimos", "gueno", "ha", "hace", "haceis", "hacemos", "hacen", "hacer", "haces", "hacia", "hago", "hasta", "incluso", "intenta", "intentais", "intentamos", "intentan", "intentar", "intentas", "intento", "ir", "jamás", "junto", "juntos", "la", "largo", "las", "lo", "los", "mas", "más", "me", "menos", "mi", "mía", "mia", "mias", "mientras", "mio", "mío", "mios", "mis", "misma", "mismas", "mismo", "mismos", "modo", "mucha", "muchas", "muchísima", "muchísimas", "muchísimo", "muchísimos", "mucho", "muchos", "muy", "nada", "ni", "ningun", "ninguna", "ningunas", "ninguno", "ningunos", "no", "nos", "nosotras", "nosotros", "nuestra", "nuestras", "nuestro", "nuestros", "nunca", "os", "otra", "otras", "otro", "otros", "para", "parecer", "pero", "poca", "pocas", "poco", "pocos", "podeis", "podemos", "poder", "podria", "podriais", "podriamos", "podrian", "podrias", "por", "por qué", "porque", "primero", "puede", "pueden", "puedo", "pues", "que", "qué", "querer", "quien", "quién", "quienes", "quienesquiera", "quienquiera", "quiza", "quizas", "sabe", "sabeis", "sabemos", "saben", "saber", "sabes", "se", "segun", "ser", "si", "sí", "siempre", "siendo	sin", "sín", "sino", "so", "sobre", "sois", "solamente", "solo", "somos", "soy", "sr", "sra", "sres", "sta", "su", "sus", "suya", "suyas", "suyo", "suyos", "tal", "tales", "también", "tambien", "tampoco", "tan", "tanta", "tantas", "tanto", "tantos", "te", "teneis", "tenemos", "tener", "tengo", "ti", "tiempo", "tiene", "tienen", "toda", "todas", "todo", "todos", "tomar", "trabaja", "trabajais", "trabajamos", "trabajan", "trabajar", "trabajas", "trabajo", "tras", "tú", "tu", "tus", "tuya", "tuyo", "tuyos", "ultimo", "un", "una", "unas", "uno", "unos", "usa", "usais", "usamos", "usan", "usar", "usas", "uso", "usted", "ustedes", "va", "vais", "valor", "vamos", "van", "varias", "varios", "vaya", "verdad", "verdadera", "vosotras", "vosotros", "voy", "vuestra", "vuestras", "vuestro", "vuestros", "y", "ya", "yo")); //Filters both words
	final static CharArraySet stopwordsSet = new CharArraySet(stopWords, true);
	public long totalHits;
	private ScoreDoc[] hits;
	private IndexSearcher s;
	public int pos = 0;
	
	public Searching(){
		
	}
	
	public void printResults(int len) throws IOException {
		if(len == this.pos) {
			System.out.println("No se encontraron más resultados");
		}
		for (int i = this.pos; i < len; i++) {
            int docId = hits[i].doc;
            Document d = this.s.doc(docId);
            System.out.println("Doc. #"+(i + 1) + ": "+ d.get("titulo"));
		}
		this.pos = len;
	}
	
	public int menuSearching() throws ParseException, IOException{
		while(true) {
			String selection = "";
	        System.out.println("-------------------------\n");
	        System.out.println("1 - Ver los siguientes 20 documentos");
	        System.out.println("2 - Obtener un documento");
	        System.out.println("3 - Obtener los enlaces de un documento");
	        System.out.println("4 - Volver");       
	        System.out.print("Seleccione una opción: ");
	        Scanner input = new Scanner(System.in);
	        selection = input.nextLine();
    		System.out.println("-------------------------\n");
	        switch(selection) {
			 	case "1":{
			 		if (this.totalHits-this.pos <= 20) {
			 			printResults((int)(this.totalHits));
					}
					else {
						printResults(this.pos+20) ;
					}
			 		break;
			 	}
			 	case "2":{
			 		System.out.println("Ingrese el número del documento:");
			 		int id =  input.nextInt();
			 		Document d = this.s.doc(hits[id].doc);
		    		RandomAccessFile raf = new RandomAccessFile("C:\\Users\\Laptop\\OneDrive\\Escritorio\\h8.txt", "rw");
		    		raf.seek(Integer.parseInt(d.get("docStart")));
		            byte[] arr = new byte[Integer.parseInt(d.get("docLenght"))];
		            System.out.println(d.get("docStart"));
		            System.out.println(d.get("docLenght"));
		            raf.readFully(arr);
		            String text = new String(arr);
		            raf.close();
			 		break;
			 	}
			 	case "3":{
			 		System.out.println("Ingrese el número del documento:");
			 		int id =  input.nextInt();
			 		Document d = this.s.doc(hits[id].doc);
			 		String enlace = "";
					for (IndexableField x : d.getFields("enlace")) {
						enlace += x.stringValue() + "\n";	
					}
					System.out.print(enlace);
			 		break;
			 	}
			 	default: {
			 		System.out.println("Error. Intente nuevamente.");
					System.out.println("-------------------------\n");
			 	}
			 }
	        	
	        
		}
		
	}
	
	public void search(String query, String stemm) throws IOException, ParseException {
		String fileName = ".\\PRUEBAS\\INDEX";
		Query q;
		Directory dir = FSDirectory.open(Paths.get(fileName));
		if(stemm.equals("S")) {
			Map<String,Analyzer> analyzerPerField = new HashMap<>();
			analyzerPerField.put("texto", new SpanishAnalyzer(stopwordsSet));
   	  	 	analyzerPerField.put("ref", new myOwnAnalyzer(stopwordsSet));
   	  	 	analyzerPerField.put("encab", new SpanishAnalyzer(stopwordsSet));
   	  	 	analyzerPerField.put("titulo", new myOwnAnalyzer(stopwordsSet));
   	  	 	PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new myOwnAnalyzer(stopwordsSet), analyzerPerField);
   	  	 	q = new QueryParser("texto",wrapper).parse(query);
		}
		else {
			myOwnAnalyzer analyzerWithoutStemming = new myOwnAnalyzer(stopwordsSet);
			q = new QueryParser("texto",analyzerWithoutStemming).parse(query);
		}
		IndexReader reader = DirectoryReader.open(dir); 
		if(reader == null) {
			System.out.println("Error con el path cargado.");
			System.out.println("-------------------------\n");
			return; 
		}
		this.s = new IndexSearcher(reader);
		TopDocs docs = this.s.search(q, 1);
		this.totalHits = docs.totalHits.value;
		if(this.totalHits>0) {
			docs = this.s.search(q, (int) this.totalHits);
			this.hits = docs.scoreDocs;
			System.out.println("Se han encontrado " + this.totalHits + " resultados");
			if (this.totalHits > 20) {
				printResults(20);
			}
			else {
				printResults((int) this.totalHits) ;
			}
			menuSearching();
			return;
		}
		else {
			System.out.println("No se encontraron resultados");
		}
	}
}

