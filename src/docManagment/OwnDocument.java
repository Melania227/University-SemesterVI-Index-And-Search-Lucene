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
        long currentOffset = 0;
        long previousPosition = 0;
        long previousOffsetFlag = -1;
        long initialPosition = -1;
        int bufferOffset = 0;
        long actualPosition = 0;
        String doc = "";
        int docID=1;
        while ((line = brRafReader.readLine()) != null) {
            long fileOffset = randomAccessFile.getFilePointer();
            if (fileOffset != previousOffsetFlag) {
                if (previousOffsetFlag != -1) {
                    currentOffset = previousOffsetFlag;
                }
                previousOffsetFlag = fileOffset;
            }
            
            if(line.matches("<!DOCTYPE .*")) {
            	System.out.println("DOC ID: " + docID);
                bufferOffset = getOffset(brRafReader);
                actualPosition=currentOffset+bufferOffset;
                initialPosition += actualPosition - (actualPosition-previousPosition) + 1;
                doc.concat(line);
            }
            if(line.matches("</html>.*")) {
                bufferOffset = getOffset(brRafReader);
            	actualPosition=currentOffset+bufferOffset; 
                System.out.println("Initial position : " + initialPosition 
                        + " and offset " + actualPosition);
                
                RandomAccessFile raf = new RandomAccessFile("C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\Colecciones\\prueba.txt", "rw");
                raf.seek(initialPosition);
                byte[] arr = new byte[(int) (actualPosition-initialPosition)];
                raf.readFully(arr);
                String text = new String(arr);
                System.out.println(text);
                
                doc.concat(line);
                initialPosition=0;
                docID++;
            }
            else {
                bufferOffset = getOffset(brRafReader); 
                actualPosition = currentOffset+bufferOffset; 
                doc.concat(line);
            }
          previousPosition=actualPosition;
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