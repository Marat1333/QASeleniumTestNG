//import java.text.SimpleDateFormat
//import groovy.json.*

MAIN_CHAT=-1001383631610 //-270543840
DEV_CHAT=-1001281010033
PUZ_CHAT=-239074717
CLIENT_PROJECTS=-391528347
LMUI=-1001254270700
OMFREM=-384530054
AAO_CHAT=-390819490
PAO_CHAT=-343860885
QA_LEGO_FRONT=-388033055

def telegramMessage(chatId, message, type = "") {
    if (chatId) {
        sh """
            curl -s -X POST https://api.telegram.org/bot596012234:AAGAaYCfc2nDS3BAr2J7l0PRTgzOoBqGqy4/sendMessage \
                    -d parse_mode=${type} \
                    -d chat_id=${chatId} \
                    -d text="${message}"
        """
    }
}

def telegramImage(chatId, file_id) {
    if (chatId) {
        sh """
            curl -s -X POST https://api.telegram.org/bot596012234:AAGAaYCfc2nDS3BAr2J7l0PRTgzOoBqGqy4/sendPhoto \
                 -d chat_id=${chatId} \
                 -d photo=${file_id}
        """
    }
}

def telegramSticker(chatId, id) {
    if (chatId) {
        sh """
            curl -s -X POST https://api.telegram.org/bot596012234:AAGAaYCfc2nDS3BAr2J7l0PRTgzOoBqGqy4/sendSticker \
                    -d chat_id=${chatId} \
                    -d sticker=${id}
        """
    }
}


def mvn_run_str = "mvn clean test -Dmaven.test.failure.ignore=true -DrunWithIssues=${env.RUN_CASE_WITH_ISSUE} -DxmlPath=testXML/mobile/api/${env.SUITE_XML} -DthreadCount=${env.THREAD_COUNT} -DmRun=${env.RUN} -DmSuite=29833 -DmProject=10 -Denv=${env.ENVIROMENT}"

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
        stage("notification") {
			steps {
                echo("Test")
                echo(WORKSPACE)
                telegramSend(
                            chatId: QA_LEGO_FRONT,
                            message: 'Результаты тестов тут -> https://jenkins.lmru.adeo.com/job/lego-front/view/MAGASIN%20mobile/job/lego-front-android-Run_API_tests/'
                        )
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