node {
    stage ('Checkout') {
        git branch:'code-coverage', url: 'https://github.com/XiongKezhi/codingstyle.git'
    }

    stage ('Line and Branch Coverage') {
        withMaven {
            sh "mvn -V -U -e jacoco:prepare-agent test jacoco:report -Dmaven.test.failure.ignore"
        }
        publishCoverage adapters: [jacocoAdapter('**/*/jacoco.xml')], 
            sourceFileResolver: sourceFiles('STORE_ALL_BUILD'), 
            calculateDiffForChangeRequests: true
    }
}
