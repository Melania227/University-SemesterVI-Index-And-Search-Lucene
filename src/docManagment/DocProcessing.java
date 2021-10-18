package docManagment;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class DocProcessing {
	private ArrayList<OwnDocument> documents;
	private String url;
	
	public DocProcessing() {
		this.documents = new ArrayList<OwnDocument>();
		this.url = "C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\Colecciones\\prueba.txt";
	}

    //Getters and Setters
	public ArrayList<OwnDocument> getDocuments() {
		return documents;
	}
	public void setDocuments(ArrayList<OwnDocument> documents) {
		this.documents = documents;
	}
    
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	//Functions
    public void processTagsInDoc() throws IOException {
    	for (OwnDocument doc : documents) {
    		RandomAccessFile raf = new RandomAccessFile(this.url, "rw");
    		raf.seek(doc.getInitialIndex());
            byte[] arr = new byte[(int) (doc.getSize())];
            raf.readFully(arr);
            String text = new String(arr);
            raf.close();
            
            this.processTextInBody(text);
            this.processTextInA(text);
            this.processTextInH(text);
            this.processTextInTitle(text);
		}
    }
	
    public void processTextInBody(String text) {
    	Document document = Jsoup.parse(text);
    	Elements body = document.select ("body");
    	System.out.println ("Body para: " + body.text ());
    }
    
    public void processTextInA(String text) {
    	Document document = Jsoup.parse(text);
    	Elements a = document.select ("a");
    	System.out.println ("A para: " + a.text ());
    }  
    
    public void processTextInH(String text) {
    	Document document = Jsoup.parse(text);
    	Elements h1 = document.select ("h1");
    	for (int i = 1; i < 10; i++) {
    		Elements h = document.select ("h"+i);
    		System.out.println ("H" + i + " para: " + h.text ());
    	}
    }  
    
    public void processTextInTitle(String text) {
    	Document document = Jsoup.parse(text);
    	Elements head = document.select ("head");
    	Elements title = head.select ("title");
    	System.out.println ("Title para: " + title.text ());
    }  
        
}