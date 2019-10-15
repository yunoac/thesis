package tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import IO.FileIO;
import utils.Cmp;

public class TestTools {
	
	public static boolean equal(double[][] a, double[][] b) {
		if(a.length != b.length) return false;
		for(int i = 0; i < a.length; i++) {
			if(a[i].length != b[i].length) return false;
			for(int j = 0; j < b.length; j++) {
				if(!Cmp.eq(a[i][j], b[i][j])) return false;
			}
		}
		return true;
	}
	
	public static boolean compareFiles(File f1, File f2) {
		ArrayList<String> l1 = FileIO.readlines(f1);
		ArrayList<String> l2 = FileIO.readlines(f2);
		if(l1.size() != l2.size()) return false;
		for(int i = 0; i < l1.size(); i++) {
			if(!l1.get(i).equals(l2.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean compare(String s, File f) {
		try {
			File ftmp = new File("./tmp");
			PrintWriter writer = new PrintWriter(ftmp);
			writer.write(s);
			writer.close();
			boolean ok = compareFiles(ftmp, f);
			//ftmp.delete();
			return ok;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


}
