package edu.montana.csci.csci468.capstone;

import edu.montana.csci.csci468.CatscriptTestBase;
import edu.montana.csci.csci468.parser.ParseErrorException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CatscriptCapstoneTest extends CatscriptTestBase {

    @Test
    void argumentListExpressionsEvaluateProperly() {
        String source = "function multiplyByTwo(num: int): int {\n" +
                        "  return num * 2" +
                        "}\n" +
                        "print(multiplyByTwo(multiplyByTwo(3)))";
        assertEquals("12\n", executeProgram(source));
    }

    @Test
    void forStatementWithIfStatementWorksProperly() {
        String source = "for (i in [3,4,5,6,7]) {\n" +
                        "  if (i >= 5) {\n" +
                        "    print(i)\n" +
                        "  }\n" +
                        "}\n";
        assertEquals("5\n6\n7\n", executeProgram(source));
    }

    @Test
    void variableNamespaceIsProtected() {
        String expectedError =  "edu.montana.csci.csci468.parser.ParseErrorException: Parse Errors Occurred:\n\n" +
                "Line 2:function test(word: string) {\n" +
                "      ^\n\n" +
                "Error: This name is already used in this program\n\n";
        try {
            executeProgram("var name = \"hello\"\n" +
                                "function test(word: string) {\n" +
                                "  return word + \"!\"\n" +
                                "}\n" +
                                "test(name)");
        } catch (ParseErrorException e) {
            assertEquals(expectedError, e.toString());
        }
    }
}
