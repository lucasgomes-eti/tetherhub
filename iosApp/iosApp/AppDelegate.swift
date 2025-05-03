//
//  AppDelegate.swift
//  iosApp
//
//  Created by Lucas Gomes on 14/04/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import FirebaseCore
import FirebaseMessaging
import ComposeApp

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseApp.configure()
        
        NotifierManager.shared.initialize(
            configuration: NotificationPlatformConfigurationIos(
                showPushNotification: true,
                askNotificationPermissionOnStart: false,
                notificationSoundName: nil
            )
        )
        
        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
}
