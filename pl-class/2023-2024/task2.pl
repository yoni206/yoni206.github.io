q(X, Y) :- p(X, Y), s(Y).

s([]).
s([_]).
s([A, B| X]) :- A =< B, s([B | X]).

p([], []).
p(X, [A | Y]) :-concat(X1, [A | X2], X), concat(X1, X2, X3), p(X3, Y).

concat([], Y, Y).
concat([A|X],Y,[A|Z]) :- concat(X, Y, Z).

