package main;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import docManagment.OwnFileManager;
import lucene.Searching;
import myOwnAnalyzer.myOwnAnalyzer;

public class main{

	
    public static void menu() throws Exception {
    	boolean bandera = true;
		while(bandera) {
	    	String selection;
	    	Scanner input = new Scanner(System.in);
			System.out.println("Recuperación De Información Textual - TP2");
			System.out.println("-------------------------\n");
			System.out.println("1 - Indexar una colección");
			System.out.println("2 - Realizar una consulta");
			System.out.println("3 - Salir");
			System.out.print("Seleccione una opción: ");
			selection = input.nextLine();
			String fileName ="";
			String path ="";
			String consulta = "";
			String tipo = "";
			System.out.println("-------------------------\n");
			switch(selection) {
				case "1":{
					 System.out.println("Ingrese el path del archivo:");
					 fileName = input.nextLine(); //"C:\Users\Laptop\OneDrive\Escritorio\h8.txt"
					 System.out.println("Ingrese el path para la indexación:");
					 path = input.nextLine(); //"C:\Users\Laptop\OneDrive\Escritorio\\h8.txt"
					 OwnFileManager.readCollection(fileName,path);
					 System.out.println("Indexación completada");
					 System.out.println("-------------------------\n");
					 fileName="";
					 break;
				}
				case "2":{
					 System.out.println("Ingrese la consulta a realizar:");
					 consulta = input.nextLine();
					 System.out.println("Se realiza la consulta con stemming (S/N):");
					 tipo = input.nextLine();
					 if(!tipo.equals("S") && !tipo.equals("N") ) {
						 System.out.println("Ingrese un valor válido (S/N):");
						 tipo="";
					 }
					 Searching search = new Searching();
					 search.search(consulta,tipo);
					 System.out.println("-------------------------\n");
					 break;
				}
				case "3":{
					System.out.println("Adios");
					bandera= false;
				}
				default:{
					System.out.println("Error. Intente nuevamente.");
					System.out.println("-------------------------\n");
					break;
				}
			}
		}
    
}
	public static void main(final String[] args) throws Exception {

		 menu();
		 System.out.println("listito");
	   /* long before = System.nanoTime();
	   
	    
	    //List<String> result = readDefault(fileName);
	    long after = System.nanoTime();
	    double ms = (after - before) / 1e6;
	    System.out.println("Reading took " + ms + "ms ");*/
	}
}
