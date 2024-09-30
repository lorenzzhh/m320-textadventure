package ch.axa;

import java.util.Scanner;

public class Console {

    private final Scanner scanner;

    public Console() {
        scanner = new Scanner(System.in);
    }

    public String readLine() {
        System.out.print("> ");  // Prompt für den Benutzer
        return scanner.nextLine();
    }

    public void writeLine(String message) {
        System.out.println(message);
    }

    public void close() {
        scanner.close();
    }
}

