//Manglicmot, Francis Cholo J.
//Exer 4
const a = 5
const b = 4
const c = 4.5

const d1 = 8
const d2 = 2
const d3 = 8
const max3 = (a, b, c) => {
	if (a > b && c){
		console.log(a)
	}else if (b > a && c){
		console.log(b)
	}else if (c > b && a){
		console.log(c)
	}
}

console.log("Test cases for no.1 max3()")
max3(a,b,c)
max3(d1, d2, d3)

const a1 = 135
const a2 = 6
const a3 = 3.5
const a4 = 0.2
const a5 = 5

const e1 = 135
const e2 = 1
const e3 = 3
const e4 = 3
const e5 = 24

const min5 = (a, b, c, d, e) => {
	if (a < b && c && d && e){
		console.log(a)
	}else if (b < c && d && e){
		console.log(b)
	}else if (c < d && e){
		console.log(c)
	}else if (d < e){
		console.log(d)
	}else {
		console.log(e)
	}
}

console.log("Test cases for no.2 min5()")
min5(a1,a2,a3,a4,a5)
min5(e1, e2, e3, e4,e5)

const b1 = 4
const b2 = 78
const b3 = 0

const f1 = 55
const f2 = 78
const f3 = 55
const median = (a, b, c) => {
	if (a > b && a < c || a > c && a < b){
		console.log(a)
	}else if (b > a && b < c || b > c && b < a){
		console.log(b)
	}else if (c > a && c < b || c > b && c < a){
		console.log(c)
	}else{
		console.log("null")
	}
}

console.log("Test cases for no.3 median()")
median(b1,b2,b3)
median(f1, f2, f3)


const c1 = 0
const c2 = 2

const c3 = 1
const c4 = 0

const c5 = 1 
const c6 = 5

const c7 = 6
const c8 = 0

const c9 = 6
const c10 = 5

const c11 = 1
const c12 = 5

single = ["one", "two", "three", "four", "five", "six", "seven", "eight", "nine"]
tens = ["twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"]
others = ["ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"]

const numbertoWord = (x, y) => {
	if (x == 0){
		console.log(single[y-1])
	}else if (x == 1){
		console.log(others[y])
	}else if (x == 2){
		console.log(tens[x-2] + single[y-1])
	}else if (x == 3){
		console.log(tens[x-2] + single[y-1])
	}else if (x == 4){
		console.log(tens[x-2] + single[y-1])
	}else if (x == 5){
		console.log(tens[x-2] + single[y-1])
	}else if (x == 6){
		console.log(tens[x-2] + single[y-1])
	}else if (x == 7){
		console.log(tens[x-2] + single[y-1])
	}else if (x == 8){
		console.log(tens[x-2] + single[y-1])
	}else if (x == 9){
		console.log(tens[x-2] + single[y-1])
	}
}

console.log("Test cases for no.4 numbertoWord()")
numbertoWord(c1,c2)
numbertoWord(c3,c4)
numbertoWord(c5,c6)
numbertoWord(c7,c8)
numbertoWord(c9,c10)
numbertoWord(c11,c12)
