def mvn_run_str = "mvn clean test -Dmaven.test.failure.ignore=true -DxmlPath=testXML/${env.SUITE_XML} -DmpropsFile=src/main/resources/configurationFiles/${env.CONFIGURATION}_grid.yml -DthreadCount=${env.THREAD_COUNT} -DmPlan=${env.PLAN} -DmRun=${env.RUN} -DmSuite=4378 -DmProject=16"

pipeline {
    agent "dockerhost"
    stages {
        stage('test') {
            agent {
                docker {
                    reuseNode true
                    image 'docker-local-lego-front.art.lmru.tech/img-jdk8-maven-allure'
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