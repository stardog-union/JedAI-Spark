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
        S3_LOCAL_HOSTNAME = "s3${env.BUILD_ID}${UUID.randomUUID().toString().replace("-", "")}"
        RHEL_CREDS = credentials('RHEL')
    }
    stages {
        stage('Setup env') {
            steps {
                script {
                    env.ARTIFACT_TAG = params.ARTIFACT_TAG.toLowerCase()
                    setJavaVersion()
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    // The default Java version in the image is 11, which should be used to build
                    printJavaVersion()
                    sh "sbt assembly"
                }
            }
        }
        stage('Publish jars and zip internally') {
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

def setJavaVersion() {
    echo "Setting Java version to Java 11"
    env.JAVA_HOME = "/usr/lib/jvm/java-11-openjdk-amd64/"
    sh "sudo update-java-alternatives --set java-1.11.0-openjdk-amd64"
    printJavaVersion()
}

def printJavaVersion() {
    echo "Checking Java version"
    echo "JAVA_HOME=${env.JAVA_HOME}"
    sh "java -version"
}