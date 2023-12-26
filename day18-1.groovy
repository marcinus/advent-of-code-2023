def sampleInput = '''R 6 (#70c710)
D 5 (#0dc571)
L 2 (#5713f0)
D 2 (#d2c081)
R 2 (#59c680)
D 2 (#411b91)
L 5 (#8ceee2)
U 2 (#caa173)
L 1 (#1b58a2)
U 2 (#caa171)
R 2 (#7807d2)
U 3 (#a77fa3)
L 2 (#015232)
U 2 (#7a21e3)'''.split('\n') as List<String>

def input = new File('inputs/day18-1.txt').readLines()

PATTERN = ~/([RDLU]) (\d+).*/

def areThereMoreRightsThanLefts(steps) {
    def lefts = 0
    def rights = 0
    def prev = steps.get(0)
    steps.drop(1).each { step ->
        if(prev == 'R' && step[0] == 'D'
        || prev == 'D' && step[0] == 'L'
        || prev == 'L' && step[0] == 'U'
        || prev == 'U' && step[0] == 'R') {
            rights++
        } else {
            lefts++
        }
        prev = step[0]
    }
    println "L: $lefts R: $rights"
    rights > lefts
}

def bfs(map, interior) {
    def dirs = [
            [0, 1],
            [1, 0],
            [0, -1],
            [-1, 0]
    ]
    def queue = new LinkedList(interior)
    while(!queue.isEmpty()) {
        def top = queue.pop()
        if(map[top[0]][top[1]] != '#') {
            for(def dir in dirs) {
                def next = [top[0]+dir[0], top[1]+dir[1]]
                if (!interior.contains(next) && map[next[0]][next[1]] == '.') {
                    queue.add(next)
                    interior.add(next)
                }
            }
        }
    }
    interior.size()
}

def createMap(steps) {
    def map = [:].withDefault { [:].withDefault { '.' } }
    def right = areThereMoreRightsThanLefts(steps)
    def interior = new HashSet()
    def topValues = steps.inject([[0, 0], [0, 0], [0, 0]]) { result, step ->
        def pos = result[0]
        if(right) {
            switch(step[0]) {
                case 'R' -> (1..(step[1])).each { map[pos[0]][pos[1]+it] = '#'; interior << [pos[0]+1, pos[1]+it] ; println "$pos $it" }
                case 'D' -> (1..(step[1])).each { map[pos[0]+it][pos[1]] = '#'; interior << [pos[0]+it, pos[1]-1] }
                case 'L' -> (1..(step[1])).each { map[pos[0]][pos[1]-it] = '#'; interior << [pos[0]-1, pos[1]-it] }
                case 'U' -> (1..(step[1])).each { map[pos[0]-it][pos[1]] = '#'; interior << [pos[0]-it, pos[1]+1] }
            }
        } else {
            switch(step[0]) {
                case 'R' -> (1..(step[1])).each { map[pos[0]][pos[1]+it] = '#'; interior << [pos[0]-1, pos[1]+it] }
                case 'D' -> (1..(step[1])).each { map[pos[0]+it][pos[1]] = '#'; interior << [pos[0]+it, pos[1]+1] }
                case 'L' -> (1..(step[1])).each { map[pos[0]][pos[1]-it] = '#'; interior << [pos[0]+1, pos[1]-it] }
                case 'U' -> (1..(step[1])).each { map[pos[0]-it][pos[1]] = '#'; interior << [pos[0]-it, pos[1]-1] }
            }
        }
        switch(step[0]) {
            case 'R' -> pos[1] += step[1]
            case 'D' -> pos[0] += step[1]
            case 'L' -> pos[1] -= step[1]
            case 'U' -> pos[0] -= step[1]
        }
        result[1][0] = Math.min(result[1][0], pos[0])
        result[1][1] = Math.min(result[1][1], pos[1])
        result[2][0] = Math.max(result[2][0], pos[0])
        result[2][1] = Math.max(result[2][1], pos[1])
        map.keySet().each { println "${it.getClass()}" }
        result
    }
    interior = interior.findAll { map[it[0]][it[1]] != '#' }
    (topValues[1][0]..topValues[2][0]).each { i ->
        (topValues[1][1]..topValues[2][1]).each { j ->
            print map[i][j]
        }
        println()
    }
    [map, interior, topValues]
}

def solve(List<String> lines) {
    def steps = lines.collect { line ->
        def match = (line =~ PATTERN)
        assert match.matches()
        [match.group(1), match.group(2) as int]
    }
    def (map, interior, topValues) = createMap(steps)
    def area = bfs(map, interior) + steps.collect { it[1] }.sum()
    interior.each {
        map[it[0]][it[1]] = '#'
    }
    (topValues[1][0]..topValues[2][0]).each { i ->
        (topValues[1][1]..topValues[2][1]).each { j ->
            print map[i][j]
        }
        println()
    }
    area
}

assert 62 == solve(sampleInput)
println solve(input)