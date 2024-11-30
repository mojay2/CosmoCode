# CosmoCode: A Cosmic Programming Language

CosmoCode is a beginner-friendly, imperative programming language inspired by the mysteries of space. It combines a space-themed syntax with a straightforward approach to programming. Designed for simplicity, CosmoCode encourages learners to explore coding while adhering to core programming principles.

## Features

### 1. Space-Themed Syntax
- Variables, keywords, and constructs are named after celestial and space-related terms, making coding imaginative and engaging.
- Example:
  ```cosmocode
  Comet stellarCount = 5; // Declare an integer variable
  transmission("Hello Universe!"); // Print a message
  ```

### 2. Data Types
- **Comet**: The only data type, used to store integers.
- Example:
  ```cosmocode
  Comet value = 10;
  ```

### 3. Reserved Keywords
- **Reception**: For input operations
- **Transmission**: For output operations
- **Orbit**, **Navigate**, **Propel**: Conditional constructs (if, else if, else)
- **Whirl**, **Launch Whirl**: Looping constructs (while, do-while)

### 4. Operators
- Arithmetic: `+`, `-`, `*`, `/`
- Relational: `==`, `!=`, `>=`, `<=`, `>`, `<`
- Logical: `&&` (AND), `||` (OR)

### 5. Control Structures
- Conditional statements:
  ```cosmocode
  Orbit (condition) { // statements }
  Navigate { // else if statements }
  Propel { // else statements }
  ```

- Loops:
  - **Whirl** (while loop):
    ```cosmocode
    Whirl (condition) { // statements }
    ```
  - **Launch Whirl** (do-while loop):
    ```cosmocode
    Launch { // statements } Whirl (condition);
    ```

### 6. Input/Output
- **Reception**: Accepts user input
- **Transmission**: Displays output
- Example:
  ```cosmocode
  Comet input = reception("Enter a value:");
  transmission("You entered: " + input);
  ```

### 7. Comments
- Single-line and multi-line comments:
  ```cosmocode
  /* This is a comment */
  ```

### 8. Semantic and Syntactic Validation
- Implements lexical, syntactic, and semantic analyzers
- Ensures proper structure and execution of programs
- Flags invalid tokens, out-of-scope variables, and undeclared identifiers during compilation

## System Architecture

### 1. Lexical Analyzer
- Converts source code into tokens
- Validates identifiers, literals, and reserved words

### 2. Syntax Analyzer
- Employs a shift-reduce parser for syntax checking
- Generates a parse tree for valid programs

### 3. Semantic Analyzer
- Validates type compatibility, variable declarations, and scope
- Executes the program based on the parse tree

## Limitations
- Only supports integer variables
- Limited to two loop constructs and basic conditionals
- No support for advanced programming paradigms
- Tokens must be space-separated
- Limited scalability for large projects

## Sample Programs

### Hello World
```cosmocode
transmission("Hello Universe!");
```

### Basic Loop
```cosmocode
Comet count = 0;
Whirl (count < 5) {
    transmission(count);
    count = count + 1;
};
```

### Conditional Statement
```cosmocode
Comet value = 10;
Orbit (value > 5) {
    transmission("Greater than 5");
} Propel {
    transmission("5 or less");
};
```
