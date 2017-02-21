# Introduction

The idea behind the language is to use a minimalist concatenative language as a host for testing some interesting features, with the aim being to create a "universal" language, a programming language in which other programming languages can be expressed as libraries of functions.

## Features

The `pull` keyword tells the interpreter to parse and push the body of the next function, as defined in the current function table.
The `sym` keyword pushes a complete copy of the current function table. Redefining `sym` replaces the function table with a copy of it's new definition.
The `tn` keyword pushes a unique identifier, for use in non-recursive definitions calling on the previous definition.
`def` does about what one would expect, except it replaces only the body of the function, which means every reference to the function refer to the it's definition. `undef` avoids this.

## Datatypes

Two datatypes are supported: integers and list pointers. Thruth values are represented by integers, non-zero being true. Characters are represented by integers in the ASCII range. Strings are represented as lists of characters.

In this document, integers will be written as whole numbers, such as 1 or -32.
Lists will be written in the style of Joy, e.g. [a b c], or as in ML; a :: b :: c :: nil.

## Built-in functions

The following functions should be built into the interpreter

| Name 	 | Arguments	 | Result	| Definition			|
| ---- 	 | ---------   	 | ------ 	| ------------------------- 	|
| dup  	 | x y	       	 | x y y  	| Duplicates top element. Lists are duplicated by reference   	|
| dip  	 | ... x [p]   	 | ... x  	| Pops x, executes p, pushes x back 	      		      		|
| pop  	 | ... x       	 | ...    	| Pops x						      							|
| i    	 | ... [p]     	 | ...    	| Executes p						      						|
| swap 	 | x y 	       	 | y x    	| Pops y, pops x, pushes y, pushes x			      			|
| cons 	 | x a	       	 | x::a   	| Prepends x to list a				      	      				|
| uncons | x::a	       	 | x a    	| Takes x from head of list a				      				|
| []	 | ...	       	 | []     	| Pushes an empty list	  			      	      				|
| 1	 	 | ...		 	 | 1		| Pushes the integer 1					      					|
| ifte	 | [b] [t] [f] 	 | ...	  	| Executes b as a test for wheather to execute t or f	      	|
| def	 | [name] [body] | ...	  	| Defines a new function   	      	 	      	      			|
| undef	 | [name] 	 	 | ...	  	| Undefines a function				      	      				|
| table	 | [table]		 | ...		| Replaces the function table									|
| sym	 | ...		 	 | table  	| Pushes a copy of the current function table		      		|
| copy	 | [l]		 	 | [l']		| Replaces top list with deep copy of same				  		|
| intern | [name]	 	 | [body]	| Retrieves function by name from the table				  		|
| pull	 | ...		 	 | [body] bool  | Pulls next function, pushes it's body and true or pushes [] and false, without removing anything from the source  |
| tn	 | ...		 	 | [name] 	| Pushes a temporary name      	       	  		      			|
| put	 | x		 	 | ...		| Prints the character represented by x to current output     	|
| input	 | [filename]	 | [previous]	| Changes source stream to "filename"			      		|
| output | [filename]	 | [previous]	| Changes current output stream to "filename"		      	|
| exit	 | ...		 	 | 			| Halts the program	 	   			      						|
| +	 	 | i1 i2	 	 | i1 + i2	| Adds two integers					      						|
| -	 	 | i1 i2	 	 | i1 - i2	| Subtracts two integers				      					|
| * 	 | i1 i2	 	 | i1 * i2	| Multiplies two integers				      					|
| /	 	 | i1 i2	 	 | i1 / i2	| Divides two integers					      					|
| %	 	 | i1 i2	 	 | i1 % i2	| Takes the remainder of two integers			      			|
| <	 	 | i1 i2	 	 | i1 < i2	| Tests for smaller value    				      				|
| > 	 | i1 i2	 	 | i1 > i2	| Tests for greater value				      					|
| =	 	 | i1 i2	 	 | i1 = i2	| Tests for equal value					      					|
| '=	 | l1 l2	 	 | l1 == l2	| Tests for pointer to same list			      				|
| int?	 | x  		 	 | bool	 	| Tests for int						      						|
| list?	 | x		 	 | bool		| Tests for list pointer				      					|

## Predefined functions

The following functions should be predefined from the built-ins

| Name 	 | Arguments 	 | Result 	| Definition			|
| ---- 	 | ---------   	 | ------ 	| ------------------------- 	|
| chars	 | ...		 | [table]	| Pushes a function table where every char is defined to push it's own ASCII value |
| [	 | ...		 | [list]	| Parses the remainder of a list until `]`			|
| ]	 | ...		 | ...		| No result, no body, just a flag for `[`			|
| "	 | ...		 | [string]	| Parses the remainder of a string				|
| pull	 | ...		 | x		| Redefined to evaluate `[` instead of pushing it's body 	|
| while	 | [t] [p]	 | ...		| Executes p as long as t yields true  	       	    		|
| 0-9 	 | ...		 | int		| Parses the remainder of an integer   	       	    		|
| -      | ...     | int    | Checks if there is a number directly following it, if so parses that number and pushes it's additive inverse, otherwise executes the old `-` |

## The function table

The function table is, outside of the stack, the main data structure for the language. It is composed of module and function definitions, in a tree structure. The top level must only contain modules. A module is a list that starts with the integer 155, ASCII "m", a string, which is it's name, and a number of functions and modules. A function contains only a string, which is it's name, and a body, which is a list of functions that will be evaluated when it is called. Defining a function with the same name as another function will change the body *without* changing the actual list pointer object, analogous to some Scheme's (or other Lisps') `set-cdr!`. This means that every function that would call the old function will call the new version, which can be used to extend an existing function. Using the new function's name in it's definition will refer to itself, so to extend an existing function by calling it from it's new definition the programmer must first define the result of a `tn` call as the old function, then undefine the name, and use the `tn` name in the new function body.

### `sym`
`sym` is not present in the function table, because it is not a normal function, unless it has been defined as something else, in which case the table is rendered inaccesible if another name hasn't been provided. Trying to redefine `sym` will replace the entire symbol table with the provided body, which must have the structure described above. This is meant to enable programs to swap out modules, or to temporarily parse the input file in a different way, for instance with the table from `chars`. Calling `sym` will push a copy of the function table, in which all links between functions will be preserved. This is meant for saving the old table before replacing it.

## Writing languages

My intention is that writing programs should be the same as describing languages, by defining language features as functions that interpret other parts of the program. For instance, the predefined list comprehension is meant to work by defining `[` as the function that repeatedly pull the next expression until it encounters a `]`. This doesn't work for nestled lists, so the meaning of `pull` must also be changed, so that pulling a `[` executes it instead of pushing it's body.
