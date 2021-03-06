package ar.edu.itba.cripto.visualSSS;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private static int seed = 0x166F;

	public static void main(String[] args) {
		try {
			Option[] recognized = ArgumentParser.getOptions(args);
			for (Option o : recognized) {
//				System.out.println(o.getOpt() + " : "
//						+ Arrays.toString(o.getValues()));
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
			if(minimoParticiones<2  || (totalParticiones!=0 && totalParticiones<minimoParticiones)){
				throw new IllegalArgumentException("Valores invalidos para k o n.");
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
							listSombras = generateSombras(o.getValues()[0]);
							secret = readImage(o.getValues()[0]);
							validarArchivosEncoding();
						} else {
							listSombras = loadSombras();
							secretRecover = createImage(o.getValues()[0]);
						}
					}
				}
			}
			
			rnd = new Random();
			rnd.setSeed(seed);
			if (tipoOperacion) {
				if (listSombras.size()== totalParticiones){
					encode();
					for (BMPImage img : listSombras) {
						img.save(folder, img.getHeader().getBiWidth(), img.getHeader().getBiHeight(), (short)seed);
					}
				}
			} else {
				if (listSombras.size() < minimoParticiones) {
					System.out
							.println("El secreto no puede recuperarse, se requiere de mas sombras.");
				} else {
					decode();
					secretRecover.save(folder, secretRecover.getHeader().getBiWidth(), secretRecover.getHeader().getBiHeight(),(short)0);
				}
			}
		}catch (IllegalArgumentException ex){
			System.out.println(ex.getMessage());
		} catch (ParseException e) {
			System.out.println("Invalid Arguments");
		} catch (Exception exx){
			exx.printStackTrace();
			System.out.println(exx.getMessage());
		}

	}

	private static void validarArchivosEncoding() {
		for(BMPImage img: listSombras){
			if(secret.getHeader().getBiWidth() != img.getHeader().getBiWidth()){
				throw new IllegalArgumentException("Los archivos para la generacion de sombras deben tener el mismo ancho que el archivo del secreto");
			}else if(img.getHeader().getBiHeight()*minimoParticiones != secret.getHeader().getBiHeight()*8){
				throw new IllegalArgumentException("Para todos los archivos sombra debe cumplise que su altura es igual a la del archivo secreto multiplicada por k dividido 8 ");
			}
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
//			System.out.println(listOfFiles[i].getName());
			BMPImage img = new BMPImage(listOfFiles[i].getName(),
					listOfFiles[i]);
			if(img.getHeader().getBiHeight()*img.getHeader().getBiWidth()%8==0){

//				img.getHeader().setShadeNumber(new Short((i+1)+""));
				imgSombras.add(img);
				if (imgSombras.size() == minimoParticiones) {
					break;
				}
			}
		}
		validateSombras(imgSombras);
		return imgSombras;
	}

	private static void validateSombras(List<BMPImage> imgSombras) {
		if(imgSombras.isEmpty()){
			throw new IllegalArgumentException("La cantidad de sombras leidas no puede ser cero.");
		}
		int size=imgSombras.get(0).getData().size();
		int ancho=imgSombras.get(0).getHeader().getBiWidth();
		int alto=imgSombras.get(0).getHeader().getBiHeight();
		short seed=imgSombras.get(0).getHeader().getSeed();
		Map<Short, Integer> idmap=new HashMap<Short, Integer>();
//		for(BMPImage img:imgSombras){
//		System.out.println(img.getHeader().getSeed());
//		}
		for(BMPImage img:imgSombras){
			if(img.getData().size()!=size || img.getHeader().getBiHeight()!=alto || img.getHeader().getBiWidth()!=ancho){
				throw new IllegalArgumentException("Los archivos con sombras deben tener las mismas dimensiones");
			}else if(img.getHeader().getSeed()!=seed){
				throw new IllegalArgumentException("Los archivos con sombras deben tener todos la misma semilla");
			}else if(img.getHeader().getShadeNumber()==0){
				throw new IllegalArgumentException("Ningun archivo sombra puede tener id = 0");
			}
			idmap.put(img.getHeader().getShadeNumber(), null);
		}
		if(idmap.keySet().size()!=imgSombras.size()){
			throw new IllegalArgumentException("Existe al menos un nro de sombra repetido entre los archivos de sombras");
		}
	}

	private static List<BMPImage> generateSombras(String filename) {
		List<BMPImage> imgSombras = new ArrayList<BMPImage>();
		File file = null;
		if(totalParticiones==0){
			totalParticiones=listOfFiles.length;
		}
		if(totalParticiones<minimoParticiones){
			throw new IllegalArgumentException("Indique un valor para n mayor o igual a k");
		}
		if(totalParticiones > listOfFiles.length){
			throw new IllegalArgumentException("Se requiere de mas archivos para generar n sombras");
		}
		for (int i = 0; i < listOfFiles.length; i++) {
//			System.out.println(listOfFiles[i].getName());
			String extension = getExtension(listOfFiles[i].getName()
					.toLowerCase());
//			System.out.println(extension);
			if (!extension.equals("bmp")) {
				continue;
			}
			if (!listOfFiles[i].getName().equals(filename)){
				file = listOfFiles[i];
			
				if (file == null) {
					throw new IllegalArgumentException("Error al generar las sombras");
				}else{
					BMPImage img = new BMPImage(file.getName() + "_s" + i+".bmp", file, imgSombras.size());
					if((img.getHeader().getBiHeight()*img.getHeader().getBiWidth())%8!=0){
						throw new IllegalArgumentException("Los archivos para las sombras deben tener una cantidad de pixeles multiplo de 8");
					}
					imgSombras.add(img);
				}
			}
			if(imgSombras.size()==totalParticiones){
				break;
			}
		}
		return imgSombras;
	}

	private static void decode() {
		int minData= listSombras.get(0).getHeader().getBiHeight() * listSombras.get(0).getHeader().getBiWidth();
		rnd.setSeed(listSombras.get(0).getHeader().getSeed());
//		System.out.println("Seed: "+listSombras.get(0).getHeader().getSeed());
		for(int i=0; i <minData/8; i++ ){
			List<Point> pSombraList = cargarPuntosSombras(i*8);
			List<Integer> colors=gauss(pSombraList);
			for (int j = 0; j < colors.size(); j++){
				byte rndValue= new Integer(rnd.nextInt(256)).byteValue();
				secretRecover.getData().set(i * colors.size() + j,  (colors.get(j) ^ rndValue) & 0x0ff);
			}
		}
	}
	
