def sampleInput = '''LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)'''.split('\n') as List<String>

def input = new File('inputs/day8-1.txt').readLines()

LINE_MATCH = ~/^([A-Z0-9]{3}) = \(([A-Z0-9]{3}), ([A-Z0-9]{3})\)$/

def getTransitionMap(left, right, instructions) {
    def map = [:]
    for (def start in left.keySet()) {
        if (map.containsKey(start)) continue
        def current = start
        def length = (int) Math.ceil(((double) instructions.size() / 64))
        def indcies = (0..<(instructions.size)).inject(new long[length]) { bitmap, index ->
            if (instructions[index]) {
                current = right[current]
            } else {
                current = left[current]
            }
            if (current.endsWith('Z')) {
                println "$start $current $index"
                bitmap[(index / 64) as int] |= (1 << (index % 64))
            }
            bitmap
        }
        map[start] = [current, indcies]
    }
    map
}

def solveDiofantine(inputs) {

}

def shortestTransitionsToElementsThatHaveZ(map, startingIndices) {
    def promisingElements = map.findAll { it.value[1].any {it != 0}}.collect { it.key } as Set
    def startingCoefficients = startingIndices.collectEntries {
        println "Processing $it"
        def next = it, index = 0
        do {
            next = map[next][0]
            index++
        } while(!(next in promisingElements))
        [(it): [next, index]]
    }
    def repeatedCoefficients = promisingElements.collectEntries {
        println "Processing $it"
        def next = it, index = 0
        do {
            next = map[next][0]
            index++
        } while(!(next in promisingElements))
        [(it): [next, index]]
    }
    println startingCoefficients
    println repeatedCoefficients

    def reducedCoefficients = startingCoefficients.collectEntries { el, table ->
        def next = table[0]
        def o = table[1]
        while(repeatedCoefficients[next][0] != next) {
            o += repeatedCoefficients[next][1]
            next = repeatedCoefficients[next][0]
        }
        [(el): [next, o, repeatedCoefficients[next][1]]]
    }
    def diofantineCoefficients =  reducedCoefficients.values().collect { [it[1], it[2]] }
}

def solve(List<String> input) {
    def (List<Boolean> instructions, Map<String, String> left, Map<String, String> right) = parse(input)
    def current = left.keySet().findAll { it.endsWith('A') } as List<String>
    def map = getTransitionMap(left, right, instructions)
    def size = map.find { true }.value[1].size()
    shortestTransitionsToElementsThatHaveZ(map, current)
    sleep(1000000)
    def index = 0
    long result = ~0L
    while(true) {
        print "."
        def arrays = current.collect { map[it][1] }
        for(int i = 0; i < size; i++) {
            result = ~0L
            for(int j = 0; j < arrays.size(); j++) {
                result &= (arrays[j][i])
            }
            if(result != 0) {
                println "Found at $i $result"
                break
            }
        }
        if(result != 0) break
        current = current.collect { map[it][0] }
        index++
        if(index % 100 == 0) println index
    }
    index * instructions.size() + result
}

def parse(List<String> input) {
    def instructions = input[0].getChars().collect { it == 'R' }
    def left = [:]
    def right = [:]
    input.drop(2).each { line ->
        def matcher = line =~ LINE_MATCH
        assert matcher.matches()
        def id = matcher.group(1)
        def leftI = matcher.group(2)
        def rightI = matcher.group(3)
        left[id] = leftI
        right[id] = rightI
    }
    [instructions, left, right]
}

//assert 6 == solve(sampleInput)
println solve(input)