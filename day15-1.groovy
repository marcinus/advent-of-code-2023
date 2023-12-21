def sampleInput = '''rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7'''

def input = new File('inputs/day15-1.txt').text.trim()

def solve(String line) {
    line.split(',').sum {
        (it.getBytes("US-ASCII") as List).inject(0) { int acc, byte val ->
            ((acc + val as int) * 17) % 256
        }
    }
}

assert 1320 == solve(sampleInput)
println solve(input)