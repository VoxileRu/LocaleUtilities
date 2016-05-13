package ru.simsonic.russifier;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class LanguageMerger
{
	public static void runLanguageMerger()
	{
		final HashMap<String, String> lng_original = new HashMap<>();
		final HashMap<String, String> trans_mojang = new HashMap<>();
		final HashMap<String, String> trans_voxile = new HashMap<>();
		// Язык оригинала
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("en_US.lang"), "UTF-8")))
		{
			System.out.print("Reading en_US.lang...");
			for(String line = br.readLine(); line != null; line = br.readLine())
			{
				String[] kv = line.split("=");
				if(kv == null)
					continue;
				if(kv.length < 2)
					continue;
				lng_original.put(kv[0], kv[1]);
			}
			System.out.println(Integer.toString(lng_original.size()));
		} catch(IOException ex) {
			System.out.println(ex.getLocalizedMessage());
			return;
		}
		// Официальный русский перевод
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("ru_Mojang.lang"), "UTF-8")))
		{
			System.out.print("Reading ru_Mojang.lang...");
			for(String line = br.readLine(); line != null; line = br.readLine())
			{
				String[] kv = line.split("=");
				if(kv == null)
					continue;
				if(kv.length < 2)
					continue;
				trans_mojang.put(kv[0], kv[1]);
			}
			System.out.println(Integer.toString(trans_mojang.size()));
		} catch(IOException ex) {
			System.out.println(ex.getLocalizedMessage());
			System.out.println("Presence of this file is not critical, so we will continue!");
		}
		// Русский перевод Fourgotten
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("ru_Fourgotten.lang"), "UTF-8")))
		{
			System.out.print("Reading ru_Fourgotten.lang...");
			for(String line = br.readLine(); line != null; line = br.readLine())
			{
				String[] kv = line.split("=");
				if(kv == null)
					continue;
				if(kv.length < 2)
					continue;
				trans_voxile.put(kv[0], kv[1]);
			}
			System.out.println(Integer.toString(trans_voxile.size()));
		} catch(IOException ex) {
			System.out.println(ex.getLocalizedMessage());
			System.out.println("Presence of this file is not critical, so we will continue!");
		}
		System.out.println("Sorting and selecting translations");
		// Полный список ключей
		final HashSet<String> all_keys = new HashSet<>(lng_original.keySet());
		all_keys.addAll(trans_voxile.keySet());
		all_keys.addAll(trans_mojang.keySet());
		// Сортировка ключей по алфавиту
		final ArrayList<String> all_list = new ArrayList(all_keys);
		Collections.sort(all_list);
		final ArrayList<String> en_list = new ArrayList(lng_original.keySet());
		Collections.sort(en_list);
		// Процесс перевода строк
		final HashMap<String, String> trans_final = new HashMap<>();
		for(String key : all_list)
		{
			// Низший приоритет у оригинального английского
			String trans = lng_original.get(key);
			// Официальный перевод посередине
			if(trans_mojang.containsKey(key))
				trans = trans_mojang.get(key);
			// Самый высокий приоритет - самодеятельность
			if(trans_voxile.containsKey(key))
				trans = trans_voxile.get(key);
			trans_final.put(key, trans);
		}
		// Запись результата
		try(OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("output.lang"), "UTF-8"))
		{
			System.out.print("Writing " + Integer.toString(all_list.size()) + " values to output.lang...");
			for(String key : en_list)
				osw.write(key + "=" + trans_final.get(key) + "\n");
			osw.write("\n# Strings with keys not found in en_US.lang\n\n");
			for(String key : all_list)
				if(lng_original.containsKey(key) == false)
					osw.write(key + "=" + trans_final.get(key) + "\n");
			osw.write("\n# Russifier v1.0 — автоматическая русификация © SimSonic, 2013-2014\n# Все права принадлежат http://voxile.ru");
		} catch(IOException ex) {
			System.out.println(ex);
			return;
		}
		System.out.println("done.");
	}
}