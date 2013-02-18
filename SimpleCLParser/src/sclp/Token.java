package sclp;


public class Token {
	private String tokenType, value;

	public Token(String tokenType, String value) {
		this.tokenType = tokenType;
		this.value = value;
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Token)) {
			return false;
		}

		Token t = (Token)obj;
		return t.getTokenType().equals(getTokenType()) && t.getValue().equals(getValue());
	}

	@Override
	public String toString() {
		return "<" + getTokenType() + ", " + getValue() + ">";
	}
}
