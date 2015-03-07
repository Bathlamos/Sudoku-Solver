package cc.legault.sudoku;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {
        try {
            Sudoku[] sudokus = SudokuLoader.fromFile("resources/ProjectEuler#096.txt", StandardCharsets.UTF_8);
            System.out.println(sudokus[0].toString());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
