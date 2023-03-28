package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.tokenizer.TokenType;

import java.util.List;

public class PrintStatement extends Statement {
    private Expression expression;

    public void setExpression(Expression parseExpression) {
        this.expression = addChild(parseExpression);
    }


    public Expression getExpression() {
        return expression;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    // type_expression = 'int' | 'string' | 'bool' | 'object' | 'list' [, '<' , type_expression, '>']
    public void execute(CatscriptRuntime runtime) {
        Object evaluate = expression.evaluate(runtime);
        Object printable = null;
        if (evaluate instanceof Integer) {
            printable = evaluate;
        } else if (evaluate instanceof Boolean) {
            printable = evaluate;
        } else if (evaluate instanceof String) {
            printable = evaluate;
        } else if (evaluate instanceof List) {
            printable = evaluate;
        } else if (expression.getType().equals(CatscriptType.BOOLEAN)) {
            BooleanLiteralExpression literalExpression = (BooleanLiteralExpression) evaluate;
            printable = (Boolean) literalExpression.evaluate(runtime);
        } else if (expression.getType().equals(CatscriptType.STRING)) {
            StringLiteralExpression literalExpression = (StringLiteralExpression) evaluate;
            printable = (String) literalExpression.evaluate(runtime);
        } else if (expression.getType().equals(CatscriptType.INT)) {
            IntegerLiteralExpression literalExpression = (IntegerLiteralExpression) evaluate;
            printable = (Integer) literalExpression.evaluate(runtime);
        } else if (expression.getType().equals(CatscriptType.OBJECT)) {
            printable = expression.evaluate(runtime);
        } else if (expression.getType().equals(CatscriptType.getListType(CatscriptType.OBJECT))) {
            ListLiteralExpression literalExpression = (ListLiteralExpression) evaluate;
            printable = literalExpression.evaluate(runtime);
        } else if (expression.getType().equals(CatscriptType.getListType(CatscriptType.BOOLEAN))) {
            ListLiteralExpression literalExpression = (ListLiteralExpression) evaluate;
            printable = literalExpression.evaluate(runtime);
        } else if (expression.getType().equals(CatscriptType.getListType(CatscriptType.INT))) {
            ListLiteralExpression literalExpression = (ListLiteralExpression) evaluate;
            printable = literalExpression.evaluate(runtime);
        } else if (expression.getType().equals(CatscriptType.getListType(CatscriptType.STRING))) {
            ListLiteralExpression literalExpression = (ListLiteralExpression) evaluate;
            printable = literalExpression.evaluate(runtime);
        }
        getProgram().print(printable);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        super.compile(code);
    }

}
