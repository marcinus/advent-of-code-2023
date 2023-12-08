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
    def jokers = counter.find { it.key == 'J' }?.value ?: 0
    def first, second
    if (counter[0].key == 'J') {
        first = jokers + (counter[1]?.value ?: 0)
        second = counter[2]?.value ?: 0
    } else {
        first = counter[0].value + jokers
        second = (counter[1]?.key == 'J' ? counter[2]?.value : counter[1]?.value) ?: 0
    }
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
    //println "$input $base"
    def encoded = (0..<(input.length())).inject((long)base) { cumulative, i ->
        def c = input[i]
        (long)((long)cumulative*14+(long)translate(c))
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
            return 1
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
        [bid, code, hand]
    }.sort { it[1] }.collect { println "${it[2]}: ${it[1]}"; it[0] }.withIndex().sum {bid, rank ->
        bid * (rank+1) // adjustment for rank
    }
}

assert 5905 == solve(sampleInput)
println solve(input)