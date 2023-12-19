// Brute-force algorithm

def sampleInput = '''#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#'''.split('\n\n') as List<String>

def input = new File('inputs/day13-1.txt').text.split('\n\n') as List

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

def findMirrorLength(List<String> input) {
    def counter = [:].withDefault { 0 }
    input.each {
        for(int b = 1; b < it.size(); b++) {
            def j
            def limit = Math.min(b, it.size()-b)
            for(j = 0; j < limit; j++) {
                if(it[b+j] != it[b-j-1]) break
            }
            if(j == limit) counter[b] += 1
        }
    }
    counter.find {it.value == input.size()-1 }?.key ?: 0
}

def solve(List<String> samples) {
    samples.sum { sample ->
        List<String> lines = (sample.split('\n') as List).findAll { it != "" }
        findMirrorLength(lines) + 100 * findMirrorLength(transpose(lines))
    }
}

assert 400 == solve(sampleInput)
println solve(input)