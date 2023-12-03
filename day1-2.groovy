def sampleInput = """two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen
""".split('\n') as List<String>

def input = new File('inputs/day1-1.txt').readLines()

NUMBERS_SPELLED = [
    'zero',
    'one',
    'two',
    'three',
    'four',
    'five',
    'six',
    'seven',
    'eight',
    'nine'
]

def solve(List<String> lines) {
    lines.collect { line ->
        def numberIndexes = (line.split("(?!^)") as List).withIndex().collect { c, index -> c.isNumber() ? index : null }.findAll { it != null }
        def numbersSpelled = NUMBERS_SPELLED.withIndex().collectEntries { it, index -> [(index): [line.indexOf(it), line.lastIndexOf(it)]] }
        def maximumSpelled = numbersSpelled.findAll { it.value[1] >= 0 }.max { it.value[1] }
        def minimumSpelled = numbersSpelled.findAll { it.value[0] >= 0 }.min { it.value[0] }
        def firstDigit = minimumSpelled != null && minimumSpelled.value[0] >= 0 && 
        (numberIndexes.isEmpty() || minimumSpelled.value[0] < numberIndexes[0]) ? minimumSpelled.key : line[numberIndexes[0]]
        def lastDigit = maximumSpelled != null && maximumSpelled.value[1] >= 0 && 
        (numberIndexes.isEmpty() || maximumSpelled.value[1] > numberIndexes[-1]) ? maximumSpelled.key : line[numberIndexes[-1]]
        "${firstDigit}${lastDigit}" as int 
    }.sum()
}

assert 281 == solve(sampleInput)
println solve(input)