package org.HO;

import java.io.*;

/**
 * A class to read and write in the file
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class FileUtils {

    /**
     * write a text to file by buffer
     *
     * @param fileName fileName
     * @param text     text to be written in the file
     */
    public void fileWriterByBuffer(String fileName, String text) {

        try (FileWriter fileWriter = new FileWriter(fileName, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read from a file by buffer
     *
     * @param fileName fileName
     * @return text
     */
    public String fileReaderByBuffer(String fileName) {

        String result = "";
        try (FileReader fileReader = new FileReader(fileName);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * copy source file and append it destination file
     * @param srcFileAdd source file
     * @param destFileAdd destination file
     */
    public void copy(String srcFileAdd, String destFileAdd) {
        String txt = fileReaderByBuffer(srcFileAdd);
        fileWriterByBuffer(destFileAdd, txt);
    }

    public void singleObjectFileWriter(String fileName, SharedData sharedData) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        File file = new File(fileName);
        try {
            fileOutputStream = new FileOutputStream(file, true);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(sharedData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public SharedData singleObjectFileReader(String fileName) {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        SharedData sharedData = null;
        try {
            fileInputStream = new FileInputStream(fileName);
            objectInputStream = new ObjectInputStream(fileInputStream);
            sharedData = (SharedData) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                objectInputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sharedData;
    }


}
