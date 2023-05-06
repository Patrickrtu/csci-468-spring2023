package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import edu.montana.csci.csci468.parser.expressions.IdentifierExpression;
import edu.montana.csci.csci468.tokenizer.TokenType;
import org.objectweb.asm.Opcodes;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class VariableStatement extends Statement {
    private Expression expression;
    private String variableName;
    private CatscriptType explicitType;
    private CatscriptType type;

    public Expression getExpression() {
        return expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setExpression(Expression parseExpression) {
        this.expression = addChild(parseExpression);
    }

    public void setExplicitType(CatscriptType type) {
        this.explicitType = type;
    }

    public CatscriptType getExplicitType() {
        return explicitType;
    }

    public void setType(CatscriptType type) {
        this.type = type;
    }

    public boolean isGlobal() {
        return getParent() instanceof CatScriptProgram;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        if (symbolTable.hasSymbol(variableName)) {
            addError(ErrorType.DUPLICATE_NAME);
        } else if (explicitType != null && !explicitType.isAssignableFrom(expression.getType())){
            addError(ErrorType.INCOMPATIBLE_TYPES);
        } else {
            // TODO if there is an explicit type, ensure it is correct
            //      if not, infer the type from the right hand side expression
            if (type == null) {
                setType(expression.getType());
            }
            symbolTable.registerSymbol(variableName, type);
        }
//        if (symbolTable.hasSymbol(variableName)) {
//            addError(ErrorType.DUPLICATE_NAME);
//        } else {
//            type = null;
//            if (explicitType != null) {
//                type = explicitType;
//                // var x : list = 1
//                if (!explicitType.isAssignableFrom(expression.getType())) {
//                    addError(ErrorType.INCOMPATIBLE_TYPES);
//                } else {
//                    type = expression.getType();
//                }
//
//                // TODO if there is an explicit type, ensure it is correct
//                //      if not, infer the type from the right hand side expression
//                symbolTable.registerSymbol(variableName, type);
//            }
//        }
    }

    public CatscriptType getType() {
        return type;
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
//        super.execute(runtime);

//        Boolean conditionalResult = (Boolean) expression.evaluate(runtime);
//        runtime.pushScope();
//        if (Boolean.TRUE.equals(conditionalResult)) {
//            for (Statement trueStatement : trueStatements) {
//                trueStatement.execute(runtime);
//            }
//        } else {
//            for (Statement elseStatement : elseStatements) {
//                elseStatement.execute(runtime);
//            }
//        }
//        runtime.popScope();
//    }
        Object val = expression.evaluate(runtime);
//        if (expression.getStart().getType().equals(TokenType.IDENTIFIER)) {
//            runtime.setValue(getVariableName(), runtime.getValue(expression.toString()));
//            return;
//        }
        runtime.setValue(getVariableName(), val);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        javascript.append("var").append(variableName).append("=");
        expression.transpile(javascript);
        javascript.append(";");
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        if (isGlobal()) {
            // distinguish between primitives and non-primitives
            if (getType().equals(CatscriptType.INT) || getType().equals(CatscriptType.BOOLEAN)) {
                code.addField(variableName, "I");
                code.addVarInstruction(Opcodes.ALOAD, 0);
                expression.compile(code);
                code.addFieldInstruction(Opcodes.PUTFIELD, variableName, "I", code.getProgramInternalName());
            } else {
                String internalName = ByteCodeGenerator.internalNameFor(getType().getJavaType());
                String descriptor = "L" + internalName + ";";
                code.addField(variableName, descriptor);
                code.addVarInstruction(Opcodes.ALOAD, 0);
                expression.compile(code);
                code.addFieldInstruction(Opcodes.PUTFIELD, variableName, descriptor, code.getProgramInternalName());
            }
        } else {
            // store in slots, first allocate a slot.
            Integer slotNumber = code.createLocalStorageSlotFor(variableName);
            if (getType().equals(CatscriptType.INT) || getType().equals(CatscriptType.BOOLEAN)) {
                expression.compile(code);
                // consume value on the stack
                code.addVarInstruction(Opcodes.ISTORE, slotNumber);
            } else {
                expression.compile(code);
                code.addVarInstruction(Opcodes.ASTORE, slotNumber);
            }
        }
    }
}
