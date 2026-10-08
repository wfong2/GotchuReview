import SwiftUI

struct MainTabView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @State private var selectedTab = 0
    @State private var showSignIn = false

    var body: some View {
        TabView(selection: $selectedTab) {
            ExploreView()
                .tabItem {
                    Label(
                        NSLocalizedString("tab.explore", comment: "Explore tab"),
                        systemImage: "magnifyingglass"
                    )
                }
                .tag(0)

            Group {
                if authViewModel.isSignedIn {
                    ScanView()
                } else {
                    SignInPromptView(
                        icon: "camera.fill",
                        title: "Sign in to scan invoices",
                        message: "Create an account to scan invoices, submit reviews, and earn credits.",
                        showSignIn: $showSignIn
                    )
                }
            }
            .tabItem {
                Label(
                    NSLocalizedString("tab.scan", comment: "Scan tab"),
                    systemImage: "camera.fill"
                )
            }
            .tag(1)

            Group {
                if authViewModel.isSignedIn {
                    HistoryView()
                } else {
                    SignInPromptView(
                        icon: "clock.fill",
                        title: "Sign in to view history",
                        message: "Create an account to track your invoices and spending history.",
                        showSignIn: $showSignIn
                    )
                }
            }
            .tabItem {
                Label(
                    NSLocalizedString("tab.history", comment: "History tab"),
                    systemImage: "clock.fill"
                )
            }
            .tag(2)
        }
        .tint(.blue)
        .sheet(isPresented: $showSignIn) {
            OnboardingView(showGuestOption: false)
                .environmentObject(authViewModel)
        }
        .onChange(of: authViewModel.isSignedIn) { _, signedIn in
            if signedIn {
                showSignIn = false
            }
        }
    }
}

struct SignInPromptView: View {
    let icon: String
    let title: String
    let message: String
    @Binding var showSignIn: Bool

    var body: some View {
        VStack(spacing: 20) {
            Spacer()

            Image(systemName: icon)
                .font(.system(size: 56))
                .foregroundColor(.secondary)

            Text(title)
                .font(.title3)
                .fontWeight(.semibold)

            Text(message)
                .font(.subheadline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 40)

            Button {
                showSignIn = true
            } label: {
                Text("Sign In")
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .cornerRadius(14)
            }
            .padding(.horizontal, 32)

            Spacer()
        }
    }
}
