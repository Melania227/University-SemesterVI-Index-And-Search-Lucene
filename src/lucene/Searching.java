package lucene;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import java.awt.Desktop;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

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
	
	public void menuSearching() throws ParseException, IOException{
		boolean bandera = true;
		while(bandera) {
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
			 		String id =  input.nextLine();
			 		System.out.println("Ingrese el path de la colección:");
			 		String path =  input.nextLine();
			 		System.out.println("Ingrese el path donde desea que se guarde el documento:");
			 		String path2 =  input.nextLine();
			 		getDoc(Integer.parseInt(id)-1, path, path2);
			 		
			 		break;
			 	}
			 	case "3":{
			 		System.out.println("Ingrese el número del documento:");
			 		int id =  input.nextInt();
			 		id--;
			 		Document d = this.s.doc(hits[id].doc);
			 		String enlace = "";
					for (IndexableField x : d.getFields("enlace")) {
						enlace += x.stringValue() + "\n";	
					}
					System.out.print(enlace);
			 		break;
			 	}
			 	case "4":{
			 		bandera = false;
			 		break;
			 	}
			 	default: {
			 		System.out.println("Error. Intente nuevamente.");
					System.out.println("-------------------------\n");
			 	}
			 }
	        	
	        
		}
		
	}
	
	public void getDoc(int id, String path, String path2) throws IOException {
		Document d = this.s.doc(hits[id].doc);
		System.out.println("Doc. #"+(id+1) + ": "+ d.get("titulo"));
 		RandomAccessFile raf = new RandomAccessFile(path, "rw");
 		raf.seek(Integer.parseInt(d.get("docStart")));
        byte[] arr = new byte[Integer.parseInt(d.get("docLenght"))];
        raf.readFully(arr);
        String text = new String(arr);
        writeFile(text,path2, id);
        raf.close();
		
	}

	public void writeFile(String text, String path2, int id) {
		try {  
		      FileWriter myWriter = new FileWriter(path2+"\\Doc"+(id+1)+".html");
		      myWriter.write(text);
		      myWriter.close();
		      System.out.println(path2+"Doc"+(id+1)+".html");
		      System.out.println("Archivo HTML creado");
		      File file = new java.io.File(path2+"\\Doc"+(id+1)+".html").getAbsoluteFile();
              Desktop.getDesktop().open(file);
		    } 
		catch (IOException e) {
		      System.out.println("Error. No se ha podido crear el archivo");
		      e.printStackTrace();
		      System.out.println("-------------------------\n");
		} 
		
	}

	public void search(String query, String stemm, String fileName, String stpw) throws IOException, ParseException {
		Query q;
		CharArraySet stopwordsSet = getStopwords(stpw);
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

