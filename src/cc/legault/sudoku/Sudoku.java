package cc.legault.sudoku;

public class Sudoku {

    //Square matrix
    private final short[][] board;
    private String name;

    public Sudoku(String name, short[][] board){
        this.setName(name);
        this.board = board;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        for(short[] row: board) {
            sb.append('\n');
            for (short cell : row)
                sb.append(cell + " ");
        }

        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getValue(Location location){
        return board[location.getI()][location.getJ()];
    }

    public void setValue(Location location, short value){
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