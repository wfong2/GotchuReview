import SwiftUI

@MainActor
class HistoryViewModel: ObservableObject {
    @Published var history: [String: [HistoryInvoice]] = [:]
    @Published var summary: HistorySummary?
    @Published var creditBalance = 0
    @Published var isLoading = false
    @Published var errorMessage: String?

    var sortedYears: [String] {
        history.keys.sorted().reversed()
    }

    func load() async {
        isLoading = true
        errorMessage = nil

        do {
            let response = try await APIClient.shared.getHistory()
            history = response.history
            summary = response.summary
            creditBalance = try await APIClient.shared.getCreditBalance()
        } catch {
            errorMessage = error.localizedDescription
        }

        isLoading = false
    }
}
