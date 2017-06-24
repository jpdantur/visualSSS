package ar.edu.itba.cripto.visualSSS;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

public class BMPHeader {

	private char id;
	private int size;
	private short seed;
	private short shadeNumber;
	private int offSet;

	public BMPHeader(BMPHeader header, int sombraNro) {
		// TODO Auto-generated constructor stub
	}

	public BMPHeader(File file, int i) {
		try {
			RandomAccessFile f = new RandomAccessFile (file, "rw");
			id = f.readChar();
			size = Integer.reverseBytes(f.readInt());
			seed = Short.reverseBytes(f.readShort()); //TODO: Escribirselo al archivo
			shadeNumber = Short.reverseBytes(f.readShort()); //TODO: Escribirselo al archivo
			offSet = Integer.reverseBytes(f.readInt());
		} catch(IOException e) {
			System.out.println("Not Found");
			
		}
	}

}
