

type 'a list = 
| Atom  of 'a
| Cons of 'a list * 'a list;;


let cons x y = Cons(x,y);;


let atom x = match x with
| Atom(x) -> true
| _ -> false;;

let car lst = match lst with
| Cons(x,y) -> x
| x -> x;;

let cdr lst = match lst with
| Cons (x,y) -> y
| x -> x;;

