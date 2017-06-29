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
	private int[] biPlanes = new int[2];
	private int[] biBitCount = new int[2];
	private int biCompression;
	private int biSizeImage;
	private int biXPelsPerMeter;
	private int biYPelsPerMeter;
	private int biClrUsed;
	private int biClrImportant;
	private int biSize;

	public BMPHeader(BMPHeader header, int sombraNro, Integer minimoParticiones) {
		id = header.id;
		size = header.size;
		seed = header.seed;
		shadeNumber = (short) sombraNro;
		offSet = header.offSet;
		randomAccessFile = header.randomAccessFile;
		biWidth = header.biWidth;
		biHeight = header.biHeight;
		biPlanes = header.biPlanes;
		biBitCount = header.biBitCount;
		biCompression = header.biCompression;
		biSizeImage = header.biSizeImage;
		biXPelsPerMeter = header.biXPelsPerMeter;
		biYPelsPerMeter = header.biYPelsPerMeter;
		biClrUsed = header.biClrUsed;
		biClrImportant = header.biClrImportant;
		biSize = header.biSize;
		
		//se fija altura del archivo del secreto
		if(header.biHeight*minimoParticiones%8!=0){
			throw new IllegalArgumentException("Para generarse el archivo del secreto la altura de las sombras multiplicadas por r debe ser multiplo de 8");
		}else{
			biHeight = header.biHeight*minimoParticiones/8;
		}
	}

	public BMPHeader(File file, int i) {
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");

			id = randomAccessFile.readChar();
			size = ByteSwapper.swap(randomAccessFile.readInt());
			seed = ByteSwapper.swap(randomAccessFile.readShort());
			shadeNumber = ByteSwapper.swap(randomAccessFile.readShort());
			offSet = ByteSwapper.swap(randomAccessFile.readInt());

			// biSize 4 Header Size - Must be at least 40
			biSize = ByteSwapper.swap(randomAccessFile.readInt());

			// biWidth 4 Image width in pixels
			biWidth = ByteSwapper.swap(randomAccessFile.readInt());

			// biHeight 4 Image height in pixels
			biHeight = ByteSwapper.swap(randomAccessFile.readInt());

			// biPlanes 2 Must be 1
			biPlanes[0] = ByteSwapper.swap(randomAccessFile.readByte());
			biPlanes[1] = ByteSwapper.swap(randomAccessFile.readByte());
			// biBitCount 2 Bits per pixel - 1, 4, 8, 16, 24, or 32
			biBitCount[0] = ByteSwapper.swap(randomAccessFile.readByte());
			biBitCount[1] = ByteSwapper.swap(randomAccessFile.readByte());

			// biCompression 4 Compression type (0 = uncompressed)
			biCompression = ByteSwapper.swap(randomAccessFile.readInt());

			// biSizeImage 4 Image Size - may be zero for uncompressed images
			biSizeImage = ByteSwapper.swap(randomAccessFile.readInt());

			// biXPelsPerMeter 4 Preferred resolution in pixels per meter
			biXPelsPerMeter = ByteSwapper.swap(randomAccessFile.readInt());

			// biYPelsPerMeter 4 Preferred resolution in pixels per meter
			biYPelsPerMeter = ByteSwapper.swap(randomAccessFile.readInt());

			// biClrUsed 4 Number Color Map entries that are actually used
			biClrUsed = ByteSwapper.swap(randomAccessFile.readInt());
			// biClrImportant 4 Number of significant colors

			biClrImportant = ByteSwapper.swap(randomAccessFile.readInt());

			randomAccessFile.seek(offSet); // Por las dudas
			// System.out.println(offSet);

			// randomAccessFile quedo apuntando a los pixeles de la imagen
		} catch (IOException e) {
			System.out.println("Error al generar la sombra de id=" + i);
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

	public int getBiSizeImage() {
		return biSizeImage;
	}

	public void setBiSizeImage(int biSizeImage) {
		this.biSizeImage = biSizeImage;
	}

	public short getSeed() {
		return seed;
	}

	public RandomAccessFile save(String filename, int width, int height,
			short seed, short shadeNumber) {
		try {
			randomAccessFile = new RandomAccessFile(filename, "rw");

			// int i = 0;
			// while (i < offSet) {
			// randomAccessFile.writeByte(0);
			// i++;
			// }
			randomAccessFile.seek(0);

			int colorPaletteLen = 256 * 4;
			int headerLen = 54;

			randomAccessFile.writeChar(id);
			randomAccessFile.writeInt(ByteSwapper.swap(headerLen
					+ colorPaletteLen + width * height));
			randomAccessFile.writeShort(ByteSwapper.swap(seed));

			randomAccessFile.writeShort(ByteSwapper.swap(shadeNumber));
			randomAccessFile.writeInt(ByteSwapper.swap(headerLen
					+ colorPaletteLen));

			randomAccessFile.writeInt(ByteSwapper.swap(40));
			randomAccessFile.writeInt(ByteSwapper.swap(width));
			randomAccessFile.writeInt(ByteSwapper.swap(height));

			randomAccessFile.writeShort(ByteSwapper.swap((short) 1)); // Color planes
			randomAccessFile.writeShort(ByteSwapper.swap((short) 8)); // Bits per pixel

			randomAccessFile.writeInt(0); // No compression

			randomAccessFile.writeInt(ByteSwapper.swap(width * height)); // Image Size

			randomAccessFile.writeInt(ByteSwapper.swap(2835)); // Pixels Per mt
			randomAccessFile.writeInt(ByteSwapper.swap(2835)); // Pixels Per mt
			randomAccessFile.writeInt(ByteSwapper.swap(256)); // Color palette
			randomAccessFile.writeInt(0); // Important Colors

			// Write Palette
			for (int i = 0; i < 256; i++) {
				randomAccessFile.writeByte(i);
				randomAccessFile.writeByte(i);
				randomAccessFile.writeByte(i);
				randomAccessFile.writeByte(0);
			}

			randomAccessFile.seek(headerLen + colorPaletteLen); // Por las dudas

			return randomAccessFile;
		} catch (IOException e) {
			System.out.println("Error al leer el archivo " + filename);

		}
		return null;
	}
	
	public void setShadeNumber(short shadeNumber) {
		this.shadeNumber = shadeNumber;
	}
}
