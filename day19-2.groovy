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
    traverseWorkflows(rules, 'in', [
            x: [
                    gte: 1,
                    lte: 4000,
            ],
            m: [
                    gte: 1,
                    lte: 4000,
            ],
            a: [
                    gte: 1,
                    lte: 4000,
            ],
            s: [
                    gte: 1,
                    lte: 4000,
            ]
    ]).collect { it ->
        long combinations = 1
        it.each { k, v ->
            combinations *= (v.lte - v.gte + 1)
        }
        combinations
    }.sum()
}

def eval(elements, rules) {
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
    }.count { true }
}

def specifyMatchingInput(rule, currentInputSpecifier) {
    def newSpecifier = new HashMap(currentInputSpecifier)
    if(rule[1]) {
        newSpecifier[rule[0]] = [
                lte: Math.min(newSpecifier[rule[0]].lte, rule[2]-1),
                gte: newSpecifier[rule[0]].gte
        ]
    } else {
        newSpecifier[rule[0]] = [
                lte: newSpecifier[rule[0]].lte,
                gte: Math.max(newSpecifier[rule[0]].gte, rule[2]+1)
        ]
    }
    newSpecifier
}
def specifyUnmatchingInput(rule, currentInputSpecifier) {
    def newSpecifier = new HashMap(currentInputSpecifier)
    if(rule[1]) {
        newSpecifier[rule[0]] = [
                lte: newSpecifier[rule[0]].lte,
                gte: Math.max(newSpecifier[rule[0]].gte, rule[2])
        ]
    } else {
        newSpecifier[rule[0]] = [
                lte: Math.min(newSpecifier[rule[0]].lte, rule[2]),
                gte: newSpecifier[rule[0]].gte
        ]
    }
    newSpecifier
}

boolean feasible(inputSpecifier) {
    inputSpecifier.every { k, v ->
        v.lte >= v.gte
    }
}

def traverseWorkflows(workflows, currentWorkflowName, currentInputSpecifier) {
    if(currentWorkflowName == 'A') return [currentInputSpecifier]
    else if (currentWorkflowName == 'R') return []
    def feasibleDefinitions = []
    def workflow = workflows[currentWorkflowName]
    for(int i = 0; i < workflow[0].size(); i++) {
        def newInputSpecifier = specifyMatchingInput(workflow[0][i], currentInputSpecifier)
        if(feasible(newInputSpecifier)) {
            def nextWorkflow = workflow[0][i][3]
            feasibleDefinitions.addAll(traverseWorkflows(workflows, nextWorkflow, newInputSpecifier))
        }
        currentInputSpecifier = specifyUnmatchingInput(workflow[0][i], currentInputSpecifier)
        if(!feasible(currentInputSpecifier)) break
    }
    if(feasible(currentInputSpecifier)) {
        feasibleDefinitions.addAll(traverseWorkflows(workflows, workflow[1], currentInputSpecifier))
    }
    feasibleDefinitions
}

assert 167409079868000 == solve(sampleInput)
println solve(input)