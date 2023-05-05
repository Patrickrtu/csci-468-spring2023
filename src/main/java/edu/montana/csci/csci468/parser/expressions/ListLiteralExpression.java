package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.SymbolTable;
import org.objectweb.asm.Opcodes;

import java.util.LinkedList;
import java.util.List;

public class ListLiteralExpression extends Expression {
    List<Expression> values;
    private CatscriptType type;

    public ListLiteralExpression(List<Expression> values) {
        this.values = new LinkedList<>();
        for (Expression value : values) {
            this.values.add(addChild(value));
        }
    }

    public List<Expression> getValues() {
        return values;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        for (Expression value : values) {
            value.validate(symbolTable);
        }
        if (values.size() > 0) {
            type = CatscriptType.getListType(values.get(0).getType());
            for (Expression value : values) {
                if (!CatscriptType.getListType(value.getType()).equals(type)) {
                    addError(ErrorType.INCOMPATIBLE_TYPES);
                }
            }
        } else {
            type = CatscriptType.getListType(CatscriptType.OBJECT);
        }
    }

    @Override
    public CatscriptType getType() {
        return type;
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        LinkedList<Object> objects = new LinkedList<>();
        for (Expression value : values) {
            // evaluate the value and add to list
            Object element = value.evaluate(runtime);
            objects.push(element);
        }
        return objects;
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        code.addTypeInstruction(Opcodes.NEW, "java/util/LinkedList");
        code.addInstruction(Opcodes.DUP);
        code.addMethodInstruction(Opcodes.INVOKESPECIAL, "java/util/LinkedList",
                "<init>", "()V");
        for (Expression value : values) {
            // since invokevirtual consumes the pointer on the stack, we need to dupe it first
            code.addInstruction(Opcodes.DUP);
            value.compile(code);
            box(code, value.getType());
            code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/util/LinkedList",
                    "add", "(Ljava/lang/Object;)Z");
            code.addInstruction(Opcodes.POP);
        }
    }


}
