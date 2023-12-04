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

PART_PATTERN = ~/\*/
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
    def gears = [:].withDefault { [:].withDefault{[]} }
    lines.eachWithIndex { line, i ->
        def suma = 0
        def matcher = line =~ NUMBER_PATTERN
        while (matcher.find()) {
            if (i > 0){
                patterns[i-1].findAll { it >= matcher.start()-1 && it <= matcher.end() }.each { gears[i-1][it] << matcher.group()}
            }
            patterns[i].findAll { it >= matcher.start()-1 && it <= matcher.end() }.each { gears[i][it] << matcher.group()}
            if (i+1 < lines.size()){
                patterns[i+1].findAll { it >= matcher.start()-1 && it <= matcher.end() }.each { gears[i+1][it] << matcher.group()}
            }
        }
    }
    gears.collect {i, row -> (row.findAll {j, nums -> nums.size() == 2}
    .collect {j, nums -> (nums[0] as long) * (nums[1] as long)} ?: []).sum() ?: 0 }.sum()
}

assert 467835 == solve(sampleInput)
println solve(input)