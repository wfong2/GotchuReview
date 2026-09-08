import SwiftUI
import AuthenticationServices
import CryptoKit

@MainActor
class AuthViewModel: ObservableObject {
    @Published var currentUser: AppUser?
    @Published var isSignedIn = false
    @Published var hasCompletedOnboarding: Bool
    @Published var isLoading = false
    @Published var errorMessage: String?

    private let tokenKey = "authToken"
    private let refreshTokenKey = "refreshToken"
    private let userKey = "cachedUser"

    private let googleClientID = "689036353712-1do1c716605eh0ivss1dauter73v6mjc.apps.googleusercontent.com"
    private let redirectScheme = "com.googleusercontent.apps.689036353712-1do1c716605eh0ivss1dauter73v6mjc"
    private let firebaseAPIKey = "AIzaSyAqGLmvZy2agbEX9UGnGuDeQo05c6m0c3g"

    init() {
        self.hasCompletedOnboarding = UserDefaults.standard.bool(forKey: "hasCompletedOnboarding")
        restoreSession()
    }

    func completeOnboarding() {
        hasCompletedOnboarding = true
        UserDefaults.standard.set(true, forKey: "hasCompletedOnboarding")
    }

    // MARK: - Google Sign-In via ASWebAuthenticationSession

    func signInWithGoogle() {
        isLoading = true
        errorMessage = nil

        let nonce = UUID().uuidString
        let redirectURI = "\(redirectScheme):/oauthredirect"
        let scope = "openid email profile"

        var components = URLComponents(string: "https://accounts.google.com/o/oauth2/v2/auth")!
        components.queryItems = [
            URLQueryItem(name: "client_id", value: googleClientID),
            URLQueryItem(name: "redirect_uri", value: redirectURI),
            URLQueryItem(name: "response_type", value: "code"),
            URLQueryItem(name: "scope", value: scope),
            URLQueryItem(name: "nonce", value: nonce),
            URLQueryItem(name: "prompt", value: "select_account"),
        ]

        guard let authURL = components.url else {
            errorMessage = "Failed to build auth URL"
            isLoading = false
            return
        }

        let session = ASWebAuthenticationSession(url: authURL, callbackURLScheme: redirectScheme) { [weak self] callbackURL, error in
            guard let self else { return }

            Task { @MainActor in
                if let error {
                    if (error as NSError).code != ASWebAuthenticationSessionError.canceledLogin.rawValue {
                        self.errorMessage = error.localizedDescription
                    }
                    self.isLoading = false
                    return
                }

                guard let callbackURL,
                      let code = URLComponents(url: callbackURL, resolvingAgainstBaseURL: false)?
                        .queryItems?.first(where: { $0.name == "code" })?.value else {
                    self.errorMessage = "Failed to get authorization code"
                    self.isLoading = false
                    return
                }

                await self.exchangeCodeForTokens(code: code, redirectURI: redirectURI)
            }
        }

        session.presentationContextProvider = WebAuthContextProvider.shared
        session.prefersEphemeralWebBrowserSession = false
        session.start()
    }

    /// Exchange authorization code for Google tokens, then for Firebase token
    private func exchangeCodeForTokens(code: String, redirectURI: String) async {
        // Step 1: Exchange auth code for Google tokens
        let tokenURL = URL(string: "https://oauth2.googleapis.com/token")!
        let body = "code=\(code)&client_id=\(googleClientID)&redirect_uri=\(redirectURI)&grant_type=authorization_code"

        var tokenRequest = URLRequest(url: tokenURL)
        tokenRequest.httpMethod = "POST"
        tokenRequest.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        tokenRequest.httpBody = body.data(using: .utf8)

        do {
            let (data, _) = try await URLSession.shared.data(for: tokenRequest)
            guard let json = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let googleIdToken = json["id_token"] as? String else {
                errorMessage = "Failed to exchange code for tokens"
                isLoading = false
                return
            }

            // Step 2: Exchange Google ID token for Firebase ID token
            await exchangeGoogleTokenForFirebase(googleIdToken: googleIdToken)
        } catch {
            errorMessage = error.localizedDescription
            isLoading = false
        }
    }

