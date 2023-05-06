package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenType;
import org.apache.commons.lang.ObjectUtils;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class EqualityExpression extends Expression {

    private final Token operator;
    private final Expression leftHandSide;
    private final Expression rightHandSide;

    public EqualityExpression(Token operator, Expression leftHandSide, Expression rightHandSide) {
        this.leftHandSide = addChild(leftHandSide);
        this.rightHandSide = addChild(rightHandSide);
        this.operator = operator;
    }

    public Expression getLeftHandSide() {
        return leftHandSide;
    }

    public Expression getRightHandSide() {
        return rightHandSide;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + operator.getStringValue() + "]";
    }

    public boolean isEqual() {
        return operator.getType().equals(TokenType.EQUAL_EQUAL);
    }
    public boolean isNotEqual() {
        return operator.getType().equals(TokenType.BANG_EQUAL);
    }
    @Override
    public void validate(SymbolTable symbolTable) {
        leftHandSide.validate(symbolTable);
        rightHandSide.validate(symbolTable);
    }

    @Override
    public CatscriptType getType() {
        return CatscriptType.BOOLEAN;
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        // TODO: handle String, list, Identifier...
        if (isEqual()) {
            // only return true when both sides have the same type
            if (getLeftHandSide().getType().equals(getRightHandSide().getType())) {
                if (getLeftHandSide().getType().equals(CatscriptType.INT)) {
                    Integer lhsVal = (Integer) getLeftHandSide().evaluate(runtime);
                    Integer rhsVal = (Integer) getRightHandSide().evaluate(runtime);
                    if (isEqual()) {
                        return lhsVal == rhsVal;
                    } else {
                        return null;
                    }
                } else if (getLeftHandSide().getType().equals(CatscriptType.BOOLEAN)) {
                    Boolean lhsVal = (Boolean) getLeftHandSide().evaluate(runtime);
                    Boolean rhsVal = (Boolean) getRightHandSide().evaluate(runtime);
                    return lhsVal == rhsVal;
                } else if (getLeftHandSide().getType().equals(CatscriptType.NULL)) {
                    return null == null;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (isNotEqual()) {
            // only return false when both sides have the same type and evaluates differently
            if (getLeftHandSide().getType().equals(getRightHandSide().getType())) {
                if (getLeftHandSide().getType().equals(CatscriptType.INT)) {
                    Integer lhsVal = (Integer) getLeftHandSide().evaluate(runtime);
                    Integer rhsVal = (Integer) getRightHandSide().evaluate(runtime);
                    if (isEqual()) {
                        return lhsVal != rhsVal;
                    } else {
                        return null;
                    }
                } else if (getLeftHandSide().getType().equals(CatscriptType.BOOLEAN)) {
                    Boolean lhsVal = (Boolean) getLeftHandSide().evaluate(runtime);
                    Boolean rhsVal = (Boolean) getRightHandSide().evaluate(runtime);
                    return lhsVal != rhsVal;
                } else if (getLeftHandSide().getType().equals(CatscriptType.NULL)) {
                    return null != null;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return null;
        }
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

//    testEquality(I)V
//    L0
//    LINENUMBER 11 L0
//    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
//    ICONST_1
//    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
//    ICONST_1
//    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
//    INVOKESTATIC java/util/Objects.equals (Ljava/lang/Object;Ljava/lang/Object;)Z
//    INVOKEVIRTUAL java/io/PrintStream.println (Z)V
//            L1
//    LINENUMBER 12 L1
//            RETURN
//    L2

    @Override
    public void compile(ByteCodeGenerator code) {
        getLeftHandSide().compile(code);
        // box lhs
        box(code, getLeftHandSide().getType());
        getRightHandSide().compile(code);
        // box rhs
        box(code, getRightHandSide().getType());
        if (isEqual()) {
            // invoke static Objects.equals() method
            code.addMethodInstruction(Opcodes.INVOKESTATIC, internalNameFor(Objects.class),
                "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z");
        } else if (isNotEqual()) {
            code.addMethodInstruction(Opcodes.INVOKESTATIC, internalNameFor(Objects.class),
                    "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z");
            code.pushConstantOntoStack(true);
            code.addInstruction(Opcodes.IXOR);
        }
    }


}
