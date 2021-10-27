package docManagment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class OwnFileManager {

    public static void readCollection(String fileName) throws Exception {
    	DocProcessing documentProcessing = new DocProcessing();
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
        //BufferedReader brRafReader = new BufferedReader(new FileReader(randomAccessFile.getFD()));
        BufferedReader brRafReader = new BufferedReader(new InputStreamReader(new FileInputStream(randomAccessFile.getFD()), "ISO-8859-1"));
        RandomAccessFile raf = new RandomAccessFile(fileName, "r");
        String line = null;
        long currentOffset = 0;
        long previousPosition = 0;
        long previousOffset = -1;
        long initialPosition = -1;
        int bufferOffset = 0;
        long actualPosition = 0;
        long docID=1;
        while ((line = brRafReader.readLine()) != null) {
        	long fileOffset = randomAccessFile.getFilePointer();
            if (fileOffset != previousOffset) {
                if (previousOffset != -1) {
                    currentOffset = previousOffset;
                }
                previousOffset = fileOffset;
            }
            
            if(line.matches("^<!DOCTYPE html PUBLIC \\\"-//W3C//DTD XHTML 1\\.0 Transitional//EN\\\" \\\"http://www\\.w3\\.org/TR/xhtml1/DTD/xhtml1-transitional\\.dtd\\\">")) {
            	System.out.println("DOC ID: " + docID);
                bufferOffset = getOffset(brRafReader);
                
                actualPosition=currentOffset+bufferOffset;
                //initialPosition += actualPosition - (actualPosition-previousPosition) + 1;
                
                if(previousPosition != 0) {
                	initialPosition = actualPosition - (actualPosition-previousPosition) + 1;
              	}
              	else {
              		initialPosition = actualPosition - (actualPosition-previousPosition);
              	}
            }
            if(line.matches("</html>.*")) {
                bufferOffset = getOffset(brRafReader);
                if(docID==7414) {
                	System.out.println("LA POSICION ES EN EL FINAL: " + initialPosition);
                }
            	actualPosition=currentOffset+bufferOffset; 
                System.out.println("Initial position : " + initialPosition 
                        + " and offset " + actualPosition + " and lenght " + (actualPosition-initialPosition));
                
                OwnDocument actualDoc = new OwnDocument(docID, (actualPosition-initialPosition), initialPosition);
                documentProcessing.getDocuments().add(actualDoc);
                documentProcessing.processTagsInDoc();
                
                /*raf.seek(initialPosition);
                byte[] arr = new byte[(int) (actualPosition-initialPosition)];
                raf.readFully(arr);
                String text = new String(arr);
                System.out.println(text);*/
                
                initialPosition=0;
                docID++;
            }
            else {
                bufferOffset = getOffset(brRafReader); 
                actualPosition = currentOffset+bufferOffset;
                if(docID==7414) {
                	System.out.println("LA POSICION ES: " + initialPosition);
                }
                
            }
          previousPosition=actualPosition;
        }
        randomAccessFile.close();
        raf.close();
        //documentProcessing.processTagsInDoc();
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