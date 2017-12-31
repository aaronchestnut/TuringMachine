package turing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Scanner;

public class tmMain {

	private File file;
	private ArrayList<String> tmFileList;

	public static void main(String[] args) {

		tmMain tmmain = new tmMain();
	}

	public tmMain() {
		run();
	}

	private void run() {
		Scanner in = new Scanner(System.in);

		System.out.println("What is the full file path? xx (This is case sensitive)");
		String input = in.nextLine();

		if (input.endsWith(".tm")) {
			file = new File(input);
			parseFile();
			if (tmFileList.size() <= 0) {
				System.out.println("This file is empty.");
			} else {
				System.out.println("What is the test word?");
				String testWord = in.nextLine();

				tmachine tm = new tmachine(tmFileList, testWord);
				if (tm.getCanSimulate()) {
					tm.simulate();
					System.out.println(tm.getResult());
				} else
					System.out.println(tm.getError());

			}
		}
		else
			System.out.println("This is not a valid file type. Maybe check path or capitalization? ");

	}


	private void parseFile() {
		String line = null;
		tmFileList = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();		// display CFG

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				tmFileList.add(line);
				sb.append(line).append("\n");
			}
			System.out.println(sb.toString());
			br.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
