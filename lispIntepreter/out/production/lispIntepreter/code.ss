(define (Parent name age)
    (define (getAge)
            age)
    (define (getName)
            name)
    (define (addAge x)
            (+ age x))
    (lambda (str)
        (cond ((eq? str 'getAge) getAge)
           ((eq? str 'getName) getName)
           ((eq? str 'addAge) addAge)
           (else 'error))))

(define (Son name age level)
    (define super
        (Parent name age))
    (define (levelUp x)
        (set! level (+ level x)))
    (define (getLevel) level)

    (lambda (m)
        (cond ((eq? m 'levelUp) levelUp)
           ((eq? m 'getLevel) getLevel)
           (else (super m)))) )

(define oj (Son 'ckq 18 1))

((oj 'levelUp) 1)
((oj 'getLevel))
((oj 'getAge))

"\n"
(define (fib-one n)
    (cond ((= n 0) 0)
          ((= n 1) 1)
          (else (+ (fib-one(- n 1))  (fib-one(- n 2))))))
(fib-one 10)