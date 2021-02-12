TELEGRAM_BOT_URL = 'https://api.telegram.org/bot596012234:AAGAaYCfc2nDS3BAr2J7l0PRTgzOoBqGqy4'

TEST_CHAT=-1001283842704
QA_LEGO_FRONT=-388033055

def getSelectedChatId() {
    if (env.TELEGRAM_CHAT == 'Test')
        return TEST_CHAT;
    if (env.TELEGRAM_CHAT == 'QA_Lego_Front')
        return QA_LEGO_FRONT;
    return 0;
}

def telegramMessage(message) {
    if (env.TELEGRAM_CHAT) {
        sh """
           curl -X POST ${TELEGRAM_BOT_URL}/sendMessage \
                -d parse_mode=Markdown \
                -d chat_id=${getSelectedChatId()} \
                -d text="${message}"
        """
    }
}

GString getMvnStrRun() {
    return "mvn clean test -B " +
            "-Dmaven.test.failure.ignore=true " +
            "-DxmlPath=testXML/portal/api/${env.SUITE_XML} " +
            "-DthreadCount=${env.THREAD_COUNT} " +
            "-DrunWithIssues=${env.RUN_CASE_WITH_ISSUE} " +
            "-DmRun=${env.RUN} " +
            "-Denv=${env.ENVIROMENT} " +
            "-DretryOnFailCount=${env.RETRY_COUNT} " +
            "-DmSuite=30121 -DmProject=16"
}

timestamps {
    node("dockerhost") {
        stage('Run API auto tests') {

            checkout(
                    [$class                           : 'GitSCM',
                     branches                         : [[name: env.AUTOTESTS_BRANCH]],
                     doGenerateSubmoduleConfigurations: false,
                     extensions                       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'auto-tests']],
                     submoduleCfg                     : [],
                     userRemoteConfigs                : [[credentialsId: 'jenkins-gitlab', url: 'https://gitlab.lmru.adeo.com/lego-front/auto-tests.git']]
                    ])


            try {
                docker.image('maven:3.6.3-jdk-8-openj9').inside("-v android-maven-cache:/root/.m2 --privileged") {
                    dir('auto-tests') {
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
            telegramMessage("Маг Портал API Тесты завершены. Test run: ${env.RUN} \n " +
                    "[Allure report](https://jenkins.lmru.adeo.com/job/lego-front/job/Puz2-API-autotests/"+ env.BUILD_NUMBER +"/allure)")
        }
    }
}