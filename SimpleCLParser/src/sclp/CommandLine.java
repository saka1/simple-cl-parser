package sclp;

import java.util.*;

public class CommandLine {
	private SortedMap<String, List<String>> result;
	private List<String> operand;

	private boolean parseFailed;

	public CommandLine() {
		this.result = new TreeMap<String, List<String>>();
		this.parseFailed = false;
		this.operand = new ArrayList<String>();
	}

	public void put(String optionName, List<String> list) {
		if (optionName.substring(0, 1).matches("[a-zA-Z\\d]")) {
			this.result.put(optionName, list);
			return;
		}
		// option name must be alphabet
		throw new IllegalArgumentException();
	}

	public boolean hasOption(String optionName) {
		return this.result.containsKey(optionName);
	}

	public Iterator<String> optionNameIterator() {
		return this.result.keySet().iterator();
	}

	public int getOptionSize() {
		return this.result.size();
	}

	public String getOptionArgument(String optionName) {
		return this.result.get(optionName).get(0);
	}

	public List<String> getOptionArgumentList(String optionName) {
		return this.result.get(optionName);
	}

	public List<String> getOperand() {
		return this.operand;
	}

	public void addOperand(String op) {
		this.operand.add(op);
	}

	public void setParseFailed() {
		this.parseFailed = true;
	}

	public boolean isParseFailed() {
		return this.parseFailed;
	}

	public boolean isParseSuccess() {
		return !this.parseFailed;
	}
}
