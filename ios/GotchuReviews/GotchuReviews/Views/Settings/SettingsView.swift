import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @Environment(\.dismiss) private var dismiss

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
                        Text(NSLocalizedString("settings.notSignedIn", comment: ""))
                            .foregroundColor(.secondary)
                    }
                }

                // Notifications
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

                // Actions
                Section {
                    if authViewModel.isSignedIn {
                        Button(role: .destructive) {
                            authViewModel.signOut()
                            authViewModel.hasCompletedOnboarding = false
                            UserDefaults.standard.set(false, forKey: "hasCompletedOnboarding")
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
        }
    }
}
