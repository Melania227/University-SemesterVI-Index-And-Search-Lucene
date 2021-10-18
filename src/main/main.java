package main;

import java.util.List;

import docManagment.OwnFileManager;

public class main{
	public static void main(final String[] args) throws Exception {

	    String fileName = "C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\Colecciones\\prueba.txt";

	    long before = System.nanoTime();
	   
	    
	    List<String> result = OwnFileManager.readColection(fileName);
	    //List<String> result = readDefault(fileName);
	    long after = System.nanoTime();
	    double ms = (after - before) / 1e6;
	    System.out.println("Reading took " + ms + "ms "
	            + "for " + result.size() + " lines");
	}
}
