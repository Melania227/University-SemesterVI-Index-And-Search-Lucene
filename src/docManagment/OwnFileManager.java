package docManagment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OwnFileManager {
    public static void main(String[] args) throws Exception {

        String fileName = "C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\Colecciones\\prueba.txt";

        long before = System.nanoTime();
        List<String> result = readBuffered(fileName);
        //List<String> result = readDefault(fileName);
        long after = System.nanoTime();
        double ms = (after - before) / 1e6;
        System.out.println("Reading took " + ms + "ms "
                + "for " + result.size() + " lines");
    }

    private static List<String> readBuffered(String fileName) throws Exception {
        List<String> lines = new ArrayList<String>();
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
        BufferedReader brRafReader = new BufferedReader(
                new FileReader(randomAccessFile.getFD()));
        String line = null;
        long currentPosition = -1;
        long previousOffset = 0;
        int bufferOffset = 0;
        String doc = "";
        while ((line = brRafReader.readLine()) != null) {
        	if(line.matches("<!DOCTYPE.*")) {
        		System.out.println("UNA VEZ");
                previousOffset = bufferOffset;
                System.out.println("previousOffset INI: " + previousOffset);
                bufferOffset = getOffset(brRafReader);
                System.out.println("bufferOffset INI: " + bufferOffset);
                currentPosition += previousOffset+1; //Sumo 1 del salto de linea
                System.out.println("currentPosition INI: " + currentPosition);
                doc.concat(line);
        	}
        	else {
        		if(line.matches("</html>.*")){
        			System.out.println("TERMINÉ");
        			doc.concat("</html>\n");
        			previousOffset = bufferOffset;
                    System.out.println("previousOffset: " + previousOffset);
                    bufferOffset = getOffset(brRafReader);
                    System.out.println("bufferOffset: " + bufferOffset);
                	System.out.println("Initial position : " + currentPosition 
                            + " and offset " + bufferOffset);

                	
                    //RandomAccessFile raf = new RandomAccessFile("C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\Colecciones\\prueba.txt", "rw");
                    //raf.seek(currentPosition);
                    
                   // byte[] arr = new byte[(int) (bufferOffset-currentPosition)];
                   // raf.readFully(arr);
                   // String text = new String(arr);
                   // System.out.println(text);
                	
                	currentPosition = 0;
                	doc = "";
        		}
        		else {
        			previousOffset = bufferOffset;
                    System.out.println("previousOffset TODOS: " + previousOffset);
                    //System.out.println("LINE TODOS: " + line);
                    bufferOffset = getOffset(brRafReader);
                    System.out.println("bufferOffset TODOS: " + bufferOffset);
                    doc.concat(line);
                    if (previousOffset>bufferOffset) {
                    	RandomAccessFile raf = new RandomAccessFile("C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\Colecciones\\prueba.txt", "rw");
                        raf.seek(currentPosition);
                        
                        byte[] arr = new byte[(int) (previousOffset-currentPosition)];
                        raf.readFully(arr);
                        String text = new String(arr);
                        System.out.println(text);
                    }
        		}
        	}
        }
        return lines;
    }

    private static int getOffset(BufferedReader bufferedReader) throws Exception {
        Field field = BufferedReader.class.getDeclaredField("nextChar");
        int result = 0;
        try {
            field.setAccessible(true);
            result = (Integer) field.get(bufferedReader);
        } finally {
            field.setAccessible(false);
        }
        return result;
    }
}