import Foundation

struct Contractor: Codable, Identifiable {
    let id: String
    let name: String
    let businessName: String
    let category: String
    let phone: String
    let email: String
    let website: String
    let city: String
    let state: String
    let zipCode: String
    let country: String
    let ratingOverall: Double
    let ratingQuality: Double
    let ratingCommunication: Double
    let ratingTimeliness: Double
    let ratingValue: Double
    let reviewCount: Int
    let pricingCurrency: String
    let pricingMin: Double
    let pricingMax: Double
    let pricingMedian: Double
    let pricingLaborMedian: Double
    let pricingMaterialsMedian: Double
    let pricingLaborRatio: Double

    var locationDisplay: String {
        [city, state].filter { !$0.isEmpty }.joined(separator: ", ")
    }

    var priceRangeDisplay: String {
        guard pricingMin > 0 && pricingMax > 0 else { return "" }
        return "$\(Int(pricingMin)) – $\(Int(pricingMax))"
    }
}

struct ContractorMatch: Codable {
    let contractor: Contractor
    let confidence: Int
}
