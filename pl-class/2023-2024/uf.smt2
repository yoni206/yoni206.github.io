(set-logic QF_UF)
; the type of all the variables
(declare-sort S 0)

; the function f
(declare-fun f (S S) S)

; copies of all variables
(declare-fun ig1 () S)
(declare-fun ih1 () S)
(declare-fun og1 () S)
(declare-fun og2 () S)
(declare-fun og3 () S)
(declare-fun oh1 () S)

; psig
(define-fun psig () Bool (and (= og1 ig1) (= og2 (f og1 ig1)) (= og3 (f og2 ig1))))

; psih
(define-fun psih () Bool (= oh1 (f (f ih1 ih1) ih1)))

; The formula we want to be valid
(define-fun psi () Bool (=> (and (= ig1 ih1) psig psih) (= og3 oh1)))

; check if negation is sat
(assert (not psi))
(check-sat)
