def sampleInput = '''broadcaster -> a, b, c
%a -> b
%b -> c
%c -> inv
&inv -> a'''.split('\n') as List<String>

def sampleInput2 = '''broadcaster -> a
%a -> inv, con
&inv -> b
%b -> con
&con -> output'''.split('\n') as List<String>

def input = new File('inputs/day20-1.txt').readLines()

BROADCASTER = 'broadcaster'
ALL = 'ALL'

PATTERN = ~/(broadcaster|[%&][a-z]+) -> ((?:[a-z]+,? ?)+)/

def transitions(map, types, invertedMap, state) {
    def zeros = 0L
    def ones = 0L
    def pulses = new LinkedList()
    pulses.push(['BUTTON', BROADCASTER, 0])
    while (!pulses.isEmpty()) {
        def (s, t, p) = pulses.pop()
        if (p) ones++
        else zeros++
        switch (types[t]) {
            case '%' -> {
                if (!p) {
                    state[t] = state[t] ? 0 : 1
                    map[t].each {
                        pulses.add([t, it, state[t]])
                    }
                }
            }
            case '&' -> {
                def prevState = state[t][s] ?: 0
                if (prevState != p) {
                    state[t][s] = p
                    if (p) {
                        state[t][ALL]++
                    } else {
                        state[t][ALL]--
                    }
                }
                if (state[t][ALL] == invertedMap[t].size()) {
                    map[t].each {
                        pulses.add([t, it, 0])
                    }
                } else {
                    map[t].each {
                        pulses.add([t, it, 1])
                    }
                }
            }
            case BROADCASTER -> {
                map[t].each {
                    pulses.add([t, it, p])
                }
            }
        }
        //println "$s $t $p $state"
    }
    [zeros, ones, state]
}

def solve(List<String> lines) {
    def types = [:]
    def invertedMap = [:].withDefault { [] }
    def map = lines.collectEntries { line ->
        def match = line =~ PATTERN
        assert match.matches()
        def source = match.group(1)
        def sourceName = (source == BROADCASTER) ? BROADCASTER : source.drop(1)
        def sourceType = (source == BROADCASTER) ? BROADCASTER : source[0]
        def targets = match.group(2).split(', ') as List<String>
        types[sourceName] = sourceType
        targets.each {
            invertedMap[it].add(sourceName)
        }
        [(sourceName): targets]
    }
    println types
    println map
    def totalZ = 0L, totalO = 0L
    def z, o, state
    state = [:]
    types.each { k, v ->
        if (v == '%') state[k] = 0
        else if (v == '&') state[k] = invertedMap[k].collectEntries { [(it): 0] } + [ALL: 0]
    }
    def stateSet = [:]
    1000.times {
        if(state in stateSet.keySet()) {
            (z, o, state) = stateSet[state]
        } else {
            def oldState = copy(state)
            (z, o, state) = transitions(map, types, invertedMap, state)
            stateSet[oldState] = [z, o, copy(state)]
        }
        //println "$z $o $state"
        totalZ += z
        totalO += o
    }
    println "$totalZ $totalO ${stateSet.size()}"
    stateSet.each {k,v->
        println "${v[0]} ${v[1]}"
    }
    totalZ * totalO
}

def copy(state) {
    state.collectEntries { k, v ->
        [(k): (v instanceof Map) ? new HashMap(v) : v]
    }
}

assert 32000000 == solve(sampleInput)
assert 11687500 == solve(sampleInput2)
println solve(input)