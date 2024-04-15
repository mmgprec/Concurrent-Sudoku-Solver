import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SolverThread extends Thread{
    private int xpos;
    private int ypos;
    private int sudoku[][];
    private HashSet<Integer> state;
    SolverThread(int[][] sudoku,int xpos,int ypos) {
        this.xpos=xpos;
        this.ypos=ypos;
        this.sudoku=sudoku;
        this.state= new HashSet<>(Set.of(1,2,3,4,5,6,7,8,9));
        // for(int i=1;i<=9;i++) {
        //     this.state.add(i);
        // }
    }
    public void run() {
        while(true) {
            // get access to this.sudoku
            synchronized(this.sudoku) {
                // start validations and update the state
                check();
                // check if state is solved (if one element exists)
                if(this.state.size()==1) {
                    this.sudoku[xpos][ypos]=getValue();
                    this.sudoku.notify();
                    // if solved quit the thread
                    break;
                }
                else{
                    // else wait
                    try {
                        this.sudoku.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {this.join();} catch (InterruptedException e) {e.printStackTrace();}
    
    }
    public int getValue() {
        // return state.stream().findFirst().get();
        if (state.size()==1) {
            Iterator<Integer> iterator = state.iterator();
            int value = iterator.next(); // Get the first (and only) element
            iterator.remove(); // Remove the element from the set
            return value;
        } else {
            throw new IllegalStateException("Set is empty");
        }
    }
    private void check() {
        this.validateRow();
        this.validateColumn();
        this.validateGrid();
    }

    private void validateRow() {
        for (int i = 0; i < 9; i++) {
            if(this.ypos!=i && this.sudoku[this.xpos][i] != 0) {
                this.state.remove(this.sudoku[this.xpos][i]);
            }
        }
    }

    private void validateColumn() {
        for (int i = 0; i < 9; i++) {
            if(this.xpos!=i && this.sudoku[i][this.ypos] != 0) {
                this.state.remove(this.sudoku[i][this.ypos]);
            }
        }
    }

    private void validateGrid() {
        int si = (this.xpos / 3) * 3;
        int sj = (this.ypos / 3) * 3;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(!(this.xpos==si+i && this.ypos==sj+j) && this.sudoku[si+i][sj+j] != 0) {
                    this.state.remove(this.sudoku[si+i][sj+j]);
                }
            }
        }
    }
}