def sampleInput = '''px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}'''.split('\n\n') as List<String>

def input = new File('inputs/day19-1.txt').text.split('\n\n') as List<String>

RULE_PATTERN = ~/(.*)\{(.*)}/
PATTERN = ~/\{x=(\d+),m=(\d+),a=(\d+),s=(\d+)}/

long solve(List<String> input) {
    def rules = (input.get(0).split('\n') as List).collectEntries {
        def match = (it =~ RULE_PATTERN)
        assert match.matches()
        def prefix = match.group(1)
        def suffix = (match.group(2).split(',') as List)
        def defal = suffix[-1]
        def rules = suffix.take(suffix.size()-1).collect {
            def els = it.split(':')
            def rule = els[0].contains('<') ? els[0].split('<') : els[0].split('>')
            println it
            println els
            println rule
            def var = rule[0]
            def val = rule[1] as long
            def lessThan = els[0].contains('<')
            def next = els[1]
            [var, lessThan, val, next]
        }
        [(prefix): [rules, defal]]
    }
    def elements = (input.get(1).split('\n') as List).collect {
        def match = (it =~ PATTERN)
        assert match.matches()
        [
                x: match.group(1) as long,
                m: match.group(2) as long,
                a: match.group(3) as long,
                s: match.group(4) as long
        ]
    }
    println rules
    println elements
    elements.findAll { element ->
        def workflowName = 'in'
        def workflowsDone = []
        while(workflowName != 'A' && workflowName != 'R') {
            workflowsDone << workflowName
            def workflow = rules[workflowName]
            workflowName = workflow[0].find {
                it[1] ? element[it[0]] < it[2] : element[it[0]] > it[2]
            }?[3] ?: workflow[1]
        }
        workflowName == 'A'
    }.collect { it.collect { it.value }.sum() }.sum()
}

assert 19114 == solve(sampleInput)
println solve(input)