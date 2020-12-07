timestamps {
    node("dockerhost") {
        try {
            stage('Run job #1') {
                build job: 'PUZ2-UI-autotests', parameters: [
                        [$class: 'StringParameterValue', name: 'SUITE_XML', value: 'UiGlobalPortalSuite.xml'],
                        [$class: 'StringParameterValue', name: 'ENVIROMENT', value: env.ENVIRONMENT],
                        [$class: 'StringParameterValue', name: 'THREAD_COUNT', value: env.THREAD_COUNT],
                        [$class: 'StringParameterValue', name: 'RUN', value: env.RUN_NAME],
                        [$class: 'BooleanParameterValue', name: 'RUN_CASE_WITH_ISSUE', value: true],
                        [$class: 'StringParameterValue', name: 'TELEGRAM_CHAT', value: env.TELEGRAM_CHAT],
                        [$class: 'StringParameterValue', name: 'RETRY_COUNT', value: '1'],
                        [$class: 'StringParameterValue', name: 'TEST_CONFIG', value: 'chrome_grid']
                ]
            }
        } finally {
            try {
                stage('Run job #2') {
                    build job: 'PUZ2-UI-autotests', parameters: [
                            [$class: 'StringParameterValue', name: 'SUITE_XML', value: 'UiMobileCasesSuite.xml'],
                            [$class: 'StringParameterValue', name: 'ENVIROMENT', value: 'portal_mock'],
                            [$class: 'StringParameterValue', name: 'THREAD_COUNT', value: env.THREAD_COUNT],
                            [$class: 'StringParameterValue', name: 'RUN', value: env.RUN_NAME],
                            [$class: 'BooleanParameterValue', name: 'RUN_CASE_WITH_ISSUE', value: true],
                            [$class: 'StringParameterValue', name: 'TELEGRAM_CHAT', value: env.TELEGRAM_CHAT],
                            [$class: 'StringParameterValue', name: 'RETRY_COUNT', value: '1'],
                            [$class: 'StringParameterValue', name: 'TEST_CONFIG', value: 'android_web_grid']
                    ]
                }
            } finally {
                stage('Run job #3') {
                    build job: 'PUZ2-UI-autotests', parameters: [
                            [$class: 'StringParameterValue', name: 'SUITE_XML', value: 'UiGlobalMockCasesSuite.xml'],
                            [$class: 'StringParameterValue', name: 'ENVIROMENT', value: 'portal_mock'],
                            [$class: 'StringParameterValue', name: 'THREAD_COUNT', value: env.THREAD_COUNT],
                            [$class: 'StringParameterValue', name: 'RUN', value: env.RUN_NAME],
                            [$class: 'BooleanParameterValue', name: 'RUN_CASE_WITH_ISSUE', value : true],
                            [$class: 'StringParameterValue', name: 'TELEGRAM_CHAT', value: env.TELEGRAM_CHAT],
                            [$class: 'StringParameterValue', name: 'RETRY_COUNT', value: '1'],
                            [$class: 'StringParameterValue', name: 'TEST_CONFIG', value: 'chrome_grid']
                    ]
                }
            }
        }
    }
}