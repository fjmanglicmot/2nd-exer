"use strict";

const amount1 = 10000;
const amount2 = 17000;
const amount3 = 14500;

const rate = .0475;
const time1 = 4.25;
const time2 = 2.75;
const time3 = 3;

const compute  = (p, r, t) => {
  let total = p*(1+r*t)
  return total
}


console.log("At " + rate*100 + " % interest, the total due for " + amount1 + " after " + time1 + " years/months is " + compute(amount1, rate, time1));
console.log("At " + rate*100 + " % interest, the total due for " + amount1 + " after " + time1 + " years/months is " + compute(amount1, rate, time1));
console.log("At " + rate*100 + " % interest, the total due for " + amount1 + " after " + time1 + " years/months is " + compute(amount1, rate, time1));