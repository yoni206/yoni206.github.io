type 'a element = 
| Nill
| Element of 'a;;


let read array i = array i;;

let write array i e = fun (x) -> 
		if x = i 
			then e 
			else array i;;
