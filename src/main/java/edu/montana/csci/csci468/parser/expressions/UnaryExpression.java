package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenType;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class UnaryExpression extends Expression {

    private final Token operator;
    private final Expression rightHandSide;

    public UnaryExpression(Token operator, Expression rightHandSide) {
        this.rightHandSide = addChild(rightHandSide);
        this.operator = operator;
    }

    public Expression getRightHandSide() {
        return rightHandSide;
    }

    public boolean isMinus() {
        return operator.getType().equals(TokenType.MINUS);
    }

    public boolean isNot() {
        return operator.getType().equals(TokenType.NOT);
    }

    @Override
    public String toString() {
        return super.toString() + "[" + operator.getStringValue() + "]";
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        rightHandSide.validate(symbolTable);
        if (isNot() && !rightHandSide.getType().equals(CatscriptType.BOOLEAN)) {
            addError(ErrorType.INCOMPATIBLE_TYPES);
        } else if(isMinus() && !rightHandSide.getType().equals(CatscriptType.INT)) {
            addError(ErrorType.INCOMPATIBLE_TYPES);
        }
    }

    @Override
    public CatscriptType getType() {
        if (isMinus()) {
            return CatscriptType.INT;
        } else {
            return CatscriptType.BOOLEAN;
        }
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        Object rhsValue = getRightHandSide().evaluate(runtime);
        if (this.isMinus()) {
            return -1 * (Integer) rhsValue;
        } else if (this.isNot()) {
            return !(Boolean) rhsValue;
        } else {
            return null;
        }
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

//    testUnary(I)V
//    L0
//    LINENUMBER 10 L0
//    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
//    ILOAD 1
//    INEG
//    INVOKEVIRTUAL java/io/PrintStream.println (I)V
//            L1
//    LINENUMBER 11 L1
//            RETURN
//    L2
//    LOCALVARIABLE this Ledu/montana/csci/csci468/demo/Scratch; L0 L2 0
//    LOCALVARIABLE i I L0 L2 1
//    MAXSTACK = 2
//    MAXLOCALS = 2
    @Override
    public void compile(ByteCodeGenerator code) {
        // a lot like what we did in comparison
        // compare if the thing is true or not, and invert it
        // or if it's a negative, multiply by -1, and push a constant ?imul operator?

        getRightHandSide().compile(code);

        if (isMinus()) {
            code.addInstruction(Opcodes.INEG);
        } else if (isNot()) {
            code.pushConstantOntoStack(true);
            code.addInstruction(Opcodes.IXOR);
        } else {
            return;
        }
        // <lhs>
        // <rhs>

        // if_icmpge PUSH_FALSE
        // PUSH TRUE
        // GOTO END_LABEL
        // PUSH_FALSE PUSH FALSE
        // END LABEL
    }


}
