import SwiftUI

enum VendorSortOption {
    case recentFirst
    case name
}

@MainActor
class HistoryViewModel: ObservableObject {
    @Published var vendors: [VendorGroup] = []
    @Published var summary: HistorySummary?
    @Published var creditBalance = 0
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var sortOption: VendorSortOption = .recentFirst
    private var hasLoadedOnce = false

    var sortedVendors: [VendorGroup] {
        switch sortOption {
        case .recentFirst:
            return vendors.sorted { $0.latestInvoiceDate > $1.latestInvoiceDate }
        case .name:
            let displayName: (VendorGroup) -> String = {
                $0.contractor.businessName.isEmpty ? $0.contractor.name : $0.contractor.businessName
            }
            return vendors.sorted { displayName($0).localizedCaseInsensitiveCompare(displayName($1)) == .orderedAscending }
        }
    }

    func load() async {
        if !hasLoadedOnce {
            isLoading = true
        }
        errorMessage = nil

        do {
            let response = try await APIClient.shared.getVendorHistory()
            vendors = response.vendors
            summary = response.summary
            creditBalance = try await APIClient.shared.getCreditBalance()
            hasLoadedOnce = true
        } catch {
            NSLog("[HistoryVM] Load error: %@", "\(error)")
            errorMessage = error.localizedDescription
        }

        isLoading = false
    }
}
