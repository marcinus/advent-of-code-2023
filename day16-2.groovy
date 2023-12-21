def sampleInput = '''.|...\\....
|.-.\\.....
.....|-...
........|.
..........
.........\\
..../.\\\\..
.-.-/..|..
.|....-|.\\
..//.|....'''.split('\n') as List<String>

def input = new File('inputs/day16-1.txt').readLines()

def inMap(N, M, pos) {
    pos[0] >= 0 && pos[0] < N && pos[1] >= 0 && pos[1] < M
}

// 1 -> >
// 2 -> D
// 4 -> <
// 8 -> U
def next(pos, dir) {
    switch(dir) {
        case 1 -> [pos[0], pos[1]+1]
        case 2 -> [pos[0]+1, pos[1]]
        case 4 -> [pos[0], pos[1]-1]
        case 8 -> [pos[0]-1, pos[1]]
        default -> throw new IllegalStateException("Shouldnt' be here ${pos} ${dir}")
    }
}

def dfs(List<String> map, startPos, startDir) {
    def N = map.size()
    def M = map.get(0).size()
    def occupations = new byte[N][M]
    def stack = new Stack()
    stack.push([startPos, startDir])
    while(!stack.isEmpty()) {
        def top = stack.pop()
        def pos = top[0]
        def dir = top[1]
        if(!inMap(N, M, pos) || occupations[pos[0]][pos[1]] & dir) continue
        occupations[pos[0]][pos[1]] |= dir
        def el = map[pos[0]][pos[1]]
        if(el == '.' || el == '|' && (dir == 2 || dir == 8) || el == '-' && (dir == 1 || dir == 4)) {
            stack.push([next(pos, dir), dir])
        } else if(el == '|') {
            stack.push([next(pos, 2), 2])
            stack.push([next(pos, 8), 8])
        } else if(el == '-') {
            stack.push([next(pos, 1), 1])
            stack.push([next(pos, 4), 4])
        } else if(el == '/') {
            switch (dir) {
                case 1 -> stack.push([next(pos, 8), 8])
                case 2 -> stack.push([next(pos, 4), 4])
                case 4 -> stack.push([next(pos, 2), 2])
                case 8 -> stack.push([next(pos, 1), 1])
                default -> throw new IllegalStateException("Shouldnt' be here ${pos} ${dir}")
            }
        } else if(el == '\\') {
            switch (dir) {
                case 1 -> stack.push([next(pos, 2), 2])
                case 2 -> stack.push([next(pos, 1), 1])
                case 4 -> stack.push([next(pos, 8), 8])
                case 8 -> stack.push([next(pos, 4), 4])
                default -> throw new IllegalStateException("Shouldnt' be here ${pos} ${dir}")
            }
        } else {
            throw new IllegalStateException("Shouldnt' be here ${pos} ${dir}")
        }
    }
    (0..<N).sum { i ->
        (0..<M).sum { j ->
            occupations[i][j] ? 1 : 0
        }
    }
}

def solve(List<String> map) {
    def left = (0..<(map.get(0).size())).collect { [[it, map.get(0).size()-1], 4] }
    def right = (0..<(map.get(0).size())).collect { [[it, 0], 1] }
    def down = (0..<(map.get(0).size())).collect { [[0, it], 2] }
    def up = (0..<(map.get(0).size())).collect { [[map.size()-1, it], 8] }
    [left, right, down, up].collect {
        it.collect { printdfs(map, it[0], it[1]) }.max()
    }.max()
}

assert 51 == solve(sampleInput)
println solve(input)