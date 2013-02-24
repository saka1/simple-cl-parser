package sclp;

import java.util.*;

public class SimpleCLParser {

	private Lexer lexer;
	private CommandLine clResult;

	HashMap<String, OPTION_NUM> optionNameSettingMap;
	String nowSettingOptionName;
	HashMap<String, String> optionDescriptionMap;
	HashMap<String, String> optionNameAliasMap;

	enum OPTION_NUM { ZERO, ONE, LIST };

	public SimpleCLParser() {
		this.optionNameSettingMap = new HashMap<String, SimpleCLParser.OPTION_NUM>();
		this.optionDescriptionMap = new HashMap<String, String>();
		this.optionNameAliasMap = new HashMap<String, String>(); // alias to optionName
	}

	/** setter for debug */
	public void setLexer(Lexer lex) {
		this.lexer = lex;
	}

	private boolean check(String tokenType) {
		Token t = lexer.lookahead();
		if (t == null) {
			return false;
		}
		return t.getTokenType().equals(tokenType);
	}

	private void updateClResult(String optionName, List<String> value) {
		this.clResult.put(optionName, value);

		//for alias
		for (Map.Entry<String, String> entry : this.optionNameAliasMap.entrySet()) {
			String aliasName = entry.getKey();
			String option = entry.getValue();
			if (option.equals(optionName)) {
				this.clResult.put(aliasName, value);
			}
		}
	}

	private String convToOrigOptionName(String optionName) {
		if (this.optionNameAliasMap.containsKey(optionName)) {
			return this.optionNameAliasMap.get(optionName);
		} else {
			return optionName;
		}
	}

	public String digit() {
		if (check("digit")) {
			return lexer.advanceToken().getValue();
		}
		this.clResult.setParseFailed();
		return null;
	}

	public String alphabet() {
		if (check("alphabet")) {
			return lexer.advanceToken().getValue();
		}
		this.clResult.setParseFailed();
		return null;
	}

	/**
	 * alphanumeric_seq := (DIGIT | ALPHABET)+
	 */
	public String alphanumericSeq() {
		StringBuilder sb = new StringBuilder();
		while (check("digit") || check("alphabet")) {
			sb.append(lexer.advanceToken().getValue());
		}
		return sb.length() == 0 ? null : sb.toString();
	}

	/** option_name := DIGIT | ALPHABET
	 *
	 * */
	private String optionName() {
		if (check("digit") || check("alphabet")) {
			return lexer.advanceToken().getValue();
		}
		return null;
	}

