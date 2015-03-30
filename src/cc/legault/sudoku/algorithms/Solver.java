package cc.legault.sudoku.algorithms;

import cc.legault.sudoku.Sudoku;

public interface Solver {
	public SudokuSolution solve(Sudoku givenSudoku);
}
