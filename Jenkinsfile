def mvn_run_str = "mvn clean test -Dmaven.test.failure.ignore=true -DxmlPath=testXML/GlobalWebSuite.xml -DmpropsFile=src/main/resources/configurationFiles/${env.CONFIGURATION}_grid.yml -DthreadCount=${env.THREAD_COUNT} -DmPlan=${env.PLAN} -DmRun=${env.RUN} -DmSuite=4378 -DmProject=16"

pipeline {
    agent {
        docker {
            image 'docker-local-lego-front.art.lmru.tech/img-jdk8-maven-allure'
            args '-v $HOME/.m2:/root/.m2'
        }
    }
    stages {
        stage('test') {
            steps {
                sh(mvn_run_str)
                sh 'echo Finish'
            }
        }
        stage('reports') {
            steps {
                script {
                    allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'target/allure-results']]
                    ])
                }
            }
        }
    }
}