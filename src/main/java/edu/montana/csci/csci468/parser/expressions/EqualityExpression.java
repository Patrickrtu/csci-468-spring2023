package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenType;
import org.apache.commons.lang.ObjectUtils;

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
        // TODO: think this through...
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

    @Override
    public void compile(ByteCodeGenerator code) {
        super.compile(code);
    }


}
