package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    private static final int LETTER_NUMBER = 5;

    private WordleDictionary dictionary;
    private PrintWriter logWriter;

    @BeforeEach
    void setUp() throws IOException {
        logWriter = new PrintWriter(System.out);
        dictionary = new WordleDictionary(LETTER_NUMBER, logWriter);
        dictionary.add("аббат");
        dictionary.add("абвер");
        dictionary.add("абзац");
        dictionary.add("аборт");
        dictionary.add("абрис");
        dictionary.add("абхаз");
        dictionary.add("абцуг");
        dictionary.add("абшид");
        dictionary.add("аваль");
        dictionary.add("аванс");
        dictionary.add("авгит");
        dictionary.add("авгур");
        dictionary.add("авеню");
        dictionary.add("аверс");
        dictionary.add("авизо");
        dictionary.add("авлос");
        dictionary.add("авось");
        dictionary.add("аврал");
        dictionary.add("авран");
        dictionary.add("автол");
        dictionary.add("автор");
        dictionary.add("абвал");
        dictionary.add("абзал");
        dictionary.add("клерк");
    }

    private WordleGame createGame(String answer, int maxSteps) throws Exception {
        WordleDictionary testDictionary = new WordleDictionary(LETTER_NUMBER, logWriter);
        for (String word : dictionary.wordsCopy()) {
            testDictionary.add(word);
        }
        WordleGame game = new WordleGame(testDictionary, maxSteps, logWriter);
        game.setAnswer(answer);
        return game;
    }

    @Test
    void testConstructorSetsInitialState() throws IOException {
        WordleGame game = new WordleGame(dictionary, 6, logWriter);
        assertEquals(6, game.getSteps());
        assertNotNull(game.getAnswer(), "При создании игры не загадан ответ");
        assertEquals(LETTER_NUMBER, game.getAnswer().length(), "Длина загаданного ответа неверная");
        assertTrue(dictionary.contains(game.getAnswer()), "Загаданного ответа нет в словаре");
    }

    @Test
    void testNextStepCorrectGuess() throws Exception {
        WordleGame game = createGame("аббат", 6);
        String result = game.nextStep("аббат");
        assertEquals("+++++", result, "Неверно вычислен ответ при полном совпадении с ответом");
        assertEquals(5, game.getSteps(), "Неверно посчитано кол-во оставшихся шагов игры");
    }

    @Test
    void testNextStepPartialMatch() throws Exception {
        WordleGame game = createGame("аббат", 6);
        String result = game.nextStep("абзал");
        assertEquals("++-+-", result, "Неверно вычислен ответ при частичном совпадении с ответом");
    }

    @Test
    void testNextStepAllWrong() throws Exception {
        WordleGame game = createGame("аббат", 6);
        String result = game.nextStep("клерк");
        assertEquals("-----", result, "Неверно вычислен ответ при полном несовпадении с ответом");
    }

    @Test
    void testNextStepDecrementsSteps() throws Exception {
        WordleGame game = createGame("аббат", 6);
        game.nextStep("абвал");
        assertEquals(5, game.getSteps(),"Неверно посчитано кол-во оставшихся шагов игры");
        game.nextStep("абвал");
        assertEquals(4, game.getSteps(), "Неверно посчитано кол-во оставшихся шагов игры");
    }

    @Test
    void testNextStepStepsAreOver() throws Exception {
        WordleGame game = createGame("аббат", 1);
        game.nextStep("абвал");
        assertThrows(StepsAreOver.class, () -> game.nextStep("абвал"), "Не выброшено исключение об исчерпании ходов");
    }

    @Test
    void testNextStepNonRussianLetters() throws Exception {
        WordleGame game = createGame("аббат", 6);
        assertThrows(WordNotFoundInDictionary.class, () -> game.nextStep("hello"), "Не выброшено исключение об отсутствии слова в словаре");
    }

    @Test
    void testNextStepEmptyString() throws Exception {
        WordleGame game = createGame("аббат", 6);
        assertThrows(WordNotFoundInDictionary.class, () -> game.nextStep(""), "Не выброшено исключение об отсутствии слова в словаре при вводе пустого слова");
    }

    @Test
    void testNextStepWrongLength() throws Exception {
        WordleGame game = createGame("аббат", 6);
        assertThrows(WordNotFoundInDictionary.class, () -> game.nextStep("абба"), "Не выброшено исключение об отсутствии слова в словаре при кол-ве букв меньше заданного");
        assertThrows(WordNotFoundInDictionary.class, () -> game.nextStep("аббатт"), "Не выброшено исключение об отсутствии слова в словаре при кол-ве букв больше заданного");
    }

    @Test
    void testGetNextHint() throws Exception {
        WordleGame game = createGame("аббат", 6);
        game.nextStep("аврал");
        String hint = game.getNextHint();
        assertNotNull(hint, "Не вычислена подсказка");
        assertEquals(LETTER_NUMBER, hint.length(), "Вычислена подсказка неверной длины");
    }

    @Test
    void testGetNextHintNoSuitableWord() throws Exception {
        WordleGame game = createGame("аббат", 6);
        game.nextStep("абзал");
        String hint = game.getNextHint();
        assertEquals("аббат", hint);
    }

    @Test
    void testTestCharExactMatch() throws Exception {
        WordleGame game = createGame("аббат", 6);
        assertEquals("+", game.testChar('а', 'а'), "Неверный ответ по совпадению двух букв");
    }

    @Test
    void testTestCharPresentWrongPosition() throws Exception {
        WordleGame game = createGame("аббат", 6);
        assertEquals("^", game.testChar('а', 'б'), "Неверный ответ по букве, стоящей на другом месте");
    }

    @Test
    void testTestCharNotPresent() throws Exception {
        WordleGame game = createGame("аббат", 6);
        assertEquals("-", game.testChar('а', 'в'), "Неверный ответ по букве, которой нет в загаданном слове");
    }

}
