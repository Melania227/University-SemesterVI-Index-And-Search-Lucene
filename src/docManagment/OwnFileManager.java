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
        while ((line = brRafReader.readLine()) != null) {
            long fileOffset = randomAccessFile.getFilePointer();
            previousOffset = bufferOffset;
            bufferOffset = getOffset(brRafReader);
            long realPosition = bufferOffset;
            currentPosition += previousOffset+1; //Sumo 1 del salto de linea
            System.out.println("Initial position : " + currentPosition 
                    + " and offset " + bufferOffset);
            lines.add(line);
        	currentPosition = 0;
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