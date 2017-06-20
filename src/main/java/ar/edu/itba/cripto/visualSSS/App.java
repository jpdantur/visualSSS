package ar.edu.itba.cripto.visualSSS;

import java.awt.Point;
import java.io.File;
import java.sql.Savepoint;
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
//			TODO CONECTAR EL PARSER CON LO QUE SIGUE ADECUADAMENTE.
			// validarConfiguracion(recognized);
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
			if (tipoOperacion) {
				encode();
				for(BMPImage img: listSombras){
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
			// TODO: que valide si se trata de una imagen bmp y cargue el valor

			BMPImage img = new BMPImage(listOfFiles[i].getName(), listOfFiles[i]);
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
			// TODO: que valide si se trata de una imagen bmp y cargue el valor
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

	private static BMPImage decode() {
		// TODO Auto-generated method stub
		return null;
	}

	private static void encode() {
		int index = 0;
		while (index <= secret.getData().size()) {
			Integer wdSecret = secret.getData().get(index);
			List<Integer> polinomioCoef = new ArrayList<Integer>(
					minimoParticiones);
			polinomioCoef.add(wdSecret);
			while (polinomioCoef.size() < minimoParticiones - 1) {
				polinomioCoef.add(rnd.nextInt(256));
			}
			for (int i = 0; i < totalParticiones; i++) {
				Point sombra = generarSombra(i, polinomioCoef);
				addSombraEnArchivo(i, index, sombra);
			}
		}
	}

	private static void addSombraEnArchivo(int i, int index, Point sombra) {
		BMPImage img = listSombras.get(i);
		for (int j = 0; j < 8; j++) {
			// reemplazar el bit menos significativo o ver como dice en la
			// consigna
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
			// TODO hay otro caso?
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
		File file = null;
		for (int i = 0; i < listOfFiles.length; i++)
			if (listOfFiles[i].getName().equals(filename))// poner la opcion
				// que corresponda
				file = listOfFiles[i];
		BMPImage img = new BMPImage(filename, file);
		return img;
	}
}
