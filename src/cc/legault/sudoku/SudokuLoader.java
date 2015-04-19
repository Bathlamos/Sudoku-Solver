package cc.legault.sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class SudokuLoader {
    public static Sudoku[] fromFile(String path, Charset encoding) throws IOException {
        try (InputStream in = new FileInputStream(new File(path));
             Reader reader = new InputStreamReader(in, encoding);
             // buffer for efficiency
             BufferedReader buffer = new BufferedReader(reader)) {
        		return parseSudoku(buffer);
        }
    }

    private static Sudoku[] parseSudoku(BufferedReader reader) throws IOException {
        List<Sudoku> sudokus = new ArrayList<>();
        int i = 0;
        String line, name = null;
        
        // First line of each input file contains the size of the boards contained
        short boardSize = Short.parseShort(reader.readLine()); 
        
        short[][] board = new short[boardSize][];;
        
        while ((line = reader.readLine()) != null) {
            if(name == null) {
                name = line;
            } else {
            	// Numbers in sudoku grid input files are delimited by spaces
            	String[] row = line.split(" ");
            	
                board[i] = new short[boardSize];
                
                // Add current line to board
                for(int j = 0; j < row.length; j++) {
                    board[i][j] = Short.parseShort(row[j]);
                }
                
                // Increment to move on to next row
                i++;
                
                // Move on to next Sudoku if completed
                if(i == row.length) {
                    sudokus.add(new Sudoku(name, board));
                    board = new short[boardSize][];
                    i = 0;
                    name = null;
                }
            }
        }
        
        return sudokus.toArray(new Sudoku[sudokus.size()]);
    }

}
