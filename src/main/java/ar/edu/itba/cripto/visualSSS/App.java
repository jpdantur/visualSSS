package ar.edu.itba.cripto.visualSSS;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

public class App {
	private static int totalParticiones;
	private static int minimoParticiones;
	private static Random rnd;
	private static boolean tipoOperacion;
	private static BMPImage secret;
	private static BMPImage secretRecover;
	private static File[] listOfFiles;
	private static List<BMPImage> listSombras;
	private static File folder;

	public static void main(String[] args) {
		try {
			Option[] recognized = ArgumentParser.getOptions(args);
			for (Option o : recognized) {
				System.out.println(o.getOpt() + " : "
						+ Arrays.toString(o.getValues()));
				if (o.getOpt().compareTo("d") == 0) {
					tipoOperacion = true;
				} else if (o.getOpt().compareTo("r") == 0) {
					tipoOperacion = false;
				} else if (o.getOpt().compareTo("k") == 0) {
					minimoParticiones = Integer.parseInt(o.getValues()[0]);
				} else if (o.getOpt().compareTo("n") == 0) {
					totalParticiones = Integer.parseInt(o.getValues()[0]);
				}
			}
			for (Option o : recognized) {
				if (o.getOpt().compareTo("dir") == 0) {
					folder = new File(o.getValues()[0]);
					listOfFiles = folder.listFiles();
				}
			}
			if (listOfFiles == null) {
				folder = new File("resources");// carpeta local
				listOfFiles = folder.listFiles();
			}
			for (Option o : recognized) {
				if (secret == null && secretRecover == null) {
					if (o.getOpt().compareTo("secret") == 0) {
						if (tipoOperacion) {
							secret = readImage(o.getValues()[0]);
							listSombras = generateSombras(o.getValues()[0]);
						} else {
							listSombras = loadSombras();
							secretRecover = createImage(o.getValues()[0]);
						}
					}
				}
			}

			rnd = new Random();
			rnd.setSeed(10);
			if (tipoOperacion) {
				encode();
				for (BMPImage img : listSombras) {
					img.save(folder);
				}
			} else {
				if (listSombras.size() < minimoParticiones) {
					System.out
							.println("el secreto no puede recuperarse, se requiere de mas sombras.");
				} else {
					decode();
					secretRecover.save(folder);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Invalid Arguments");
		}

	}

	private static List<BMPImage> loadSombras() {
		List<BMPImage> imgSombras = new ArrayList<BMPImage>();
		for (int i = 0; i < listOfFiles.length; i++) {

			String extension = getExtension(listOfFiles[i].getName()
					.toLowerCase());
			if (!extension.equals("bmp")) {
				continue;
			}
			BMPImage img = new BMPImage(listOfFiles[i].getName(),
					listOfFiles[i]);
			imgSombras.add(img);
			if (imgSombras.size() == minimoParticiones) {
				break;
			}
		}
		return imgSombras;
	}

	private static List<BMPImage> generateSombras(String filename) {
		List<BMPImage> imgSombras = new ArrayList<BMPImage>();
		File file = null;
		for (int i = 0; i < listOfFiles.length; i++) {
			System.out.println(listOfFiles[i].getName());
			String extension = getExtension(listOfFiles[i].getName()
					.toLowerCase());
			System.out.println(extension);
			if (!extension.equals("bmp")) {
				continue;
			}
			if (listOfFiles[i].getName().equals(filename))
				file = listOfFiles[i];
		}
		if (file == null) {
			System.out.println("Error al generar las sombras");
		}
		for (int i = 0; i < totalParticiones; i++) {
			BMPImage img = new BMPImage(filename + "_s" + i, file, i);
			imgSombras.add(img);
		}
		return imgSombras;
	}

	private static void decode() {
		int minData=getMinData(listSombras);
		for(int i=0; i <minData; ){
			List<Point> pSombraList = cargarPuntosSombras(minData);
			int color=gauss(pSombraList);
			secretRecover.getData().set(i, color);
			minData+=8;
		}
	}
	
//	https://www.nayuki.io/page/gauss-jordan-elimination-over-any-field
	private static int gauss(List<Point> pSombraList) {
		int primo=256;
		int[][] input=new int[pSombraList.size()][pSombraList.size()+1];
		for(int i=0; i <pSombraList.size(); i++){
			for(int j=0; j<pSombraList.size()+1; j++){
				if(j!=pSombraList.size()){
					input[i][j]=(int) Math.pow(pSombraList.get(i).getX(), j)%primo;
				}else{
					input[i][j]=(int) pSombraList.get(i).getY();
				} 
			}
		}
    	// The actual matrix object
        Matrix<Integer> mat = new Matrix<Integer>(
                input.length, input[0].length, new PrimeField(primo));
        for (int i = 0; i < mat.rowCount(); i++) {
            for (int j = 0; j < mat.columnCount(); j++)
                mat.set(i, j, input[i][j]);
        }
        // Gauss-Jordan elimination
        mat.reducedRowEchelonForm();
		return (int) mat.get(mat.rowCount()-1, mat.columnCount()-1);
	}

	private static List<Point> cargarPuntosSombras(int minData) {
		ArrayList<Point> list = new ArrayList<Point>();
		for(int i =0 ; i<listSombras.size(); i++){
			int value=0;
			for(int k=0; k<8; k++){
				int color=listSombras.get(i).getData().get(minData+k);
				value = value<<1+ (color%2);
			}
			list.add(new Point(listSombras.get(i).getHeader().getShadeNumber(), value));
		}
		return list;	
	}

	private static int getMinData(List<BMPImage> listSombras2) {
		int min=listSombras2.get(0).getData().size();
		for(BMPImage img:listSombras2){
			if(img.getData().size()<min)
				min=img.getData().size();
		}
		return min;
	}

	private static void encode() {
		int index = 0;
		while (index < secret.getData().size()) {
			Integer wdSecret = secret.getData().get(index);
			List<Point> psombrasList = new ArrayList<Point>();
			while (secretLoop(psombrasList)) {
				List<Integer> polinomioCoef = new ArrayList<Integer>(
						minimoParticiones);
				polinomioCoef.add(wdSecret);
				while (polinomioCoef.size() < minimoParticiones - 1) {
					polinomioCoef.add(rnd.nextInt(256));
				}
				for (int i = 0; i < totalParticiones; i++) {
					psombrasList.add(generarSombra(i, polinomioCoef));
				}
				wdSecret=wdSecret-1;
				polinomioCoef.clear();
			}
			for (int i = 0; i < totalParticiones; i++) {
				addSombraEnArchivo(i, index, psombrasList.get(i));
			}
			index++;
		}
	}

	private static boolean secretLoop(List<Point> psombrasList) {
		if (psombrasList.isEmpty()) {
			return true;
		}
		for (Point p : psombrasList) {
			if (p.y == 256){
				psombrasList.clear();
				return true;
			}
		}
		return false;

	}

	private static void addSombraEnArchivo(int i, int index, Point sombra) {
		BMPImage img = listSombras.get(i);
		for (int j = 0; j < 8; j++) {
			byte color = new Integer(sombra.y).byteValue();
			byte somb = ((Integer) ((Double) Math.pow(2, j)).intValue())
					.byteValue();
			int res = color & somb;

			int resultado = sombra.y;
			if (sombra.y % 2 != 0) {
				resultado -= sombra.y % 2;
			}
			if (res != 0) {
				resultado++;
			}
			img.getData().set(index * 8 + j, resultado);
		}

	}

	private static Point generarSombra(Integer x, List<Integer> polinomioCoef) {
		int y = 0;
		for (int i = 0; i < polinomioCoef.size(); i++) {
			y += polinomioCoef.get(i) * Math.pow(x, i);
		}
		return new Point(x, y);

	}

	private static BMPImage createImage(String filename) {
		return new BMPImage(filename, listSombras.get(0));
	}

	private static BMPImage readImage(String filename) {
		//System.out.println(filename);
		File file = null;
		for (int i = 0; i < listOfFiles.length; i++) {
			//System.out.println(listOfFiles[i].getName());
			if (listOfFiles[i].getName().equals(filename)) { // poner la opcion
				// que corresponda
				file = listOfFiles[i];
			}
		}
		BMPImage img = new BMPImage(filename, file);
		return img;
	}

	public static String getExtension(String s) {
		String ext = "";
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}
