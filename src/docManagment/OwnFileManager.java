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

    public static void readCollection(String fileName, String path, String pathStopWords) throws Exception {
    	DocProcessing documentProcessing = new DocProcessing(path, pathStopWords);
    	documentProcessing.setUrl(fileName);
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
        BufferedReader brRafReader = new BufferedReader(new InputStreamReader(new FileInputStream(randomAccessFile.getFD()), "ISO-8859-1"));
        String line = null;
        long currentOffset = 0;
        long previousPosition = 0;
        long previousOffset = -1;
        long initialPosition = 0;
        int bufferOffset = 0;
        long actualPosition = 0;
        long docID=1;
        boolean hasEnding = true;
        while ((line = brRafReader.readLine()) != null) {
        	long fileOffset = randomAccessFile.getFilePointer();
            if (fileOffset != previousOffset) {
                if (previousOffset != -1) {
                    currentOffset = previousOffset;
                }
                previousOffset = fileOffset;
            }
            
            //System.out.println(line);
            
            if(line.matches("^<!DOCTYPE html PUBLIC \\\"-//W3C//DTD XHTML 1\\.0 Transitional//EN\\\" \\\"http://www\\.w3\\.org/TR/xhtml1/DTD/xhtml1-transitional\\.dtd\\\">") && hasEnding) {
            	hasEnding = false;
            	
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
            else {
            	if(line.matches("^?<!DOCTYPE html PUBLIC \\\"-//W3C//DTD XHTML 1\\.0 Transitional//EN\\\" \\\"http://www\\.w3\\.org/TR/xhtml1/DTD/xhtml1-transitional\\.dtd\\\">")) {
            		bufferOffset = getOffset(brRafReader);
                	actualPosition=currentOffset+bufferOffset; 
                                        
                    OwnDocument actualDoc = new OwnDocument(docID, (actualPosition-initialPosition), initialPosition);
                    documentProcessing.getDocuments().add(actualDoc);
                    documentProcessing.addIgnoredDoc(actualDoc);
                    //documentProcessing.processTagsInDoc();
                    
                    initialPosition=0;
                    docID++;
                    previousPosition=actualPosition;
                    
                	hasEnding = false;
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
            }
            
            if(line.matches("</html>.*")) {
            	hasEnding = true;
                bufferOffset = getOffset(brRafReader);
            	actualPosition=currentOffset+bufferOffset; 
                
                OwnDocument actualDoc = new OwnDocument(docID, (actualPosition-initialPosition), initialPosition);
                documentProcessing.getDocuments().add(actualDoc);
                documentProcessing.processTagsInDoc();
                
                initialPosition=0;
                docID++;
            }
            else {
                bufferOffset = getOffset(brRafReader); 
                actualPosition = currentOffset+bufferOffset;            
            }
          previousPosition=actualPosition;
        }
        randomAccessFile.close();
        documentProcessing.getIndexer().getIndexWriter().close();
        //documentProcessing.processTagsInDoc();
        System.out.println();
        documentProcessing.printIgnoredDocsAlert();
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