import SwiftUI

struct OnboardingView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @State private var currentPage = 0

    var body: some View {
        TabView(selection: $currentPage) {
            // Screen 1: Welcome
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

                Button {
                    withAnimation { currentPage = 1 }
                } label: {
                    Text(NSLocalizedString("onboarding.getStarted", comment: ""))
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .cornerRadius(14)
                }
                .padding(.horizontal, 32)
                .padding(.bottom, 40)
            }
            .tag(0)

            // Screen 2: Choose path
            VStack(spacing: 24) {
                Spacer()

                Text(NSLocalizedString("onboarding.whatToDo", comment: ""))
                    .font(.title2)
                    .fontWeight(.semibold)

                Button {
                    authViewModel.completeOnboarding()
                } label: {
                    VStack(spacing: 8) {
                        Image(systemName: "doc.text.viewfinder")
                            .font(.system(size: 36))
                        Text(NSLocalizedString("onboarding.haveInvoice", comment: ""))
                            .font(.headline)
                        Text(NSLocalizedString("onboarding.haveInvoiceDesc", comment: ""))
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 24)
                    .background(Color(.systemGray6))
                    .cornerRadius(16)
                }
                .buttonStyle(.plain)

                Button {
                    authViewModel.completeOnboarding()
                } label: {
                    VStack(spacing: 8) {
                        Image(systemName: "magnifyingglass")
                            .font(.system(size: 36))
                        Text(NSLocalizedString("onboarding.lookingFor", comment: ""))
                            .font(.headline)
                        Text(NSLocalizedString("onboarding.lookingForDesc", comment: ""))
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 24)
                    .background(Color(.systemGray6))
                    .cornerRadius(16)
                }
                .buttonStyle(.plain)

                Spacer()
            }
            .padding(.horizontal, 32)
            .tag(1)
        }
        .tabViewStyle(.page(indexDisplayMode: .never))
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
