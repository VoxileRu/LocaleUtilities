package ru.simsonic.russifier;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GlyphSizesBinFixer
{
	public static void runGlyphSizeBinFixer(boolean bVerbose)
	{
		// Read internal glyph_sizes.bin
		byte glyphSizes[] = new byte[65536];
		try
		{
			InputStream istream = GlyphSizesBinFixer.class.getResourceAsStream("/glyph_sizes.bin");
			istream.read(glyphSizes);
			// Enumerate all found files
			int changedFiles = 0;
			for(int nFile = 0; nFile < 256; nFile += 1)
			{
				final String strFile = String.format("unicode_page_%02x.png", nFile);
				final File file = new File(strFile);
				if(file.exists() == false)
					continue;
				System.out.println("Found unicode page file " + strFile + "...");
				final BufferedImage pageImage = javax.imageio.ImageIO.read(file);
				int changedChars = 0;
				for(int nChar = 0; nChar < 256; nChar += 1)
				{
					int cellX = nChar % 16;
					int cellY = nChar / 16;
					// Found start
					int charStart = 0;
					for(int charX = charStart; charX <= 16; charX += 1)
					{
						if(charX == 16)
						{
							charStart = 0;
							break;
						}
						boolean bEmptyLine = true;
						for(int charY = 0; charY < 16; charY += 1)
						{
							final int pixel = pageImage.getRGB(cellX * 16 + charX, cellY * 16 + charY);
							if((pixel & 0xFF000000) != 0)
							{
								bEmptyLine = false;
								break;
							}
						}
						charStart = charX;
						if(bEmptyLine == false)
							break;
					}
					// Found end
					int charEnd = charStart - 1;
					for(int charX = 15; charX > charStart; charX -= 1)
					{
						boolean bEmptyLine = true;
						for(int charY = 0; charY < 16; charY += 1)
						{
							final int pixel = pageImage.getRGB(cellX * 16 + charX, cellY * 16 + charY);
							if((pixel & 0xFF000000) != 0)
							{
								bEmptyLine = false;
								break;
							}
						}
						if(bEmptyLine == false)
							break;
						charEnd = charX - 1;
					}
					// Save char's data into array
					byte charDataOld = glyphSizes[nFile * 256 + nChar];
					byte charDataNew = (byte)((charStart << 4) + (charEnd & 0x0F));
					glyphSizes[nFile * 256 + nChar] = charDataNew;
					if(bVerbose)
						System.out.format("Char #%02X: old start=%2d, end=%2d; new start=%2d, end=%2d%s\n", nChar,
							(byte)(charDataOld >>> 4 & 0x0F), (byte)(charDataOld & 0x0F),
							(byte)(charDataNew >>> 4 & 0x0F), (byte)(charDataNew & 0x0F),
							(charDataNew != charDataOld) ? ", changed." : ".");
					if(charDataNew != charDataOld)
						changedChars += 1;
				}
				System.out.println((changedChars > 0) ? "Done." : "File seems to be like an original.");
				if(changedChars > 0)
					changedFiles += 1;
			}
			if(changedFiles == 0)
				System.err.println("Nothing to do.");
		} catch(Exception ex) {
			System.out.println(ex.getLocalizedMessage());
			return;
		}
		// Save results
		try(OutputStream ostream = new FileOutputStream("glyph_sizes.bin"))
		{
			ostream.write(glyphSizes);
			ostream.flush();
			System.out.println("File glyph_sizes.bin has been generated successfully.");
		} catch(IOException ex) {
			System.out.println(ex.getLocalizedMessage());
		}
	}
}