package sclp;

import sclp.SimpleCLParser.OPTION_NUM;

public class ParserQualifier {

	private final SimpleCLParser parser;

	public ParserQualifier(SimpleCLParser parser) {
		this.parser = parser;
	}

	public ParserQualifier description(String str) {
		String optionName = parser.nowSettingOptionName;
		parser.optionDescriptionMap.put(optionName, str);
		return this;
	}

	public ParserQualifier withArgument() {
		String optionName = parser.nowSettingOptionName;
		parser.optionNameSettingMap.put(optionName, OPTION_NUM.ONE);
		return this;
	}

	public ParserQualifier withArgumentList() {
		String optionName = parser.nowSettingOptionName;
		parser.optionNameSettingMap.put(optionName, OPTION_NUM.LIST);
		return this;
	}

	public ParserQualifier withAlias(String aliasName) {
		String optionName = parser.nowSettingOptionName;
		parser.optionNameAliasMap.put(aliasName, optionName);
		return this;
	}
}
