package sclp;

import java.util.ArrayList;
import java.util.List;


public class Lexer {

	private char[] source;
	private final int sourceLen;
	private int index;

	public Lexer(String str) {
		source = str.toCharArray();
		sourceLen = source.length;
		index = 0;
	}

	private void skipWhiteCharacter() {
		while (index < sourceLen
				&& (source[index] == ' ' || source[index] == '\r' || source[index] == '\n')) {
			index++;
		}
	}

	public boolean isEOF() {
		Token t = lookahead();
		if (t == null) return false;
		return t.getTokenType().equals("EOS");
	}

	public Token lookahead() {
		int origIndex = index;
		Token t = advanceToken();
		this.index = origIndex;
		return t;
	}

	public Token lookahead(int n) {
		List<Token> list = lookaheadN(n);
		return list.get(n - 1);
	}

	public List<Token> lookaheadN(int n) {
		int origIndex = this.index;
		List<Token> result = new ArrayList<Token>();
		for (int i = 0; i < n; i++) {
			result.add(advanceToken());
		}
		this.index = origIndex;
		return result;
	}

	public Token advanceToken() {

		if (index == sourceLen) {
			return new Token("EOS", null);
		}

		if (source[index] == ' ' || source[index] == '\r' || source[index] == '\n') {
			skipWhiteCharacter();
			return new Token("space", " ");
		}

		// alphabet [a-zA-Z]
		if (('a' <= source[index] && source[index] <= 'z') || ('A' <= source[index] && source[index] <= 'Z')) {
			Token t = new Token("alphabet", new String(source, index, 1));
			index++;
			return t;
		}

		// digit(0 .. 9)
		if ('0' <= source[index] && source[index] <= '9') {
			Token t = new Token("digit", new String(source, index, 1));
			index++;
			return t;
		}

		// "-" or "--"
		if (source[index] == '-') {
			if (index + 1 < sourceLen && source[index + 1] == '-') {
				Token t = new Token("--", "--");
				index += 2;
				return t;
			} else {
				Token t = new Token("-", "-");
				index++;
				return t;
			}
		}

		if (source[index] == '"') { // STR_LITERAL
			int tail = index + 1;
			StringBuilder buffer = new StringBuilder();
			outerLoop:
				while (true) {
					switch (source[tail]) {
					case '"' : break outerLoop;
					case '\\':
						switch (source[tail + 1]) {
						case '"':
							buffer.append("\"");
							tail += 2;
							break;
						case '\\': // \\
							buffer.append("\\");
							tail += 2;
							break;
						default:
							return null;
						}
						break;
					default:
						buffer.append(source[tail]);
						tail++;
					}
				}

			Token t = new Token("StringLiteral", buffer.toString());
			this.index = tail + 1;
			return t;
		}

		//ident
		int tail = this.index;
		while (tail < sourceLen
				&& !(source[index] == ' ' || source[index] == '\r' || source[index] == '\n')) {
			tail++;
		}
		Token t = new Token("ident", new String(source, index, tail - index));
		this.index = tail;
		return t;
	}
}
