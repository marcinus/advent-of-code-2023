def sampleInput = """1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
""".split('\n') as List<String>

def input = new File('inputs/day1-1.txt').readLines()

def solve(List<String> lines) {
    lines.collect { 
        def elements = it.findAll { it.isNumber() }
        "${elements[0]}${elements[-1]}" as int 
    }.sum()
}

assert 142 == solve(sampleInput)
println solve(input)