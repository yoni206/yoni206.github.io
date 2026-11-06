simplesort(X, Y) :- permut(X, Y), sorted(Y).

sorted([]).
sorted([_]).
sorted([A, B| X]) :- A =< B, sorted([B | X]).

permut([], []).
permut(X, [A | Y]) :-concat(X1, [A | X2], X), concat(X1, X2, X3), permut(X3, Y).

concat([], Y, Y).
concat([A|X],Y,[A|Z]) :- concat(X, Y, Z).
