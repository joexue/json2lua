/*
 * Json2Lua.java
 *
 * Copyright (c) Joe Xue (lgxue@hotmail.com) 2026. All rights reserved
 */

package org.json2lua;

import java.io.PrintWriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.misc.Interval;

class Json2Lua
{

    static void help() {
        System.out.println("Usage: json2lua <inputfile> -o <outout file>\n");
    }

    public static void main(String[] args) throws Exception {
        String outFile = "a.lua";
        String inFile = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-o")) {
                i++;
                if (i < args.length) {
                    outFile = args[i];
                }
            } else {
                inFile = arg;
            }
        }

        if (inFile == null) {
            help();
            return;
        }

        CharStream input;
        input = CharStreams.fromFileName(inFile);

        JsonLexer lexer = new JsonLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        JsonParser parser = new JsonParser(tokens);
        ParseTree tree = parser.json();

        String fileName = outFile.substring(outFile.lastIndexOf(java.io.File.separator) + 1);

        JsonHandler handler = new JsonHandler(fileName, input.getText(new Interval(0, input.size())));

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(handler, tree);

        String lua = handler.toLua();

        PrintWriter out = new PrintWriter(outFile);
        out.write(lua);
        out.close();
    }
}
