def user_id
def group_id
node {
    user_id = sh(returnStdout: true, script: 'id -u').trim()
    group_id = sh(returnStdout: true, script: 'id -g').trim()
}

pipeline {
    agent {
        dockerfile {
            dir '.ops/build-docker'
            args '-v /var/run/docker.sock:/var/run/docker.sock -v /etc/group:/etc/group'
            additionalBuildArgs "--build-arg USER_ID=${user_id} --build-arg GROUP_ID=${group_id}"
        }
    }
    parameters {
        string(name: 'ARTIFACT_TAG', defaultValue: '', description: 'The tag used to stage artifacts (requires creating release zip and will push artifacts to artifactory, docker, etc.)')
        string(name: 'SLACK', defaultValue: '', description: 'The slack channel to notify of the builds results.  This is typically #notifications-eng-gh.')
    }
    options {
        timeout(time: 30, unit: 'MINUTES')
    }
    environment {
        jenkinsOsxPub = credentials('jenkins.osx.pub')
        jenkinsOsxPriv = credentials('jenkins.osx.priv')
        artifactoryUsername = credentials('artifactoryUsername')
        artifactoryPassword = credentials('artifactoryPassword')

        LANG = 'C.UTF-8'
    }
    stages {
        stage('Build') {
            steps {
                script {
                    sh "sbt assembly"
                }
            }
        }
        stage('Publish jars and zip to Jfrog maven repo') {
            steps {
                script {
                    sh "sudo chmod -R 755 ./ops"
                    sh "./ops/jenkins/delete-nightly-snapshot.sh"
                    sh "./ops/jenkins/publish_artifacts.sh"
                }
            }
        }
        stage('Slack Message') {
            when { expression { params.SLACK != "" } }
            steps {
                script {
                    notifyBuild("Success :discodancer:", "GREEN", params)
                }
            }
        }
    }
    post {
        failure {
            script {
                if (params.SLACK != "") {
                    notifyBuild("Failed :computerrage:", "RED", params)
                }
            }
        }
    }
}