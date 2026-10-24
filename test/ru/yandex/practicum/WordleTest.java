package ru.yandex.practicum;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private static PrintStream originalOut;
    private static InputStream originalIn;
    private ByteArrayOutputStream capturedOut;

    @BeforeAll
    static void saveOriginalStreams() {
        originalOut = System.out;
        originalIn = System.in;
    }

    @BeforeEach
    void redirectOutput() {
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreStreamsAndCleanUp() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        deleteLogFiles();
    }

    private void deleteLogFiles() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of("."), "wordle_log_*.txt")) {
            stream.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }

    private String getOutput() {
        return capturedOut.toString(StandardCharsets.UTF_8);
    }

    @Test
    void testMainPrintsWelcomeMessages() throws Exception {
        String input = "аббат\nабвер\nабзац\nаборт\nабрис\nабхаз\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        Wordle.main(new String[]{});

        String output = getOutput();
        assertTrue(output.contains("Игра Wordle начинается!"));
        assertTrue(output.contains("Количество букв в загаданном слове - 5"));
        assertTrue(output.contains("Максимальное кол-во ходов - 6"));
    }

    @Test
    void testMainGameTerminatesWithResult() throws Exception {
        String input = "аббат\nабвер\nабзац\nаборт\nабрис\nабхаз\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        Wordle.main(new String[]{});

        String output = getOutput();
        assertTrue(output.contains("Вы выйграли!") || output.contains("Вы проиграли!"));
    }

    @Test
    void testMainHandlesBlankInputWithHint() throws Exception {
        String input = "\nаббат\nабвер\nабзац\nаборт\nабрис\nабхаз\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        Wordle.main(new String[]{});

        String output = getOutput();
        assertTrue(output.contains("Игра Wordle начинается!"));
        assertTrue(output.contains("Вы выйграли!") || output.contains("Вы проиграли!"));
    }

    @Test
    void testMainReportsInvalidWord() throws Exception {
        String input = "123\nаббат\nабвер\nабзац\nаборт\nабрис\nабхаз\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        Wordle.main(new String[]{});

        String output = getOutput();
        assertTrue(output.contains("Игра Wordle начинается!"));
        assertTrue(output.contains("Слово должно состоять из русских букв!"));
    }

    @Test
    void testMainReportsWrongWordLength() throws Exception {
        String input = "абба\nаббатт\nаббат\nабвер\nабзац\nаборт\nабрис\nабхаз\nаваль\nаванс\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        Wordle.main(new String[]{});

        String output = getOutput();
        assertTrue(output.contains("Игра Wordle начинается!"));
        assertTrue(output.contains("Количество букв в слове должно быть 5!"));
    }

    @Test
    void testMainReportsWordNotInDictionary() throws Exception {
        String input = "абвгд\nаббат\nабвер\nабзац\nаборт\nабрис\nабхаз\nаваль\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        Wordle.main(new String[]{});

        String output = getOutput();
        assertTrue(output.contains("Игра Wordle начинается!"));
        assertTrue(output.contains("Такого слова нет в словаре!"));
    }
}
