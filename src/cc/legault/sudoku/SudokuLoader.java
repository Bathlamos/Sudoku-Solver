package cc.legault.sudoku;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class SudokuLoader {

    private SudokuLoader() {
    }

    public static Sudoku[] fromFile(String path, Charset encoding) throws IOException {
        try (InputStream in = new FileInputStream(new File(path));
             Reader reader = new InputStreamReader(in, encoding);
             // buffer for efficiency
             BufferedReader buffer = new BufferedReader(reader)) {

            return parseSudoku(buffer);

        }
    }

    private static Sudoku[] parseSudoku(BufferedReader reader) throws IOException{
        List<Sudoku> sudokus = new ArrayList<>();
        int i = 0;
        String line, name = null;
        short[][] board = new short[9][];
        while ((line = reader.readLine()) != null) {
            if(name == null)
                name = line;
            else{
                board[i] = new short[9];
                for(int j = 0; j < 9; j++)
                    board[i][j] = (short) (line.charAt(j) - '0');
                i ++;
                if(i == 9){
                    sudokus.add(new Sudoku(name, board));
                    board = new short[9][];
                    i = 0;
                    name = null;
                }
            }
        }
        return sudokus.toArray(new Sudoku[sudokus.size()]);
    }

}
