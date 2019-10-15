package IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIO {

	public static ArrayList<String> readlines(File f) {
		try {
			Scanner reader = new Scanner(new FileReader(f));
			ArrayList<String> lines = new ArrayList<>();
			while(reader.hasNextLine()) {
				lines.add(reader.nextLine());
			}
			reader.close();
			return lines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