    /// Exchange Google ID token for Firebase ID token via REST API
    private func exchangeGoogleTokenForFirebase(googleIdToken: String) async {
        let urlString = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=\(firebaseAPIKey)"
        guard let url = URL(string: urlString) else {
            errorMessage = "Invalid Firebase URL"
            isLoading = false
            return
        }

        let body: [String: Any] = [
            "postBody": "id_token=\(googleIdToken)&providerId=google.com",
            "requestUri": "http://localhost",
            "returnIdpCredential": true,
            "returnSecureToken": true
        ]

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try? JSONSerialization.data(withJSONObject: body)

        do {
            let (data, _) = try await URLSession.shared.data(for: request)
            guard let json = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let firebaseIdToken = json["idToken"] as? String,
                  let refreshToken = json["refreshToken"] as? String else {
                errorMessage = "Failed to get Firebase token"
                isLoading = false
                return
            }

            // Step 3: Send Firebase ID token to our backend
            let user = try await APIClient.shared.signInWithGoogle(idToken: firebaseIdToken)
            APIClient.shared.authToken = firebaseIdToken

            // Persist
            UserDefaults.standard.set(firebaseIdToken, forKey: tokenKey)
            UserDefaults.standard.set(refreshToken, forKey: refreshTokenKey)
            if let userData = try? JSONEncoder().encode(user) {
                UserDefaults.standard.set(userData, forKey: userKey)
            }

            currentUser = user
            isSignedIn = true
        } catch {
            errorMessage = error.localizedDescription
        }

        isLoading = false
    }

    // MARK: - Session Persistence

    private func restoreSession() {
        guard let token = UserDefaults.standard.string(forKey: tokenKey) else { return }

        APIClient.shared.authToken = token

        if let userData = UserDefaults.standard.data(forKey: userKey),
           let user = try? JSONDecoder().decode(AppUser.self, from: userData) {
            currentUser = user
            isSignedIn = true
        }

        Task {
            await refreshToken()
        }
    }

    private func refreshToken() async {
        guard let refreshToken = UserDefaults.standard.string(forKey: refreshTokenKey) else {
            return
        }

        let urlString = "https://securetoken.googleapis.com/v1/token?key=\(firebaseAPIKey)"
        guard let url = URL(string: urlString) else { return }

        let body = "grant_type=refresh_token&refresh_token=\(refreshToken)"

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        request.httpBody = body.data(using: .utf8)

        do {
            let (data, _) = try await URLSession.shared.data(for: request)
            guard let json = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let newIdToken = json["id_token"] as? String,
                  let newRefreshToken = json["refresh_token"] as? String else {
                signOut()
                return
            }

            APIClient.shared.authToken = newIdToken
            UserDefaults.standard.set(newIdToken, forKey: tokenKey)
            UserDefaults.standard.set(newRefreshToken, forKey: refreshTokenKey)

            let user = try await APIClient.shared.getCurrentUser()
            currentUser = user
            if let userData = try? JSONEncoder().encode(user) {
                UserDefaults.standard.set(userData, forKey: userKey)
            }
        } catch {
            signOut()
        }
    }

    // MARK: - Sign Out

    func signOut() {
        APIClient.shared.authToken = nil
        UserDefaults.standard.removeObject(forKey: tokenKey)
        UserDefaults.standard.removeObject(forKey: refreshTokenKey)
        UserDefaults.standard.removeObject(forKey: userKey)
        currentUser = nil
        isSignedIn = false
    }

    // MARK: - Refresh

    func refreshUser() async {
        guard isSignedIn else { return }
        do {
            currentUser = try await APIClient.shared.getCurrentUser()
        } catch {
            // Silently fail on refresh
        }
    }
}

// MARK: - ASWebAuthenticationSession context provider

class WebAuthContextProvider: NSObject, ASWebAuthenticationPresentationContextProviding {
    static let shared = WebAuthContextProvider()

    func presentationAnchor(for session: ASWebAuthenticationSession) -> ASPresentationAnchor {
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = scene.windows.first else {
            return ASPresentationAnchor()
        }
        return window
    }
}
