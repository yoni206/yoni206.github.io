father(john,mark).
father(john,mary).
mother(jane,francis).
parent(X,Y) :- mother(X,Y).
parent(X,Y) :- father(X,Y).
