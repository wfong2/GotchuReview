import SwiftUI

@MainActor
class ContractorDetailViewModel: ObservableObject {
    @Published var contractor: Contractor?
    @Published var reviews: [Review] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    func load(contractorId: String) async {
        isLoading = true
        errorMessage = nil

        do {
            let response = try await APIClient.shared.getContractor(id: contractorId)
            contractor = response.contractor
            reviews = response.reviews
        } catch {
            errorMessage = error.localizedDescription
        }

        isLoading = false
    }
}
