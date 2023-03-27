package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.tokenizer.TokenType;

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
        // we must eval twice when printing an identifier expression
        if (expression.getStart() != null && expression.getStart().getType().equals(TokenType.IDENTIFIER)) {
            if (expression.getType().equals(CatscriptType.BOOLEAN)) {
                BooleanLiteralExpression booleanLiteralExpression = (BooleanLiteralExpression) expression.evaluate(runtime);
                Boolean bool = (Boolean) booleanLiteralExpression.evaluate(runtime);
                getProgram().print(bool);
                return;
            }
            if (expression.getType().equals(CatscriptType.STRING)) {
                StringLiteralExpression stringLiteralExpression = (StringLiteralExpression) expression.evaluate(runtime);
                String string = (String) stringLiteralExpression.evaluate(runtime);
                getProgram().print(string);
                return;
            }
//            if (expression.getType().isAssignableFrom(CatscriptType.getListType(CatscriptType.BOOLEAN))) {
            if (expression.getType().equals(CatscriptType.getListType(CatscriptType.OBJECT))) {
                ListLiteralExpression listLiteralExpression = (ListLiteralExpression) expression.evaluate(runtime);
                getProgram().print(listLiteralExpression.evaluate(runtime));
                return;
            }
            if (expression.getType().equals(CatscriptType.getListType(CatscriptType.INT))) {
                ListLiteralExpression listLiteralExpression = (ListLiteralExpression) expression.evaluate(runtime);
                getProgram().print(listLiteralExpression.evaluate(runtime));
                return;
            }
        }
//        if (expression.getType().equals(CatscriptType.ListType.INT)) {
//            getProgram().print(expression.evaluate(runtime));
//            return;
//        };
        getProgram().print(expression.evaluate(runtime));
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
