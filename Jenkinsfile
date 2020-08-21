node {
    stage ('Checkout') {
        git branch:'publish-checks-step', url: 'https://github.com/jenkins-checks-api-test/codingstyle/'
    }

    stage ('Customized Check') {
        publishChecks name: 'customized-check', summary: 'this is a customized check created in pipeline', text: 'You can create customized check in pipeline', title: 'Publish Checks Step'
    }
}
