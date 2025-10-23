package common.helpers;

import io.qameta.allure.Allure;

public class StepLogger {
    @FunctionalInterface
    public interface ThrowableRunnable<T> {
        T run() throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowableVoidRunnable {
        void run() throws Throwable;
    }

    public static <T> T log(String title, ThrowableRunnable<T> runnable) {
        return Allure.step(title, runnable::run);
    }

    public static void log(String title, ThrowableVoidRunnable runnable) {
        Allure.step(title, runnable::run);
    }
}
