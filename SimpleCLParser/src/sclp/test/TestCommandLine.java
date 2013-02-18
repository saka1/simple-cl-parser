package sclp.test;

import static org.testng.Assert.*;

import java.util.*;

import org.testng.annotations.*;

import sclp.*;

public class TestCommandLine {
	@Test
	public void testConstruct() {
		CommandLine cl = new CommandLine();

		assertEquals(cl.getOperand().size(), 0);
		assertFalse(cl.optionNameIterator().hasNext());
	}

	@Test
	public void testOptionSize() {
		CommandLine cl = new CommandLine();
		assertEquals(cl.getOptionSize(), 0);
		cl.put("a", new ArrayList<String>());
		assertEquals(cl.getOptionSize(), 1);
	}

	@Test
	public void testPutAndGetOptionName() {
		CommandLine cl = new CommandLine();
		List<String> list = new ArrayList<String>();
		list.add("x");
		list.add("y");
		cl.put("a", list);

		assertTrue(cl.hasOption("a"));
		assertFalse(cl.hasOption("b"));
		assertEquals(cl.getOptionArgumentList("a"), list);
		assertEquals(cl.getOptionArgument("a"), cl.getOptionArgumentList("a").get(0));
	}

	@Test
	public void testAddOperand() {
		CommandLine cl = new CommandLine();
		cl.addOperand("x");
		cl.addOperand("y");

		assertEquals(cl.getOperand().get(0), "x");
		assertEquals(cl.getOperand().get(1), "y");
	}

	@Test
	public void testSuccessAndFail() {
		CommandLine cl = new CommandLine();

		assertFalse(cl.isParseFailed());
		assertTrue(cl.isParseSuccess());

		cl.setParseFailed();

		assertTrue(cl.isParseFailed());
		assertFalse(cl.isParseSuccess());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testPutIllegalOptionName() {
		CommandLine cl = new CommandLine();
		cl.put("ぬこ", new ArrayList<String>());
	}
}
