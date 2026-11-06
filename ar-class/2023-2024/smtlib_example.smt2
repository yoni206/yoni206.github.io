; Comments are written with `;` at the beginning of the line.
; This file represents the formula x = y /\ f(x) != f(y)
; To check for satisfiability, run `<solver> <path>`, 
; e.g., `z3 smtlib_example.smt2` or `cvc5 smtlib_example.smt2`

; set the logic to be quantifier-free (QF) over uninterpreted functions (UF)
; in other words -- this is a logic that is based on a functional signature.
(set-logic QF_UF)

; create a sort. 
; SMT-LIB allows to define variables over various sorts.
; Here we just create a single sort.
; All of our variables will range over this sort.
(declare-sort S 0)

; add a function symbol f that takes a single argument
; to the signature
(declare-fun f (S) S)

; create two variables x and y
(declare-fun x () S)
(declare-fun y () S)


; assert that x=y and f(x) != f(y)
(assert (and (= x y) (not (= (f x) (f y)))))

; check for satisfiability
(check-sat)
