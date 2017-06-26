package ar.edu.itba.cripto.visualSSS;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GaussianPruebas {

	// sample client
	public static void main(String[] args) {
		// Nice-looking matrix
		// int[][] input = {
		// {3, 1, 4, 1},
		// {5, 2, 6, 5},
		// {0, 5, 2, 1},
		// };

		List<Point> pSombraList = new ArrayList<Point>();
		pSombraList.add(new Point(1, 2));
		pSombraList.add(new Point(2, 5));
		pSombraList.add(new Point(3, 6));
		int[][] input = new int[pSombraList.size()][pSombraList.size() + 1];
		for (int i = 0; i < pSombraList.size(); i++) {
			for (int j = 0; j < pSombraList.size() + 1; j++) {
				if (j != pSombraList.size()) {
					input[i][j] = (int) Math.pow(pSombraList.get(i).getX(), j) % 7;
				} else {
					input[i][j] = (int) pSombraList.get(i).getY();
				}
			}
		}
		// The actual matrix object
		Matrix<Integer> mat = new Matrix<Integer>(input.length,
				input[0].length, new PrimeField(7));
		for (int i = 0; i < mat.rowCount(); i++) {
			for (int j = 0; j < mat.columnCount(); j++)
				mat.set(i, j, input[i][j]);
		}
		System.out.println(mat);
		// Gauss-Jordan elimination
		mat.reducedRowEchelonForm();
		System.out.println(mat);
		// for (int i = 0; i < mat.rowCount(); i++) {
		// System.out.println(mat.get(i, mat.columnCount()-1));
		// }
		System.out.println(mat.get(mat.rowCount() - 1, mat.columnCount() - 1));
	}

}