//	https://www.nayuki.io/page/gauss-jordan-elimination-over-any-field
	private static List<Integer> gauss(List<Point> pSombraList) {
		int primo=257;
		List<Integer> ret = new ArrayList<Integer>();
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
        
        for (int i = 0; i < mat.rowCount(); i++) {
        	ret.add(mat.get(i, mat.columnCount()-1));
        }
        
		return ret;
	}

	private static List<Point> cargarPuntosSombras(int minData) {
		ArrayList<Point> list = new ArrayList<Point>();
		for(int i =0 ; i<listSombras.size(); i++){
			int value=0;
			for(int k=0; k<8; k++){
				int color=listSombras.get(i).getData().get(minData+k);
				value = value*2 + (color%2);
			}
			list.add(new Point(listSombras.get(i).getHeader().getShadeNumber(), value));
		}
		return list;	
	}

//	private static int getMinData(List<BMPImage> listSombras2) {
//		int min=Integer.MAX_VALUE;
//		for(BMPImage img:listSombras2){
//			int data = (int)Math.floor(img.getHeader().getBiHeight() * minimoParticiones / 8.0) * img.getHeader().getBiWidth();
//			if(data<min)
//				min=data;
//		}
//		return min;
//	}

	private static void encode() {
		int index = 0;
		while (index < secret.getData().size()) {
			List<Integer> polinomioCoef = new ArrayList<Integer>(
					minimoParticiones);
			for (int i = 0; i < minimoParticiones; i++) {
				byte rndValue= new Integer(rnd.nextInt(256)).byteValue();
				polinomioCoef.add((secret.getData().get(index + i) ^ rndValue) & 0x0ff);
			}
			
			List<Point> psombrasList = new ArrayList<Point>();
			while (psombrasList.isEmpty()) {
				for (int i = 0; i < totalParticiones; i++) {
					psombrasList.add(generarSombra(i+1, polinomioCoef));
				}
				
				for (int i = 0; i < psombrasList.size(); i++) {
//					System.out.println("Punto vale: "+psombrasList.get(i).y);
					if (psombrasList.get(i).y == 256) {
						for (int j = 0; j < polinomioCoef.size(); j++) {
							int coef = polinomioCoef.get(j);
							if ( coef > 0) {
								polinomioCoef.set(j, coef - 1);
//								System.out.println("Reset");
								psombrasList.clear();
								break;
							}
						}
					}
				}
			}
			
			for (int i = 0; i < totalParticiones; i++) {
				addSombraEnArchivo(i, index * 8 / minimoParticiones, psombrasList.get(i));
			}
			
			index = index + minimoParticiones;
		}
	}

	private static void addSombraEnArchivo(int i, int index, Point sombra) {
		BMPImage img = listSombras.get(i);
		for (int j = 0; j < 8; j++) {
			byte color = new Integer(sombra.y).byteValue();
			int somb = (1<<(7-j)) & 0x0ff;
			int res = ((color & somb) & 0x0ff) ==0?0:1;
			

			int resultado = img.getData().get(index+j);
			if (resultado % 2 != 0) {
				resultado--;
			}
			resultado = resultado|res;
			img.getData().set(index + j, resultado);
		}

	}

	private static Point generarSombra(Integer x, List<Integer> polinomioCoef) {
		int y = 0;
		for (int i = 0; i < polinomioCoef.size(); i++) {
//			System.out.println("Coef "+i+ ": "+ Integer.toHexString(polinomioCoef.get(i)));
			y += polinomioCoef.get(i) * Math.pow(x, i);
		}
		return new Point(x, y%257);

	}

	private static BMPImage createImage(String filename) {
		return new BMPImage(filename, listSombras.get(0), minimoParticiones);
	}

	private static BMPImage readImage(String filename) {
		File file = null;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].getName().equals(filename)) { // poner la opcion que corresponda
				file = listOfFiles[i];
			}
		}
		if(file==null){
			throw new IllegalArgumentException("No se encontro el archivo: " +filename);
		}
		BMPImage img = new BMPImage(filename, file);
		if(img.getHeader().getBiHeight()*img.getHeader().getBiWidth()%totalParticiones!=0){
			throw new IllegalArgumentException("El tamaño de la imagen secreta debe ser multiplo de "+totalParticiones);
		}
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
