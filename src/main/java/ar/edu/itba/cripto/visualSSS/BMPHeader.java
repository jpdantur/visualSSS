package ar.edu.itba.cripto.visualSSS;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BMPHeader {

	private char id;
	private int size;
	private short seed;
	private short shadeNumber;
	private int offSet;
	private RandomAccessFile randomAccessFile;
	private int biWidth;
	private int biHeight;
	private int[] biPlanes=new int[2];
	private int[] biBitCount=new int[2];
	private int biCompression;
	private int biSizeImage;
	private int biXPelsPerMeter;
	private int biYPelsPerMeter;
	private int biClrUsed;
	private int biClrImportant;
	private int biSize;

	public BMPHeader(BMPHeader header, int sombraNro) {
		id=header.id;
		size=header.size;
		seed=header.seed;
		shadeNumber=(short) sombraNro;
		offSet=header.offSet;
		randomAccessFile=header.randomAccessFile;
		biWidth=header.biWidth;
		biHeight=header.biHeight;
		biPlanes=header.biPlanes;
		biBitCount=header.biBitCount;
		biCompression=header.biCompression;
		biSizeImage=header.biSizeImage;
		biXPelsPerMeter=header.biXPelsPerMeter;
		biYPelsPerMeter=header.biYPelsPerMeter;
		biClrUsed=header.biClrUsed;
		biClrImportant=header.biClrImportant;
		biSize=header.biSize;
	}

	public BMPHeader(File file, int i) {
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			id = randomAccessFile.readChar();
			size = Integer.reverseBytes(randomAccessFile.readInt());
			seed = Short.reverseBytes(randomAccessFile.readShort()); // TODO:
																		// Escribirselo
																		// al
																		// archivo
			shadeNumber = Short.reverseBytes(randomAccessFile.readShort()); // TODO:
																			// Escribirselo
																			// al
																			// archivo
			offSet = Integer.reverseBytes(randomAccessFile.readInt());

			// biSize 4 Header Size - Must be at least 40
			biSize = Integer.reverseBytes(randomAccessFile.readInt());

			// biWidth 4 Image width in pixelsç
			biWidth = Integer.reverseBytes(randomAccessFile.readInt());

			// biHeight 4 Image height in pixelsç
			biHeight = Integer.reverseBytes(randomAccessFile.readInt());

			// biPlanes 2 Must be 1
			biPlanes[0] = Integer.reverseBytes(randomAccessFile.readByte());
			biPlanes[1] = Integer.reverseBytes(randomAccessFile.readByte());
			// biBitCount 2 Bits per pixel - 1, 4, 8, 16, 24, or 32
			biBitCount[0] = Integer.reverseBytes(randomAccessFile.readByte());
			biBitCount[1] = Integer.reverseBytes(randomAccessFile.readByte());

			// biCompression 4 Compression type (0 = uncompressed)
			biCompression = Integer
					.reverseBytes(randomAccessFile.readInt());

			// biSizeImage 4 Image Size - may be zero for uncompressed images
			biSizeImage = Integer.reverseBytes(randomAccessFile.readInt());

			// biXPelsPerMeter 4 Preferred resolution in pixels per meter
			biXPelsPerMeter = Integer.reverseBytes(randomAccessFile
					.readInt());

			// biYPelsPerMeter 4 Preferred resolution in pixels per meter
			biYPelsPerMeter = Integer.reverseBytes(randomAccessFile
					.readInt());

			// biClrUsed 4 Number Color Map entries that are actually used
			biClrUsed = Integer.reverseBytes(randomAccessFile.readInt());
			// biClrImportant 4 Number of significant colors

			biClrImportant = Integer.reverseBytes(randomAccessFile
					.readInt());
			
			
//			randomAccessFile quedo apuntando a los pixeles de la imagen
		} catch (IOException e) {
			System.out.println("Not Found");

		}
	}

	public RandomAccessFile getRandomAccessFile() {
		return randomAccessFile;
	}

	public int getBiHeight() {
		return biHeight;
	}

	public int getBiWidth() {
		return biWidth;
	}

	public short getShadeNumber() {
		return shadeNumber;
	}
}
