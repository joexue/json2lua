/*
 * JsonHandler.java
 *
 * Copyright (c) Joe Xue (lgxue@hotmail.com) 2026. All rights reserved
 */

package org.json2lua;

import java.util.Stack;
import java.util.ArrayList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class JsonHandler extends JsonBaseListener {

    private String fileName;
    private String jsonContent;
    private Stack<ArrayList<String>> stack;
    private int ident;
    private STGroup stg;
    private String lua;

    public JsonHandler(String fileName, String jsonContent) {
        this.fileName = fileName;
        this.jsonContent = jsonContent;
        stack = new Stack<ArrayList<String>>();
        stg = new STGroupFile("st/json2lua.stg");
        ident = 0;
        lua = "";
    }

	@Override
    public void enterJson(JsonParser.JsonContext ctx) {
        ArrayList<String> json = new ArrayList<String>();
        stack.push(json);
    }

	@Override
    public void exitJson(JsonParser.JsonContext ctx) {
        ArrayList<String> json = stack.pop();

        ST st = stg.getInstanceOf("json2lua");
        st.add("val", json);
        st.add("year", java.time.Year.now());
        st.add("fileName", fileName);
        st.add("jsonContent", jsonContent);

        lua = st.render();
    }

	@Override
    public void enterObject(JsonParser.ObjectContext ctx) {
        ArrayList<String> object = new ArrayList<String>();
        stack.push(object);
        ident++;
    }

	@Override
    public void exitObject(JsonParser.ObjectContext ctx) {
        ArrayList<String> object = stack.pop();
        ident--;

        ST st = stg.getInstanceOf("object");
        String identStr = " ".repeat(ident * 4);

        st.add("ident", identStr);
        st.add("vals", object);

        stack.peek().add(st.render());
    }

	@Override
    public void enterArray(JsonParser.ArrayContext ctx) {
        ArrayList<String> array = new ArrayList<String>();
        stack.push(array);
        ident++;
    }

	@Override
    public void exitArray(JsonParser.ArrayContext ctx) {
        ArrayList<String> array = stack.pop();
        ident--;

        ST st = stg.getInstanceOf("array");
        String identStr = " ".repeat(ident * 4);

        st.add("ident", identStr);
        st.add("vals", array);

        stack.peek().add(st.render());
    }

	@Override
    public void enterPair(JsonParser.PairContext ctx) {
        ArrayList<String> pair = new ArrayList<String>();
        stack.push(pair);
    }

	@Override
    public void exitPair(JsonParser.PairContext ctx) {
        ArrayList<String> pair = stack.pop();

        ST st = stg.getInstanceOf("pair");
        String key = ctx.getChild(0).getText();

        st.add("key", key);
        st.add("val", pair);

        stack.peek().add(st.render());
    }

	@Override
    public void exitValue(JsonParser.ValueContext ctx) {
        ST st = stg.getInstanceOf("value");

        if (ctx.STRING() != null ||
                ctx.NUMBER() != null ||
                ctx.BOOLEAN() != null) {
            st.add("val", ctx.getText());
            stack.peek().add(st.render());
        }

        if (ctx.NULL() != null) {
            st.add("val", "nil");
            stack.peek().add(st.render());
        }
    }

    public String toLua() {
        return lua;
    }
}
