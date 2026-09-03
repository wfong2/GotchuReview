import Foundation

struct VendorTemplate: Codable {
    let vendorNameNormalized: String
    let fieldLabels: [String]
    let sectionOrder: [String]
    let invoiceNumberFormat: String
    let logoPosition: String
    let fontCategory: String
    let dominantColors: [String]
    let tableStyle: String
}

struct ExtractedInvoice: Codable {
    let contractorName: String
    let businessName: String
    let totalAmount: Double
    let currency: String
    let laborCost: Double?
    let materialsCost: Double?
    let hourlyRate: Double?
    let projectDuration: String?
    let invoiceDate: String?
    let description: String
    let lineItems: [LineItem]
    let contractorPhone: String?
    let contractorEmail: String?
    let contractorAddress: String?
    let zipCode: String?
    let vendorTemplate: VendorTemplate?
}

struct LineItem: Codable {
    let description: String
    let amount: Double
    let category: String
}

struct ExtractionResponse: Codable {
    let extracted: ExtractedInvoice
    let documentHash: String
    let vendorFingerprint: String?
    let vendorTemplate: VendorTemplate?
    let contractorMatches: [ContractorMatch]
    let estimatedBreakdown: Bool
}

struct HistoryResponse: Codable {
    let history: [String: [HistoryInvoice]]
    let summary: HistorySummary
}

struct HistoryInvoice: Codable, Identifiable {
    let id: String
    let totalAmount: Double
    let description: String
    let invoiceDate: String?
    let createdAt: String
    let contractor: ContractorBrief
    let review: ReviewBrief?
}

struct ContractorBrief: Codable {
    let id: String
    let name: String
    let businessName: String
    let category: String
    let city: String?
    let state: String?
}

struct ReviewBrief: Codable {
    let id: String
    let overallRating: Double
    let title: String
}

struct HistorySummary: Codable {
    let totalInvoices: Int
    let totalSpent: Double
    let contractorCount: Int
}

struct VendorHistoryResponse: Codable {
    let vendors: [VendorGroup]
    let summary: HistorySummary
}

struct VendorGroup: Codable, Identifiable {
    let contractor: ContractorBrief
    let invoiceCount: Int
    let totalSpent: Double
    let latestInvoiceDate: String
    let invoices: [DetailedInvoice]
    var id: String { contractor.id }
}

struct DetailedInvoice: Codable, Identifiable {
    let id: String
    let documentHash: String
    let totalAmount: Double
    let currency: String
    let laborCost: Double?
    let materialsCost: Double?
    let hourlyRate: Double?
    let projectDuration: String?
    let invoiceDate: String?
    let description: String
    let lineItems: [LineItem]
    let createdAt: String
    let contractor: ContractorBrief
    let review: ReviewBrief?
}
