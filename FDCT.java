import java.util.*; 

public class FDCT
{
	public static void main(String[] args) {
		int matrix[][] = { { 188, 180, 155, 149, 179, 116, 86, 96 }, 
                         { 168, 179, 168, 174, 180, 111, 86, 95 }, 
                         { 150, 166, 175, 189, 165, 101, 88, 97 }, 
                         { 163 , 165, 179, 184, 135, 90, 91, 96 }, 
                         { 170, 180, 178, 144, 102, 87, 91, 98 }, 
                         { 175, 174, 141, 104, 85, 83, 88, 96 }, 
                         { 153, 134, 105, 82, 83, 87, 92, 96  }, 
                         { 117, 104, 86, 80, 86, 90, 92, 103 } }; 

        double[][] dctMat = dctTransform(matrix);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j < 7)
                    System.out.print(dctMat[i][j] + " & ");
                else
                    System.out.print(dctMat[i][j]);
            }
            System.out.println("\\\\");
        }
        System.out.println();

       double[][] quantMat = quantization(dctMat, 100); 
       for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j < 7)
                    System.out.print(quantMat[i][j] + " & ");
                else
                    System.out.print(quantMat[i][j]);
            }
            System.out.println("\\\\");
        }
        System.out.println();

        double[][] zigzagMat = constructZigZagMat(quantMat);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j < 7)
                    System.out.print(zigzagMat[i][j] + " & ");
                else
                    System.out.print(zigzagMat[i][j]);
            }
             System.out.println("\\\\");
        }
        System.out.println();

	}

	public static double[][] dctTransform(int imgMat[][]) 
    { 
        int n = 8,m = 8; 
        double pi = 3.142857;
        int u, v, x, y; 

        //Apply level shift by subtracting 128
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                imgMat[i][j] -= 128;
                System.out.print(imgMat[i][j] + "& ");
            }
            System.out.println("\\");
        }
        System.out.println();

   
        // dct will store the discrete cosine transform 
        double[][] dct = new double[m][n]; 
   
        double cu, cv, dct1, sum; 
   
        for (u = 0; u < m; u++)  
        { 
            for (v = 0; v < n; v++)  
            { 
                // ci and cj depends on frequency as well as 
                // number of row and columns of specified matrix 
                if (u == 0) 
                    cu = 1 / Math.sqrt(m); 
                else
                    cu = Math.sqrt(2) / Math.sqrt(m); 
                      
                if (v == 0) 
                    cv = 1 / Math.sqrt(n); 
                else
                    cv = Math.sqrt(2) / Math.sqrt(n); 
   
                sum = 0; 
                for (x = 0; x < m; x++)  
                { 
                    for (y = 0; y < n; y++)  
                    { 
                        dct1 = imgMat[x][y] *  
                               Math.cos((2 * x + 1) * u * pi / (2 * m)) *  
                               Math.cos((2 * y + 1) * v * pi / (2 * n)); 
                        sum = sum + dct1; 
                    } 
                } 
                dct[u][v] = Math.round(cu * cv * sum * 100.0)/100.0; 
            } 
        } 
   
        return dct;
    } //End dctTransform

    public static double[][] quantization(double[][] dctMat, int step)
    {
        double[][] quantMat = dctMat;
        for (int i = 0; i < quantMat.length; i++) {
            for (int j = 0; j < quantMat[0].length; j++) {
                quantMat[i][j] = Math.round(dctMat[i][j]/step);
            }
        }
        return quantMat;
    }

    //Construct ZigZag Matrix
    public static double[][] constructZigZagMat(double[][] quantMat)
    {
        int m = quantMat.length;
        int n = quantMat[0].length;

        double[][] zigzagMat = new double[m][n];
        int row = 0, col = 0, row_i = 0, col_i = 0;
        boolean rowIncrementFlag = false;

        int mn = m == n ? m : Math.min(m, n);
        for (int len = 1; len <= mn; ++len) {
            for (int i = 0; i < len; ++i) {
                if (col_i == mn) {
                    col_i = 0;
                    row_i++;
                }

                zigzagMat[row_i][col_i] = quantMat[row][col];

                // increment col in zigzag matrix
                col_i++;

                if (i + 1 == len)
                    break;
                if (rowIncrementFlag) {
                    ++row;
                    --col;
                } else {
                    --row;
                    ++col;
                }
            }

            if (len == mn)
                break; //TODO -- Here algorith not yet implemented

            if (rowIncrementFlag) {
                ++row;
                rowIncrementFlag = false;
            } else {
                ++col;
                rowIncrementFlag = true;
            }
        }

        //Check filling array index
        if (col_i == mn) {
            row_i++;
            col_i = 0;
        }


        //Update indexes
        if (row == 0) { 
            if (col == m - 1) 
                ++row; 
            else
                ++col; 
            rowIncrementFlag = true; 
        } else {
            if (row == n - 1)
                ++col; 
            else
                ++row; 
            rowIncrementFlag = false; 
        }


        // Fill in the next half of the zizag pattern 
        int MAX = Math.max(m, n) - 1;
        for (int len, diag = MAX; diag > 0; --diag) {
            if (diag > mn)
                len = mn;
            else
                len = diag;
            for (int i = 0; i < len; ++i) {
                if (col_i == mn) {
                    col_i = 0;
                    row_i++;
                }

                zigzagMat[row_i][col_i] = quantMat[row][col];

                // increment col in zigzag matrix
                col_i++;

                if (i + 1 == len)
                    break;
                if (rowIncrementFlag) {
                    ++row;
                    --col;
                } else {
                    ++col;
                    --row;
                }
            }

            if (row == 0 || col == m - 1) {
                if (col == m - 1)
                    ++row;
                else
                    ++col;
                rowIncrementFlag = true;
            } else if (col == 0 || row == n - 1) {
                if (row == n - 1)
                    ++col;
                else
                    ++row;
                rowIncrementFlag = false;
            }
        }
        return zigzagMat;

    } //End of constructZigZagMat()

}//End Main Class