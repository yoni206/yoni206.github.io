q(X, Y) :- p(X, Y), s(Y).

s([]).
s([_]).
s([A, B| X]) :- A =< B, s([B | X]).

p([], []).
p(X, [A | Y]) :-c(X1, [A | X2], X), c(X1, X2, X3), p(X3, Y).

c([], Y, Y).
c([A|X],Y,[A|Z]) :- c(X, Y, Z).
