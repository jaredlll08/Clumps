#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        jdk "jdk-17.0.1"
    }
    environment {
        modrinth_token = credentials('modrinth_token')
        curseforgeApiToken = credentials('curseforge_token')
        discordCFWebhook = credentials('discord_cf_webhook')
        versionTrackerKey = credentials('version_tracker_key')
        versionTrackerAPI = credentials('version_tracker_api')
    }
    stages {
        stage('Clean') {
            steps {
                echo 'Cleaning Project'
                sh 'chmod +x gradlew'
                sh './gradlew clean'
            }
        }
        stage('Build') {
            steps {
                echo 'Building'
                sh './gradlew build'
            }
        }

        stage('Publish') {
            steps {
                echo 'Updating version'
                sh './gradlew updateVersionTracker'

                echo 'Deploying to Maven'
                sh './gradlew publish'

                echo 'Deploying to CurseForge'
                sh './gradlew publishCurseForge modrinth postDiscord'
            }
        }
    }
}
