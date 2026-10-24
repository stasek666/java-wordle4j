package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class WordleDictionaryLoader {
    private Charset fileCharset = StandardCharsets.UTF_8;
    private PrintWriter logWriter;
    private int letterNumber;

    public WordleDictionaryLoader(PrintWriter logWriter, int letterNumber) {
        this.logWriter = logWriter;
        this.letterNumber = letterNumber;
    }

    public WordleDictionary loadFromFile(String fileName) throws IOException, DictionaryIsEmpty {
        WordleDictionary wordleDictionary = new WordleDictionary(letterNumber, logWriter);
        int wordsCount = 0;
        try (FileReader  fileReader = new FileReader(fileName, fileCharset); BufferedReader br = new BufferedReader(fileReader)) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.length() == wordleDictionary.getLetterNumber()) {
                    wordleDictionary.add(line.toLowerCase().replace("ё", "е"));
                    wordsCount++;
                }
            }
            if (wordsCount == 0) throw new DictionaryIsEmpty("Словарь пуст!");
            return wordleDictionary;
        } catch (FileNotFoundException e) {
            e.printStackTrace(logWriter);
            throw new IOException(e);
        }
    }
}
