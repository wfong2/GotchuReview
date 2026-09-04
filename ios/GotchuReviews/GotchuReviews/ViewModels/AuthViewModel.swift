import SwiftUI

@MainActor
class AuthViewModel: ObservableObject {
    @Published var currentUser: AppUser?
    @Published var isSignedIn = false
    @Published var hasCompletedOnboarding: Bool
    @Published var isLoading = false
    @Published var errorMessage: String?

    init() {
        self.hasCompletedOnboarding = UserDefaults.standard.bool(forKey: "hasCompletedOnboarding")
    }

    func completeOnboarding() {
        hasCompletedOnboarding = true
        UserDefaults.standard.set(true, forKey: "hasCompletedOnboarding")
    }

    func signInWithGoogle(idToken: String) async {
        isLoading = true
        errorMessage = nil

        do {
            let user = try await APIClient.shared.signInWithGoogle(idToken: idToken)
            APIClient.shared.authToken = idToken
            currentUser = user
            isSignedIn = true
        } catch {
            errorMessage = error.localizedDescription
        }

        isLoading = false
    }

    #if DEBUG
    func devSignIn() {
        isLoading = true
        errorMessage = nil

        Task {
            do {
                let response = try await APIClient.shared.devSignIn()
                APIClient.shared.authToken = response.token
                currentUser = response.user
                isSignedIn = true
            } catch {
                errorMessage = error.localizedDescription
            }
            isLoading = false
        }
    }
    #endif

    func signOut() {
        APIClient.shared.authToken = nil
        currentUser = nil
        isSignedIn = false
    }

    func refreshUser() async {
        guard isSignedIn else { return }
        do {
            currentUser = try await APIClient.shared.getCurrentUser()
        } catch {
            // Silently fail on refresh
        }
    }
}
