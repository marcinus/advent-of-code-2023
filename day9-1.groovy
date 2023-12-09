def sampleInput = '''0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45'''.split('\n') as List<String>

def input = new File('inputs/day9-1.txt').readLines()

def solve(List<String> input) {
    input.sum { line ->
        def series = (line.split(' ') as List<String>).collect {it as long }
        def stack = new Stack<List<Long>>()
        stack.push(series)
        while(!stack.lastElement().every { it == 0 } ) {
            def lastSeries = stack.lastElement()
            def newSeries = (1..<(lastSeries.size())).collect { lastSeries[it]-lastSeries[it-1]}
            stack.push(newSeries)
        }
        long difference = 0
        while(!stack.isEmpty()) {
            difference += stack.pop().last()
        }
        difference
    }
}

assert 114 == solve(sampleInput)
println solve(input)