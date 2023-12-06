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
    seeds.collect {dfs(map, 0, it)
    }.min()
}

def dfs(map, k, i) {
    def minCost = 100000000000 as long
    if(k == map.size()) return i
    def found = false
    for(int j = 0; j < map[k].size(); j++) {
        def startNew = map[k][j][0]
        def start = map[k][j][1]
        def range = map[k][j][2]
        println "Traversing $k $j $i with $startNew $start $range"
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

assert 35 == solve(sampleInput)
println solve(input)