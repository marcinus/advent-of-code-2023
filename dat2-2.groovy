sampleInput = """Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
""".split('\n') as List<String>

def input = new File('inputs/day2-1.txt').readLines()

GAME_PATTERN = /Game (?<id>\d+): (?<repeatedThrows>.*)/
BLUE_PATTERN = ~/(\d+) blue/
RED_PATTERN = ~/(\d+) red/
GREEN_PATTERN = ~/(\d+) green/

def solve(List<String> lines) {
    lines.findResults { line ->
        def blueMax=0
        def redMax=0
        def greenMax=0
        def match = (line =~ GAME_PATTERN)
        assert match.matches()
        def id = match.group('id') as int
        def repeatedThrows = match.group('repeatedThrows')
        def allThrows = repeatedThrows.split(';') as List
        def feasible = allThrows.every { singleThrow ->
            def blueMatch = (singleThrow =~ BLUE_PATTERN)
            def redMatch = (singleThrow =~ RED_PATTERN)
            def greenMatch = (singleThrow =~ GREEN_PATTERN)
            def blue = blueMatch.size() == 1 ? blueMatch[0][1] as int : 0
            def red = redMatch.size() == 1 ? redMatch[0][1] as int : 0
            def green = greenMatch.size() == 1 ? greenMatch[0][1] as int : 0
            blue <= BLUE_MAX && red <= RED_MAX && green <= GREEN_MAX
        } ? id : null
    }.sum()
}

assert 2286 == solve(sampleInput)
println solve(input)