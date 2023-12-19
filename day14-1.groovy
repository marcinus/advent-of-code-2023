def sampleInput = '''O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#....'''.split('\n') as List<String>

def input = new File('inputs/day14-1.txt').readLines()

def transpose(List<String> input) {
    def output = []
    for(int i = 0; i < input.get(0).size(); i++) {
        def transposed = ""
        input.each {
            transposed += it[i]
        }
        output << transposed
    }
    output
}

long solveSingle(List<String> lines) {
    lines.sum {
        long ovals = 0
        long lastSolidPosition = -1
        long total = 0
        for(int i = 0; i < it.size(); i++) {
            if(it[i] == '#')  {
                lastSolidPosition = i
                ovals = 0
            }
            if(it[i] == 'O') {
                total += (it.size()-lastSolidPosition-ovals-1)
                ovals += 1
            }
        }
        total
    }
}

def solve(List<String> lines) {
    solveSingle(transpose(lines))
}

assert 136 == solve(sampleInput)
println solve(input)