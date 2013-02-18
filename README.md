simple-cl-parser
================

Summary
----------
An implementation of command line parser in Java.
It has some features as follows:
* Easy to use
* A little fluent inteterface
* POSIX compatible(incomplete)

Sample
----------
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
