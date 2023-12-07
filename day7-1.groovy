sampleInput = '''32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483'''.split('\n') as List<String>

def input = new File('inputs/day7-1.txt').readLines()

LINE_MATCH = ~/^([AKQJT2-9]{5}) (\d+)$/

def getOrderableHash(String input) {
    def counter = [:].withDefault { 0 }
    (0..<(input.length())).each { counter[input[it]]++ }
    counter = counter.collect { it }.sort { -it.value }
    def first = counter[0].value
    def second = counter[1]?.value ?: 0
    def base
    switch (first) {
        case 5:
            base = 6
            break
        case 4:
            base = 5
            break
        case 3:
            base = (second == 2 ? 4 : 3)
            break
        case 2:
            base = (second == 2 ? 2 : 1)
            break
        default:
            base = 0
    }
    def encoded = (0..<(input.length())).inject(base) { cumulative, i ->
        def c = input[i]
        cumulative*14+translate(c)
    }
    encoded
}

def translate(String c) {
    switch(c) {
        case 'A':
            return 14
        case 'K':
            return 13
        case 'Q':
            return 12
        case 'J':
            return 11
        case 'T':
            return 10
        default:
            return c as int
    }
}

def solve(List<String> input) {
    input.collect { line ->
        def match = line =~ LINE_MATCH
        assert match.matches()
        def hand = match[0][1]
        def bid = match[0][2] as long
        def code = getOrderableHash(hand) as long
        [bid, code]
    }.sort { it[1] }.collect { it[0] }.withIndex().sum {bid, rank ->
        bid * (rank+1) // adjustment for rank
    }
}

assert 6440 == solve(sampleInput)
println solve(input)