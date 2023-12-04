sampleInput = '''Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
'''.split('\n') as List<String>

def input = new File('inputs/day3-1.txt').readLines()

GAME_PATTERN = ~/Card (?<id>\d+): (?<winning>.*)|(?<have>.*).*/
NUMBER_PATTERN = ~/\d+/

def solve(List<String> lines) {
    def patterns = lines.sum { line ->
        def match = line =~ GAME_PATTERN
        assert match.matches()
        def id = match.group('id')
        def winning = match.group('winning').split(' ') as List<String>
        def have = match.group('have').split(' ') as List<String>
        println "$id $winning $have"
        0
    }
}

assert 467835 == solve(sampleInput)
println solve(input)