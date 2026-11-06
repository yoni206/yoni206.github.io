parent(pam,bob).
parent(tom,bob).
parent(tom,liz).
parent(bob,ann).
parent(bob,pat).
parent(pat,jim).

ancestor1(X,Z) :- parent(X,Z).
ancestor1(X,Z) :- parent(X,Y), ancestor1(Y,Z).

ancestor2(X,Z) :- parent(X,Z).
ancestor2(X,Z) :- ancestor2(Y,Z), parent(X,Y). 

ancestor3(X,Z) :- parent(X,Y), ancestor3(Y,Z).
ancestor3(X,Z) :- parent(X,Z).

ancestor4(X,Z) :- ancestor4(Y,Z), parent(X,Y).
ancestor4(X,Z) :- parent(X,Z).
