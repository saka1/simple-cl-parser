package sclp.test;


import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import sclp.*;



public class TestTemp {
	public static void mainDemo(String[] args) {
		System.out.println("source " + Arrays.toString(args));
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgument();
		parser.addOption("b").withArgumentList();

		CommandLine result = parser.parse(args);
		if (result.isParseSuccess()) {
			System.out.println("Parse Success");
			for (Iterator<String> it = result.optionNameIterator(); it.hasNext();) {
				String optionName = it.next();
				System.out.println("Option " + optionName + ": " +
				                   result.getOptionArgumentList(optionName));
			}
		} else {
			System.out.println("Parse failed");
		}
	}


	public static void main(String[] args) {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withAlias("b");
		CommandLine result = parser.parse("-b hoge");

		assertTrue(result.hasOption("a"));
		assertTrue(result.hasOption("b"));
	}
}
