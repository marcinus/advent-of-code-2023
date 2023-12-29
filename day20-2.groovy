def input = new File('inputs/day20-1.txt').readLines()

BROADCASTER = 'broadcaster'
ALL = 'ALL'

PATTERN = ~/(broadcaster|[%&][a-z]+) -> ((?:[a-z]+,? ?)+)/

def traverse(map, target, exits) {
    def elements = [target] as Set
    def queue = new ArrayDeque()
    queue.add(target)
    while(!queue.isEmpty()) {
        def top = queue.pop()
        map[top].each {
            if(!elements.contains(it)) {
                if(!(it in exits)) {
                    queue.add(it)
                }
                elements.add(it)
            }
        }
    }
    elements
}

def copyState(state) {
    state.collectEntries { k, v ->
        [(k): v instanceof Map ? new HashMap<>(v) : v]
    }
}

def singleBroadCast(map, types, invertedMap, target, exits) {
    def relevantElements = traverse(map, target, exits)
    println relevantElements
    def state = makeState(relevantElements, types, invertedMap)
    println state
    def states = [:]
    def ups = []
    def downs = []
    def pulses = new ArrayDeque()
    def iterations = 0
    def stateHistory = []
    stateHistory.add(copyState(state))
    while(true) {
        def oldState = copyState(state)
        iterations++
        pulses.push(['BUTTON', BROADCASTER, 0])
        while (!pulses.isEmpty()) {
            def (s, t, p) = pulses.pop()
            if(t in exits) {
                if(p) ups.add(iterations)
                else downs.add(iterations)
            }
            switch (types[t]) {
                case '%' -> {
                    if (!p) {
                        state[t] = state[t] ? 0 : 1
                        map[t].each {
                            if(it in relevantElements) {
                                pulses.add([t, it, state[t]])
                            }
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
                            if(it in relevantElements) {
                                pulses.add([t, it, 0])
                            }
                        }
                    } else {
                        map[t].each {
                            if(it in relevantElements) {
                                pulses.add([t, it, 1])
                            }
                        }
                    }
                }
                case BROADCASTER -> {
                    pulses.add([t, target, p])
                }
            }
        }
        if (state in states) {
            return [stateHistory.indexOf(state), stateHistory.size(), ups, downs]
        } else {
            def newState = copyState(state)
            states[oldState] = newState
            stateHistory.add(newState)
        }
    }
}

def transitions(map, types, invertedMap, state) {
    def pulses = new ArrayDeque()
    def iterations = 0
    while(true) {
        iterations++
        if(iterations % 1000 == 0) println "$iterations"
        if(iterations % 100000 == 0) println state
        pulses.push(['BUTTON', BROADCASTER, 0])
        while (!pulses.isEmpty()) {
            def (s, t, p) = pulses.pop()
            if(t == 'rx' && p == 0) {
                return iterations
            }
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
        }
    }
}

def makeState(relevantElements, types, invertedMap) {
    def state = [:]
    types.each { k, v ->
        if(!(k in relevantElements)) return
        if (v == '%') state[k] = 0
        else if (v == '&') state[k] = invertedMap[k].collectEntries { [(it): 0] } + [ALL: 0]
    }
    state
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

    def RX = 'rx'

    assert invertedMap[RX].size() == 1
    def prev = invertedMap[RX][0]
    assert types[prev] == '&'
    def prevPrevs = invertedMap[prev]
    assert prevPrevs.every { types[it] == '&' }
    def exits = ([RX, prev] + prevPrevs as Set) as Set
    def targets = map[BROADCASTER]
    targets.collect { target ->
        def (i, cycle, ups, downs) = singleBroadCast(map, types, invertedMap, target, exits)
        assert downs.size() == 1
        assert i == 1
        assert downs[0] == cycle-1
        downs[0] as long
    }.inject(1L) { lcm, next ->
        LCM(lcm, next)
    }
}

/**
 * Calculate Lowest Common Multiplier
 */
public static long LCM(long a, long b) {
    return (a * b) / GCF(a, b);
}

/**
 * Calculate Greatest Common Factor
 */
public static long GCF(long a, long b) {
    if (b == 0) {
        return a;
    } else {
        return (GCF(b, a % b));
    }
}

println solve(input)