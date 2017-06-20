package ar.edu.itba.cripto.visualSSS;

import java.io.File;
import java.util.List;

public class BMPImage {

	private List<Integer> data;
	private String filename;
	private int sombraNro;
	private BMPHeader header;

	// escribir archivos bmp
	// https://stackoverflow.com/questions/14885134/java-bmp-header-files

	public BMPImage(String filename, File file, int i) {
		// TODO constructor para las sombras, hay que indicar en el header que
		// tipo de sombra es
		this.filename = filename;
		header=new BMPHeader(file, i);
		loadData(file, header);
		this.sombraNro = i;
	}

	private void loadData(File file, BMPHeader header2) {
		// TODO Auto-generated method stub
		
	}

	public BMPImage(String filename, File file) {
		this(filename, file, 0);
	}

	public BMPImage(String filename, BMPImage bmpImage) {
		this.filename = filename;
		this.sombraNro = 0;
		header=new BMPHeader(bmpImage.header, sombraNro);
		loadData(bmpImage);
	}

	private void loadData(BMPImage bmpImage) {
		// TODO Auto-generated method stub
		
	}

	public List<Integer> getData() {
		return data;
	}

	public BMPHeader getHeader() {
		return null;
	}

	public void save(File folder) {
		// TODO guardar imagen en el directorio que se indica
		
	}

}
