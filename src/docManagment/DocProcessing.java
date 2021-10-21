package docManagment;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DocProcessing {
	private ArrayList<OwnDocument> documents;
	private ArrayList<String> hrefs;
	private String url;
	
	public DocProcessing() {
		this.documents = new ArrayList<OwnDocument>();
		this.hrefs = new ArrayList<String>();
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
            
            System.out.println(doc.getDocID());
            //this.processTextInBody(text);
            this.processTextInA(text);
            //this.processTextInH(text);
            //this.processTextInTitle(text);
		}
    	Set<String> set = new HashSet<>(this.hrefs);
    	this.hrefs.clear();
    	this.hrefs.addAll(set);
    }
	
    public void processTextInBody(String text) {
    	Document document = Jsoup.parse(text);
    	Elements body = document.select ("body");
    	Pattern pat = Pattern.compile("([0-9]*[a-zA-Z_ÁÉÍÓÚÜáéíóúüÑñ]+[0-9]*)");
		String stringInBody = body.text(); 
		System.out.println(stringInBody);
		Matcher mat = pat.matcher(stringInBody);
		while(mat.find()) {
			System.out.println("SI: "+mat.group());
	    }
    }
    
    public void processTextInA(String text) {
    	Document document = Jsoup.parse(text);
    	Elements a = document.select ("a");
    	Pattern pat = Pattern.compile("([0-9]*[a-zA-Z_ÁÉÍÓÚÜáéíóúüÑñ]+[0-9]*)");
		String stringInA = a.text(); 
		System.out.println(stringInA);
		Matcher mat = pat.matcher(stringInA);
		while(mat.find()) {
			System.out.println("SI: "+mat.group());
	    }
		for (Element aFound : a) {
			String hrefTemp = aFound.attr("href");
			if(hrefTemp.startsWith("../../../../articles/")) {
				hrefTemp = hrefTemp.replace("../../../../articles/", "");
				this.hrefs.add(hrefTemp);
				System.out.println ("Href: " + hrefTemp);
			}
		}
    }  
    
    public void processTextInH(String text) {
    	Document document = Jsoup.parse(text);
    	Pattern pat = Pattern.compile("([0-9]*[a-zA-Z_ÁÉÍÓÚÜáéíóúüÑñ]+[0-9]*)");
    	
    	for (int i = 1; i < 10; i++) {
    		System.out.println("h"+i);
    		Elements h = document.select ("h"+i);
    		String stringInH = h.text(); 
    		System.out.println(stringInH);
    		Matcher mat = pat.matcher(stringInH);
    		while(mat.find()) {
    			System.out.println("SI: "+mat.group());
    	    }
    	}
    }  
    
    public void processTextInTitle(String text) {
    	Document document = Jsoup.parse(text);
    	Elements head = document.select ("head");
    	Elements title = head.select ("title");
    	Pattern pat = Pattern.compile("([0-9]*[a-zA-Z_ÁÉÍÓÚÜáéíóúüÑñ]+[0-9]*)");
		String stringInTitle = title.text(); 
		System.out.println(stringInTitle);
		Matcher mat = pat.matcher(stringInTitle);
		while(mat.find()) {
			System.out.println("SI: "+mat.group());
	    }
    }  
        
}