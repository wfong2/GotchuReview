import SwiftUI

@main
struct GotchuReviewsApp: App {
    @StateObject private var authViewModel = AuthViewModel()

    var body: some Scene {
        WindowGroup {
            Group {
                if authViewModel.hasCompletedOnboarding {
                    MainTabView()
                        .environmentObject(authViewModel)
                } else {
                    OnboardingView()
                        .environmentObject(authViewModel)
                }
            }
        }
    }
}
