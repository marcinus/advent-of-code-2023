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

PATTERN = ~/.*\(#([0-9a-f]{6})\)/

def createMap(steps) {
    def map = new TreeMap<Integer, TreeMap<Integer, String>>()

    def prev = ''
    def topValues = steps.inject([[0, 0], [0, 0], [0, 0]]) { result, step ->
        def pos = result[0]
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
        result
    }
    def allRows = [topValues[1][0], topValues[2][0]]
    map.put(topValues[1][0], new TreeMap<Integer, String>())
    map.put(topValues[2][0], new TreeMap<Integer, String>())
    def prevRow = steps[-1][0]
    def prevRowSize = steps[-1][1]
    def pos = [0, 0]
    steps.each { step ->
            switch(step[0]) {
                case 'R' -> {
                    def floorEntry = map.floorEntry(pos[0])
                    def newVal = new TreeMap(floorEntry.value)
                    if (prevRow == 'U') {
                        newVal.put(pos[1], 'CUR')
                    } else if(prevRow == 'D') {
                        newVal.put(pos[1], 'CDR')
                    }
                    map.put(pos[0], newVal)
                    pos[1] += step[1]
                }
                case 'D' -> {
                    def floorEntry = map.floorEntry(pos[0])
                    def newVal = new TreeMap(floorEntry.value)
                    if (prevRow == 'L') {
                        newVal.put(pos[1], 'CLD')
                    } else if(prevRow == 'R') {
                        newVal.put(pos[1], 'CRD')
                    } else if(prevRow == 'D') {
                        newVal.put(pos[1], 'V')
                    }
                    def entry = map.floorEntry(pos[0]+1)
                    def potentialIntermediateRow = new TreeMap(entry.value)
                    if (entry == floorEntry) {
                        potentialIntermediateRow.remove(pos[1] + (prevRow == 'R' ? -prevRowSize : prevRowSize))
                    }
                    potentialIntermediateRow.put(pos[1], 'V')
                    map.put(pos[0]+1, potentialIntermediateRow)
                    map.put(pos[0], newVal)
                    map.subMap(pos[0]+1, pos[0]+step[1]).each { i, row ->
                        def newRow = new TreeMap(row)
                        newRow.put(pos[1], 'V')
                        map.put(i, newRow)
                    }
                    pos[0] += step[1]
                }
                case 'L' -> {
                    def floorEntry = map.floorEntry(pos[0])
                    def newVal = new TreeMap(floorEntry.value)
                    if (prevRow == 'U') {
                        newVal.put(pos[1], 'CUL')
                    } else if(prevRow == 'D') {
                        newVal.put(pos[1], 'CDL')
                    }
                    map.put(pos[0], newVal)
                    pos[1] -= step[1]
                }
                case 'U' -> {
                    def floorEntry = map.floorEntry(pos[0])
                    def newVal = new TreeMap(floorEntry.value)
                    if (prevRow == 'L') {
                        newVal.put(pos[1], 'CLU')
                    } else if(prevRow == 'R') {
                        newVal.put(pos[1], 'CRU')
                    } else if(prevRow == 'D') {
                        newVal.put(pos[1], 'V')
                    }
                    def entry = map.floorEntry(pos[0]+1)
                    def potentialIntermediateRowUp = new TreeMap(entry.value)
                    if (entry == floorEntry) {
                        potentialIntermediateRowUp.remove(pos[1] + (prevRow == 'R' ? -prevRowSize : prevRowSize))
                    }
                    map.put(pos[0]+1, potentialIntermediateRowUp)
                    entry = map.floorEntry(pos[0]-step[1]+1)
                    def potentialIntermediateRow = new TreeMap(entry.value)
                    potentialIntermediateRow.put(pos[1], 'V')
                    map.put(pos[0]-step[1]+1, potentialIntermediateRow)
                    map.subMap(pos[0]-step[1]+2, pos[0]).each { i, row ->
                        def newRow = new TreeMap(row)
                        newRow.put(pos[1], 'V')
                        map.put(i, newRow)
                    }
                    map.put(pos[0], newVal)
                    pos[0] -= step[1]
                }
            }
        prevRowSize = step[1]
        prevRow = step[0]
    }
    [map, topValues]
}

def sameDir(a, b) {
    a[1] == b[2] && b[1] == a[2]
}

def area(map) {
    long area = 0
    def prevRow = []
    def prevRowArea = []
    def areas = [:]
    def prevI = 0
    map.sort { it.key } .each { i, row ->
        if(i-prevI > 1) {
            area += (prevRowArea*(i-prevI-1))
        }
        def previousCount = false
        def countThis = false
        def previous = 0
        def openCorner = false
        def previousCorner
        long thisRowArea = 0
        row.findAll { it.value != 'H' && it.value != '.' }.sort { it.key }.each { j, v ->
            if (countThis) {
                thisRowArea += (j - previous)
            } else {
                thisRowArea += 1
            }
            if(v == 'V') countThis = !countThis
            else {
                if (openCorner) {
                    if(sameDir(previousCorner, v)) countThis = !previousCount
                    else countThis = previousCount
                    openCorner = false
                } else {
                    previousCorner = v
                    previousCount = countThis
                    countThis = true
                    openCorner = true
                }
            }
            previous = j
        }
        prevRowArea = thisRowArea
        prevRow = row
        area += thisRowArea
        areas[i] = thisRowArea
        prevI = i
    }
    [area, areas]
}

def decode(String line) {
    def match = (line =~ PATTERN)
    assert match.matches()
    def group = match.group(1)
    def chars = ['R', 'D', 'L', 'U']
    [chars[group[5] as int], Integer.parseInt(group.dropRight(1), 16)]
}

def solve(List<String> lines) {
    def steps = lines.collect { line ->
        decode(line)
    }
    def (map, topValues) = createMap(steps)
    def (area, areas) = area(map)
    area
}

assert 952408144115 == solve(sampleInput)
println solve(input)