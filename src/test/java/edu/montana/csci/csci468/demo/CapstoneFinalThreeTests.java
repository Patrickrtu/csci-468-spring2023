package edu.montana.csci.csci468.demo;

import edu.montana.csci.csci468.CatscriptTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CapstoneFinalThreeTests extends CatscriptTestBase
{
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━  TEST #1  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //   function foo(x : string) {
    //       print(x)
    //   }
    //
    //   function main() {
    //       foo("Buzz")
    //   }
    //
    //   main()
    @Test
    void callFunctionFromAnotherFunction() {
        assertEquals("Buzz\n", executeProgram("function foo(x : string) { print(x) }" +
                "function main() { foo(\"Buzz\")}" +
                "main()"
        ));
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━  TEST #2  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //   var length: int  = 0
    //
    //   var lst = [1, 2, 3, 4, 5, 6, 7, 8]
    //
    //   for( i in lst ) {
    //     length = length + 1
    //   }
    //
    //   print(length)
    @Test
    void incrementVarInsideForLoop() {
        assertEquals("8\n", executeProgram("var length: int = 0" +
                "var lst = [1, 2, 3, 4, 5, 6, 7, 8]" +
                "for ( i in lst) { length = length + 1 }" +
                "print(length)"
        ));
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━  TEST #3  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //   function foo() {
    //      return [1, 2, 3, 4]
    //   }
    //
    //   var lst = foo()
    //
    //   print(lst)
    @Test
    void varIsAssignableToFunctionCall() {
        assertEquals("[1, 2, 3, 4]\n", executeProgram("function foo() { return [1, 2, 3, 4] }" +
                "var lst = foo()" +
                "print(lst)"
        ));
    }
}
