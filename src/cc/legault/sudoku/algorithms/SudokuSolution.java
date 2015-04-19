package cc.legault.sudoku.algorithms;

import cc.legault.sudoku.Sudoku;

public class SudokuSolution {

    Sudoku completed;
    long time; // In millisecond;

    public SudokuSolution(Sudoku sudoku, long time) {
        completed = sudoku;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public Sudoku getSudoku() {
        return completed;
    }
}
