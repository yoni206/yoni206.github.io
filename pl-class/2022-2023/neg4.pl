newlist(U,X,Y) :- inlist(U,X), del(U,X,Y).
newlist(U,X,Y) :- \+(inlist(U,X)), insert(U,X,Y).

inlist(U,[U|_]).
inlist(U,[_|X]) :- inlist(U,X).

insert(U,[],[U]).
insert(U,L,[U|L]).

del(X,[X|Tail],Tail).
del(X,[Y|Tail],[Y|Tail1]) :- del(X, Tail, Tail1).
