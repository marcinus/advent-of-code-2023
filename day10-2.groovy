def sampleInput1 = '''...........
.S-------7.
.|F-----7|.
.||.....||.
.||.....||.
.|L-7.F-J|.
.|..|.|..|.
.L--J.L--J.
...........'''.split('\n') as List<String>

def sampleInput2 = '''.F----7F7F7F7F-7....
.|F--7||||||||FJ....
.||.FJ||||||||L7....
FJL7L7LJLJ||LJ.L-7..
L--J.L7...LJS7F-7L7.
....F-J..F7FJ|L7L7L7
....L7.F7||L7|.L7L7|
.....|FJLJ|FJ|F7|.LJ
....FJL-7.||.||||...
....L---J.LJ.LJLJ...'''.split('\n') as List<String>

def sampleInput3 = '''FF7FSF7F7F7F7F7F---7
L|LJ||||||||||||F--J
FL-7LJLJ||||||LJL-77
F--JF--7||LJLJ7F7FJ-
L---JF-JLJ.||-FJLJJ7
|F|F-JF---7F7-L7L|7|
|FFJF7L7F-JF7|JL---7
7-L-JL7||F7|L7F-7F7|
L.L7LFJ|||||FJL7||LJ
L7JLJL-JLJLJL--JLJ.L'''.split('\n') as List<String>

directions = [
        [0, 1],
        [0, -1],
        [-1, 0],
        [1, 0]
]

def input = new File('inputs/day10-1.txt').readLines()

def solve(List<String> input) {
    def map = input.collect { it.getChars() as List }
    def startPosition = map.withIndex().findResults { list, i -> def j = list.findIndexOf { it == 'S' }; j != -1 ? [i, j] : null }.find {true}

    def calculateRight = isRightFromStart(map, startPosition)
    def calculateLeft = !calculateRight

    def integrationStartingPoints = new HashSet()

    def elementsOfPath = dfs(map, startPosition, { top, nextElement, dir ->
        def potentialInnerElement = calculateRight ? getRightFrom(top[0], dir) : getLeftFrom(top[0], dir)
        def nextElementSymbol = map[nextElement[0]][nextElement[1]]
        if(inMapRegion(map, potentialInnerElement)) {
            integrationStartingPoints.add(potentialInnerElement)
        }
        if (calculateRight && nextElementSymbol == '7' && dir == [-1, 0]) {
            potentialInnerElement = getRightFrom(nextElement, dir)
            if(inMapRegion(map, potentialInnerElement)) {
                integrationStartingPoints.add(potentialInnerElement)
            }
        }
        if (calculateRight && nextElementSymbol == 'J' && dir == [0, 1]) {
            potentialInnerElement = getRightFrom(nextElement, dir)
            if(inMapRegion(map, potentialInnerElement)) {
                integrationStartingPoints.add(potentialInnerElement)
            }
        }
        if (calculateRight && nextElementSymbol == 'L' && dir == [1, 0]) {
            potentialInnerElement = getRightFrom(nextElement, dir)
            if(inMapRegion(map, potentialInnerElement)) {
                integrationStartingPoints.add(potentialInnerElement)
            }
        }
        if (calculateRight && nextElementSymbol == 'F' && dir == [0, -1]) {
            potentialInnerElement = getRightFrom(nextElement, dir)
            if(inMapRegion(map, potentialInnerElement)) {
                integrationStartingPoints.add(potentialInnerElement)
            }
        }

        if (calculateLeft && nextElementSymbol == 'F' && dir == [-1,0]) {
            potentialInnerElement = getLeftFrom(nextElement, dir)
            if(inMapRegion(map, potentialInnerElement)) {
                integrationStartingPoints.add(potentialInnerElement)
            }
        }

        if (calculateLeft && nextElementSymbol == 'L' && dir == [0,-1]) {
            potentialInnerElement = getLeftFrom(nextElement, dir)
            if(inMapRegion(map, potentialInnerElement)) {
                integrationStartingPoints.add(potentialInnerElement)
            }
        }


        if (calculateLeft && nextElementSymbol == '7' && dir == [0,1]) {
            potentialInnerElement = getLeftFrom(nextElement, dir)
            if(inMapRegion(map, potentialInnerElement)) {
                integrationStartingPoints.add(potentialInnerElement)
            }
        }


        if (calculateLeft && nextElementSymbol == 'J' && dir == [1,0]) {
            potentialInnerElement = getLeftFrom(nextElement, dir)
            if(inMapRegion(map, potentialInnerElement)) {
                integrationStartingPoints.add(potentialInnerElement)
            }
        }
    })

    integrationStartingPoints -= elementsOfPath

    def visited = new HashSet()

    println integrationStartingPoints

    for (el in integrationStartingPoints) {
        println System.identityHashCode(visited)
        traverse(map, el, visited, elementsOfPath)
    }
    println visited

    (0..<(map.size())).each { i ->
        (0..<(map[i].size())).each { j ->
            if([i, j] in visited) print '.'
            else if([i, j] in elementsOfPath) print map[i][j]
            else print ' '
        }
        println ""
    }

    visited.size()
    // now do the BFS from this position to all other positions, and get the latest element
}

