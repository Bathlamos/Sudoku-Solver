package cc.legault.sudoku;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import cc.legault.sudoku.algorithms.BacktrackingAlgorithm;
import cc.legault.sudoku.algorithms.ConstraintPropagationAlgorithm;
import cc.legault.sudoku.algorithms.Solver;
import cc.legault.sudoku.algorithms.SudokuSolution;

public class Main {

    public static void main(String[] args) {
        try {
            Sudoku[] sudokus = SudokuLoader.fromFile("resources/ProjectEuler#096.txt", StandardCharsets.UTF_8);
//            Sudoku[] sudokus = SudokuLoader.fromFile("resources/Hexudoku.txt", StandardCharsets.UTF_8);
            
            Solver solver = new BacktrackingAlgorithm();
            Solver solver2 = new ConstraintPropagationAlgorithm();

            long btAverageTime = 0;
            long cpAverageTime = 0;

            long btMaxTime = 0;
            long cpMaxTime = 0;

            long btMinTime = Long.MAX_VALUE;
            long cpMinTime = Long.MAX_VALUE;

            for(int i = 0; i < sudokus.length; i++) {
            	System.out.println(sudokus[i]);
            	
            	SudokuSolution solution = solver.solve(sudokus[i]);
            	SudokuSolution solution2 = solver2.solve(sudokus[i]);

                System.out.println("Backtracking total time for Grid" + (i+1) + ": " + solution.getTime() + "ms");
                System.out.println("Constraint total time for Grid" + (i+1) + ": " + solution2.getTime() + "ms");
                System.out.println(solution.getSudoku());
                System.out.println();

                btAverageTime += solution.getTime();
                cpAverageTime += solution2.getTime();

                if(solution.getTime() > btMaxTime) {
                	btMaxTime = solution.getTime();
                }

                if(solution.getTime() < btMinTime) {
                	btMinTime = solution.getTime();
                }

                if(solution2.getTime() > cpMaxTime) {
                	cpMaxTime = solution2.getTime();
                }

                if(solution2.getTime() < cpMinTime) {
                	cpMinTime = solution2.getTime();
                }
            }

            btAverageTime /= sudokus.length;
            cpAverageTime /= sudokus.length;

            System.out.println("Backtracking max time: " + btMaxTime + "ms");
            System.out.println("Backtracking average time: " + btAverageTime + "ms");
            System.out.println("Backtracking min time: " + btMinTime + "ms");

            System.out.println();

            System.out.println("Constraint propagation max time: " + cpMaxTime + "ms");
            System.out.println("Constraint propagation average time: " + cpAverageTime + "ms");
            System.out.println("Constraint propagation min time: " + cpMinTime + "ms");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
