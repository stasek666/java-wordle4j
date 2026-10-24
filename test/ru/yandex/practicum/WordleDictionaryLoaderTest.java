package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    private static final int LETTER_NUMBER = 5;
    private PrintWriter logWriter;

    @BeforeEach
    void setUp() {
        logWriter = new PrintWriter(System.out);
    }

    @Test
    void testLoadFromFile() throws IOException {
        Path tempDir = Files.createTempDirectory("wordle-test");
        try {
            Path wordsFile = tempDir.resolve("words.txt");
            Files.writeString(wordsFile, "аббат\nабвер\nабзац\nаборт\nабрис\n");

            WordleDictionaryLoader loader = new WordleDictionaryLoader(logWriter, LETTER_NUMBER);
            WordleDictionary dictionary = loader.loadFromFile(wordsFile.toString());

            assertNotNull(dictionary);
            assertEquals(LETTER_NUMBER, dictionary.getLetterNumber());
            assertTrue(dictionary.contains("аббат"), "Из словаря не загрузилось слово аббат");
            assertTrue(dictionary.contains("абвер"),"Из словаря не загрузилось слово абвер");
            assertTrue(dictionary.contains("абзац"),"Из словаря не загрузилось слово абзац");
            assertTrue(dictionary.contains("аборт"),"Из словаря не загрузилось слово аборт");
            assertTrue(dictionary.contains("абрис"),"Из словаря не загрузилось слово абрис");
            assertFalse(dictionary.contains("автор"),"Из словаря загрузилось лишнее слово автор");
        } finally {
            try (var stream = Files.walk(tempDir)) {
                stream.map(Path::toFile).forEach(java.io.File::delete);
            }
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    void testLoadFromFileFiltersByLength() throws IOException {
        Path tempDir = Files.createTempDirectory("wordle-test");
        try {
            Path wordsFile = tempDir.resolve("words.txt");
            Files.writeString(wordsFile, "аббат\nабвер\nабзац\nаб\nаббатт\n");

            WordleDictionaryLoader loader = new WordleDictionaryLoader(logWriter, LETTER_NUMBER);
            WordleDictionary dictionary = loader.loadFromFile(wordsFile.toString());

            assertTrue(dictionary.contains("аббат"), "Из словаря не загрузилось слово аббат (5 букв)");
            assertTrue(dictionary.contains("абвер"), "Из словаря не загрузилось слово абвер (5 букв)");
            assertTrue(dictionary.contains("абзац"), "Из словаря не загрузилось слово абзац (5 букв)");
            assertFalse(dictionary.contains("аб"), "Из словаря загрузилось лишнее слово аб (2 буквы)");
            assertFalse(dictionary.contains("аббатт"), "Из словаря загрузилось лишнее слово аббатт (6 букв)");
        } finally {
            try (var stream = Files.walk(tempDir)) {
                stream.map(Path::toFile).forEach(java.io.File::delete);
            }
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    void testLoadFromFileNotFound() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(logWriter, LETTER_NUMBER);
        assertThrows(IOException.class, () -> loader.loadFromFile("nonexistent.txt"), "Не выброшено исключение о несуществующем файле");
    }

    @Test
    void testLoadFromFileEmptyDictionary() throws IOException {
        Path tempDir = Files.createTempDirectory("wordle-test");
        try {
            Path wordsFile = tempDir.resolve("empty.txt");
            Files.writeString(wordsFile, "аб\nаббатт\n");

            WordleDictionaryLoader loader = new WordleDictionaryLoader(logWriter, LETTER_NUMBER);
            assertThrows(DictionaryIsEmpty.class, () -> loader.loadFromFile(wordsFile.toString()), "Не выброшено исключение о том, что словарь пуст");
        } finally {
            try (var stream = Files.walk(tempDir)) {
                stream.map(Path::toFile).forEach(java.io.File::delete);
            }
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    void testLoadFromFileReplacesYo() throws IOException {
        Path tempDir = Files.createTempDirectory("wordle-test");
        try {
            Path wordsFile = tempDir.resolve("words.txt");
            Files.writeString(wordsFile, "ёлка\n");
            WordleDictionaryLoader loader = new WordleDictionaryLoader(logWriter, 4);
            WordleDictionary dictionary = loader.loadFromFile(wordsFile.toString());
            assertTrue(dictionary.contains("елка"), "В словаре не обнаружено слово елка (нет подмены ё на е)");
        } finally {
            try (var stream = Files.walk(tempDir)) {
                stream.map(Path::toFile).forEach(java.io.File::delete);
            }
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    void testLoadFromFileLowercases() throws IOException {
        Path tempDir = Files.createTempDirectory("wordle-test");
        try {
            Path wordsFile = tempDir.resolve("words.txt");
            Files.writeString(wordsFile, "АББАТ\n");
            WordleDictionaryLoader loader = new WordleDictionaryLoader(logWriter, LETTER_NUMBER);
            WordleDictionary dictionary = loader.loadFromFile(wordsFile.toString());
            assertTrue(dictionary.contains("аббат"), "В словаре не обнаружено слово аббат (нет переноса слов в нижний регистр)");
        } finally {
            try (var stream = Files.walk(tempDir)) {
                stream.map(Path::toFile).forEach(java.io.File::delete);
            }
            Files.deleteIfExists(tempDir);
        }
    }
}
