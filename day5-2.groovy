sampleInput = '''seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4
'''.split('\n') as List<String>

def input = new File('inputs/day5-1.txt').readLines()

GAME_PATTERN = ~/Card (?<id>\d+): (?<winning>.*)|(?<have>.*).*/

enum Part {
    SEED,
    SEED_TO_SOIL,
    SOIL_TO_FERTILIZER,
    FERTILIZER_TO_WATER,
    WATER_TO_LIGHT,
    LIGHT_TO_TEMPERATURE,
    TEMPERATURE_TO_HUMIDITY,
    HUMIDITY_TO_LOCATION
}

def solve(List<String> lines) {
    def key = -1
    def seeds = []
    def map = []
    def match
    for (it in lines) {
        if (match = it =~ /seeds: ((\d+ *)+)$/) {
            seeds = (match.group(1).split(' ') as List).collect {it as long}
        } else if (match = it =~ /(.*) map:/) {
            key += 1
            map << []
        } else if (match = it =~ /(\d+) (\d+) (\d+)/) {
            map[key] << [match[0][1] as long, match [0][2] as long, match[0][3] as long]
        }
    }

    println seeds
    println map

    def flattened = flatten(map)

    println flattened
    seeds.collate(2).collect { s ->
        def data = flattened.collect { f ->
            if(f[1] > s[0] && f[1] < s[0]+s[1]) {
                return f[0]
            } else if(f[1] <= s[0] && f[1]+f[2] > s[0]) {
                return f[0] + (s[0]-f[1])
            } else {
                return null
            }
        }.min()
    }.min()
}

// fill in gaps
// and then, consider the edge cases

def fill(batch) {
    batch = batch.sort { x -> x[1] }
    def lastEnd = -10000000
    batch = batch.inject ([]) { list, newElement ->
        if(lastEnd < newElement[1]) {
            list << [lastEnd, lastEnd, newElement[1]-lastEnd]
        }
        list << newElement
        lastEnd = newElement[1]+newElement[2]
        list
    }
    batch << [batch[-1][1]+batch[-1][2], batch[-1][1]+batch[-1][2], 10000000000]
    batch
}

def flatten(map) {
    def effective = fill(map[0])
    for(int i = 1; i < map.size(); i++) {
        def newEffectiveBatchMapped = []
        for(int j = 0; j < map[i].size(); j++) {
            println "Effective: $effective"
            println "Processing ${map[i][j]} at $i level"
            // each
            def newEffectiveBatchUnmapped = []
            for (int k = 0; k < effective.size(); k++) {
                def startNew = map[i][j][0]
                def start = map[i][j][1]
                def range = map[i][j][2]
                def effectiveNewStart = effective[k][0]
                def effectiveStart = effective[k][1]
                def effectiveRange = effective[k][2]

                if (start <= effectiveNewStart && start + range > effectiveNewStart) {
                    println "Case 1"
                    def secondOffset = effectiveNewStart - start
                    if(effectiveNewStart+effectiveRange > start+range) {
                        def mappedRange = range - secondOffset
                        newEffectiveBatchMapped << [startNew + secondOffset, effectiveStart, mappedRange]
                        newEffectiveBatchUnmapped << [effectiveNewStart + mappedRange, effectiveStart + mappedRange, effectiveRange - mappedRange]
                    } else {
                        newEffectiveBatchMapped << [startNew+secondOffset, effectiveStart, effectiveRange]
                    }
                } else if(start > effectiveNewStart && start <= effectiveNewStart + effectiveRange) {
                    def secondOffset = start - effectiveNewStart
                    println "Case 2"
                    if(start + range >= effectiveNewStart + effectiveRange) {
                        def mappedRange = (effectiveRange - secondOffset)
                        newEffectiveBatchUnmapped << [effectiveNewStart, effectiveStart, effectiveRange-mappedRange]
                        newEffectiveBatchMapped << [startNew, effectiveStart+secondOffset, mappedRange]
                    } else {
                        newEffectiveBatchUnmapped << [effectiveNewStart, effectiveStart, secondOffset]
                        newEffectiveBatchMapped << [startNew, effectiveStart+secondOffset, range]
                        newEffectiveBatchUnmapped << [effectiveNewStart+secondOffset+range, effectiveStart+secondOffset+range, effectiveRange-secondOffset-range]
                    }
                } else {
                    println "Case 3 ${effective[k]}"
                    newEffectiveBatchUnmapped << effective[k]
                }
            }
            println "Got: $newEffectiveBatchMapped, $newEffectiveBatchUnmapped"
            effective = newEffectiveBatchUnmapped.sort { it[1] }
        }
        effective = effective + newEffectiveBatchMapped
        effective = effective.sort { it[1] }
    }
    effective
}

def dfs(map, k, i) {
    def minCost = 100000000000 as long
    if(k == map.size()) return i
    def found = false
    for(int j = 0; j < map[k].size(); j++) {
        def startNew = map[k][j][0]
        def start = map[k][j][1]
        def range = map[k][j][2]
        if(start <= i && start+range > i) {
            def newValue = i-start+startNew
            minCost = Math.min(minCost, dfs(map,k+1,newValue))
            found = true
            break
        }
    }
    if (!found) {
        minCost = Math.min(minCost, dfs(map,k+1,i))
    }
    return minCost
}

assert 46 == solve(sampleInput)
println solve(input)