	private String optionArgument() {
		if (check("ident") || check("StringLiteral")) {
			return lexer.advanceToken().getValue();
		} else if (check("alphabet")) {
			StringBuilder sb = new StringBuilder();
			while (lexer.lookahead().getTokenType().equals("alphabet")) {
				sb.append(lexer.advanceToken().getValue());
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * NOTE: option argument is "argument list" or "one argument". It depends on the configulation.
	 * By default, no argument is assumed.
	 * option_suffix_list = space option_argument (SPACE option_argument)*
	 *                    |       option_argument (SPACE option_argument)*
	 *                    | ε
	 */
	private List<String> optionSuffixList(OPTION_NUM optionNum) {
		List<Token> lookahead = lexer.lookaheadN(2);
		Token first = lookahead.get(0), second = lookahead.get(1);
		if (first.getTokenType().equals("space") && second.getTokenType().equals("-")) {
			return new ArrayList<String>();
		}

		if (first.getTokenType().equals("space")) {
			lexer.advanceToken();
		}

		List<String> list = new ArrayList<String>();
		switch (optionNum) {
		default:
		case ZERO:
			return new ArrayList<String>();
		case ONE:
			list.add(optionArgument());
			return list;
		case LIST:
			String str = optionArgument();
			list.add(str);
			String expectSpace = lexer.lookahead(1).getTokenType();
			String expectOptionArgument = lexer.lookahead(2).getTokenType();
			while(str != null && expectSpace.equals("space") &&
					(expectOptionArgument.equals("ident") ||
					 expectOptionArgument.equals("StringLiteral") ||
					 expectOptionArgument.equals("alphabet"))) {
				lexer.advanceToken();
				list.add(optionArgument());

				expectSpace = lexer.lookahead(1).getTokenType();
				expectOptionArgument = lexer.lookahead(2).getTokenType();
			}
			return list;
		}
	}

	/**
	 * shortNameOption := "-" option_name option_suffix
	 * @return return whether parse success
	 */
	private void shortNameOption() {
		if (!check("-")) {
			this.clResult.setParseFailed();
			return;
		}
		lexer.advanceToken(); //dispose "-"

		String optionName = optionName();
		optionName = convToOrigOptionName(optionName);

		if (optionName == null) {
			this.clResult.setParseFailed();
			return;
		}

		List<String> optionSuffix;
		if (this.optionNameSettingMap.containsKey(optionName)) {
			OPTION_NUM optionNum = this.optionNameSettingMap.get(optionName);
			optionSuffix = optionSuffixList(optionNum);
		} else {
			optionSuffix = optionSuffixList(OPTION_NUM.ZERO);
		}
		updateClResult(optionName, optionSuffix);
	}

	/**
	 * longNameOption := "--" alphanumeric_sec option_suffix
	 */
	private void longNameOption() {
		if (!check("--")) {
			this.clResult.setParseFailed();
			return;
		}
		lexer.advanceToken();

		if (check("alphabet") || check("digit")) {
			String optionName = alphanumericSeq();
			optionName = convToOrigOptionName(optionName);

			List<String> optionSuffix;
			if (this.optionNameSettingMap.containsKey(optionName)) {
				OPTION_NUM optionNum = this.optionNameSettingMap.get(optionName);
				optionSuffix = optionSuffixList(optionNum);
			} else {
				optionSuffix = optionSuffixList(OPTION_NUM.ZERO);
			}
			updateClResult(optionName, optionSuffix);

			return;
		}
		this.clResult.setParseFailed();
	}

	/**
	 * operand := alphanumeric_seq (SPACE alphanumeric_seq)*
	 *          | ε
	 */
	private void operand() {
		String head = alphanumericSeq();
		if (head == null) {
			return;
		}
		this.clResult.addOperand(head);
		while (check("space")) {
			lexer.advanceToken();
			String alphanumeric = alphanumericSeq();
			if (alphanumeric == null) {
				return;
			}
			this.clResult.addOperand(alphanumeric);
		}
	}

	/**
	 * grammar := (SPACE? shortNameOption)* ("--" SPACE)? operand SPACE*
	 *          | (SPACE?  longNameOption)* ("--" SPACE)? operand SPACE*
	 */
	private void grammar() {
		while (!lexer.isEOF()) {
			if (check("space")) {
				lexer.advanceToken();
			}

			// LL(2)
			String first = lexer.lookahead(1).getTokenType();
			String second = lexer.lookahead(2).getTokenType();
			if (first.equals("-") && (second.equals("digit") || second.equals("alphabet"))) {
				shortNameOption();
			} else if (first.equals("--") && (second.equals("digit") || second.equals("alphabet"))) {
				longNameOption();
			} else {
				break;
			}
		}

		if (!lexer.isEOF() && check("--")) {
			lexer.advanceToken();
			if (check("space")) {
				lexer.advanceToken();
			}
			operand();
		} else {
			operand();
		}

		while (check("space")) {
			lexer.advanceToken();
		}
		if (!lexer.isEOF()) {
			this.clResult.setParseFailed();
		}
	}

	public ParserQualifier addOption(String optionName) {
		if (optionName.length() != 1) {
			throw new IllegalArgumentException();
		}
		this.nowSettingOptionName = optionName;
		this.optionNameSettingMap.put(optionName, OPTION_NUM.ZERO);
		return new ParserQualifier(this);
	}

	/*
	public ParserQualifier addExclusiveOption(String exclusiveOptionGroup) {
		this.nowSettingOptionName = exclusiveOptionGroup;
	}
	*/

	public ParserQualifier addLongnameOption(String optionName) {
		this.nowSettingOptionName = optionName;
		this.optionNameSettingMap.put(optionName, OPTION_NUM.ZERO);
		return new ParserQualifier(this);
	}

	public CommandLine parse(String source) {
		lexer = new Lexer(source);
		this.clResult = new CommandLine();

		grammar();

		return this.clResult;
	}

	public CommandLine parse(String[] args) {
		StringBuilder sb = new StringBuilder();
		for (String str : args) {
			sb.append(str).append(" ");
		}
		return parse(sb.toString());
	}

	public String getUsage() {
		StringBuilder sb = new StringBuilder("Usage: command");
		for (Map.Entry<String, OPTION_NUM> e : this.optionNameSettingMap.entrySet()) {
			String optionName = e.getKey();
			sb.append(" [-").append(optionName).append("]");
		}
		sb.append(" operand");
		return sb.toString();
	}
}
