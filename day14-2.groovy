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

def rotateLeft(List<String> input) {
    def output = []
    for(int i = input.get(0).size()-1; i >= 0; i--) {
        def transposed = ""
        input.each {
            transposed += it[i]
        }
        output << transposed
    }
    output
}

def rotateRight(List<String> input) {
    def output = []
    input = input.reverse()
    for(int i = 0; i < input.get(0).size(); i++) {
        def transposed = ""
        input.each {
            transposed += it[i]
        }
        output << transposed
    }
    output
}

def solveSingle(List<String> lines) {
    lines.collect {
        StringBuilder myName = new StringBuilder(it)
        int lastBumpPosition = 0
        for(int i = 0; i < it.size(); i++) {
            if(it[i] == '#')  {
                lastBumpPosition = i+1
            }
            if(it[i] == 'O') {
                myName.setCharAt(i, '.' as char)
                myName.setCharAt(lastBumpPosition++, 'O' as char)
            }
        }
        myName.toString()
    }
}

def singleCycle(List<String> lines) {
    for(int i = 0; i < 4; i++) {
        lines = solveSingle(lines)
        lines = rotateRight(lines)
    }
    lines
}

def eval(List<String> lines) {
    lines.sum {
        def partial = 0
        for(int i = 0; i < it.size(); i++) {
            if(it[i] == 'O') partial += it.size()-i
        }
        partial
    }
}

def solve(List<String> lines) {
    def unique = new HashSet()
    def uniqueList = new ArrayList()
    lines = rotateLeft(lines)
    def newLines = new ArrayList(lines)
    unique.add(newLines)
    uniqueList.add(newLines)
    def iterations = 0
    def loopedValue = 0
    for(int i = 0; i < 1000; i++) {
        lines = singleCycle(lines)
        if(unique.contains(lines)) {
            // println "Found after ${i+1} iterations!"
            // println "The return value is ${uniqueList.indexOf(lines)}"
            iterations = i+1
            loopedValue = uniqueList.indexOf(lines)
            break
        }
        newLines = new ArrayList(lines)
        unique.add(newLines)
        uniqueList.add(newLines)
    }

    def loopSize = iterations-loopedValue
    def prefix = loopedValue
    def lastIndex = (1000000000L - loopedValue) % loopSize

    def targetState = uniqueList.get((lastIndex + prefix) as int)

    eval(targetState)
}

assert 64 == solve(sampleInput)
println solve(input)