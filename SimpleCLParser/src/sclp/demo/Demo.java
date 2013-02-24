package sclp.demo;

import java.io.*;
import java.util.*;

import sclp.CommandLine;
import sclp.SimpleCLParser;

public class Demo {

	public static void main(String[] args) throws Exception {
		//System.out.println("source " + Arrays.toString(args));
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgument();
		parser.addOption("b").withArgumentList();

		while (true) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			CommandLine result = parser.parse(br.readLine());
			if (result.isParseSuccess()) {
				System.out.println("Parse Success");
				for (Iterator<String> it = result.optionNameIterator(); it.hasNext();) {
					String optionName = it.next();
					System.out.println("Option " + optionName + ": " +
							result.getOptionArgumentList(optionName));
				}
				System.out.println("Operand: " + result.getOperand());
			} else {
				System.out.println("Parse failed");
			}
			System.out.println("-----");
		}
	}
}
