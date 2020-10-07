node {
    stage ('Checkout') {
        git branch:env.BRANCH_NAME, url:'https://github.com/jenkins-checks-api-test/codingstyle'
    }

    stage ('Build and Static Analysis') {
        sh 'mvn -V -e clean verify -Dmaven.test.failure.ignore'

        recordIssues tools: [java(), javaDoc()], aggregatingResults: 'true', id: 'java', name: 'Java'
        recordIssues tool: errorProne(), healthy: 1, unhealthy: 20

        recordIssues tools: [checkStyle(pattern: 'target/checkstyle-result.xml'),
            spotBugs(pattern: 'target/spotbugsXml.xml'),
            pmdParser(pattern: 'target/pmd.xml'),
            cpd(pattern: 'target/cpd.xml')],
            qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]]
    }
}
