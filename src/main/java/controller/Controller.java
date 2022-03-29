package controller;

import java.util.Scanner;

public abstract class Controller {
    protected static Scanner scanner = new Scanner(System.in);

    public abstract Controller run();
}
