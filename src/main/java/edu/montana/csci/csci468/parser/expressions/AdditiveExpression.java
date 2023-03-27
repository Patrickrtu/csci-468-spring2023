package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenType;
import org.objectweb.asm.Opcodes;

public class AdditiveExpression extends Expression {

    private final Token operator;
    private final Expression leftHandSide;
    private final Expression rightHandSide;

    public AdditiveExpression(Token operator, Expression leftHandSide, Expression rightHandSide) {
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
    public boolean isAdd() {
        return operator.getType() == TokenType.PLUS;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        leftHandSide.validate(symbolTable);
        rightHandSide.validate(symbolTable);
        if (getType().equals(CatscriptType.INT)) {
            if (!leftHandSide.getType().equals(CatscriptType.INT)) {
                leftHandSide.addError(ErrorType.INCOMPATIBLE_TYPES);
            }
            if (!rightHandSide.getType().equals(CatscriptType.INT)) {
                rightHandSide.addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        }
        // TODO handle strings
    }

    @Override
    public CatscriptType getType() {
        if (leftHandSide.getType().equals(CatscriptType.STRING) || rightHandSide.getType().equals(CatscriptType.STRING)) {
            return CatscriptType.STRING;
        } else {
            return CatscriptType.INT;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "[" + operator.getStringValue() + "]";
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        // TODO: handle recursive behavior in lhs and rhs
        AdditiveExpression lhsVal = null;
        AdditiveExpression rhsVal = null;
        Integer lhsIntVal = null;
        Integer rhsIntVal = null;
        try {
            lhsVal = (AdditiveExpression) leftHandSide.evaluate(runtime);
        } catch (ClassCastException e) {
        }
        if (lhsVal != null) {
            lhsIntVal = (Integer)lhsVal.evaluate(runtime);
        }

        try {
            rhsVal = (AdditiveExpression) rightHandSide.evaluate(runtime);
        } catch (ClassCastException e) {
        }
        if (rhsVal != null) {
            rhsIntVal = (Integer)rhsVal.evaluate(runtime);
        }


        if (leftHandSide.getType().equals(CatscriptType.STRING) && rightHandSide.getType().equals(CatscriptType.INT)) {
            String lhsValue = (String) leftHandSide.evaluate(runtime);
            if (rhsIntVal != null) {
                if (isAdd()) {
                    return lhsValue + rhsIntVal;
                } else {
                    addError(ErrorType.UNEXPECTED_TOKEN);
                    return lhsValue + rhsIntVal;
                }
            }
            Integer rhsValue = (Integer) rightHandSide.evaluate(runtime);
            if (isAdd()) {
                return lhsValue + rhsValue;
            } else {
                addError(ErrorType.UNEXPECTED_TOKEN);
                return lhsValue + rhsValue;
            }
        }

        if (leftHandSide.getType().equals(CatscriptType.STRING) && rightHandSide.getType().equals(CatscriptType.STRING)) {
            String lhsValue = (String) leftHandSide.evaluate(runtime);
            String rhsValue = (String) rightHandSide.evaluate(runtime);
            if (isAdd()) {
                return lhsValue + rhsValue;
            } else {
                addError(ErrorType.UNEXPECTED_TOKEN);
                return lhsValue + rhsValue;
            }
        }
        if (leftHandSide.getType().equals(CatscriptType.INT) && rightHandSide.getType().equals(CatscriptType.STRING)) {
            String rhsValue = (String) rightHandSide.evaluate(runtime);

            if (lhsIntVal != null) {
                if (isAdd()) {
                    return lhsIntVal + rhsValue;
                } else {
                    addError(ErrorType.UNEXPECTED_TOKEN);
                    return lhsIntVal + rhsValue;
                }
            }
            Integer lhsValue = (Integer) leftHandSide.evaluate(runtime);
            if (isAdd()) {
                return lhsValue + rhsValue;
            } else {
                addError(ErrorType.UNEXPECTED_TOKEN);
                return lhsValue + rhsValue;
            }
        }

        if (leftHandSide.getType().equals(CatscriptType.INT) && rightHandSide.getType().equals(CatscriptType.INT)) {
            if (lhsIntVal == null) {
                lhsIntVal = (Integer) leftHandSide.evaluate(runtime);
            }
            if (rhsIntVal == null) {
                rhsIntVal = (Integer) rightHandSide.evaluate(runtime);
            }
            if (isAdd()) {
                return lhsIntVal + rhsIntVal;
            } else {
                return lhsIntVal - rhsIntVal;
            }
        }

        if (leftHandSide.getType().equals(CatscriptType.NULL)){
            return "null" + rightHandSide.evaluate(runtime);
        }
        if (rightHandSide.getType().equals(CatscriptType.NULL)){
            return leftHandSide.evaluate(runtime) + "null";
        }
        return null;
    }

    @Override
    public void transpile(StringBuilder javascript) {
        getLeftHandSide().transpile(javascript);
        javascript.append(operator.getStringValue());
        getRightHandSide().transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        getLeftHandSide().compile(code);
        getRightHandSide().compile(code);
        if (isAdd()) {
            code.addInstruction(Opcodes.IADD);
        } else {
            code.addInstruction(Opcodes.ISUB);
        }
    }

}
