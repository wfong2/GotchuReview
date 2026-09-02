import Foundation

struct Review: Codable, Identifiable {
    let id: String
    let contractorId: String
    let userId: String
    let ratingQuality: Int
    let ratingCommunication: Int
    let ratingTimeliness: Int
    let ratingValue: Int
    let overallRating: Double
    let title: String
    let body: String
    let workType: String
    let isVerified: Bool
    let invoiceId: String?
    let createdAt: String
    let invoice: InvoiceSummary?
}

struct InvoiceSummary: Codable {
    let totalAmount: Double
    let laborCost: Double?
    let materialsCost: Double?
    let description: String
    let invoiceDate: String?
    let source: String
}
