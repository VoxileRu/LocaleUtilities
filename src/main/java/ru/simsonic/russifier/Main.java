package ru.simsonic.russifier;

public class Main
{
	public static enum EMode { glyph, lang, help };
	public static void main(String[] args)
	{
		EMode mode = EMode.help;
		boolean bModeSel = false;
		boolean bVerbose = false;
		for(String arg : args)
		{
			switch(arg.toLowerCase())
			{
				// Mode
				case "-glyph":
					if(bModeSel)
						return;
					mode = EMode.glyph;
					bModeSel = true;
					break;
				case "-lang":
					if(bModeSel)
						return;
					mode = EMode.lang;
					bModeSel = true;
					break;
				case "-help":
					if(bModeSel)
						return;
					bModeSel = true;
					break;
				// Options
				case "-verbose":
					bVerbose = true;
					break;
			}
		}
		System.out.println("Russifier for minecraft v1.0 © SimSonic, 2013");
		switch(mode)
		{
			case glyph:
				GlyphSizesBinFixer.runGlyphSizeBinFixer(bVerbose);
				break;
			case lang:
				LanguageMerger.runLanguageMerger();
				break;
			case help:
				System.out.println("Usage: java -jar <thisfile>.jar [-glyph|-lang|-help]\n");
				System.out.println("\t-glyph\tfind all unicode_page_##.png files and generate glyph_sizes.bin");
				System.out.println("\t-lang\tmerge en_US.lang, ru_Mojang.lang and ru_Fourgotten.lang into single output.lang");
				break;
		}
	}
}
