import SwiftUI

@MainActor
class ExploreViewModel: ObservableObject {
    @Published var contractors: [Contractor] = []
    @Published var searchText = ""
    @Published var selectedCategory: TradeCategory?
    @Published var locationText = ""
    @Published var zipCode = ""
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var totalResults = 0
    @Published var hasSearched = false

    func search() async {
        isLoading = true
        errorMessage = nil

        do {
            let response = try await APIClient.shared.searchContractors(
                query: searchText.isEmpty ? nil : searchText,
                category: selectedCategory?.rawValue,
                zipCode: zipCode.isEmpty ? nil : zipCode
            )
            contractors = response.contractors
            totalResults = response.total
            hasSearched = true
        } catch {
            errorMessage = error.localizedDescription
            hasSearched = true
        }

        isLoading = false
    }

    func selectCategory(_ category: TradeCategory) async {
        selectedCategory = category
        await search()
    }

    func loadNearby() async {
        isLoading = true
        errorMessage = nil

        do {
            let response = try await APIClient.shared.searchContractors(
                zipCode: zipCode.isEmpty ? nil : zipCode,
                limit: 10
            )
            contractors = response.contractors
            totalResults = response.total
        } catch {
            errorMessage = error.localizedDescription
        }

        isLoading = false
    }
}
