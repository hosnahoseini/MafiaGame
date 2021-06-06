package org.HO;

import java.io.*;

public class FileUtils {
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
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
