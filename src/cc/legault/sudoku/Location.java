package cc.legault.sudoku;

public class Location {

    private int i;
    private int j;

    public Location(int i, int j){
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(i);
        sb.append(", ");
        sb.append(j);
        sb.append(")");
        return sb.toString();
    }
}
