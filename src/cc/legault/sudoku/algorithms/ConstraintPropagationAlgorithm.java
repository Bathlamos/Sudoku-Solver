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

//		LinkedList<Short>[][] workingSet = initWorkingSet(sudoku.getBoardSize());
//		setInitialWorkingSetValues(sudoku, workingSet);
//
//		// Keep applying constraints until no progress is made
//		boolean hasImproved = true;
//		while(hasImproved)
//			hasImproved = applyConstraints(workingSet);
//
//		// Solve remaining using DFS
//		sudoku = sudokuDepthFirstSearch(sudoku, workingSet);
		
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

	private boolean applyConstraints(LinkedList<Short>[][] workingSet) {
        boolean hasImproved = false;
		
		boolean horizontalImproved = false;
		boolean verticalImproved = false;
		boolean subsquareImproved = false;
		
		for(int i = 0; i < workingSet.length; i++) {
			for(int j = 0; j < workingSet.length; j++) {
				if(workingSet[i][j].size() == 1) {
					Short cellValue = workingSet[i][j].get(0);

					// Remove cellValue from every cell in the same column
					for(int x = 0; x < workingSet.length; x++) {
						if(x != i) {
							if(workingSet[x][j].size() != 1) {
								verticalImproved = workingSet[x][j].remove(cellValue);
								
								if(!hasImproved)
									hasImproved = verticalImproved;
							}
						}
					}

					// Remove cellValue from every cell in the same row
					for(int y = 0; y < workingSet.length; y++) {
						if(y != j) {
							if(workingSet[i][y].size() != 1) {
								horizontalImproved = workingSet[i][y].remove(cellValue);
								
								if(!hasImproved) {
									hasImproved = horizontalImproved;
								}
							}
						}
					}

					// Remove cellValue from every cell in the same subsquare
					int subSize = (int) Math.sqrt(workingSet.length); // Subquare size (sudoku must always be perfect squares)
					int baseX = i - (i % subSize);
			        int baseY = j - (j % subSize);
					
					for(int x = baseX; x < baseX + subSize; x++) {
						for(int y = baseY; y < baseY + subSize; y++) {
							if(x != i || y != j) {
								if(workingSet[x][y].size() != 1) {
									subsquareImproved = workingSet[x][y].remove(cellValue);
									
									if(!hasImproved) {
										hasImproved = subsquareImproved;
									}
								}
							}
						}
					}
				}
			}
		}

		return hasImproved;
	}

	private Sudoku sudokuDepthFirstSearch(Sudoku sudoku, LinkedList<Short>[][] workingSet) {
		Stack<Location> locations = new Stack<Location>();
		Stack<Integer> indexes = new Stack<Integer>();
		
		Location firstEmptyLocation = sudoku.getFirstEmptyLocation();
        if(firstEmptyLocation != null)
            locations.add(firstEmptyLocation);

        int index = 0;
        
        while(!locations.isEmpty()){
            Location location = locations.peek();
            LinkedList<Short> values = workingSet[location.getI()][location.getJ()];
            boolean valid = false;
            
            for(int i = index; i < values.size(); i++) {
            	if(sudoku.isValidAssignment(location, values.get(i))) {
            		sudoku.setValue(location, values.get(i));
            		firstEmptyLocation = sudoku.getFirstEmptyLocation(location);
            		
            		if(firstEmptyLocation != null) {
            			locations.push(firstEmptyLocation);
            			indexes.push(i+1);
            			index = 0;
            		} else {
            			return sudoku;
            		}
            		
            		valid = true;
            		break; // Move on to next location
            	}
            }
            
            if(!valid) {
        		sudoku.setValue(location, (short) 0);
        		locations.pop();
        		index = indexes.pop();
        	}
        }
        return sudoku;
	}

	@SuppressWarnings("unchecked")
	private LinkedList<Short>[][] initWorkingSet(int size) {
		LinkedList<Short>[][] workingSet = new LinkedList[size][size];
		
		for(int i = 0; i < workingSet.length; i++) {
			for(int j = 0; j < workingSet.length; j++) {
				workingSet[i][j] = new LinkedList<Short>();
			}
		}
		
		return workingSet;
	}
	
	private LinkedList<Short> getAllOptions(int size) {
		LinkedList<Short> allOptions = new LinkedList<Short>();
		
		for(short i = 1; i <= size; i++) {
			allOptions.add(i);
		}
		
		return allOptions;
	}
	
	@SuppressWarnings("unchecked")
	private void setInitialWorkingSetValues(Sudoku sudoku, LinkedList<Short>[][] workingSet) {
		LinkedList<Short> allOptions = getAllOptions(sudoku.getBoardSize());
		
		for(int i = 0; i < sudoku.getBoardSize(); i++) {
			for(int j = 0; j < sudoku.getBoardSize(); j++) {
				Location location = new Location(i, j);

				short cellValue = sudoku.getValue(location);

				// If the cell is empty, add all possible options, otherwise add the value found in the cell
				if(cellValue == 0) {
					workingSet[location.getI()][location.getJ()] = (LinkedList<Short>) allOptions.clone();
				} else {
					workingSet[location.getI()][location.getJ()].add(cellValue);
				}
			}
		}
	}
	
	public static void printWorkingSet(LinkedList<Short>[][] set) {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				System.out.print(set[i][j] + "          ");
			}

			System.out.println("");
		}
	}
}