package com.maurice.app.cube;

import android.graphics.Point;

import com.maurice.app.cube.utils.Logg;
import com.maurice.app.cube.ImageParser.GenUtils;

import java.util.ArrayList;

/**
 *  This function of this module is to generate solution to sudoku puzzle in the most efficient wat
 *  The input is generally a nxn matrix and output is the solved version of it
 */

public class SudokuAI {
    static String TAG = "SUDOKU-AI" ;

    public static int[][] getSolved(int[][] question){
        int[][] result = new int[9][9];
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++) {
                result[i][j] = question[i][j];
            }
        }

        long start = System.currentTimeMillis();
        //Solve through normal
        solveWithoutGuessing(result);

        Logg.d(TAG, "SOLVED IN :" + (System.currentTimeMillis() - start) + " ms");

//        isSolvable(question);

        return result;
    }

    private static void solveWithoutGuessing(int[][] result) {

        //Get initial probables
        ArrayList<Integer>[][] probables = reformProbablesArray(result);

        //solve using probables
        while(solveOneCellFromProbables(result, probables)){
            int count = getUnsolvedCellsNum(result);
            Logg.d(TAG,"Found one more Cell result, probables count : "+count);

            //Reform probables
            probables = reformProbablesArray(result);
        };

        GenUtils.printBoard(result);
        probables = reformProbablesArray(result);

        if(getUnsolvedCellsNum(result)!=0){
            //Solve furthur using Backtracing and guessing
            isSolvable(result,probables);
        }
    }

    private static int getUnsolvedCellsNum(int[][] result){
        int count = 0;
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++) {
                if(result[i][j]==0) count+=1;
            }
        }
        return count;
    }

    private static boolean solveOneCellFromProbables(int[][] result, ArrayList<Integer>[][] probables) {

        //Search for cells where only one digit is possible
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++) {
                if(probables[i][j].size()==1){
                    result[i][j] =  probables[i][j].get(0);
                    return true;
                };
            }
        }

        //try finding digits that are only possible at one cell in a row
        for(int n=1;n<10;n++) {
            for (int i = 0; i < 9; i++) {
                int count = 0;
                int jFound = -1;
                for (int j = 0; j < 9; j++) {
                    if (probables[i][j].contains(n)){
                        count++;
                        jFound = j;
                    }
                }
                if(count == 1){
//                    Logg.d(TAG,"Found digit from rows match");
                    result[i][jFound] = n;
                    return true;
                }
            }
        }

        //try finding digits that are only possible at one cell in a column
        for(int n=1;n<10;n++) {
            for (int i = 0; i < 9; i++) {
                int count = 0;
                int jFound = -1;
                for (int j = 0; j < 9; j++) {
                    if (probables[j][i].contains(n)){
                        count++;
                        jFound = j;
                    }
                }
                if(count == 1){
//                    Logg.d(TAG,"Found digit from cols match");
                    result[jFound][i] = n;
                    return true;
                }
            }
        }


        //try finding digits that are only possible at one cell in a grid
        for(int n=1;n<10;n++) {
            for (int p = 0; p < 3; p++) {
                for (int q = 0; q < 3; q++) {
                    //Each grid

                    int count = 0;
                    int iFound = -1;
                    int jFound = -1;
                    for (int i = 3*p; i < 3*p+3; i++) {
                        for (int j = 3*q; j < 3*q+3; j++) {
                            if (probables[i][j].contains(n)){
                                count++;
                                iFound = i;
                                jFound = j;
                            }

                        }

                    }

                    if(count == 1){
//                        Logg.d(TAG,"Found digit from grids match");
                        result[iFound][jFound] = n;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static ArrayList<Integer>[][] reformProbablesArray(int[][] result){
        //Initiate probables Map
//        Logg.d(TAG,"Initiating probables Map...");
        ArrayList<Integer>[][] probables = new ArrayList[9][9];
        for(int i=0;i<9;i++) {
            for (int j = 0; j < 9; j++) {
                probables[i][j] = new ArrayList<>();
            }
        }

//        Logg.d(TAG,"Creating probables map for start...");
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(result[i][j]!=0) continue;
                for(int n=1;n<10;n++) {
                    result[i][j] = n;
                    if(!hasConflict(result)){
                        probables[i][j].add(n);
                    }
                    result[i][j] = 0;
                }
            }
        }

        return probables;
    }


    public static boolean isSolvable(int[][] result, ArrayList<Integer>[][] probables) {
        String text = "";
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(result[i][j]!=0) text +=result[i][j];
                else text +="-";
            }
            text +=" ";
        }
//        Logg.d(TAG,"STATUS : "+text);

        //Find row, col of an unassigned cell
        Point cell = getUnassignedCell(result);

        //If there is none, return true
        if(cell==null){
            if(!hasConflict(result)){
                Logg.d(TAG,"COMPLETELY SOLVED");
                return true;
            }
            return false;
        }
        //For digits from 1 to 9
        for(int i : probables[cell.x][cell.y]){
//        for(int i=1;i<10;i++){

            //If there is no conflict for digit at row,col assign digit to row,col and recursively try fill in rest of grid
            result[cell.x][cell.y] = i;
            if(!hasConflict(result)){
                if(isSolvable(result, probables)){
                    //If recursion successful, return true
                    //SOLVED
//                    Logg.d(TAG,"SOLVED");
                    return true;
                }
            }else{
            //Else, remove digit and try another
                result[cell.x][cell.y] = 0;
            }
            result[cell.x][cell.y] = 0;
        }

        //If all digits have been tried and nothing worked, return false
        return false;




    }

    private static boolean hasConflict(int[][] result) {
        //check rows
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < result.length; i++) {
            temp.clear();
            for (int j = 0; j < result[0].length; j++) {
                if(result[i][j]!=0){
                    if(temp.contains(result[i][j])){
                        return true;
                    }
                    temp.add(result[i][j]);
                }

            }
        }

        //check columns
        for (int i = 0; i < result.length; i++) {
            temp.clear();
            for (int j = 0; j < result[0].length; j++) {
                if(result[j][i]!=0){
                    if(temp.contains(result[j][i])){
                        return true;
                    }
                    temp.add(result[j][i]);
                }

            }
        }

        //check grid
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ArrayList<Integer> temp3 = new ArrayList<>();
                for (int m = 3*i; m < 3*i+3; m++) {
                    for (int n = 3*j; n < 3*j+3; n++) {
                        if(result[m][n]!=0){
                            if(temp3.contains(result[m][n])){
                                return true;
                            }
                            temp3.add(result[m][n]);
                        }

                    }

                }
            }
        }

        return false;
    }


    public static Point getUnassignedCell(int[][] result){
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                if(result[i][j]==0) return new Point(i,j);
            }
        }
        return null;
    }

}