def traverse(map, startPosition, Set visited, Set boundary) {
    println "In loop: " + (System.identityHashCode(visited))
    def queue = new LinkedList()
    def top
    queue.push([startPosition, 0])
    while(!queue.isEmpty()) {
        top = queue.pop()
        visited.add(top[0])
        for(dir in directions) {
            def nextElement = [top[0][0]+dir[0], top[0][1]+dir[1]]
            if (!visited.contains(nextElement) && inMapRegion(map, nextElement) && !boundary.contains(nextElement)) {
                queue.addLast([nextElement, top[1]+1])
            }
        }
    }
    //println visited
}

def inMapRegion(map, position) {
    return position[0] >= 0 && position[0] < map.size() && position[1] >= 0 && position[1] < map.find {true}.value.size()
}

def getRightFrom(position, dir) {
    if(dir == [0, 1]) {
        return [position[0]+1, position[1]]
    }
    if(dir == [1, 0]) {
        return [position[0], position[1]-1]
    }
    if(dir == [0, -1]) {
        return [position[0]-1, position[1]]
    }
    if(dir == [-1, 0]) {
        return [position[0], position[1]+1]
    }
    throw new IllegalStateException("Invalid dir");
}

def getLeftFrom(position, dir) {
    if(dir == [0, 1]) {
        return [position[0]-1, position[1]]
    }
    if(dir == [1, 0]) {
        return [position[0], position[1]+1]
    }
    if(dir == [0, -1]) {
        return [position[0]+1, position[1]]
    }
    if(dir == [-1, 0]) {
        return [position[0], position[1]-1]
    }
    throw new IllegalStateException("Invalid dir");
}

def compatible(map, current, dir) {
    if (current[0][0] == 0 && dir[0] == -1 || current[0][0] == map.get(0).size()-1 && dir[0] == 1 || current[0][1] == 0 && dir[1] == -1 || current[0][1] == map.get(0).size()-1 && dir[1] == 1) return false
    def currentEl = map[current[0][0]][current[0][1]] as String
    def nextEl = map[current[0][0]+dir[0]][current[0][1]+dir[1]] as String
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

def dfs(map, startPosition, closure) {
    def queue = new LinkedList()
    def visited = new HashSet()
    def top
    queue.push([startPosition, 0])
    while(!queue.isEmpty()) {
        top = queue.pop()
        visited.add(top[0])
        for(dir in directions) {
            def nextElement = [top[0][0]+dir[0], top[0][1]+dir[1]]
            if (!visited.contains(nextElement) && compatible(map, top, dir)) {
                queue.addFirst([nextElement, top[1]+1])
                closure(top, nextElement, dir)
                break
            }
        }
    }
    visited
}

def isRightTurn(prevDir, dir) {
    prevDir == [0, 1] && dir == [1, 0] ||
    prevDir == [-1, 0] && dir == [0, 1] ||
    prevDir == [0, -1] && dir == [-1, 0] ||
    prevDir == [1, 0] && dir == [0, -1]
}

def isLeftTurn(prevDir, dir) {
    prevDir == [0, 1] && dir == [-1, 0] ||
    prevDir == [1, 0] && dir == [0, 1] ||
    prevDir == [0, -1] && dir == [1, 0] ||
    prevDir == [-1, 0] && dir == [0, -1]
}

boolean isRightFromStart(map, startPosition) {
    def prevDir = [0, 0]
    def leftTurns = 0
    def rightTurns = 0
    def n = 0

    def rightTurnElements = []
    def leftTurnElements = []

    def pathElements = dfs(map, startPosition,  { top, nextElement, dir ->
        if (map[nextElement[0]][nextElement[1]] as String in ['L', 'J', 'F', '7']) n+= 1
        //n += 1
        leftTurns += isLeftTurn(prevDir, dir) ? 1 : 0
        if(isLeftTurn(prevDir, dir)) leftTurnElements << top[0]
        rightTurns += isRightTurn(prevDir, dir) ? 1 : 0
        if(isRightTurn(prevDir, dir)) rightTurnElements << top[0]
        prevDir = dir
    })
    println n
    println "$rightTurns $leftTurns"
    (0..<(map.size())).each { i ->
        (0..<(map[i].size())).each { j ->
            if([i, j] in rightTurnElements) print 'R'
            else if([i, j] in leftTurnElements) print 'L'
            else if([i, j] in pathElements) print '.'
            else print ' '
        }
        println ""
    }
    rightTurns > leftTurns
}

assert 4 == solve(sampleInput1)
assert 8 == solve(sampleInput2)
assert 10 == solve(sampleInput3)
println solve(input)