package cc.legault.sudoku.algorithms;

import java.util.LinkedList;
import java.util.Stack;

import cc.legault.sudoku.Location;
import cc.legault.sudoku.Sudoku;

public class ConstraintPropagationAlgorithm implements Solver {

	@Override
	public SudokuSolution solve(Sudoku givenSudoku) {
		long init = System.currentTimeMillis();

		Sudoku sudoku = givenSudoku.clone();
		sudoku.setName(sudoku.getName() + " - Solution");

		LinkedList<Short>[][] workingSet = initWorkingSet(sudoku.getBoardSize());
		setInitialWorkingSetValues(sudoku, workingSet);
		
		// Keep applying constraints until no progress is made
		boolean hasImproved = true;
		while(hasImproved) {
			hasImproved = applyConstraints(workingSet);
		}
		
		// Solve remaining using DFS
		sudoku = sudokuDepthFirstSearch(sudoku, workingSet);
		
		return new SudokuSolution(sudoku, System.currentTimeMillis() - init);
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
								
								if(!hasImproved) {
									hasImproved = verticalImproved;
								}
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