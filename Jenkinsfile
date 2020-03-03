def mvn_run_str = "mvn clean test -Dmaven.test.failure.ignore=true -DxmlPath=testXML/mobile/api/${env.SUITE_XML} -DmpropsFile=src/main/resources/configurationFiles/${env.CONFIGURATION}_grid.yml -DmSuite=258 -DmProject=10 -Dmenv=${env.ENVIROMENT}"

pipeline {
    agent { label 'dockerhost' }
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