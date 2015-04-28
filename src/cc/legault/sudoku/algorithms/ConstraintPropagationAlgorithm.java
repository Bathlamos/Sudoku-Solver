package cc.legault.sudoku.algorithms;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

import cc.legault.sudoku.Location;
import cc.legault.sudoku.Sudoku;

public class ConstraintPropagationAlgorithm implements Solver {

	@Override
	public SudokuSolution solve(Sudoku givenSudoku) {
		long init = System.currentTimeMillis();

        Sudoku sudoku = givenSudoku.clone();
		sudoku.setName(sudoku.getName() + " - Solution");

        LinkedList<Short> possibilitiesAtACell = new LinkedList<>();
        for(short i = 1; i <= sudoku.getBoardSize(); i++)
            possibilitiesAtACell.add(i);

        TreeSet<Short>[][] constraints = new TreeSet[sudoku.getBoardSize()][sudoku.getBoardSize()];
        for(int i = 0; i < constraints.length; i++)
            for(int j = 0; j < constraints[i].length; j++)
                constraints[i][j] = new TreeSet<>(possibilitiesAtACell);

        //Build the initial constraints
        Location firstFilledLocation = sudoku.getFirstFilledLocation();
        while(firstFilledLocation != null){
            alterConstraints(sudoku, constraints, firstFilledLocation);
            Location nextLocation = sudoku.nextLocation(firstFilledLocation);
            if(nextLocation != null)
                firstFilledLocation = sudoku.getFirstFilledLocation(sudoku.nextLocation(firstFilledLocation));
            else
                firstFilledLocation = null;
        }

        ReturnType type = propagateConstraintsAndFork(sudoku, constraints);
		
		return new SudokuSolution(type.sudoku, System.currentTimeMillis() - init);
	}

    private static final class ReturnType{
        private Sudoku sudoku;
        private TreeSet<Short>[][] constraints;
    }

    private ReturnType propagateConstraintsAndFork(Sudoku sudoku, TreeSet<Short>[][] constraints){
        Location minConstraint = null;
        minConstraint = getMinConstraint(sudoku, constraints);
        do{
            TreeSet<Short> constraint = constraints[minConstraint.getI()][minConstraint.getJ()];
            if(constraint.size() == 1){
                sudoku.setValue(minConstraint, constraint.first());
                alterConstraints(sudoku, constraints, minConstraint);
            }else if (constraint.size() == 0)
                return null;
            else{
                for(Short s: constraint){
                    Sudoku newSudoku = sudoku.clone();
                    TreeSet<Short>[][] newConstraint = new TreeSet[sudoku.getBoardSize()][sudoku.getBoardSize()];
                    for(int i = 0; i < newConstraint.length; i++)
                        for(int j = 0; j < newConstraint[i].length; j++)
                            newConstraint[i][j] = (TreeSet<Short>) constraints[i][j].clone();

                    newSudoku.setValue(minConstraint, s);
                    alterConstraints(newSudoku, newConstraint, minConstraint);
                    ReturnType result = propagateConstraintsAndFork(newSudoku, newConstraint);
                    if(result != null)
                        return result;
                }
                return null;
            }

            minConstraint = getMinConstraint(sudoku, constraints);

        }while(minConstraint != null);

        ReturnType type = new ReturnType();
        type.constraints = constraints;
        type.sudoku = sudoku;

        return type;
    }

    private static Location getMinConstraint(Sudoku sudoku, TreeSet<Short>[][] constraints){
        int minLength = Integer.MAX_VALUE;
        Location minLocation = null;
        for(int i = 0; i < constraints.length; i++)
            for(int j = 0; j < constraints[i].length; j++)
                if(constraints[i][j].size() < minLength && sudoku.getValue(new Location(i, j)) == 0){
                    minLocation = new Location(i, j);
                    minLength = constraints[i][j].size();
                }
        return minLocation;
    }

    private static void alterConstraints(Sudoku sudoku, TreeSet<Short>[][] constraints, Location loc){
        short value = sudoku.getValue(loc);

        constraints[loc.getI()][loc.getJ()].clear();
        constraints[loc.getI()][loc.getJ()].add(sudoku.getValue(loc));

        if(value == 0)
            return;

        for(Location[] locations: sudoku.getDependentLocations(loc)) {

            LinkedList<Location> removedLocation = new LinkedList<>();
            for (Location d : locations) {
                boolean removed = constraints[d.getI()][d.getJ()].remove(value);
                if (removed)
                    removedLocation.add(d);
            }
            //For each cell dependent on the dependent location,
            //we remove all other possibilities if it is the only one
            //of its column, row or subsquare with a number
            if(!removedLocation.isEmpty()) {
                for (Location d : locations) {
                    for(Location[] dependentOnDependent: sudoku.getDependentLocations(d)){

                        Location onlyChoice = null;
                        for(Location checkForSingle: dependentOnDependent){
                            if (constraints[checkForSingle.getI()][checkForSingle.getJ()].contains(value)) {
                                if (onlyChoice == null)
                                    onlyChoice = checkForSingle;
                                else {
                                    onlyChoice = null;
                                    break;
                                }
                            }
                        }

                        if (onlyChoice != null && !onlyChoice.equals(loc)) {
                            TreeSet<Short> cellConstraint = constraints[onlyChoice.getI()][onlyChoice.getJ()];
                            cellConstraint.clear();
                            cellConstraint.add(value);
                        }
                    }

                }
            }
        }
    }

}