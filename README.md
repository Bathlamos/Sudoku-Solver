###Executive Summary 
The game of Sudoku is NP-Complete for grid sizes of width n9. A variety of algorithms exist to solve a Sudoku grid, and the current project compares two of them. The first algorithm uses a backtracking approach guaranteed to finish, but possibly checking every one of the 6.671021 combinations [1], while the second uses inferencing to limit unfeasible possibilities.
###Problem Statement
A Sudoku puzzle is a 9x9 grid that is subdivided into 3x3 sub squares. Each of the sub squares may contain a number from [1, 9]. A puzzle is given with sub squares that already contain a number: a solution consists of attributing a number from [1-9] to all the other sub squares such that the numbers for all the sub squares in the same column, row and 3x3 sub square are unique. A Sudoku Puzzle typically only allows for a unique solution. The Sudoku Puzzle Problem is a decision problem and can be generalized to an n square x n square grid divided in n x nsub squares. This version of the problem has been shown to be NP-Complete [6]. It has also been shown to be translatable from the 3-SAT Problem, and Vertex Coloring Problem using n2colors [6] (e.g. a 9-colouring for a standard 9x9 grid). Knowing that both problems are NP-Complete (Vertex Coloring is NP-Complete when more than 2 colors are needed) implies that the Sudoku Puzzle Problem is NP-Complete as well (forn3).

The current project aims at comparing the implementation of two algorithms, one using a backtracking strategy, and the other, a constraint propagation strategy, to solve various Sudoku grids. The running time will be the main metric measured.

###Compared Algorithms
The algorithms chosen are not the result of research papers, but rather well-known programming strategies, for which several pseudo-code implementation exist.

**1. Backtracking Strategy.** Sub squares of the Sudoku grid are filled in order with numbers that do not violate the row, column and 3x3 sub square constraint of unicity. When all numbers for a given sub square violate the constraint, an unfeasible assignment of numbers is reached, which means that we backtrack to the last feasible assignment, and try with a different set of values. This algorithm, although known to work fast in practice, may have to analyze every possible assignment of number before finding a feasible one, in its worst case. Though several implementations exist for the algorithm. The project will focus on Johansson and Broström’s implementation [2].

**2. Constraint Propagation Strategy.** Sudoku Puzzles can be solved using two rules: (1) If a number is set in a sub square, it is removed from possible assignment of other sub square in the row, column and 3x3 subsquare, (2) If a sub square only has one possible assignment, then that number is set to the sub square. Using these rules does not allow to solve every Sudoku board, but eliminates a high number of unfeasible assignments. A Depth-First Search approach is then used to try all possible values. A very low number of final assignments are tested, however there are no good runtime guarantees [4].

###Sample Data
Due to the relative facility of solving Sudoku boards, it follows that a high volume of data can be tested within the scope of the project. Therefore, a high variety of board will be tested, including: 

1. Boards that allow more than one solution.
2. Boards of various sizes.
3. Boards with a low/high number of initial assignments.
4. Boards known as difficult to compute.

Our data will come from several online resources (e.g. [4], [5]), which categorize puzzles according to their perceived difficulty by humans. This information will be used to validate the existence of a correlation between a puzzle’s difficulty for humans, and for computers.
###Evaluation method
A theoretical analysis of the time complexity of the algorithms will be contrasted with their execution time using sample data. The programs will be traced to identify how many atomic operations are executed, as well as to observe the flow of the program depending on the data. To better compare the relative performance of the algorithms under study, the same data will be analyzed by a brute-force approach, and by genetic algorithms approach, thought to be particularly effective with Sudoku boards. In the case of the backtracking algorithm, the number of boards computed which were later found to be unfeasible will also be recorded.

###References
[1] Felgenhauer B, Jarvis F. Enumerating possible Sudoku grids. [Internet]. 2005 [cited 2013 Mar 21]. Available from: http://www.afjarvis.staff.shef.ac.uk/sudoku/.
[2] Johansson S., Broström A. “Sudoku Solvers: Man versus Machine”, Bachelor Thesis, KTH Royal Institute of Technology, 2000.
[3] Norvig, P. Solving Every Sudoku Puzzle [Internet]. [cited 2015 Feb]. Available from: http://norvig.com/sudoku.html.
[4] Project Euler. [Internet]. [cited 2015 Feb]. Available from:https://projecteuler.net.
[5] Sudoku Shack. [Internet]. 2007 [cited 2015 Feb]. Available from: http://www.soduko.org/sudoku-list.php.
[6] Yato T., Seta T. “Complexity and Completeness of Finding Another Solution and Its Applications to Puzzles”, 2003.
