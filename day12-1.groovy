def sampleInput = '''???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1'''.split('\n') as List<String>

def input = new File('inputs/day12-1.txt').readLines()

def solve(List<String> lines) {
    lines.sum { line ->
        def els = line.split(' ')
        def springs = els[0] as String
        def pattern = (els[1].split(',') as List).collect {it as int }
        def unknowns = (springs as List).count { it == '?' }
        eval(springs, pattern, unknowns)
    }
}

def eval(springs, pattern, d) {
    def stack = new Stack()
    def combinations = 0
    d.times { stack.push(0) }
    def k = 0
    def limit = (1 << (d as int))
    def numberOfOnes = 0
    while (k < limit) {
        k++
        if(feasible(springs, pattern, stack)) combinations++
        def taken = 1
        while(!stack.isEmpty() && stack.pop() == 1) taken += 1;
        stack.push(1)
        (taken-1).times { stack.push(0) }
    }
    println combinations
    combinations
}

def feasible(String springs, pattern, stack) {
    def j = 0
    def k = 0
    def series = 0
    for(int i = 0; i < springs.size(); i++) {
        def el = springs[i]
        if(el == '?') {
            el = stack[j] == 0 ? '#' : '.'
            j += 1
        }
        if(el == '#') series += 1
        else if (el == '.' && series > 0) {
            if(k >= pattern.size() || pattern[k] != series) return false
            series = 0
            k += 1
        }
    }
    if(series > 0) {
        if(k != pattern.size()-1 || pattern[k] != series) return false
        k++
    }
    if(k < pattern.size()) return false

    println stack
    return true
}

assert 21 == solve(sampleInput)
println solve(input)