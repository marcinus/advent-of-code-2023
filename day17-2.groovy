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

def sampleInput2 = '''111111111111
999999999991
999999999991
999999999991
999999999991'''.split('\n') as List<String>

def input = new File('inputs/day17-1.txt').readLines()

minC = 4
maxC = 10

S = (maxC + 1)*4

def inMap(N, M, pos) {
    return pos[0] >= 0 && pos[0] < N && pos[1] >= 0 && pos[1] < M
}

def neighbours(N, M, pos) {
    def neighs = []
    def dirs = [
        [0, 1],
        [1, 0],
        [0, -1],
        [-1, 0]
    ]
    for(int i = 0; i < 4; i++) {
        if((pos[2]/(maxC+1)) as int != i && ((pos[2] / (maxC+1)) as int + 2) % 4 != i && (pos[2] % (maxC+1)) <= maxC-minC) {
            def newPos = [pos[0]+dirs[i][0], pos[1]+dirs[i][1], i*(maxC+1)+maxC-1]
            if(inMap(N, M, newPos)) neighs.add(newPos)
        }
        if((pos[2]/(maxC+1)) as int == i && (pos[2] % (maxC+1)) > 0) {
            def newPos = [pos[0]+dirs[i][0], pos[1]+dirs[i][1], pos[2]-1]
            if(inMap(N, M, newPos)) neighs.add(newPos)

        }
    }
    //println "Neigs of $pos are $neighs"
    neighs
}

def dijsktra(absDist, dist, startPos) {
    def prev = [:]
    def q = new PriorityQueue({ List a, List b -> (a[1]-b[1]) as int })
    def visits = 0
    def N = absDist.size()
    def M = absDist.find { true }.value.size()
    def u
    startPos.each {
        q.offer([it, 0L])
    }
    do {
        visits += 1
        if(visits % 1000 == 0) {
            print '.'
        }
        u = q.poll()
        if(dist[u[0][0]][u[0][1]][u[0][2]] < u[1]) continue
        for(next in neighbours(N, M, u[0])) {
            def alt = (u[1] + absDist[next[0]][next[1]]) as long
            def previous = dist[next[0]][next[1]][next[2]]
            if (alt < previous) {
                dist[next[0]][next[1]][next[2]] = alt
                //q.remove([next, previous])
                q.add([next, alt])
                prev[next] = u[0]
            }
        }
    } while(!q.isEmpty())
    println()
    println visits
    def minDist = (0..<4).collect { i ->
        (0..(maxC-minC)).collect {
            dist[N-1][M-1][i*(maxC+1)+it]
        }.min()
    }.min()
    def slot = (0..<S).find {
        dist[N-1][M - 1][it] == minDist
    }
    def current = [N-1, M-1, slot]
    def i = 0
    def bestPath = new HashSet()
    while(!(current in startPos)) {
        bestPath.add(current)
        println "$i $current ${dist[current[0]][current[1]][current[2]]} ${prev[current]}"
        current = prev[current]
        i += 1
    }
    (0..<(absDist.size())). each { ii ->
        (0..<(absDist.get(0).size())).each { j ->
            def dir = bestPath.find { it[0] == ii && it[1] == j }?[2]
            if(dir != null) {
                switch ((dir/(maxC+1)) as int) {
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
    }
    minDist
}

def solve(List<String> map) {
    def distances = [:].withDefault { [:] }
    def N = map.size()
    def M = map.get(0).size()
    long[][][] dist = new long[N][M][S]
    (0..<N).each { i ->
        (0..<M).each { j ->
            distances[i][j] = map[i][j] as int
            (0..<S).each { k ->
                dist[i][j][k] = Long.MAX_VALUE / 2
            }
        }
    }
    dijsktra(distances, dist, [[0, 0, maxC], [0, 0, 2*(maxC+1)-1]])
}

assert 94 == solve(sampleInput)
assert 71 == solve(sampleInput2)
println solve(input)