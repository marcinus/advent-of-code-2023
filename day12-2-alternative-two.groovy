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

        def springs = String.join('?', Collections.nCopies(5, els[0] as String))
        def pattern = (String.join(',', Collections.nCopies(5, els[1])).split(',') as List).collect {it as int }
        def data = eval(springs, pattern)
        //println "dATA: $data"
        data
    }
}

def getPartial(pattern) {
    def output = []
    def sum = pattern.sum()
    for(int i = 0; i < pattern.size(); i++) {
        output << sum
        sum -= pattern[i]
    }
    output
}

def getHashAndQuotesTotal(pattern) {
    def output = []
    def sum = (pattern as List).sum { it == '#' || it == '?' ? 1 : 0 }
    for(int i = 0; i < pattern.size(); i++) {
        output << sum
        if(pattern[i] == '#' || pattern[i] == '?') sum -= 1
    }
    output
}

def getHashAndQuotesConsecutive(pattern) {
    def output = []
    def sum = 0
    for(int i = pattern.size()-1; i >= 0; i--) {
        if(pattern[i] == '#' || pattern[i] == '?') sum += 1
        else sum = 0
        output << sum
    }
    output.reverse()
}

def getHashTotal(pattern) {
    def output = []
    def sum = (pattern as List).sum { it == '#' ? 1 : 0 }
    for(int i = 0; i < pattern.size(); i++) {
        output << sum
        if(pattern[i] == '#') sum -= 1
    }
    output
}

def eval(springs, pattern) {
    def T = [:].withDefault { [:] }
    def c = [
            N: pattern.size(),
            K: springs.size(),
            P: springs,
            l: pattern,
            pN: getPartial(pattern),
            hAqT: getHashAndQuotesTotal(springs),
            hAqC: getHashAndQuotesConsecutive(springs),
            hT: getHashTotal(springs),
    ]
    //println c
    getT(T, 0, 0, c)
}

long getT(T, k, n, c) {
    //println "Evaluating $k $n"
    def N = c['N']
    def K = c['K']
    if(k >= K) return n == N ? 1 : 0
    if(T.containsKey(k) && T[k].containsKey(n)) return T[k][n]
    //println "Calculating $k $n"
    def P = c['P']
    def lengths = c['l']
    def partialN = c['pN']
    def hashAndQuotesTotal = c['hAqT']
    def hashAndQuotesConsecutive = c['hAqC']
    def hashesTotal = c['hT']
    if (n < N) {
        //println "Case 1"
        if(hashAndQuotesTotal[k] < partialN[n]) {
            //println "$hashAndQuotesTotal $partialN"
            T[k][n] = 0L
        } else {
            if(P[k] == '.') {
                //println "Case 1.1"
                T[k][n] = getT(T, k+1, n, c)
            }
            else if(P[k] == '#') {
                //println "Case 1.2"
                if(hashAndQuotesConsecutive[k] == lengths[n] && k+lengths[n]==K) T[k][n] = 1
                else if(hashAndQuotesConsecutive[k] >= lengths[n] && (P[k+lengths[n]] == '.' || P[k+lengths[n]] == '?')) {
                    T[k][n] = getT(T, k+lengths[n]+1, n+1, c)
                } else {
                    T[k][n] = 0L
                }
            } else {
                //println "Case 1.3"
                T[k][n] = getT(T, k+1, n, c)
                if(hashAndQuotesConsecutive[k] == lengths[n] && k+lengths[n]==K) T[k][n] += 1
                else if(hashAndQuotesConsecutive[k] >= lengths[n] && (P[k+lengths[n]] == '.' || P[k+lengths[n]] == '?')) {
                    T[k][n] += getT(T, k+lengths[n]+1, n+1, c)
                }
            }
        }
    } else {
        //println "Case 2"
        T[k][n] = hashesTotal[k] == 0 ? 1 : 0
    }
    T[k][n]
}

def solveSimple(List<String> lines) {
    lines.sum { line ->
        def els = line.split(' ')
        def springs = els[0] as String
        def pattern = (els[1].split(',') as List).collect {it as int }
        def data = eval(springs, pattern)
        //println "dATA: $data"
        data
    }
}

assert 21 == solveSimple(sampleInput)
println solveSimple(input)
assert 525152 == solve(sampleInput)
println solve(input)

// The predicate is as following:
// T[k, set] = T[k+1, set] + T[k+m, set[1:]] assuming k:set[0] has quotationmarks/hashtags in place here and there's a dot or anything later
// There depends if
// k= 0..(#?)

// Can this reasoning be reverted?