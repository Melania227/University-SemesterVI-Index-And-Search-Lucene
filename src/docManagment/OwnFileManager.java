package docManagment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OwnFileManager {
    public static void main(String[] args) throws Exception {

        String fileName = "C:\\Users\\melan\\OneDrive\\6. TEC-SEXTO SEMESTRE\\RECUPERACION DE INFORMACION TEXTUAL\\PROYECTO 2\\Colecciones\\h8.txt";

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
        long previousOffset = -1;
        while ((line = brRafReader.readLine()) != null) {
            long fileOffset = randomAccessFile.getFilePointer();
            if (fileOffset != previousOffset) {
                if (previousOffset != -1) {
                    currentOffset = previousOffset;
                }
                previousOffset = fileOffset;
            }
            int bufferOffset = getOffset(brRafReader);
            long realPosition = currentOffset + bufferOffset;
            System.out.println("Position : " + realPosition 
                    + " with FP " + randomAccessFile.getFilePointer()
                    + " and offset " + bufferOffset);
            lines.add(line);
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

    private static List<String> readDefault(String fileName) throws Exception {
        List<String> lines = new ArrayList<String>();
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
        String line = null;
        while ((line = randomAccessFile.readLine()) != null) {
            System.out.println("Position : " + randomAccessFile.getFilePointer());
            lines.add(line);
        }
        return lines;
    }
}