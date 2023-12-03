sampleInput = '''467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598..
'''.split('\n') as List<String>

def input = new File('inputs/day3-1.txt').readLines()

PART_PATTERN = ~/[^\d\.]/
NUMBER_PATTERN = ~/\d+/

def solve(List<String> lines) {
    def patterns = lines.withIndex().collectEntries { line, i ->
        def positions = []
        def matcher = line =~ PART_PATTERN
        while (matcher.find()) {
            positions << matcher.start()
        }
        [(i): positions]
    }
    lines.withIndex().sum { line, i ->
        def suma = 0
        def matcher = line =~ NUMBER_PATTERN
        while (matcher.find()) {
            if ((i > 0 && patterns[i-1].any { it >= matcher.start()-1 && it <= matcher.end()+1 }) ||
            patterns[i].any { it >= matcher.start()-1 && it <= matcher.end()+1 } || 
            (i+1 < lines.size()) && patterns[i+1].any { it >= matcher.start()-1 && it <= matcher.end()+1 })) {
                suma += matcher.group() as int
            }
        }
        suma   
    }
}



assert 4361 == solve(sampleInput)
println solve(input)