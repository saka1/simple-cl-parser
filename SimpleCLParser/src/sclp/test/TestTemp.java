package sclp.test;


import static org.testng.Assert.assertTrue;

import sclp.*;



public class TestTemp {
	public static void main(String[] args) {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withAlias("b");
		CommandLine result = parser.parse("-b hoge");

		assertTrue(result.hasOption("a"));
		assertTrue(result.hasOption("b"));
	}
}
