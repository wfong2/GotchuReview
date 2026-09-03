import SwiftUI

@MainActor
class HistoryViewModel: ObservableObject {
    @Published var vendors: [VendorGroup] = []
    @Published var summary: HistorySummary?
    @Published var creditBalance = 0
    @Published var isLoading = false
    @Published var errorMessage: String?

    func load() async {
        isLoading = true
        errorMessage = nil

        do {
            let response = try await APIClient.shared.getVendorHistory()
            vendors = response.vendors
            summary = response.summary
            creditBalance = try await APIClient.shared.getCreditBalance()
        } catch {
            errorMessage = error.localizedDescription
        }

        isLoading = false
    }
}
