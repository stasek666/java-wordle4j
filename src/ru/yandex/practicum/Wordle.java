package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        int defaultLetterNumber = 5; // количество букв в загадываемом слове
        int defaultMaxStepNumber = 6; // максимально возможное количество ходов
        WordleDictionary wordleDictionary;
        String logFileName = "wordle_log_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        String wordsFileName = "words_ru.txt";
        try (PrintWriter logWriter = new PrintWriter(new FileWriter(logFileName))) {
            WordleDictionaryLoader wordleDictionaryLoader = new WordleDictionaryLoader(logWriter, defaultLetterNumber);
            try {
                wordleDictionary = wordleDictionaryLoader.loadFromFile(wordsFileName);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            } catch (DictionaryIsEmpty e) {
                System.out.println(e.getMessage());
                e.printStackTrace(logWriter);
                return;
            }
            if (wordleDictionary == null) return;
            WordleGame wordleGame = new WordleGame(wordleDictionary, defaultMaxStepNumber, logWriter);
            System.out.println("Игра Wordle начинается!");
            System.out.println("Количество букв в загаданном слове - " + defaultLetterNumber);
            System.out.println("Максимальное кол-во ходов - " + defaultMaxStepNumber);
            Scanner scanner = new Scanner(System.in);
            String result = " ";
            try {
                do {
                    if (wordleGame.getSteps() == 0) throw new StepsAreOver("Ходы закончились!", wordleGame.getAnswer());
                    String command = scanner.nextLine();
                    command = command.strip().toLowerCase().replace("ё", "е");
                    if (command.isBlank()) {
                        String hint = wordleGame.getNextHint();
                        System.out.println(hint);
                        command = hint;
                    }
                    try {
                        result = wordleGame.nextStep(command);
                        System.out.println(result);
                    } catch (WordNotFoundInDictionary e) {
                        e.printStackTrace(logWriter);
                        System.out.println(e.getMessage());
                    }
                } while (!result.matches("\\++"));
            } catch (StepsAreOver e) {
                e.printStackTrace(logWriter);
                System.out.println("Вы проиграли! " + e.getDetailMessage());
            }
            if (result.matches("\\++")) System.out.println("Вы выйграли!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
