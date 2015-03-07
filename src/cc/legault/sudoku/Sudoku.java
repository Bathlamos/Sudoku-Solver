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
                sb.append(cell);
        }

        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
