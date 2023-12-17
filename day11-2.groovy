def sampleInput = '''...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#.....'''.split('\n') as List<String>

def input = new File('inputs/day11-1.txt').readLines()

def solve(List<String> lines, long multiplier) {
    def galaxyIndexes = lines.withIndex().collectMany { String line, i ->
        line.findIndexValues { it == '#' }.collect {[i, it] }
    }

    def emptyLines = lines.findIndexValues { !it.contains('#') }
    def emptyColumns = (0..<(lines.get(0).size())).findResults {j -> lines.every { it[j] != '#'} ? j : null }

    def rowNos = galaxyIndexes.collect { it[0] as long }.sort()
    def colNos = galaxyIndexes.collect { it[1] as long }.sort()

    calculateSumOfDistances(rowNos, emptyLines, multiplier) + calculateSumOfDistances(colNos, emptyColumns, multiplier)
}

def calculateSumOfDistances(List<Long> indexesSorted, List<Long> expandedSorted, long multiplier) {
    def distances = []
    int e = 0
    for(int i = 1; i < indexesSorted.size(); i++) {
        def distance = (indexesSorted[i] - indexesSorted[i-1])
        while(e < expandedSorted.size() && expandedSorted[e] < indexesSorted[i]) {
            distance += (multiplier-1)
            e++
        }
        distances << distance
    }
    (0..<(distances.size())).sum { distances[it] * (it+1) * (distances.size()-it) }
}

assert 1030 == solve(sampleInput, 10)
assert 8410 == solve(sampleInput, 100)
println solve(input, 1000000)