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
	private ArrayList<OwnDocument> ignoredDocs;
	private ArrayList<String> hrefs;
	private Indexing indexing;
	private String url;
	private Boolean doStemming;
	
	public DocProcessing() {
		this.documents = new ArrayList<OwnDocument>();
		this.indexing = new Indexing();
		this.hrefs = new ArrayList<String>();
		this.ignoredDocs = new ArrayList<OwnDocument>();
		this.url = "C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\Colecciones\\h8.txt";
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
    	ArrayList<String> hrefList = new ArrayList<String>();
    	OwnDocument docAct = this.documents.get(this.documents.size()-1);
    	
    	//for (OwnDocument doc : documents) {
    		RandomAccessFile raf = new RandomAccessFile(this.url, "rw");
    		raf.seek(docAct.getInitialIndex());
            byte[] arr = new byte[(int) (docAct.getSize())];
            raf.readFully(arr);
            String text = new String(arr);
            raf.close();
            
            bodyText = this.processTextInBody(text);
            aText = this.processTextInA(text);
            hrefList = this.hrefs;
            hText = this.processTextInH(text);
            titleText = this.processTextInTitle(text);
            this.indexing.addDocument(docAct.getInitialIndex(), docAct.getSize(), this.doStemming, bodyText, aText, hText, titleText, hrefList);
            this.hrefs.clear();
    	//}
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
    
    public String processTextInA(String text) {
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
		
		Set<String> set = new HashSet<>(hrefsAct);
		hrefsAct.clear();
		hrefsAct.addAll(set);
    	
    	this.hrefs = hrefsAct;
    	
		return aText;
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
       
    public void addIgnoredDoc(OwnDocument doc) {
    	ignoredDocs.add(doc);
    }
    
    public void printIgnoredDocsAlert() {
    	for (OwnDocument ignored : ignoredDocs) {
			System.out.println("ALERTA: El documento ID: " + ignored.getDocID() +" no ha sido tomado en cuenta para la indexación debido a que no tiene formato de final.");
		}
    }
}