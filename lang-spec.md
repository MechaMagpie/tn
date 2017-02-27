## Datatypes

There are two datatypes: integers and lists pointers. List pointers point to a list of integers and other list pointers, potentially itself. Truth values are represented as integers, zero being false and non-zero being true. Strings are represented as lists of integers in the ASCII-range (1-127).

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
| input	 | [filename]	 | ...		| Changes source stream to "filename"			      			|
| output | [filename]	 | ...		| Changes current output stream to "filename"		      		|
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

The following functions are predefined from the built-ins

| Name 	 | Arguments 	 | Result 	| Definition													|
| ---- 	 | ---------   	 | ------ 	| ------------------------- 									|
| chars	 | ...		 	 | [table]	| Pushes a function table where every char is defined to push it's own ASCII value |
| rotate | x y z		 | z y x	| Rotates top of stack 	  			  	   	  		  	 	  	|
| dupd	 | y z		 	 | y y z	| Dups next to top element 										|
| not	 | b 			 | ¬b  		| Negates top element											|
| and	 | a b 			 | a ∧ b	| Yields conjunction of top elements							|
| or	 | a b 			 | a ∨ b	| Yields disjunction of top elements							|
| xor	 | a b 			 | a ⊕ b	| Yields exclusive disjunction of top elements					|
| fold	 | [list] [fun]	 | result	| Folds list with given function  	  							|
| any	 | [[pred]..]	 | bool		| Tests if any predicate from list holds						|
| number? | [ls]			 | [ls] bool | Tests if given list is function body of a number parser		|
| numtable | ...		 | [tabl]	| Pushes table where only numbers are defined  	 				|
| print  | [str]		 | ...		| Prints string to output										|
| spill	 | [a b ...]	 | ... b a	| Empties list onto stack										|
| smash	 | [a b] [c d]	 | [d c a b] | Puts top list reversed ahead of bottom list					|
| reverse | [a b c]	     | [c b a]	| Reverses list	 		  		   		  						|
| parsenum | x 	 		 | int		| Number parsing helper function								|
| [	 	 | ...		 	 | [list]	| Parses the remainder of a list until `]`						|
| ]	 	 | ...		 	 | ...		| No result, no body, just a flag for `[`						|
| "	 	 | ...		 	 | [string]	| Parses the remainder of a string								|
| pull	 | ...		 	 | x		| Redefined to evaluate `[` instead of pushing it's body 		|
| while	 | [t] [p]	 	 | ...		| Executes p as long as t yields true  	       	    			|
| 0-9 	 | ...		 	 | int		| Parses the remainder of an integer   	       	    			|

Furthermore all whitespace is defined as the null function (does nothing)

## The function table

The function table is, outside of the stack, the main data structure for the language. It is composed of module and function definitions, in a tree structure. The top level must only contain modules. A module is a list that starts with the integer 155 (ASCII "m"), a string, which is it's name, and a number of functions or modules. A function contains only a string, which is it's name, and a body, which is a list of functions that will be evaluated when it is called. Defining a function with the same name as another function will change the body *without* changing the actual list pointer object, analogous to some Scheme's (or other Lisps') `set-cdr!`. This means that every function that would call the old function will call the new version, which can be used to extend an existing function.