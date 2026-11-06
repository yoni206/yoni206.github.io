(set-logic ALL)
(declare-sort I 0)
(declare-sort E 0)
(declare-fun i () I)
(declare-fun j () I)
(declare-fun tmp () E)
(declare-fun a (I) E)
(declare-fun a_0 (I) E)
(declare-fun a_1 (I) E) 
(declare-fun a_2 (I) E)

(assert (= a_0 a))
(assert (= tmp (a_0 i)))
(assert (= (a_1 i) (a_0 j)))
(assert (= (a_2 j) tmp))

(assert (distinct a a_2))

(check-sat)
