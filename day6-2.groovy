import org.apache.tools.ant.util.StringUtils

sampleInput = '''Time:      7  15   30
Distance:  9  40  200
'''.split('\n') as List<String>

def input = new File('inputs/day6-1.txt').readLines()

TIME = ~/Time:\s+(\d.*)$/
DISTANCE = ~/Distance:\s+(\d.*)$/

def solveSingle(long time, long distance) {
    double delta = Math.sqrt(time*time-4*distance)
    long left = Math.max(0, Math.floor((time-delta)/2))
    long right = Math.min(time, Math.ceil((time+delta)/2))
    if(left*left - time*left + distance < 0) left -= 1
    if(right*right - time*right + distance < 0) right += 1
    return right - left - 1
}

def solve(List<String> input) {
    def (time, distance) = parse(input)
    solveSingle(time, distance)
}

def parse(List<String> input) {
    def time = input[0]
    def distance = input[1]
    def timesMatcher = (time =~ TIME)
    def distancesMatcher = (distance =~ DISTANCE)
    assert timesMatcher.matches() && distancesMatcher.matches()
    def times = timesMatcher.group(1).replaceAll('\\D', '') as long
    def distances = distancesMatcher.group(1).replaceAll('\\D', '') as long
    [times, distances]
}

assert 71503 == solve(sampleInput)
println solve(input)