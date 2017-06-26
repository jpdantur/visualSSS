package ar.edu.itba.cripto.visualSSS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
		header = new BMPHeader(file, i);
		loadData();
		this.sombraNro = header.getShadeNumber();
	}

	private void loadData() {
		data = new ArrayList<Integer>();
		//System.out.println(header.getBiHeight()*header.getBiWidth());
		System.out.println("hhh: "+header.getBiHeight()*header.getBiWidth());
		for (int i = 0; i < header.getBiHeight()*header.getBiWidth(); i++) {

				try {
					data.add(Character.getNumericValue(header
							.getRandomAccessFile().readByte()));
				} catch (IOException e) {
					//e.printStackTrace();
					System.out.println(header.getBiHeight()*header.getBiWidth());
					System.out.println("fdsa "+data.size());
					System.out.println("Error al leer de archivo fotoso");
					;
				}
			
		}
	}

	public BMPImage(String filename, File file) {
		this(filename, file, 0);
	}

	public BMPImage(String filename, BMPImage bmpImage) {
		this.filename = filename;
		this.sombraNro = 0;
		header = new BMPHeader(bmpImage.header, sombraNro);
		loadData(bmpImage);
	}

	private void loadData(BMPImage bmpImage) {
		data = new ArrayList<Integer>();
		for (int i = 0; i < bmpImage.header.getBiWidth(); i++) {
			for (int j = 0; j < header.getBiHeight(); j++) {
				try {
					data.add(Character.getNumericValue(bmpImage.header
							.getRandomAccessFile().readChar()));
				} catch (IOException e) {
					System.out.println("Error al leer de archivo foto");
				}
			}
		}
	}

	public List<Integer> getData() {
		return data;
	}

	public BMPHeader getHeader() {
		return header;
	}

	public void save(File folder) {
		// TODO guardar imagen en el directorio que se indica

	}
	

}
