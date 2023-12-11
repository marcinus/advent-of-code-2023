def sampleInput1 = '''.....
.S-7.
.|.|.
.L-J.
.....'''.split('\n') as List<String>

def sampleInput2 = '''..F7.
.FJ|.
SJ.L7
|F--J
LJ...'''.split('\n') as List<String>

directions = [
        [0, 1],
        [0, -1],
        [-1, 0],
        [1, 0]
]

def input = new File('inputs/day10-1.txt').readLines()

def solve(List<String> input) {
    def m = input.size()
    def n = input.get(0).size()
    def map = input.collect { it.getChars() as List }
    def startPosition = map.withIndex().findResults { list, i -> def j = list.findIndexOf { it == 'S' }; j != -1 ? [i, j] : null }.find {true}

    bfs(map, startPosition)
    // now do the BFS from this position to all other positions, and get the latest element
}

def compatible(map, current, dir) {
    if (current[0][0] == 0 && dir[0] == -1 || current[0][0] == map.get(0).size()-1 && dir[0] == 1 || current[0][1] == 0 && dir[1] == -1 || current[0][1] == map.get(0).size()-1 && dir[1] == 1) return false
    def currentEl = map[current[0][0]][current[0][1]] as String
    def nextEl = map[current[0][0]+dir[0]][current[0][1]+dir[1]] as String
    //println "$currentEl: $nextEl $dir"
    switch(currentEl) {
        case 'J':
            return (dir[1] == -1 && nextEl in ['L', '-', 'F']) || (dir[0] == -1 && nextEl in ['7', 'F', '|'])
        case 'L':
            return (dir[1] == 1 && nextEl in ['7', '-', 'J']) || (dir[0] == -1 && nextEl in ['7', 'F', '|'])
        case '|':
            //println " HERE $currentEl $nextEl $dir ${nextEl in ['J', '|', 'L']}"
            return (dir[0] == 1 && nextEl in ['J', '|', 'L']) || (dir[0] == -1 && nextEl in ['7', 'F', '|'])
        case '-':
            return (dir[1] == -1 && nextEl in ['L', '-', 'F']) || (dir[1] == 1 && nextEl in ['7', '-', 'J'])
        case 'S':
            return (dir[1] == -1 && nextEl in ['L', '-', 'F']) || (dir[0] == -1 && nextEl in ['7', 'F', '|']) || (dir[1] == 1 && nextEl in ['J', '-', '7']) || (dir[0] == 1 && nextEl in ['J', '|', 'L'])
        case '7':
            return (dir[1] == -1 && nextEl in ['L', '-', 'F']) || (dir[0] == 1 && nextEl in ['J', '|', 'L'])
        case 'F':
            return (dir[1] == 1 && nextEl in ['J', '-', '7']) || (dir[0] == 1 && nextEl in ['J', '|', 'L'])
        case '.':
            return false
        default:
            throw new IllegalStateException("$currentEl")
    }
}

def bfs(map, startPosition) {
    def queue = new LinkedList()
    def visited = new HashSet()
    def top
    queue.push([startPosition, 0])
    while(!queue.isEmpty()) {
        top = queue.pop()
        visited.add(top[0])
        //println "$top $visited"
        for(dir in directions) {
            def nextElement = [top[0][0]+dir[0], top[0][1]+dir[1]]
            if (!visited.contains(nextElement) && compatible(map, top, dir)) {
                queue.addLast([nextElement, top[1]+1])
            }
        }
    }
    (0..<(map.size())).each { i ->
        (0..<(map.get(i).size())).each { j ->
            if([i, j] in visited) {
                print "-"
            } else {
                print "."
            }
        }
        println ""
    }
    top[1]
}

assert 4 == solve(sampleInput1)
assert 8 == solve(sampleInput2)
println solve(input)