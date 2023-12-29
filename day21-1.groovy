def sampleInput = '''...........
.....###.#.
.###.##..#.
..#.#...#..
....#.#....
.##..S####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
...........'''.split('\n') as List<String>

def input = new File('inputs/day21-1.txt').readLines()

def neighbours(pos, map) {
    def dirs = [
            [0, 1],
            [0, -1],
            [1, 0],
            [-1, 0]
    ]
    dirs.collect { dir ->
        [pos[0] + dir[0], pos[1] + dir[1]]
    }.findAll { newPos ->
        newPos[0] >= 0 && newPos[0] < map.size() && newPos[1] >= 0 && newPos[1] < map[newPos[0]].size() && map[newPos[0]][newPos[1]] != '#'
    }
}

def bfsControlled(map, startPos, steps) {
    def currentLayer = [startPos] as Set
    for(int i = 0; i < steps; i++) {
        def nextLayer = new HashSet()
        currentLayer.each { pos ->
            nextLayer.addAll(neighbours(pos, map))
        }
        currentLayer = nextLayer
    }
    println currentLayer
    currentLayer.size()
}

def solve(List<String> map, int steps = 64) {
    def pos = []
    map.eachWithIndex { row, i ->
        row.eachWithIndex{ String sign,  j ->
            if(sign == 'S') {
                pos = [i, j]
                return
            }
        }
        if(pos != []) return
    }
    bfsControlled(map, pos, steps)
}

assert 16 == solve(sampleInput, 6)
println solve(input)