package main;

import java.util.List;

import docManagment.OwnFileManager;

public class main{
	public static void main(final String[] args) throws Exception {

	    String fileName = "C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\Colecciones\\h8.txt";

	    long before = System.nanoTime();
	   
	    
	    OwnFileManager.readCollection(fileName);
	    //List<String> result = readDefault(fileName);
	    long after = System.nanoTime();
	    double ms = (after - before) / 1e6;
	    System.out.println("Reading took " + ms + "ms ");
	}
}
