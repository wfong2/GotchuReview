import SwiftUI

@main
struct GotchuReviewsApp: App {
    @StateObject private var authViewModel = AuthViewModel()

    var body: some Scene {
        WindowGroup {
            if authViewModel.hasCompletedOnboarding {
                MainTabView()
                    .environmentObject(authViewModel)
                    .task {
                        #if DEBUG
                        if !authViewModel.isSignedIn {
                            authViewModel.devSignIn()
                        }
                        #endif
                    }
            } else {
                OnboardingView()
                    .environmentObject(authViewModel)
            }
        }
    }
}
