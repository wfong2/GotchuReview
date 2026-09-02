import Foundation

struct AppUser: Codable, Identifiable {
    let id: String
    let googleEmail: String
    let displayName: String
    let firebaseUid: String
    let creditBalance: Int
    let notifyNewReview: Bool
    let notifyPriceUpdate: Bool
    let notifyInvoiceProcessed: Bool
    let notifyDraftReminder: Bool
    let prefZipCode: String
    let prefCity: String
    let prefState: String
    let prefCountry: String
    let fcmToken: String?
    let createdAt: String
    let updatedAt: String
}
