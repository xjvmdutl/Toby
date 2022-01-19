package strategy;

public interface LineCallback<T> {
    T doSomethingWithLine(String line, T value);
}
