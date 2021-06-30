TELEGRAM_BOT_URL = 'https://api.telegram.org/bot596012234:AAGAaYCfc2nDS3BAr2J7l0PRTgzOoBqGqy4'
TELEGRAM_REPORTS_CHAT = '-1001343153150'

env.TELEGRAM_CHAT = env.TELEGRAM_CHAT.replaceFirst(/^(.*?)\(.*\)/, '$1')

//Костыль для поддержки и ТестРейла, и Аллюра, после отказа от ТестРейла - удалить
env.AllURE_RUN_NAME = env.RUN
if (env.ALLURE_TEST_OPS) {
    env.RUN = ""
}

def telegramMessage(message) {
    if (env.TELEGRAM_CHAT) {
        sh """
           curl -X POST ${TELEGRAM_BOT_URL}/sendMessage \
                -d parse_mode=Markdown \
                -d chat_id=${env.TELEGRAM_CHAT} \
                -d text="${message}"
        """
    }
    if (env.SEND_TO_REPORTS_CHAT == "true") {
        sh """
           curl -X POST ${TELEGRAM_BOT_URL}/sendMessage \
                -d parse_mode=Markdown \
                -d chat_id=${TELEGRAM_REPORTS_CHAT} \
                -d text="${message}"
        """
    }
}

GString getMvnStrRun() {
    return "mvn clean test " +
            "-Dmaven.test.failure.ignore=true " +
            "-DrunWithIssues=${env.RUN_CASE_WITH_ISSUE} " +
            "-DxmlPath=testXML/mobile/api/${env.SUITE_XML} " +
            "-DthreadCount=${env.THREAD_COUNT} " +
            "-DmRun=${env.RUN} " +
            "-Denv=${env.ENVIROMENT}" +
            "-DmSuite=29833 -DmProject=10 "

}

timestamps {
    node("dockerhost") {
        stage('Run API auto tests') {

            checkout(
                    [$class                           : 'GitSCM',
                     branches                         : [[name: env.AUTOTESTS_BRANCH ? env.AUTOTESTS_BRANCH : 'master']],
                     doGenerateSubmoduleConfigurations: false,
                     extensions                       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'auto-tests']],
                     submoduleCfg                     : [],
                     userRemoteConfigs                : [[credentialsId: 'jenkins-gitlab', url: 'https://gitlab.lmru.adeo.com/lego-front/auto-tests.git']]
                    ])

            agent {
                docker {
                    reuseNode true
                    image 'maven:3.6.3-jdk-8-openj9'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }

            try {
                    dir('auto-tests') {
                        if (env.ALLURE_TEST_OPS == "true") {
                            withAllureUpload(serverId: 'allure-server', projectId: '13', results: [[path: 'target/allure-results']], name: env.AllURE_RUN_NAME) {
                                sh(getMvnStrRun())
                            }
                            //Костыль для поддержки и ТестРейла, и Аллюра, после отказа от ТестРейла - удалить
                        } else {
                            sh(getMvnStrRun())
                        }
                    }
            } finally {
                stage('Generate Allure Reports') {
                    allure([
                            includeProperties: false,
                            jdk              : '',
                            properties       : [],
                            reportBuildPolicy: 'ALWAYS',
                            results          : [[path: 'auto-tests/target/allure-results']]
                    ])
                }
            }
        }

        stage('Send notification') {
            telegramMessage("Маг Мобайл API Тесты завершены. Test run: ${env.AllURE_RUN_NAME} \n " +
                    "[Allure report](" + env.BUILD_URL + "allure)")
        }
    }
}