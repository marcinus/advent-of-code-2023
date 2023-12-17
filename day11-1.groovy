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

def solve(List<String> lines) {
    def galaxyIndexes = lines.withIndex().collectMany { String line, i ->
        line.findIndexValues { it == '#' }.collect {[i, it] }
    }

    def emptyLines = lines.findIndexValues { !it.contains('#') }
    def emptyColumns = (0..<(lines.get(0).size())).findResults {j -> lines.every { it[j] != '#'} ? j : null }

    def rowNos = galaxyIndexes.collect { it[0] as int }.sort()
    def colNos = galaxyIndexes.collect { it[1] as int }.sort()

    calculateSumOfDistances(rowNos, emptyLines) + calculateSumOfDistances(colNos, emptyColumns)
}

def calculateSumOfDistances(List<Integer> indexesSorted, List<Integer> expandedSorted) {
    def distances = []
    def e = 0
    for(int i = 1; i < indexesSorted.size(); i++) {
        def distance = (indexesSorted[i] - indexesSorted[i-1])
        while(e < expandedSorted.size() && expandedSorted[e] < indexesSorted[i]) {
            distance++
            e++
        }
        distances << distance
    }
    (0..<(distances.size())).sum { distances[it] * (it+1) * (distances.size()-it) }
}

assert 374 == solve(sampleInput)
println solve(input)