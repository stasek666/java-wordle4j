package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

public class WordleDictionary {

    private List<String> words;
    private int letterNumber = 5;
    private PrintWriter logFileWriter;

    WordleDictionary (int letterNumber, PrintWriter logFileWriter) {
        words = new ArrayList<>();
        this.letterNumber = letterNumber;
        this.logFileWriter = logFileWriter;
    }

    public void add (String newWord) {
        words.add(newWord);
    }

    public String getRandomWord() {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    public int getLetterNumber() {
        return letterNumber;
    }

    public String getSuitableWord(String wordPattern, HashSet<Character> lettersInWord, HashSet<Character> lettersNotInWord, ArrayList<HashSet<Character>> lettersWrongPositions, Set<String> notSuitableWords) {
        //int unknownLettersNumber = letterNumber-wordPattern.replaceAll(".", "").length();
        for (String word: words) {
            if (word.matches(wordPattern) && !notSuitableWords.contains(word)) {
                boolean sutable = true;
                HashSet<Character> lettersInWordCopy = new HashSet<Character>(lettersInWord);
                for (int i = 0; i<letterNumber && sutable; i++) {
                    if (wordPattern.charAt(i) == '.') {
                        if (lettersNotInWord.contains(word.charAt(i)) || lettersWrongPositions.get(i).contains(word.charAt(i))) {
                            sutable = false;
                        } else {
                            lettersInWordCopy.remove(word.charAt(i));
                        }
                    }
                }
                if (sutable && lettersInWordCopy.isEmpty()) return word;
            }
        }
        return null;
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    java.util.List<String> wordsCopy() {
        return new java.util.ArrayList<>(words);
    }
}
