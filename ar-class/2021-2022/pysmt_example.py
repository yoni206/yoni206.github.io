# import stuff from pysmt
from pysmt.shortcuts import Type, FunctionType
from pysmt.shortcuts import Symbol, Equals, Function, Not, And
from pysmt.shortcuts import is_sat

# In pysmt sorts are called types
# The following line corresponds to
# `(declare-sort S 0)`
S = Type("S")

# The following line creates the type "S->S"
# of functions that take one argument.
# It will be used when we want to declare such a function.
function_type = FunctionType(S, [S])

# (declare-fun f (S) S)
f = Symbol("f", function_type)

# (declare-fun x () S)
x = Symbol("x", S)

# (declare-fun y () S)
y = Symbol("y", S)

# the following lines create the formula
# x = y and f(x) != f(y)
formula1 = Equals(x, y)
formula2 = Not(Equals(Function(f, [x]), Function(f, [y])))
formula = And(formula1, formula2)

print("**********************")

# The following lines check the main symbol of
# `formula1` and `formula`
print(formula1.is_equals())
print(formula1.is_not())
print(formula.is_and())
print(formula.is_or())

print("**********************")

# The following lines print the children of `formula`
print(formula.args())
print(formula.args()[0])
print(formula.args()[1])

print("**********************")

# The last line check for satisfiability
# It corresponds to the following two lines in SMT-LIB
#
# (assert (and (= x y) (not (= (f x) (f y)))))
# (check-sat)
print(is_sat(formula))
