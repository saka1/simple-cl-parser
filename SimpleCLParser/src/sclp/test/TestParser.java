package sclp.test;

import static org.testng.Assert.*;

import java.util.*;

import org.testng.annotations.*;

import sclp.*;

public class TestParser {

	@Test
	public void testDigit() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.setLexer(new Lexer("1"));
		String result = parser.digit();
		assertEquals(result, "1");
	}

	@Test
	public void testAlphabet() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.setLexer(new Lexer("a"));
		String result = parser.alphabet();
		assertEquals(result, "a");
	}

	@Test
	public void testAlphanumericSeq() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.setLexer(new Lexer("a100b"));
		String result = parser.alphanumericSeq();
		assertEquals(result, "a100b");

		parser.setLexer(new Lexer("---"));
		result = parser.alphanumericSeq();
		assertNull(result);
	}

	@Test
	public void testParseFlag() {
		SimpleCLParser parser = new SimpleCLParser();
		CommandLine result = parser.parse("-c");

		assertTrue(result.isParseSuccess());

		assertTrue(result.hasOption("c"));
	}

	@Test
	public void testParseWithOptionArgument() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("c").withArgument();
		CommandLine result = parser.parse("-c hoge");

		assertTrue(result.isParseSuccess());

		assertTrue(result.hasOption("c"));
		assertEquals(result.getOptionArgument("c"), "hoge");
	}

	@Test
	public void testParseWithOptionArgumentWithoutSpace() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("c").withArgument();
		CommandLine result = parser.parse("-choge");

		assertTrue(result.isParseSuccess());

		assertTrue(result.hasOption("c"));
		assertEquals(result.getOptionArgument("c"), "hoge");
	}


	@Test
	public void testWithArgumentWithoutArgumnt() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgument();
		CommandLine result = parser.parse("-a");

		assertTrue(result.isParseSuccess());
		assertTrue(result.hasOption("a"));
		assertNull(result.getOptionArgument("a"));
	}

	@Test
	public void testParseWithTwoOptionsAndArguments() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgument();
		parser.addOption("1").withArgument();
		CommandLine result = parser.parse("-a hoge -1 foo");

		assertTrue(result.isParseSuccess());

		assertTrue(result.hasOption("a"));
		assertTrue(result.hasOption("1"));
		assertEquals(result.getOptionArgument("a"), "hoge");
		assertEquals(result.getOptionArgument("1"), "foo");
	}

	@Test
	public void testParseWithStringLiteralOptionArgument() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgument();
		CommandLine result = parser.parse("-a\"msg\"");

		assertTrue(result.hasOption("a"));
		assertEquals(result.getOptionArgument("a"), "msg");
	}

	@Test
	public void testParseWithTwoFlags() {
		SimpleCLParser parser = new SimpleCLParser();
		CommandLine result = parser.parse("-a -1");

		assertFalse(result.isParseFailed());
		assertTrue(result.hasOption("a"));
		assertTrue(result.hasOption("1"));
	}

	@Test
	public void testFailCase() {
		SimpleCLParser parser = new SimpleCLParser();
		CommandLine result = parser.parse("---");
		assertTrue(result.isParseFailed());
	}

	@Test
	public void testIllegalOptionName() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a");
		CommandLine result = parser.parse("-日本語");
		assertTrue(result.isParseFailed());
		assertFalse(result.isParseSuccess());
	}

	@Test
	public void testWhiteSpaceAfterOperand() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a");
		CommandLine result = parser.parse("-a      ");
		assertTrue(result.isParseSuccess());
	}

	@Test
	public void testOneOperand() {
		SimpleCLParser parser = new SimpleCLParser();
		CommandLine result = parser.parse("hoge");
		List<String> expected = new ArrayList<String>();
		expected.add("hoge");

		assertTrue(result.isParseSuccess());
		assertEquals(result.getOperand(), expected);
	}

	@Test
	public void testTwoOperands() {
		SimpleCLParser parser = new SimpleCLParser();
		CommandLine result = parser.parse("hoge foo");
		List<String> expected = new ArrayList<String>();
		expected.add("hoge");
		expected.add("foo");

		assertTrue(result.isParseSuccess());
		assertEquals(result.getOperand(), expected);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testAddOptionWithEmptyArgument() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testAddOptionWithNullArgument() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption(null);
	}

	@Test
	public void testAddOptionWithArgumentList() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgumentList();
		CommandLine result = parser.parse("-a x y z");

		List<String> expected = new ArrayList<String>();
		expected.add("x");
		expected.add("y");
		expected.add("z");

		assertTrue(result.isParseSuccess());
		assertEquals(result.getOptionArgumentList("a"), expected);
	}

	@Test
	public void testGetUsage() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgumentList();
		String expected = "Usage: command [-a] operand";

		assertEquals(parser.getUsage(), expected);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testIllegalAddOption() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("-a");
	}

	@Test
	public void testDoubleHyphen() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgumentList();
		CommandLine result = parser.parse("-a x y -- z w");

		List<String> expectedArgument = new ArrayList<String>();
		expectedArgument.add("x");
		expectedArgument.add("y");

		List<String> expectedOperand = new ArrayList<String>();
		expectedOperand.add("z");
		expectedOperand.add("w");

		assertTrue(result.isParseSuccess());
		assertEquals(result.getOptionArgumentList("a"), expectedArgument);
		assertEquals(result.getOperand(), expectedOperand);
	}

	@Test
	public void testParseArrayArgument() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgument();
		CommandLine result = parser.parse(new String[] {"-a", "hoge"});

		assertEquals(result.getOptionArgument("a"), "hoge");
	}

	@Test
	public void testQualifierOverride() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgumentList().withArgument(); //override
		CommandLine result = parser.parse("-a x y z");

		List<String> expectedArgumentList = new ArrayList<String>();
		expectedArgumentList.add("x");

		List<String> expectedOperand = new ArrayList<String>();
		expectedOperand.add("y");
		expectedOperand.add("z");

		assertEquals(result.getOptionArgumentList("a"), expectedArgumentList);
		assertEquals(result.getOperand(), expectedOperand);
	}

	@Test
	public void testFailCaseAddFlagWithArgument() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a");
		parser.addOption("b");
		CommandLine result = parser.parse("-a x -b");

		assertTrue(result.isParseFailed());
	}

	//exclusive option

	@Test
	public void testAddLongname() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addLongnameOption("opt");
		CommandLine result = parser.parse("--opt");

		assertTrue(result.isParseSuccess());
		assertTrue(result.hasOption("opt"));
	}

	@Test
	public void testAddLongnameWithOptionArgument() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addLongnameOption("opt").withArgument();
		CommandLine result = parser.parse("--opt hoge");

		assertTrue(result.hasOption("opt"));
		assertEquals(result.getOptionArgument("opt"), "hoge");
	}

	@Test
	public void testWithAliasWithArgument() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgument().withAlias("b");
		CommandLine result = parser.parse("-b hoge");

		assertTrue(result.isParseSuccess());
		assertTrue(result.hasOption("a"));
		assertTrue(result.hasOption("b"));
		assertEquals(result.getOptionArgument("a"), "hoge");
		assertEquals(result.getOptionArgument("b"), "hoge");
	}

	@Test
	public void testWithAlias() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withAlias("b");
		CommandLine result = parser.parse("-b");

		assertTrue(result.isParseSuccess());
		assertTrue(result.hasOption("a"));
		assertTrue(result.hasOption("b"));
	}

	@Test
	public void testWithLongnameAliasAndArguments() {
		SimpleCLParser parser = new SimpleCLParser();
		parser.addOption("a").withArgument().withAlias("opt");
		CommandLine result = parser.parse("--opt hoge");

		assertTrue(result.isParseSuccess());
		assertTrue(result.hasOption("opt"));
		assertTrue(result.hasOption("a"));
		assertEquals(result.getOptionArgument("a"), "hoge");
		assertEquals(result.getOptionArgument("opt"), "hoge");
	}
}
