package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.*;
import edu.montana.csci.csci468.tokenizer.CatScriptTokenizer;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenList;
import edu.montana.csci.csci468.tokenizer.TokenType;

import javax.swing.plaf.nimbus.State;
import java.util.LinkedList;
import java.util.List;

import static edu.montana.csci.csci468.tokenizer.TokenType.*;

public class CatScriptParser {

    private TokenList tokens;
    private FunctionDefinitionStatement currentFunctionDefinition;

    public CatScriptProgram parse(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();

        // first parse an expression
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = null;
        try {
            expression = parseExpression();
        } catch(RuntimeException re) {
            // ignore :)
        }
        if (expression == null || tokens.hasMoreTokens()) {
            tokens.reset();
            while (tokens.hasMoreTokens()) {
                program.addStatement(parseProgramStatement());
            }
        } else {
            program.setExpression(expression);
        }

        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    public CatScriptProgram parseAsExpression(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = parseExpression();
        program.setExpression(expression);
        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    //============================================================
    //  Statements
    //============================================================

    private Statement parseProgramStatement() {
        Statement functionDefStatement = parseFunctionDefinitionStatement();
        if (functionDefStatement != null) {
            return functionDefStatement;
        }
        return parseStatement();
    }

    // function_declaration = 'function', IDENTIFIER, '(', parameter_list, ')' +
    //                       [ ':' + type_expression ], '{',  { function_body_statement },  '}';
    private Statement parseFunctionDefinitionStatement() {
        if (tokens.match(FUNCTION)){
            FunctionDefinitionStatement def = new FunctionDefinitionStatement();
            def.setStart(tokens.consumeToken());
            // require an identifier (name of the function)
            Token name = require(IDENTIFIER, def);
            def.setName(name.getStringValue());
            // require a left paren
            require(LEFT_PAREN, def);
            if (tokens.matchAndConsume(RIGHT_PAREN)) {
//                CatscriptType catType = new CatscriptType(null, null);
//                TypeLiteral type = new TypeLiteral();
//                type.setType(catType);
//                def.addParameter(null, type);
            } else {
            // require some num of args (comma separated)
                do {
                    String argumentName = tokens.getCurrentToken().getStringValue();
                    Expression expression = parseExpression();
                    TypeLiteral type = new TypeLiteral();
                    type.setType(CatscriptType.OBJECT);
                    // optionally match :
                    if (tokens.matchAndConsume(COLON)) {
                        Expression argType = parseTypeExpression();
                        //assert argType != null;
                        type.setType(argType.getType());
                    }
                    def.addParameter(argumentName, type);
                } while (tokens.matchAndConsume(COMMA));
                require(RIGHT_PAREN, def);
            }

            TypeLiteral type = new TypeLiteral();
            type.setType(CatscriptType.VOID);
            def.setType(type);
            // optionally match :
            if (tokens.matchAndConsume(COLON)) {
                Expression expression = parseTypeExpression();
                type.setType(expression.getType());
                def.setType(type);
            }
            // require {
            require(LEFT_BRACE, def);
            // body of a function declaration
            this.currentFunctionDefinition = def;
            LinkedList<Statement> statements = new LinkedList<Statement>();
            while(!tokens.match(RIGHT_BRACE) && tokens.hasMoreTokens()){
                statements.add(parseStatement());
            }
            // require }
            require(RIGHT_BRACE, def);
            def.setBody(statements);
            this.currentFunctionDefinition = null;
            return def;
        }
        return null;
    }

    // type_expression = 'int' | 'string' | 'bool' | 'object' | 'list' [, '<' , type_expression, '>']
    private Expression parseTypeExpression() {
        Token type = tokens.consumeToken();
        TypeLiteral typeLiteral = new TypeLiteral();
        if (type.getStringValue().equals("int")) {
            typeLiteral.setType(CatscriptType.INT);
            return typeLiteral;
        } else if (type.getStringValue().equals("string")) {
            typeLiteral.setType(CatscriptType.STRING);
            return typeLiteral;
        } else if (type.getStringValue().equals("bool")) {
            typeLiteral.setType(CatscriptType.BOOLEAN);
            return typeLiteral;
        } else if (type.getStringValue().equals("object")) {
            typeLiteral.setType(CatscriptType.OBJECT);
            return typeLiteral;
        }
        // recursive call here to deal with lists
        // TODO: support list
        else if (type.getStringValue().equals("list")) {
            require(LESS, typeLiteral);
            Expression typeExpression = parseTypeExpression();
            typeLiteral.setType(CatscriptType.getListType(typeExpression.getType()));
            require(GREATER, typeLiteral);
            return typeLiteral;
        }
        else {
            return null;
        }
    }

    private Statement parseStatement() {
        Statement printStmt = parsePrintStatement();
        if (printStmt != null) {
            return printStmt;
        }
        Statement forStmt = parseForStatement();
        if (forStmt != null) {
            return forStmt;
        }
        Statement ifStmt = parseIfStatement();
        if (ifStmt != null) {
            return ifStmt;
        }
        Statement varStmt = parseVariableStatement();
        if (varStmt != null) {
            return varStmt;
        }
        Statement assignmentOrFunctionCallStmt = parseAssignmentOrFunctionCallStatement();
        if (assignmentOrFunctionCallStmt != null) {
            return assignmentOrFunctionCallStmt;
        }
        Statement returnStmt = parseReturnStatement();
        if (returnStmt != null) {
            return returnStmt;
        }
        Statement assignmentOrFuncCall = parseAssignmentOrFunctionCallStatement();
        return new SyntaxErrorStatement(tokens.consumeToken());
    }

//    variable_statement = 'var', IDENTIFIER,
//            [':', type_expression, ] '=', expression;
    private Statement parseVariableStatement() {

        if (tokens.match(VAR)) {
            VariableStatement varStatement = new VariableStatement();
            varStatement.setStart(tokens.consumeToken());
            varStatement.setVariableName(require(IDENTIFIER, varStatement).getStringValue());

            if (tokens.matchAndConsume(COLON)) {
                Expression typeExpression = parseTypeExpression();
                varStatement.setExplicitType(typeExpression.getType());
            }
            require(EQUAL, varStatement);
            varStatement.setEnd(tokens.getCurrentToken());
            Expression expression = parseExpression();
            varStatement.setExpression(expression);
            varStatement.setType(expression.getType());
            // a hack...
            if (varStatement.getType() == null){
                varStatement.setType(varStatement.getExplicitType());
            }
            return varStatement;
        } else {
            return null;
        }
    }

    // if_statement = 'if', '(', expression, ')', '{',
    //                    { statement },
    //               '}' [ 'else', ( if_statement | '{', { statement }, '}' ) ];
    private Statement parseIfStatement() {
        if (tokens.match(IF)){
            IfStatement ifStatement = new IfStatement();
            ifStatement.setStart(tokens.consumeToken());
            require(LEFT_PAREN, ifStatement);
            ifStatement.setExpression(parseExpression());
            require(RIGHT_PAREN, ifStatement);
            require(LEFT_BRACE, ifStatement);
            LinkedList<Statement> statements = new LinkedList<Statement>();
            while(!tokens.match(RIGHT_BRACE) && tokens.hasMoreTokens()){
                Statement statement = parseStatement();
                statements.add(statement);
            }
            ifStatement.setTrueStatements(statements);
            Token end = require(RIGHT_BRACE, ifStatement);
            if (tokens.matchAndConsume(ELSE)) {
                //parseIfStatement();
                require(LEFT_BRACE, ifStatement);
                LinkedList<Statement> elseStatements = new LinkedList<Statement>();
                while(!tokens.match(RIGHT_BRACE) && tokens.hasMoreTokens()){
                    Statement statement = parseStatement();
                    elseStatements.add(statement);
                }
                ifStatement.setElseStatements(elseStatements);
                end = require(RIGHT_BRACE, ifStatement);
            }
            ifStatement.setEnd(end);
            return ifStatement;
        } else {
            return null;
        }
    }

    private Statement parseAssignmentOrFunctionCallStatement() {
        if (tokens.match(IDENTIFIER)){
            Token id = tokens.consumeToken();
            if (tokens.match(LEFT_PAREN)){
                return parseFunctionCallStatement(id);
            } else {
                return parseAssignmentStatement(id);
            }
        } else {
            return null;
        }
    }

    // assignment_statement = IDENTIFIER, '=', expression;
    private Statement parseAssignmentStatement(Token id) {
        AssignmentStatement assignStmt = new AssignmentStatement();
        assignStmt.setStart(id);
        assignStmt.setVariableName(id.getStringValue());
        require(EQUAL, assignStmt);
        assignStmt.setExpression(parseExpression());
        assignStmt.setEnd(tokens.lastToken());
        return assignStmt;
    }

    private Statement parseFunctionCallStatement(Token id) {
        FunctionCallExpression e = parseFunctionCallExpression(id);
        return new FunctionCallStatement(e);
    }

    // function_declaration = 'function', IDENTIFIER, '(', parameter_list, ')' +
    //                       [ ':' + type_expression ], '{',  { function_body_statement },  '}';
    private FunctionCallExpression parseFunctionCallExpression(Token id) {
        tokens.matchAndConsume(LEFT_PAREN);
        LinkedList<Expression> expressions = new LinkedList<Expression>();
        // we have some number of expressions, which implies a while loop
        do {
            Expression expression = parseExpression();
            expressions.push(expression);
            if (tokens.match(RIGHT_PAREN)) {
                Token end = tokens.consumeToken();
                FunctionCallExpression funcCallExpression = new FunctionCallExpression(id.getStringValue(), expressions);
                return funcCallExpression;
            }
        } while (tokens.matchAndConsume(COMMA));

        if (tokens.match(EOF)) {
            FunctionCallExpression functionCallExpression = new FunctionCallExpression(id.getStringValue(), expressions);
            functionCallExpression.addError(ErrorType.UNTERMINATED_ARG_LIST);
            return functionCallExpression;
        }
        return null;
    }

    private Statement parseForStatement() {
        if(tokens.match(FOR)){
            ForStatement forStatement = new ForStatement();
            forStatement.setStart(tokens.consumeToken());
            require(LEFT_PAREN, forStatement);
            // require a loop identifier
            Token identifier = require(IDENTIFIER, forStatement);
            forStatement.setVariableName(identifier.getStringValue());
            // require 'in'    for(x in ...)
            require(IN, forStatement);
            // ... is going to be a parseExpression()
            forStatement.setExpression(parseExpression());
            require(RIGHT_PAREN, forStatement);
            // require {
            require(LEFT_BRACE, forStatement);
            LinkedList<Statement> statements = new LinkedList<Statement>();
            while(!tokens.match(RIGHT_BRACE) && tokens.hasMoreTokens()){
                Statement statement = parseStatement();
                statements.add(statement);
            }
            forStatement.setBody(statements);
            forStatement.setEnd(require(RIGHT_BRACE, forStatement));
            return forStatement;
        } else {
            return null;
        }
    }

    // return_statement = 'return' [, expression];
    private Statement parseReturnStatement() {
        if (tokens.match(RETURN)) {
            ReturnStatement returnStatement = new ReturnStatement();
            returnStatement.setStart(tokens.consumeToken());
            returnStatement.setFunctionDefinition(currentFunctionDefinition);

            if (!tokens.match(RIGHT_BRACE)) {
                if (this.currentFunctionDefinition != null) {
                    // do the real return statement parsing...
                    Expression expression = parseExpression();
                    returnStatement.setExpression(expression);
                    return returnStatement;
                }
            }
            if (this.currentFunctionDefinition != null) {
                return returnStatement;
            }
        }
        return null;
    }

    private Statement parsePrintStatement() {
        if (tokens.match(PRINT)) {

            PrintStatement printStatement = new PrintStatement();
            printStatement.setStart(tokens.consumeToken());

            require(LEFT_PAREN, printStatement);
            printStatement.setExpression(parseExpression());
            printStatement.setEnd(require(RIGHT_PAREN, printStatement));

            return printStatement;
        } else {
            return null;
        }
    }

    //============================================================
    //  Expressions
    //============================================================

    private Expression parseExpression() {
        return parseEqualityExpression();
    }

    private Expression parseEqualityExpression() {
        Expression expression = parseComparisonExpression();
        while (tokens.match(EQUAL_EQUAL, BANG_EQUAL)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseComparisonExpression();
            EqualityExpression equalityExpression = new EqualityExpression(operator, expression, rightHandSide);
            equalityExpression.setStart(expression.getStart());
            equalityExpression.setEnd(rightHandSide.getEnd());
            expression = equalityExpression;
        }
        return expression;
    }

    private Expression parseComparisonExpression() {
        Expression expression = parseAdditiveExpression();
        while (tokens.match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseAdditiveExpression();
            ComparisonExpression comparisonExpression = new ComparisonExpression(operator, expression, rightHandSide);
            comparisonExpression.setStart(expression.getStart());
            comparisonExpression.setEnd(rightHandSide.getEnd());
            expression = comparisonExpression;
        }
        return expression;
    }

    private Expression parseAdditiveExpression() {
        Expression expression = parseFactorExpression();
        while (tokens.match(PLUS, MINUS)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseFactorExpression();
            AdditiveExpression additiveExpression = new AdditiveExpression(operator, expression, rightHandSide);
            additiveExpression.setStart(expression.getStart());
            additiveExpression.setEnd(rightHandSide.getEnd());
            expression = additiveExpression;
        }
        return expression;
    }

    private Expression parseFactorExpression() {
        Expression expression = parseUnaryExpression();
        while (tokens.match(STAR, SLASH)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseUnaryExpression();
            FactorExpression factorExpression = new FactorExpression(operator, expression, rightHandSide);
            factorExpression.setStart(expression.getStart());
            factorExpression.setEnd(rightHandSide.getEnd());
            expression = factorExpression;
        }
        return expression;
    }

    private Expression parseUnaryExpression() {
        if (tokens.match(MINUS, NOT)) {
            Token token = tokens.consumeToken();
            Expression rhs = parseUnaryExpression();
            UnaryExpression unaryExpression = new UnaryExpression(token, rhs);
            unaryExpression.setStart(token);
            unaryExpression.setEnd(rhs.getEnd());
            return unaryExpression;
        } else {
            return parsePrimaryExpression();
        }
    }

    private Expression parsePrimaryExpression() {
        if (tokens.match(INTEGER)) {
            Token integerToken = tokens.consumeToken();
            IntegerLiteralExpression integerExpression = new IntegerLiteralExpression(integerToken.getStringValue());
            integerExpression.setToken(integerToken);
            return integerExpression;
        } else if (tokens.match(STRING)){
            Token stringToken = tokens.consumeToken();
            StringLiteralExpression stringExpression = new StringLiteralExpression(stringToken.getStringValue());
            stringExpression.setToken(stringToken);
            return stringExpression;
        } else if (tokens.match(LEFT_PAREN)){
            Token leftParenToken = tokens.consumeToken();
            Expression expression = parseExpression();
            if (tokens.match(RIGHT_PAREN)) {
                Token rightParenToken = tokens.consumeToken();
                ParenthesizedExpression parenExpression = new ParenthesizedExpression(expression);
                return parenExpression;
            }
            SyntaxErrorExpression syntaxErrorExpression = new SyntaxErrorExpression(tokens.consumeToken());
            return syntaxErrorExpression;
        } else if (tokens.match(IDENTIFIER)){
            Token identifier = tokens.consumeToken();
            if (tokens.match(LEFT_PAREN)) {
                return parseFunctionCall(identifier);
            } else {
                IdentifierExpression identifierExpression = new IdentifierExpression(identifier.getStringValue());
                identifierExpression.setToken(identifier);
                return identifierExpression;
            }
        } else if (tokens.match(TRUE)){
            Token booleanToken = tokens.consumeToken();
            BooleanLiteralExpression booleanExpression = new BooleanLiteralExpression(Boolean.parseBoolean(booleanToken.getStringValue()));
            booleanExpression.setToken(booleanToken);
            return booleanExpression;
        } else if (tokens.match(FALSE)){
            Token booleanToken = tokens.consumeToken();
            BooleanLiteralExpression booleanExpression = new BooleanLiteralExpression(Boolean.parseBoolean(booleanToken.getStringValue()));
            booleanExpression.setToken(booleanToken);
            return booleanExpression;
        } else if (tokens.match(NULL)){
            Token nullToken = tokens.consumeToken();
            NullLiteralExpression nullExpression = new NullLiteralExpression();
            nullExpression.setToken(nullToken);
            return nullExpression;
        } else if (tokens.match(LEFT_BRACKET)){
            return parseListLiteral();
        } else {
            //SyntaxErrorExpression syntaxErrorExpression = new SyntaxErrorExpression(tokens.consumeToken());
            //return syntaxErrorExpression;
            return null;
        }
    }

    // still not sure about setStart XD
    private Expression parseFunctionCall(Token identifier) {
        Token leftParen = tokens.consumeToken();
        LinkedList<Expression> list = new LinkedList<Expression>();
        // if the argumentList is empty
        if (tokens.match(RIGHT_PAREN)) {
            Token rightParen = tokens.consumeToken();
            FunctionCallExpression functionCallExpression = new FunctionCallExpression(identifier.getStringValue(),list);
            return functionCallExpression;
        }
        // we have some number of argument expressions, which implies a while loop
        do {
            Expression expression = parseExpression();
            list.push(expression);
            if (tokens.match(RIGHT_PAREN)) {
                Token rightParen = tokens.consumeToken();
                FunctionCallExpression functionCallExpression = new FunctionCallExpression(identifier.getStringValue(), list);
                return functionCallExpression;
            }
        } while (tokens.matchAndConsume(COMMA));

        if (tokens.match(EOF)) {
            FunctionCallExpression functionCallExpression = new FunctionCallExpression(identifier.getStringValue(), list);
            functionCallExpression.addError(ErrorType.UNTERMINATED_ARG_LIST);
            return functionCallExpression;
        }
        return null;
    }


    private Expression parseListLiteral() {
        if (tokens.match(LEFT_BRACKET)) {
            Token start = tokens.consumeToken();
            LinkedList<Expression> list = new LinkedList<Expression>();
            // two possibilities here
            if (tokens.match(RIGHT_BRACKET)) {
                Token end = tokens.consumeToken();
                ListLiteralExpression listExpression = new ListLiteralExpression(list);
                return listExpression;
            }
            // we have some number of expressions, which implies a while loop
            do {
                Expression expression = parseExpression();
                list.push(expression);
                if (tokens.match(RIGHT_BRACKET)) {
                    Token end = tokens.consumeToken();
                    ListLiteralExpression listExpression = new ListLiteralExpression(list);
                    return listExpression;
                }
            } while (tokens.matchAndConsume(COMMA));
            if (tokens.match(EOF)) {
                ListLiteralExpression listExpression = new ListLiteralExpression(list);
                listExpression.addError(ErrorType.UNTERMINATED_LIST);
                return listExpression;
            }
        }
        return null;
    }

    //============================================================
    //  Parse Helpers
    //============================================================
    private Token require(TokenType type, ParseElement elt) {
        return require(type, elt, ErrorType.UNEXPECTED_TOKEN);
    }

    private Token require(TokenType type, ParseElement elt, ErrorType msg) {
        if(tokens.match(type)){
            return tokens.consumeToken();
        } else {
            elt.addError(msg, tokens.getCurrentToken());
            return tokens.getCurrentToken();
        }
    }
}
