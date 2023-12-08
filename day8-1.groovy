import com.sun.org.apache.xpath.internal.operations.Bool

def sampleInput1 = '''RL

AAA = (BBB, CCC)
BBB = (DDD, EEE)
CCC = (ZZZ, GGG)
DDD = (DDD, DDD)
EEE = (EEE, EEE)
GGG = (GGG, GGG)
ZZZ = (ZZZ, ZZZ)'''.split('\n') as List<String>

def sampleInput2 = '''LLR

AAA = (BBB, BBB)
BBB = (AAA, ZZZ)
ZZZ = (ZZZ, ZZZ)'''.split('\n') as List<String>

def input = new File('inputs/day8-1.txt').readLines()

LINE_MATCH = ~/^([A-Z]{3}) = \(([A-Z]{3}), ([A-Z]{3})\)$/

def solve(List<String> input) {
    def (List<Boolean> instructions, Map<String, String> left, Map<String, String> right) = parse(input)
    def current = 'AAA'
    def index = 0
    do {
        if(instructions[index++ % instructions.size()]) {
            current = right[current]
        } else {
            current = left[current]
        }
    } while('ZZZ' != current)
    index
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

assert 2 == solve(sampleInput1)
assert 6 == solve(sampleInput2)
println solve(input)