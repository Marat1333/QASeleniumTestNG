package com.leroy.utils.file_manager;

import lombok.SneakyThrows;

import java.io.File;

public class FileWaiter extends Thread {
    private int timeOut;
    private File file;

    public FileWaiter(File file, int timeOut) {
        this.file = file;
        this.timeOut = timeOut;
    }

    @SneakyThrows
    @Override
    public void run() {
        long frequency = 500;
        long attemptCounter = timeOut * 1000 / frequency;
        long passedAttemptsCounter = 0;
        while (attemptCounter != passedAttemptsCounter) {
            if (file.exists()) {
                interrupt();
            } else {
                Thread.sleep(frequency);
            }
            passedAttemptsCounter++;
        }
    }
}
