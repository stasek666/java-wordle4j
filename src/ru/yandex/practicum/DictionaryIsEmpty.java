package ru.yandex.practicum;

public class DictionaryIsEmpty extends RuntimeException {
    public DictionaryIsEmpty(String message) {
        super(message);
    }
}
