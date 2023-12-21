def sampleInput = '''rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7'''

def input = new File('inputs/day15-1.txt').text.trim()

def hash(String it) {
    (it.getBytes("US-ASCII") as List).inject(0) { int acc, byte val ->
        ((acc + val as int) * 17) % 256
    }
}

def eval(maps) {
    (0..255).collect { box ->
        maps[box]?.withIndex()?.collect { lens, index ->
            (1+box)*(index+1)*lens[1]
        }.sum() ?: 0
    }.sum()
}

def solve(String line) {
    def maps = (0..255).collect { [] }
    line.split(',').each {
        if(it.contains('-')) {
            def label = it - '-'
            def labelId = hash(label)
            maps[labelId].removeIf { it[0] == label }
        } else {
            def label = it.substring(0, it.indexOf('='))
            def labelId = hash(label)
            def val = it[-1] as int
            def lens = maps[labelId].find { it[0] == label }
            if (lens) {
                lens[1] = val
            } else {
                maps[labelId].add([label, val])
            }
        }
    }
    eval(maps)
}

assert 145 == solve(sampleInput)
println solve(input)