# Catscript Guide

This document should be used to create a guide for catscript, to satisfy capstone requirement 4

## Introduction

Catscript is a simple scripting language.  

Here's an example of Catscript in action:

```
var x = "foo"
print(x)
```

## Table of Contents

### [Statements](#statements)

- [For Loop Statement](#for-loop-statement)
- [If Statement](#if-statement)
- [Print Statement](#print-statement)
- [Variable Statement](#variable-statement)
- [Assignment Statement](#assignment-statement)
- [Function Call Statement](#function-call-statement)

### [Function Declaration](#function-declaration)

- [Function Body](#function-body)
- [Parameter List](#parameter-list)
- [Return Statement](#return-statement)

### [Expressions](#expressions)

- [Equality Expression](#equality-expression)
- [Comparison Expression](#comparison-expression)
- [Additive Expression](#additive-expression)
- [Factor Expression](#factor-expression)
- [Unary Expression](#unary-expression)
- [Primary Expression](#primary-expression)
- [List Literal Expression](#list-literal-expression)
- [Function Call Expression](#function-call-expression)
- [Argument List Expression](#argument-list-expression)
- [Type Expression](#type-expression)

## Features

- Catscript includes all the fundamental features of a programming language.
- These features can be categorized as **statements** or **expressions**.
- **Statements** execute actions or control the code flow, while **expressions** evaluate to a single value.
- **Function declaration statements** are more complex and consist of several other components, so they are listed separately from Statements.

---

## Statements

##### For Loop Statement

To declare a for loop in Catscript, use the "for" keyword followed by a set of parentheses containing an identifier, the "in" keyword, and an expression that produces a list of values. This expression can be created using any code that generates a list of values, such as a list literal or a function call that returns a list. Once the expression is evaluated and produces a list, the for loop executes a sequence of statements enclosed within curly braces for each item in the list.

> Note that in Catscript, you don't need to declare the identifier before using it in the for loop.

Here's an example of how to use a for loop in Catscript to print the numbers 1 to 5:

```
for (i in [1, 2, 3, 4, 5]) {
  print(i)
}
```

##### If Statement

In Catscript, you can declare an if statement using the "if" keyword followed by a set of parentheses that contain a boolean expression. If the expression evaluates to true, the statements enclosed within the curly braces following the if statement will be executed. Optionally, you can include an "else" clause that contains statements to be executed if the expression evaluates to false.

In the following example, the if statement checks if the value of the variable x is greater than 5, and if it is, the string 'x is greater than 5' will be printed.

```
var x = 10
if (x > 5) {
  print("x is greater than 5")
}
```

The following example demonstrates how to use an if statement with an else clause. If the boolean expression in the if statement evaluates to true, the string 'You can vote!' will be printed; otherwise, the statement within the else clause will be executed.

```
var age = 19
if (age < 18) {
  print("You are not old enough to vote.")
} else {
  print("You can vote!")
}
```

##### Print Statement

In Catscript, you can use a print statement to write a value to the output. To declare a print statement, use the "print" keyword followed by a set of parentheses containing an expression to be printed. This expression can be a variable, a literal value, or the result of an expression.

> Keep in mind that the string representation of the expression will be 
> written to output, which means that print statements in Catscript are 
> not limited to printing strings.

In the following example, the print statement writes the string 'Hello, world!' to the output:

```
print("Hello, world!")

// Output: Hello, world!
```

In the following example, the print statement displays the result of a calculation:

```
print(2 + 3)

// Output: 5
```

##### Variable Statement

To declare a variable and assign it an initial value in Catscript, you can use a variable statement. The statement is declared using the "var" keyword, followed by an identifier, an optional type annotation, an equal sign, and an expression. This expression can be a literal value, the result of an expression, or the return value of a function call.

> Remember that the type annotation in the variable statement is optional, but it can be used to specify the type of the variable.

In the following example, a variable named "y" is declared with an initial value of the result of a calculation:

```
var y = 2 + 3
```

In the following example, a variable named "name" is declared with an initial value of "John" and a type annotation of "string":

```
var name: string = "John"
```

##### Assignment Statement

In Catscript, you can use an assignment statement to modify the value of a previously declared variable. To declare an assignment statement, use an identifier followed by an equal sign and an expression that evaluates to the new value of the variable.

> Keep in mind that for an assignment statement to work in Catscript, the 
> variable must have been previously declared, and the type of the 
> expression on the right-hand side of the equals sign must be compatible 
> with the type declared for the variable.

In the following example, the variable x is declared with an initial value of 10, and then its value is changed to 5 using an assignment statement:

```
var x = 10
x = 5
print(x)

// Output: 5
```

##### Function Call Statement

In Catscript, you can use a function call statement to invoke a function and execute its code. The statement consists of the function name followed by parentheses containing any arguments passed to the function. Remember that the arguments passed to the function must match the parameters defined in the function's declaration in terms of data type and order.

Here's an example of a function call statement, where the function `myFunction` is called with three arguments:

```
myFunction(2, "hello", true)
```

For a more detailed explanation of the function call syntax in Catscript, please refer to the [Function Call Expression Section](#function-call-expression).

## Function Declaration Statement

In Catscript, you can use function declaration to define a reusable piece of code that can be called from different parts of the program. To declare a function, use the "function" keyword followed by the function name, a set of parentheses that contain a list of parameters (if any), and the function's body enclosed in curly braces.

Here's the basic syntax for a function declaration statement:

```
function sum(num1, num2): int {
  var result = 2 + 3
  return result
}
```

Next, let's take a look at the three types of statements that can be used in function declaration statements in Catscript.

##### Function Body

The function body is where the actual logic of the function resides, and it can contain any valid Catscript statement, such as variable declarations, control structures, and function calls. The function body is defined within the curly braces {} that follow the function declaration statement.

> Keep in mind that the function body statement in Catscript must contain at least one Catscript statement.

The following example illustrates a valid function body statement:

```
function myFunction(parameter1, parameter2) {
    var x: int = 10;
    if (x > 5) {
        print("x is greater than 5");
    }
    return parameter1 + parameter2;
}
```

##### Parameter List

The parameter list is a comma-separated list of parameters that the function accepts, with each parameter consisting of a name and an optional type annotation. If a type annotation is not provided, Catscript will try to infer the type from the value passed in when the function is called.

Here's an example of a parameter list composed of two values with type annotations:

```
function myFunction(parameter1: int, parameter2: bool) {
    // function body
}
```

##### Return Statement

In Catscript, a function can return a value using the return statement. If the function returns a value, the return type must be specified in the function declaration. The return statement can also be used to exit a function early and return a value.

> Keep in mind that in Catscript, all branches of code are expected to have a return statement.

The following example illustrates a function with valid return coverage.

```
function validFunction(parameter1: int, parameter2: int): int {
    return parameter1 + parameter2
}
```

A function with invalid return coverage has at least one branch of code that will never hit a return statement.

Here's an example of a function with invalid return coverage:

```
function invalidFunction(x: int): bool {
    if (x >= 5) {
        return true
    } else {
        print("Less than 5")
    }
}
```

If an argument less than 5 is passed into the function, the return statement will never be executed, resulting in invalid return coverage.

### Expressions

#### Equality Expression

In Catscript, you can use equality expressions to compare two values and determine if they are equal. The `==` operator is used to check if two values are equal, while `!=` is used to check if two values are not equal. Equality expressions evaluate to boolean values and are frequently used as the expression in if statements.

Here's an example that compares two numbers using the equality expression:

```
if (5 == 10) {
    print("x and y are equal")
} else {
    print("x and y are not equal")
}
```

#### Comparison Expression

Comparison expressions can be used to compare values and produce boolean results. Available operators include:

`>` for greater than

`<` for less than

`>=` for greater than or equal to

`<=` for less than or equal to

```
if (5 > 10) {
  print("x is greater than y")
} else {
  print("x is not greater than y")
}
```

#### Additive Expression

Catscript provides additive expressions to perform arithmetic operations on numeric values. The addition operator `+` is used to add two values, while the subtraction operator `-` is used to subtract one value from another.

Here's an example of a print statement that uses an additive expression:

```
print(5 + 10)  // Output: 15
```

Additive expressions can also be used with string values. In this case, the addition operator `+` is used to concatenate strings together. The following example demonstrates this behavior:

```
var str1 = "Hello"
var str2 = "world!"
print(str1 + ", " + str2) // Output: Hello, world!
```

#### Factor Expression

In Catscript, a factor expression refers to an expression that involves multiplication or division of two operands. The order of operations applies to factor expressions just like any other mathematical expression. Multiplication and division are performed before addition and subtraction, so it's important to use parentheses when necessary to ensure that the expressions are evaluated in the correct order.

The following example demonstrates a simple multiplication operation between two integers:

```
print(5 * 10) // Output: 50
```

Here's an example of a factor expression that includes parentheses to enforce order of operations with an additive expression:

```
var result = (10 + 2) / 6

print(result) // Output: 2
```

#### Unary Expression

A unary expression is an expression that operates on a single operand. The unary operator can be either `not` or `-`.

The `not` operator performs a logical negation on a boolean value. When applied to a boolean value, it returns the opposite boolean value. The following example demonstrates this:

```
var x = true

print(not x) // Output: false
```

The `-` operator in Catscript performs negation on a numeric value. When applied to a numeric value, it returns the value multiplied by `-1`. Here's an example:

```
var x = 5
print(-x) // Output: -5
```

#### Primary Expression

In Catscript, a primary expression is the simplest form of expression and can take on several forms, such as an identifier, a literal, a function call, or a parenthesized expression.

Identifiers are used to access the value of a variable or to call a function. In the following example, `name` is an identifier:

```
var name = "Alice"
```

Literals are fixed values that are directly represented in the code. There are several types of literals, including strings, integers, booleans, null, and list literals.

```
"Hello, world!" // String literal
21              // Integer literal
true            // Boolean literal
null            // Null literal
[1,2,3]         // List literal
```

Function calls are used to invoke a function with zero or more arguments. A function call is denoted by the function name followed by parentheses containing the arguments passed to the function.

```
function greet(name) {
  print("Hello, " + name + "!")
}

greet("Alice") // Function call
```

Parenthesized expressions are used to group expressions together to enforce order of operations or to clarify code. The expression inside the parentheses can be any valid expression.

```
24 / (10 + 2) // Parenthesized expression
```

#### List Literal Expression

In Catscript, a list is a collection of ordered values. A list literal is a way to define a list with a specific set of values. List literals are enclosed in square brackets `[]`, with each element separated by a comma `,`.

Here's an example of a list literal containing three integers:

```
[1,2,3]
```

List literals can also include elements of distinct types:

```
[true, "banana", 3, "dog"]
```

#### Function Call Expression

A function call expression is used to call a function and pass arguments to it. It consists of the function name followed by parentheses containing the arguments passed to the function. The arguments can be expressions that evaluate to the expected data types specified in the function's parameter list.

> It's important to note that in Catscript, the function being called must
>  have been previously defined through a function declaration statement.

The syntax for a function call expression in Catscript is as follows:

```
function_name(argument1, argument2, ..., argumentN)
```

Here's an example of a function call expression in Catscript that passes two arguments, an integer and a string, to a function called `myFunction`:

```
myFunction(42, "hello")
```

It's important to note that the order and number of the arguments passed to the function must match the parameters defined in the function's declaration. For instance, the function `myFunction` should have a signature like this:

```
func myFunction(num: int, message: string) {
  // function body
}
```

It's also worth noting that the function call expression is an expression that evaluates to a value. The value returned by the function can be stored in a variable or used in an expression.

#### Argument List Expression

Catscript uses an argument list expression to pass one or more arguments to a function. This expression consists of zero or more expressions enclosed in parentheses and separated by commas.

The example below shows an argument list being used in a function call, with the argument list being the series of expressions between the parentheses.

```
myFunction(42, "hello")
```

#### Type Expression

CatScript variables are statically typed, which means their type is determined at compile-time and cannot change during program execution. Type expressions are used in function declarations, variable statements, and parameter lists. The CatScript type system includes the following types:

- `int`: A 32-bit integer.
- `string`: A Java-style string.
- `bool`: A boolean value, which can be either true or false.
- `list<x>`: A list of values with a specific type `x`.
- `null`: The null type, which represents the absence of a value.
- `object`: The most general type, which can hold any kind of value.

The following example shows a variable statement with a type expression:

```
var num: int = 42
```

Type expressions can be used to indicate the return type of functions and specify the parameter types, as shown in this example:

```
function isGreaterThan(x: int, y: int): bool {
    return x > y
}
```
