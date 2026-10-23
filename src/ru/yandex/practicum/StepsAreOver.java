package ru.yandex.practicum;

public class StepsAreOver extends RuntimeException {
    private final String answer;
    public StepsAreOver(String message, final String answer)
    {
        super(message);
        this.answer = answer;
    }
    public String getDetailMessage () {
        return getMessage() + " Верный ответ был: " + answer;
    }
}
