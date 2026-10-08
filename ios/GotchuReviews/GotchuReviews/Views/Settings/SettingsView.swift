import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @Environment(\.dismiss) private var dismiss
    @State private var showSignIn = false
    @State private var showDeleteConfirmation = false
    @State private var isDeleting = false

    var body: some View {
        NavigationStack {
            List {
                // Account
                Section(NSLocalizedString("settings.account", comment: "")) {
                    if let user = authViewModel.currentUser {
                        HStack {
                            Text(NSLocalizedString("settings.email", comment: ""))
                            Spacer()
                            Text(user.googleEmail)
                                .foregroundColor(.secondary)
                        }
                        HStack {
                            Text(NSLocalizedString("settings.credits", comment: ""))
                            Spacer()
                            Text("\(user.creditBalance)")
                                .foregroundColor(.secondary)
                        }
                    } else {
                        HStack {
                            Text(NSLocalizedString("settings.notSignedIn", comment: ""))
                                .foregroundColor(.secondary)
                            Spacer()
                            Button("Sign In") {
                                showSignIn = true
                            }
                        }
                    }
                }

                // Notifications
                if authViewModel.isSignedIn {
                    Section(NSLocalizedString("settings.notifications", comment: "")) {
                        Toggle(
                            NSLocalizedString("settings.notifyNewReview", comment: ""),
                            isOn: .constant(true)
                        )
                        Toggle(
                            NSLocalizedString("settings.notifyPriceUpdate", comment: ""),
                            isOn: .constant(true)
                        )
                        Toggle(
                            NSLocalizedString("settings.notifyInvoice", comment: ""),
                            isOn: .constant(true)
                        )
                        Toggle(
                            NSLocalizedString("settings.notifyDraft", comment: ""),
                            isOn: .constant(true)
                        )
                    }
                }

                // Actions
                Section {
                    if authViewModel.isSignedIn {
                        Button(role: .destructive) {
                            showDeleteConfirmation = true
                        } label: {
                            if isDeleting {
                                HStack {
                                    Text("Deleting Account…")
                                    Spacer()
                                    ProgressView()
                                }
                            } else {
                                Text("Delete Account")
                            }
                        }
                        .disabled(isDeleting)

                        Button(role: .destructive) {
                            authViewModel.signOut()
                            dismiss()
                        } label: {
                            Text(NSLocalizedString("settings.signOut", comment: ""))
                        }
                    }
                }

                // About
                Section(NSLocalizedString("settings.about", comment: "")) {
                    HStack {
                        Text(NSLocalizedString("settings.version", comment: ""))
                        Spacer()
                        Text("1.0.0")
                            .foregroundColor(.secondary)
                    }
                }
            }
            .navigationTitle(NSLocalizedString("settings.title", comment: ""))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button(NSLocalizedString("common.done", comment: "")) {
                        dismiss()
                    }
                }
            }
            .sheet(isPresented: $showSignIn) {
                OnboardingView(showGuestOption: false)
                    .environmentObject(authViewModel)
            }
            .onChange(of: authViewModel.isSignedIn) { _, signedIn in
                if signedIn {
                    showSignIn = false
                }
            }
            .alert("Delete Account", isPresented: $showDeleteConfirmation) {
                Button("Cancel", role: .cancel) { }
                Button("Delete", role: .destructive) {
                    Task {
                        await deleteAccount()
                    }
                }
            } message: {
                Text("Are you sure you want to delete your account? This action cannot be undone. All your data, reviews, and credits will be permanently removed.")
            }
        }
    }

    private func deleteAccount() async {
        isDeleting = true
        do {
            try await APIClient.shared.deleteAccount()
            await MainActor.run {
                authViewModel.signOut()
                isDeleting = false
                dismiss()
            }
        } catch {
            await MainActor.run {
                isDeleting = false
                authViewModel.errorMessage = error.localizedDescription
            }
        }
    }
}
