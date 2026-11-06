// what does `let` do for scoping

// the problem
var x = 2;
{var x = 3}
console.log(x)

// our solution
var y = 2;
(function() {var y = 3})()
console.log(y)

// `let`'s solution
var y = 2;
{let y = 3}
console.log(y)

console.log("-------------------")
function f() {return 0}
Function.prototype.a = 3
console.log(f.a)
