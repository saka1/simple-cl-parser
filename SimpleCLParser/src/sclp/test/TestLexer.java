package sclp.test;

import static org.testng.Assert.*;

import java.util.List;

import org.testng.annotations.Test;

import sclp.Lexer;
import sclp.Token;

public class TestLexer {

	@Test
	public void readDigit() {
		Lexer lex = new Lexer("0");
		Token expected = new Token("digit", "0");

		assertFalse(lex.isEOF());
		assertEquals(lex.advanceToken(), expected);
		assertTrue(lex.isEOF());
	}

	@Test
	public void readAlphabet() {
		Lexer lex = new Lexer("aA");

		assertFalse(lex.isEOF());
		assertEquals(lex.advanceToken(), new Token("alphabet", "a"));
		assertEquals(lex.advanceToken(), new Token("alphabet", "A"));
		assertTrue(lex.isEOF());
	}

	@Test
	public void readAlphabetWithWhiteSpace() {
		Lexer lex = new Lexer("a");

		assertFalse(lex.isEOF());
		assertEquals(lex.advanceToken(), new Token("alphabet", "a"));
		assertTrue(lex.isEOF());
	}

	@Test
	public void readHyphen() {
		Lexer lex = new Lexer("-");
		Token expected = new Token("-", "-");

		assertFalse(lex.isEOF());
		assertEquals(lex.advanceToken(), expected);
		assertTrue(lex.isEOF());
	}

	@Test
	public void readDoubleHyphen() {
		Lexer lex = new Lexer("--");
		Token expected = new Token("--", "--");

		assertFalse(lex.isEOF());
		assertEquals(lex.advanceToken(), expected);
		assertTrue(lex.isEOF());
	}


	/*
	@Test
	public void readDoubleHyphen() {
		Lexer lex = new Lexer("--");
		Token expected = new Token("--", "--");

		assertFalse(lex.isEOF());
		assertEquals(lex.advanceToken(), expected);
		assertTrue(lex.isEOF());
	}
	*/

	@Test
	public void readStringLiteral() {
		Lexer lex = new Lexer("\"str\"");
		Token expected = new Token("StringLiteral", "str");

		assertFalse(lex.isEOF());
		assertEquals(lex.advanceToken(), expected);
		assertTrue(lex.isEOF());
	}

	@Test
	public void readStringLiteralWithEscapeCharacter() {
		Lexer lex = new Lexer("\"a\\\\ \\\"b\"");
		Token expected = new Token("StringLiteral", "a\\ \"b");
		assertEquals(lex.advanceToken(), expected);
		assertTrue(lex.isEOF());
	}

	@Test
	public void readWhiteSpace() {
		Lexer lex = new Lexer("   ");
		Token expected = new Token("space", " ");

		assertFalse(lex.isEOF());
		assertEquals(lex.advanceToken(), expected);
		assertTrue(lex.isEOF());
	}

	@Test
	public void readIdent() {
		Lexer lex = new Lexer("ぬこ");
		Token expected = new Token("ident", "ぬこ");

		assertFalse(lex.isEOF());
		assertEquals(lex.advanceToken(), expected);
		assertTrue(lex.isEOF());
	}


	@Test
	public void complexTest() {
		Lexer lex = new Lexer("-c foo");

		assertEquals(lex.advanceToken().getTokenType(), "-");
		assertEquals(lex.advanceToken(), new Token("alphabet", "c"));
		assertEquals(lex.advanceToken().getTokenType(), "space");
		assertEquals(lex.advanceToken(), new Token("alphabet", "f"));
		assertEquals(lex.advanceToken(), new Token("alphabet", "o"));
		assertEquals(lex.advanceToken(), new Token("alphabet", "o"));
		assertTrue(lex.isEOF());
	}

	@Test
	public void testLookahead() {
		Lexer lex = new Lexer("-1");
		List<Token> list = lex.lookaheadN(2);

		assertEquals(list.size(), 2);
		assertEquals(list.get(0), new Token("-", "-"));
		assertEquals(list.get(1), new Token("digit", "1"));

		assertEquals(lex.lookahead(), new Token("-", "-"));
	}
}
