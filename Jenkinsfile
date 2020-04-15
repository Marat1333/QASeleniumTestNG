def mvn_run_str = "mvn clean test -Dmaven.test.failure.ignore=true -DrunWithIssues=false -DxmlPath=testXML/mobile/api/${env.SUITE_XML} -DthreadCount=${env.THREAD_COUNT} -DmRun=${env.RUN} -DmSuite=29833 -DmProject=10 -Denv=${env.ENVIROMENT}"

pipeline {
    agent { label 'dockerhost' }
    stages {
        stage('test') {
            agent {
                docker {
                    reuseNode true
                    image 'maven:3.6.3-jdk-8-openj9'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            steps {
                sh(mvn_run_str)
                stash name: 'allure-results', includes: 'target/allure-results/*'
                sh 'echo Finish'
            }
        }
    }
    post {
        always {
            unstash 'allure-results'
            script {
                allure results: [[path: 'target/allure-results']]
            }
        }
    }
}