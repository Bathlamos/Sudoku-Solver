package cc.legault.sudoku;

import java.util.ArrayList;
import java.util.TreeSet;

public class Sudoku {

    //Square matrix
    private final short[][] board;
    private String name;
    private int digitsFilled = 0;

    public Sudoku(String name, short[][] board){
        this.setName(name);
        this.board = board;

        //Check the number of non-empty digits
        for(int i = 0; i < board.length; i++)
            for(int j = 0; j < board[i].length; j++)
                if(board[i][j] != 0)
                    digitsFilled++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        int squareSize = (int) Math.sqrt(board.length);

        sb.append("\n┌");
        for(int i = board[0].length - 1; i >= 0; i--){
            sb.append("──");
            if(i != 0 && i % squareSize == 0)
                sb.append('┬');
        }
        sb.append('┐');

        int i = 0;
        for(short[] row: board) {
            sb.append("\n│");
            for (int j = 0; j < row.length; j++) {
                sb.append((row[j] == 0 ? "  " : row[j] + " "));
                if(j % squareSize == squareSize - 1)
                    sb.append("│");
            }

            i++;

            if(i % squareSize == 0 && i != board.length){
                sb.append("\n├");
                for(int k = row.length - 1; k >= 0; k--){
                    sb.append("──");
                    if(k != 0 && k % squareSize == 0)
                        sb.append('┼');
                }
                sb.append('┤');
            }
        }

        sb.append("\n└");
        for(i = board[0].length - 1; i >= 0; i--){
            sb.append("──");
            if(i != 0 && i % squareSize == 0)
                sb.append('┴');
        }
        sb.append('┘');

        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDigitsFilled() {
        return digitsFilled;
    }

    public short getValue(Location location){
        return board[location.getI()][location.getJ()];
    }

    public void setValue(Location location, short value){
        if(board[location.getI()][location.getJ()] != value){
            if(value == 0)
                digitsFilled--;
            else
                digitsFilled++;
        }
        board[location.getI()][location.getJ()] = value;
    }

    public boolean isValidAssignment(Location location, short value){
        int i = location.getI();
        int j = location.getJ();
        //Check the column
        for(int i2 = 0; i2 < board.length; i2++)
            if(board[i2][j] == value && i2 != i)
                return false;
        //Check the row
        for(int j2 = 0; j2 < board[0].length; j2++)
            if(board[i][j2] == value && j2 != j)
                return false;
        //Check the rest of the square
        int subsquareSize = (int) Math.sqrt(board.length);
        int baseI = i - (i % subsquareSize);
        int baseJ = j - (j % subsquareSize);
        for(int i2 = baseI; i2 < baseI + subsquareSize; i2++)
            for(int j2 = baseJ; j2 < baseJ + subsquareSize; j2++)
                if (board[i2][j2] == value && j2 != j && i2 != i)
                    return false;
        return true;
    }

    public Location getFirstEmptyLocation(){
        return getFirstEmptyLocation(new Location(0, 0));
    }

    public Location getFirstEmptyLocation(Location searchFrom){
        int j = searchFrom.getJ();
        for(int i = searchFrom.getI(); i < board.length; i++) {
            for (; j < board[i].length; j++)
                if (board[i][j] == 0)
                    return new Location(i, j);
            j = 0;
        }
        return null;
    }

    public Location nextLocation(Location l){
        int i = l.getI();
        int j = l.getJ();
        j++;
        if(j == board.length){
            j = 0;
            i++;
            if(i == board.length)
                return null;
        }
        return new Location(i, j);
    }

    public Location getFirstFilledLocation() {
        return getFirstFilledLocation(new Location(0, 0));
    }

    public Location getFirstFilledLocation(Location searchFrom){
        int j = searchFrom.getJ();
        for(int i = searchFrom.getI(); i < board.length; i++) {
            for (; j < board[i].length; j++)
                if (board[i][j] != 0)
                    return new Location(i, j);
            j = 0;
        }
        return null;
    }

    public Location[][] getDependentLocations(Location location){
        return new Location[][]{
                getDependentColumn(location),
                getDependentRow(location),
                getDependentSubsquare(location)};
    }

    public Location[] getDependentColumn(Location location){
        ArrayList<Location> dependent = new ArrayList<>();
        int i = location.getI();
        int j = location.getJ();

        //Get the column
        for(int i2 = 0; i2 < board.length; i2++)
            if(i2 != i)
                dependent.add(new Location(i2, j));

        return dependent.toArray(new Location[dependent.size()]);
    }

    public Location[] getDependentRow(Location location){
        ArrayList<Location> dependent = new ArrayList<>();
        int i = location.getI();
        int j = location.getJ();

        //Get the row
        for(int j2 = 0; j2 < board[0].length; j2++)
            if(j2 != j)
                dependent.add(new Location(i, j2));

        return dependent.toArray(new Location[dependent.size()]);
    }

    public Location[] getDependentSubsquare(Location location){
        ArrayList<Location> dependent = new ArrayList<>();
        int i = location.getI();
        int j = location.getJ();

        //Get the subsquare
        int subsquareSize = (int) Math.sqrt(board.length);
        int baseI = i - (i % subsquareSize);
        int baseJ = j - (j % subsquareSize);
        for(int i2 = baseI; i2 < baseI + subsquareSize; i2++)
            for(int j2 = baseJ; j2 < baseJ + subsquareSize; j2++)
                if (j2 != j || i2 != i)
                    dependent.add(new Location(i2, j2));

        return dependent.toArray(new Location[dependent.size()]);
    }

    public Sudoku clone() {
        return new Sudoku(name, clone2DShortArray(board));
    }

    public int getBoardSize() {
		return board.length;
    }

    private static short[][] clone2DShortArray(short[][] array) {
        int rows = array.length ;
        //clone the 'shallow' structure of array
        short[][] newArray =(short[][]) array.clone();
        //clone the 'deep' structure of array
        for(int row=0;row<rows;row++)
            newArray[row]=(short[]) array[row].clone();

        return newArray;
    }
}