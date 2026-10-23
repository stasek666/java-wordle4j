package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class WordleGame {

    private String answer; // загаданное слово

    private int steps; // кол-во оставшихся ходов

    private WordleDictionary dictionary;

    private HashMap<String, String> stepsHistory;

    private PrintWriter logWriter;

    WordleGame(WordleDictionary wordleDictionary, int maxStepNumber, PrintWriter logWriter) throws IOException {
        dictionary = wordleDictionary;
        answer = dictionary.getRandomWord();
        steps = maxStepNumber;
        stepsHistory = new HashMap<>();
        this.logWriter = logWriter;
        logWriter.write("Загадано слово '" + answer + "'\n");
    }

    public String testChar(Character answerCharacter, Character testingCharacter) {
        if (answerCharacter.equals(testingCharacter)) return "+";
        if (answer.contains(testingCharacter.toString())) return "^";
        return "-";
    }

    public String testWord(String testingWord) {
        String result = "";
        for (int i = 0; i < dictionary.getLetterNumber(); i++) {
            result += testChar(answer.charAt(i), testingWord.charAt(i));
        }
        return result;
    }

    public String nextStep(String word) throws IOException, StepsAreOver, WordNotFoundInDictionary {
        if (steps == 0) {
            throw new StepsAreOver("Ходы уже закончились!", answer);
        }
        if (!word.matches("[а-я]+")) {
            throw new WordNotFoundInDictionary("Слово должно состоять из русских букв!");
        } else if (word.length() != dictionary.getLetterNumber()) {
            throw new WordNotFoundInDictionary("Количество букв в слове должно быть " + dictionary.getLetterNumber() + "!");
        } else if (!dictionary.contains(word)) {
            throw new WordNotFoundInDictionary("Такого слова нет в словаре!");
        }
        String answerCode = testWord(word);
        stepsHistory.put(word, answerCode);
        steps--;
        logWriter.write("Результат хода: " + answerCode + ", осталось ходов: " + steps + "\n");
        return answerCode;
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getNextHint() throws IOException {
        int letterNumber = dictionary.getLetterNumber();
        StringBuilder wordPattern = new StringBuilder(".".repeat(letterNumber));
        HashSet<Character> lettersInWord = new HashSet<>();
        HashSet<Character> lettersNotInWord = new HashSet<>();
        ArrayList<HashSet<Character>> lettersWrongPositions = new ArrayList<>();
        for (int i = 0; i < letterNumber; i++) {
            lettersWrongPositions.add(new HashSet<Character>());
        }
        for (String stepWord : stepsHistory.keySet()) {
            String stepPattern = stepsHistory.get(stepWord);
            for (int i = 0; i < letterNumber; i++) {
                if (stepPattern.charAt(i) == '+') {
                    if (wordPattern.charAt(i) != stepWord.charAt(i)) {
                        wordPattern.replace(i,i + 1,stepWord.substring(i,i+1));
                    }
                } else if (stepPattern.charAt(i) == '^') {
                    lettersInWord.add(stepWord.charAt(i));
                    lettersWrongPositions.get(i).add(stepWord.charAt(i));
                } else {
                    lettersNotInWord.add(stepWord.charAt(i));
                }
            }
            for (int i = 0; i < letterNumber; i++) {
                lettersInWord.remove(wordPattern.charAt(i));
            }
        }
        logWriter.write("getNextHint\n");
        logWriter.write("Шаблон искомого слова: " + wordPattern.toString() + "\n");
        logWriter.write("Буквы, которые есть в слове на неизвестных местах: " + lettersInWord.toString() + "\n");
        logWriter.write("Буквы, которых точно нет в слове: " + lettersNotInWord.toString() + "\n");
        logWriter.write("Буквы, которых точно нет в определённых позициях: " + lettersWrongPositions.toString() + "\n");
        String hint = dictionary.getSuitableWord(wordPattern.toString(), lettersInWord, lettersNotInWord, lettersWrongPositions, stepsHistory.keySet());
        logWriter.write("Вычислена подсказка: " + hint + "\n");
        return hint;
    }

}
