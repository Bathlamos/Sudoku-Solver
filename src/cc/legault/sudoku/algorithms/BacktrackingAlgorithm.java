package cc.legault.sudoku.algorithms;

import cc.legault.sudoku.Location;
import cc.legault.sudoku.Sudoku;

import java.util.Stack;

public class BacktrackingAlgorithm implements Solver{

    @Override
    public SudokuSolution solve(Sudoku givenSudoku) {
        long init = System.currentTimeMillis();

        Sudoku sudoku = givenSudoku.clone();
        Stack<Location> locations = new Stack<>();

        Location firstEmptyLocation = sudoku.getFirstEmptyLocation();
        if(firstEmptyLocation != null)
            locations.add(firstEmptyLocation);

        while(!locations.isEmpty()){
            Location location = locations.peek();
            boolean valid = false;
            for(short i = (short) (sudoku.getValue(location) + 1); i <= 9 && !valid; i++)
                if(sudoku.isValidAssignment(location, i)){
                    sudoku.setValue(location, i);
                    firstEmptyLocation = sudoku.getFirstEmptyLocation(location);
                    if (firstEmptyLocation != null)
                        locations.push(firstEmptyLocation);
                    else
                        return new SudokuSolution(sudoku, System.currentTimeMillis() - init);
                    valid = true;
                }
            if(!valid) {
                sudoku.setValue(location, (short) 0);
                locations.pop();
            }
        }
        return new SudokuSolution(sudoku, System.currentTimeMillis() - init);
        //throw new RuntimeException("Sudoku is unsatisfiable");
    }
}
