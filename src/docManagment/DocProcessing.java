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

import lucene.Indexing;

public class DocProcessing {
	private ArrayList<OwnDocument> documents;
	private ArrayList<String> hrefs;
	private Indexing indexing;
	private String url;
	private Boolean doStemming;
	
	public DocProcessing() {
		this.documents = new ArrayList<OwnDocument>();
		this.indexing = new Indexing();
		this.hrefs = new ArrayList<String>();
		this.url = "C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\Colecciones\\prueba.txt";
		this.doStemming = true;
		this.indexing.startIndex(this.doStemming);
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
    	String bodyText = "";
    	String aText = "";
    	String hText = "";
    	String titleText = "";
    	String hrefText = "";
    	ArrayList<String> aContent = new ArrayList<String>();
    	
    	for (OwnDocument doc : documents) {
    		RandomAccessFile raf = new RandomAccessFile(this.url, "rw");
    		raf.seek(doc.getInitialIndex());
            byte[] arr = new byte[(int) (doc.getSize())];
            raf.readFully(arr);
            String text = new String(arr);
            raf.close();
            
            System.out.println(doc.getDocID());
            bodyText = this.processTextInBody(text);
            aContent = this.processTextInA(text);
            aText = aContent.get(0);
            hrefText = aContent.get(1);
            hText = this.processTextInH(text);
            titleText = this.processTextInTitle(text);
            this.indexing.addDocument(doc.getInitialIndex(), doc.getSize(), this.doStemming, bodyText, aText, hText, titleText, hrefText);
    	}
    }
	
    public String processTextInBody(String text) {
    	String bodyText = "";
    	Document document = Jsoup.parse(text);
    	Elements body = document.select ("body");
    	Pattern pat = Pattern.compile("([0-9]*[a-zA-Z_ÁÉÍÓÚÜáéíóúüÑñ]+[0-9]*)");
		String stringInBody = body.text(); 
		
		//System.out.println(stringInBody);
		Matcher mat = pat.matcher(stringInBody);
		while(mat.find()) {
			bodyText += mat.group() + " ";
			//System.out.println("SI: "+mat.group());
	    }
		return bodyText;
    }
    
    public ArrayList<String> processTextInA(String text) {
    	String aText = "";
    	Document document = Jsoup.parse(text);
    	Elements a = document.select ("a");
    	Pattern pat = Pattern.compile("([0-9]*[a-zA-Z_ÁÉÍÓÚÜáéíóúüÑñ]+[0-9]*)");
		String stringInA = a.text(); 
		//System.out.println(stringInA);
		
		Matcher mat = pat.matcher(stringInA);
		while(mat.find()) {
			aText += mat.group() + " ";
			//System.out.println("SI: "+mat.group());
	    }
		
		ArrayList<String> hrefsAct = new ArrayList<String>();
		for (Element aFound : a) {
			String hrefTemp = aFound.attr("href");
			if(hrefTemp.startsWith("../../../../articles/")) {
				hrefTemp = hrefTemp.replace("../../../../articles/", "");
				hrefsAct.add(hrefTemp);
				//System.out.println ("Href: " + hrefTemp);
			}
		}
		
		String hrefText = "";
		Set<String> set = new HashSet<>(hrefsAct);
		hrefsAct.clear();
		hrefsAct.addAll(set);
    	for (String hrefAct : hrefsAct) {
    		hrefText += hrefAct+" ";
		}
    	
    	hrefsAct.clear();
    	hrefsAct.add(aText);
    	hrefsAct.add(hrefText);
    	
		return hrefsAct;
    }  
    
    public String processTextInH(String text) {
    	String hText = "";
    	Document document = Jsoup.parse(text);
    	Pattern pat = Pattern.compile("([0-9]*[a-zA-Z_ÁÉÍÓÚÜáéíóúüÑñ]+[0-9]*)");
    	
    	for (int i = 1; i < 10; i++) {
    		//System.out.println("h"+i);
    		Elements h = document.select ("h"+i);
    		String stringInH = h.text(); 
    		//System.out.println(stringInH);
    		Matcher mat = pat.matcher(stringInH);
    		while(mat.find()) {
    			hText += mat.group() + " ";
    			//System.out.println("SI: "+mat.group());
    	    }
    	}
    	return hText;
    }  
    
    public String processTextInTitle(String text) {
    	String titleText = "";
    	Document document = Jsoup.parse(text);
    	Elements head = document.select ("head");
    	Elements title = head.select ("title");
    	Pattern pat = Pattern.compile("([0-9]*[a-zA-Z_ÁÉÍÓÚÜáéíóúüÑñ]+[0-9]*)");
		String stringInTitle = title.text(); 
		//System.out.println(stringInTitle);
		Matcher mat = pat.matcher(stringInTitle);
		while(mat.find()) {
			titleText += mat.group() + " ";
			//System.out.println("SI: "+mat.group());
	    }
		return titleText;
    }  
        
}