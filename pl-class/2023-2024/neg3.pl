newlist(U,X,Y) :- inlist*(U,X,Z), newlist*(U,X,Y,Z).

newlist*(U,X,Y,yes) :- del(U,X,Y).
newlist*(U,X,Y,no) :- insert(U,X,Y).

inlist*(_,[],no).
inlist*(U,[U|_],yes).
inlist*(U,[_|X],Z) :- U \== Z, inlist*(U,X,Z).

insert(U,[],[U]).
insert(U,L,[U|L]).

del(X,[X|Tail],Tail).
del(X,[Y|Tail],[Y|Tail1]) :- del(X, Tail, Tail1).

