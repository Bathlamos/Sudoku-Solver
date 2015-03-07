package cc.legault.sudoku;

import cc.legault.sudoku.algorithms.BacktrackingAlgorithm;
import cc.legault.sudoku.algorithms.Solver;
import cc.legault.sudoku.algorithms.SudokuSolution;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {
        try {
            Sudoku[] sudokus = SudokuLoader.fromFile("resources/ProjectEuler#096.txt", StandardCharsets.UTF_8);
            Solver solver = new BacktrackingAlgorithm();

            System.out.println("The input:");
            System.out.println(sudokus[0].toString());
            System.out.println();
            System.out.println("And the solution:");
            System.out.println(solver.solve(sudokus[0]).getSudoku().toString());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
