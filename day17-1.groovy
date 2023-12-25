def sampleInput = '''2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533'''.split('\n') as List<String>

def input = new File('inputs/day17-1.txt').readLines()

def inMap(absDist, pos) {
    return pos[0] >= 0 && pos[0] < absDist.size() && pos[1] >= 0 && pos[1] < absDist.find { true }.value.size()
    && pos[2] >= 0 && pos[2] <= 16
}

def neighbours(absDist, pos) {
    def neighs = []
    def dirs = [
        [0, 1],
        [1, 0],
        [0, -1],
        [-1, 0]
    ]
    for(int i = 0; i < 4; i++) {
        if((pos[2]/4) as int != i && ((pos[2] / 4) as int + 2) % 4 != i) {
            def newPos = [pos[0]+dirs[i][0], pos[1]+dirs[i][1], i*4+2]
            if(inMap(absDist, newPos)) neighs.add(newPos)
        }
        if((pos[2]/4) as int == i && (pos[2] % 4) > 0) {
            def newPos = [pos[0]+dirs[i][0], pos[1]+dirs[i][1], pos[2]-1]
            if(inMap(absDist, newPos)) neighs.add(newPos)

        }
    }
    neighs
}

def dijsktra(absDist, dist, startPos) {
    def prev = [:]
    def q = new PriorityQueue({ List a, List b -> (a[1]-b[1]) as int })
    def visits = 0
    def N = absDist.size()
    def M = absDist.find { true }.value.size()
    def u
    q.offer([startPos, 0L])
    do {
        visits += 1
        if(visits % 1000 == 0) {
            println visits
        }
        u = q.poll()
        for(next in neighbours(absDist, u[0])) {
            def alt = (u[1] + absDist[next[0]][next[1]]) as long
            if (alt < dist[next[0]][next[1]][next[2]]) {
                //println "Shortest path to $next is through ${u[0]}, ${u[1]}, $alt, ${dist[next[0]][next[1]][next[2]]}"
                dist[next[0]][next[1]][next[2]] = alt
                q.removeIf { it[0] == next }
                q.add([next, alt])
                prev[next] = u[0]
            }
        }
    } while(!q.isEmpty())
    //println visits
    def minDist = (0..<16).collect {
        dist[N-1][M-1][it]
    }.min()
    /*def slot = (0..<16).find {
        dist[N-1][M - 1][it] == minDist
    }
    def current = [N-1, M-1, slot]
    def i = 0
    def bestPath = new HashSet()
    while(current != startPos) {
        bestPath.add(current)
        println "$i $current ${dist[current[0]][current[1]][current[2]]} ${prev[current]}"
        current = prev[current]
        i += 1
    }
    (0..<(absDist.size())). each { ii ->
        (0..<(absDist.get(0).size())).each { j ->
            def dir = bestPath.find { it[0] == ii && it[1] == j }?[2]
            if(dir != null) {
                switch ((dir/4) as int) {
                    case 0 -> print '>'
                    case 1 -> print 'v'
                    case 2 -> print '<'
                    case 3 -> print '^'
                }
            } else {
                print absDist[ii][j]
            }
        }
        println()
    }*/
    minDist
}

def solve(List<String> map) {
    def distances = [:].withDefault { [:] }
    def N = map.size()
    def M = map.get(0).size()
    def S = 16
    long[][][] dist = new long[N][M][S]
    (0..<N).each { i ->
        (0..<M).each { j ->
            distances[i][j] = map[i][j] as int
            (0..<16).each { k ->
                dist[i][j][k] = Long.MAX_VALUE / 2
            }
        }
    }
    dijsktra(distances, dist, [0, 0, 3])
}

assert 102 == solve(sampleInput)
println solve(input)