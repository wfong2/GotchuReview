import SwiftUI
import AuthenticationServices

struct OnboardingView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @Environment(\.dismiss) private var dismiss
    var showGuestOption: Bool = true

    var body: some View {
        VStack(spacing: 24) {
            Spacer()

            Image(systemName: "checkmark.shield.fill")
                .font(.system(size: 72))
                .foregroundColor(.blue)

            Text(NSLocalizedString("onboarding.title", comment: ""))
                .font(.largeTitle)
                .fontWeight(.bold)
                .multilineTextAlignment(.center)

            Text(NSLocalizedString("onboarding.subtitle", comment: ""))
                .font(.title3)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 32)

            VStack(alignment: .leading, spacing: 16) {
                OnboardingFeatureRow(
                    icon: "camera.fill",
                    text: NSLocalizedString("onboarding.feature1", comment: "")
                )
                OnboardingFeatureRow(
                    icon: "dollarsign.circle.fill",
                    text: NSLocalizedString("onboarding.feature2", comment: "")
                )
                OnboardingFeatureRow(
                    icon: "clock.fill",
                    text: NSLocalizedString("onboarding.feature3", comment: "")
                )
            }
            .padding(.horizontal, 40)

            Spacer()

            // Sign in with Apple button
            Button {
                authViewModel.signInWithApple()
            } label: {
                SignInWithAppleButton(.signIn) { _ in } onCompletion: { _ in }
                    .signInWithAppleButtonStyle(.black)
                    .frame(height: 50)
                    .allowsHitTesting(false)
            }
            .cornerRadius(14)
            .padding(.horizontal, 32)
            .disabled(authViewModel.isLoading)

            // Google Sign-In button
            Button {
                authViewModel.signInWithGoogle()
            } label: {
                HStack(spacing: 12) {
                    Image(systemName: "person.crop.circle.fill")
                        .font(.title3)
                    Text("Sign in with Google")
                        .font(.headline)
                }
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .cornerRadius(14)
            }
            .padding(.horizontal, 32)
            .disabled(authViewModel.isLoading)

            if authViewModel.isLoading {
                ProgressView()
            }

            if let error = authViewModel.errorMessage {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
            }

            if showGuestOption {
                Button {
                    authViewModel.completeOnboarding()
                } label: {
                    Text("Continue as Guest")
                        .font(.subheadline)
                        .foregroundColor(.blue)
                }
                .padding(.top, 4)
            }

            Spacer().frame(height: 20)
        }
        .onChange(of: authViewModel.isSignedIn) { _, signedIn in
            if signedIn {
                authViewModel.completeOnboarding()
                dismiss()
            }
        }
    }
}

struct OnboardingFeatureRow: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.title3)
                .foregroundColor(.blue)
                .frame(width: 32)
            Text(text)
                .font(.body)
        }
    }
}
