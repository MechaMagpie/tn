# tn
This is my attempt at making an extreme self-modifying language, hopefully powerful enough to contain any possible language (I haven't actually proven this yet). To this end, sanity and legibility were sacrificed. The language is stack-based, and is centered on functions that redefine how the remainder of the program is parsed. Indeed, the only syntax the language has is function evaluation. List quotations, for instance, are implemented as a function that tells the environment to parse the remainder of a list.
## Files
`lang-spec.md` describes the language specification

`std.tn` defines some useful functions

`sexps.tn` allows the usage of S-expressions for some limited functionality. For instance:

```
Ready!
"sexps.tn" input
(print (rep (+ 2 3)))
5
```
