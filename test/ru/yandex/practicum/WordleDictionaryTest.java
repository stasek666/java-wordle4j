package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    private static final int LETTER_NUMBER = 5;
    private WordleDictionary dictionary;
    private PrintWriter logWriter;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void testAddAndContains() {
        assertTrue(dictionary.contains("аббат"), "В словаре не найдено слово аббат");
        assertTrue(dictionary.contains("автор"), "В словаре не найдено слово автор");
        assertFalse(dictionary.contains("абба"), "В словаре найдено лишнее слово абба");
        assertFalse(dictionary.contains("абсолют"), "В словаре найдено лишнее слово абсолют");
    }

    @Test
    void testGetLetterNumber() {
        assertEquals(LETTER_NUMBER, dictionary.getLetterNumber(), "Кол-во букв не соответствует заданному при создании словаря");
    }

    @Test
    void testGetRandomWord() {
        String word = dictionary.getRandomWord();
        assertNotNull(word, "Словарь не вернул рандомное слово");
        assertEquals(LETTER_NUMBER, word.length(), "Словарь вернул рандомное слово неверной длины");
        assertTrue(dictionary.contains(word), "Словарь вернул рандомное слово, которого нет в словаре");
    }

    @Test
    void testGetRandomWordFromEmptyDictionary() {
        WordleDictionary empty = new WordleDictionary(LETTER_NUMBER, logWriter);
        assertThrows(IllegalArgumentException.class, empty::getRandomWord, "Получено рандомное слово из пустого словаря");
    }

    @Test
    void testGetSuitableWordExactPattern() {
        HashSet<Character> lettersInWord = new HashSet<>();
        HashSet<Character> lettersNotInWord = new HashSet<>();
        java.util.ArrayList<HashSet<Character>> lettersWrongPositions = new java.util.ArrayList<>();
        for (int i = 0; i < LETTER_NUMBER; i++) {
            lettersWrongPositions.add(new HashSet<>());
        }
        Set<String> notSuitable = new HashSet<>();

        String result = dictionary.getSuitableWord("аббат", lettersInWord, lettersNotInWord, lettersWrongPositions, notSuitable);
        assertEquals("аббат", result, "Подходящее слово определено некорректно, хотя в шаблоне оно было задано полностью");
    }

    @Test
    void testGetSuitableWordWildcardPattern() {
        HashSet<Character> lettersInWord = new HashSet<>();
        HashSet<Character> lettersNotInWord = new HashSet<>();
        java.util.ArrayList<HashSet<Character>> lettersWrongPositions = new java.util.ArrayList<>();
        for (int i = 0; i < LETTER_NUMBER; i++) {
            lettersWrongPositions.add(new HashSet<>());
        }
        Set<String> notSuitable = new HashSet<>();

        String result = dictionary.getSuitableWord("аб..т", lettersInWord, lettersNotInWord, lettersWrongPositions, notSuitable);
        assertNotNull(result);
        assertTrue(result.matches("аб..т"), "Неверно найдено подходящее слово по шаблону");
        assertTrue(dictionary.contains(result), "Найденное подходящее слово отсутствует в словаре");
    }

    @Test
    void testGetSuitableWordExcludesNotSuitable() {
        HashSet<Character> lettersInWord = new HashSet<>();
        HashSet<Character> lettersNotInWord = new HashSet<>();
        java.util.ArrayList<HashSet<Character>> lettersWrongPositions = new java.util.ArrayList<>();
        for (int i = 0; i < LETTER_NUMBER; i++) {
            lettersWrongPositions.add(new HashSet<>());
        }
        Set<String> notSuitable = new HashSet<>();
        notSuitable.add("аббат");

        String result = dictionary.getSuitableWord("аббат", lettersInWord, lettersNotInWord, lettersWrongPositions, notSuitable);
        assertNull(result, "При поиске подходящего слова не отбрасываются слова-исключения (проверенные на предыдущих шагах)");
    }

    @Test
    void testGetSuitableWordNoMatch() {
        HashSet<Character> lettersInWord = new HashSet<>();
        HashSet<Character> lettersNotInWord = new HashSet<>();
        java.util.ArrayList<HashSet<Character>> lettersWrongPositions = new java.util.ArrayList<>();
        for (int i = 0; i < LETTER_NUMBER; i++) {
            lettersWrongPositions.add(new HashSet<>());
        }
        Set<String> notSuitable = new HashSet<>();

        String result = dictionary.getSuitableWord("zzzzz", lettersInWord, lettersNotInWord, lettersWrongPositions, notSuitable);
        assertNull(result, "Поиск по заведомо неподходящему шаблону дал непустой результат");
    }

    @Test
    void testGetSuitableWordWithLettersInWord() {
        HashSet<Character> lettersInWord = new HashSet<>();
        lettersInWord.add('а');
        lettersInWord.add('б');
        HashSet<Character> lettersNotInWord = new HashSet<>();
        java.util.ArrayList<HashSet<Character>> lettersWrongPositions = new java.util.ArrayList<>();
        for (int i = 0; i < LETTER_NUMBER; i++) {
            lettersWrongPositions.add(new HashSet<>());
        }
        Set<String> notSuitable = new HashSet<>();

        String result = dictionary.getSuitableWord("..б..", lettersInWord, lettersNotInWord, lettersWrongPositions, notSuitable);
        assertNotNull(result, "Подходящее слово не найдено");
        assertTrue(result.contains("а"), "Вернулось неподходящее слово, т.к. в нём нет буквы а");
        assertTrue(result.contains("б"), "Вернулось неподходящее слово, т.к. в нём нет буквы б");
    }

    @Test
    void testGetSuitableWordWithLettersNotInWord() {
        HashSet<Character> lettersInWord = new HashSet<>();
        HashSet<Character> lettersNotInWord = new HashSet<>();
        lettersNotInWord.add('в');
        java.util.ArrayList<HashSet<Character>> lettersWrongPositions = new java.util.ArrayList<>();
        for (int i = 0; i < LETTER_NUMBER; i++) {
            lettersWrongPositions.add(new HashSet<>());
        }
        Set<String> notSuitable = new HashSet<>();

        String result = dictionary.getSuitableWord("а....", lettersInWord, lettersNotInWord, lettersWrongPositions, notSuitable);
        assertNotNull(result, "Подходящее слово не найдено");
        assertFalse(result.contains("в"), "Вернулось неподходящее слово, т.к. в нём есть буква в");
    }

    @Test
    void testGetSuitableWordWithWrongPositions() {
        HashSet<Character> lettersInWord = new HashSet<>();
        HashSet<Character> lettersNotInWord = new HashSet<>();
        java.util.ArrayList<HashSet<Character>> lettersWrongPositions = new java.util.ArrayList<>();
        for (int i = 0; i < LETTER_NUMBER; i++) {
            lettersWrongPositions.add(new HashSet<>());
        }
        lettersWrongPositions.get(0).add('а');
        Set<String> notSuitable = new HashSet<>();

        String result = dictionary.getSuitableWord(".ббат", lettersInWord, lettersNotInWord, lettersWrongPositions, notSuitable);
        assertNull(result, "Неверно отработала проверка букв, которых нет на определённых позициях");
    }

    @Test
    void testContainsCaseSensitive() {
        assertFalse(dictionary.contains("АББАТ"), "В словаре будто бы есть слово в верхнем регистре");
    }

    @Test
    void testContainsEmptyString() {
        assertFalse(dictionary.contains(""), "В словаре есть пустое слово");
    }
}
