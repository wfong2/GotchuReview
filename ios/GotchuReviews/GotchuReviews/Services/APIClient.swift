import Foundation

enum APIError: Error, LocalizedError {
    case invalidURL
    case unauthorized
    case serverError(String)
    case decodingError
    case networkError(Error)
    case duplicateInvoice

    var errorDescription: String? {
        switch self {
        case .invalidURL: return NSLocalizedString("error.invalidURL", comment: "")
        case .unauthorized: return NSLocalizedString("error.unauthorized", comment: "")
        case .serverError(let msg): return msg
        case .decodingError: return NSLocalizedString("error.decodingError", comment: "")
        case .networkError(let err): return err.localizedDescription
        case .duplicateInvoice: return NSLocalizedString("error.duplicateInvoice", comment: "")
        }
    }
}

class APIClient {
    static let shared = APIClient()

    // Local dev: "http://192.168.1.243:3000/api/v1"
    private let baseURL = "https://xduzpwxmsp.us-east-1.awsapprunner.com/api/v1"

    var authToken: String?

    private let decoder: JSONDecoder = {
        let d = JSONDecoder()
        return d
    }()

    private func makeRequest(_ path: String, method: String = "GET", body: Data? = nil, contentType: String = "application/json") -> URLRequest? {
        guard let url = URL(string: "\(baseURL)\(path)") else { return nil }
        var request = URLRequest(url: url)
        request.httpMethod = method
        request.setValue(contentType, forHTTPHeaderField: "Content-Type")
        if let token = authToken {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        request.httpBody = body
        return request
    }

    private func perform<T: Decodable>(_ request: URLRequest) async throws -> T {
        let (data, response) = try await URLSession.shared.data(for: request)

        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.networkError(URLError(.badServerResponse))
        }

        switch httpResponse.statusCode {
        case 200...201:
            do {
                return try decoder.decode(T.self, from: data)
            } catch {
                let preview = String(data: data.prefix(500), encoding: .utf8) ?? "n/a"
                NSLog("[APIClient] Decoding error for %@: %@ | Response: %@", "\(T.self)", "\(error)", preview)
                throw APIError.decodingError
            }
        case 401:
            throw APIError.unauthorized
        case 409:
            throw APIError.duplicateInvoice
        default:
            let errorBody = try? JSONDecoder().decode([String: String].self, from: data)
            throw APIError.serverError(errorBody?["error"] ?? "Unknown error")
        }
    }

    // MARK: - Auth

    struct AuthResponse: Codable {
        let user: AppUser
    }

    func signInWithGoogle(idToken: String) async throws -> AppUser {
        let body = try JSONEncoder().encode(["idToken": idToken])
        guard let request = makeRequest("/auth/google", method: "POST", body: body) else {
            throw APIError.invalidURL
        }
        let response: AuthResponse = try await perform(request)
        return response.user
    }

    // MARK: - Contractors

    struct ContractorListResponse: Codable {
        let contractors: [Contractor]
        let total: Int
    }

    struct ContractorDetailResponse: Codable {
        let contractor: Contractor
        let reviews: [Review]
    }

    func searchContractors(query: String? = nil, category: String? = nil, zipCode: String? = nil, limit: Int = 20, offset: Int = 0) async throws -> ContractorListResponse {
        var params: [String] = ["limit=\(limit)", "offset=\(offset)"]
        if let q = query, !q.isEmpty { params.append("q=\(q.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? q)") }
        if let cat = category { params.append("category=\(cat)") }
        if let zip = zipCode { params.append("zipCode=\(zip)") }

        let queryString = params.joined(separator: "&")
        guard let request = makeRequest("/contractors?\(queryString)") else {
            throw APIError.invalidURL
        }
        return try await perform(request)
    }

    func getContractor(id: String) async throws -> ContractorDetailResponse {
        guard let request = makeRequest("/contractors/\(id)") else {
            throw APIError.invalidURL
        }
        return try await perform(request)
    }

    // MARK: - Invoice Extraction

    func extractInvoice(imageData: Data, mimeType: String = "image/jpeg") async throws -> ExtractionResponse {
        let boundary = UUID().uuidString
        let filename = mimeType == "application/pdf" ? "invoice.pdf" : "invoice.jpg"
        var body = Data()

        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"image\"; filename=\"\(filename)\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: \(mimeType)\r\n\r\n".data(using: .utf8)!)
        body.append(imageData)
        body.append("\r\n--\(boundary)--\r\n".data(using: .utf8)!)

        guard let request = makeRequest("/invoices/extract", method: "POST", body: body, contentType: "multipart/form-data; boundary=\(boundary)") else {
            throw APIError.invalidURL
        }

        return try await perform(request)
    }

    // MARK: - Reviews

    struct ReviewSubmission: Codable {
        let contractorId: String?
        let newContractor: NewContractor?
        let invoiceData: InvoiceData
        let documentHash: String
        let ratings: Ratings
        let title: String
        let body: String
        let workType: String
        let estimatedBreakdown: Bool
    }

    struct NewContractor: Codable {
        let name: String
        let businessName: String?
        let category: String
        let phone: String?
        let email: String?
        let city: String?
        let state: String?
        let zipCode: String?
    }

    struct InvoiceData: Codable {
        let totalAmount: Double
        let currency: String
        let laborCost: Double?
        let materialsCost: Double?
        let hourlyRate: Double?
        let projectDuration: String?
        let invoiceDate: String?
        let description: String
        let zipCode: String?
        let lineItems: [LineItem]
    }

    struct Ratings: Codable {
        let quality: Int
        let communication: Int
        let timeliness: Int
        let value: Int
    }

    struct ReviewResponse: Codable {
        let review: Review
        let creditBalance: Int
    }

    func submitReview(_ submission: ReviewSubmission) async throws -> ReviewResponse {
        let body = try JSONEncoder().encode(submission)
        guard let request = makeRequest("/reviews", method: "POST", body: body) else {
            throw APIError.invalidURL
        }
        return try await perform(request)
    }

    // MARK: - User

    func getCurrentUser() async throws -> AppUser {
        guard let request = makeRequest("/users/me") else {
            throw APIError.invalidURL
        }
        let response: AuthResponse = try await perform(request)
        return response.user
    }

    func getHistory() async throws -> HistoryResponse {
        guard let request = makeRequest("/users/me/history") else {
            throw APIError.invalidURL
        }
        return try await perform(request)
    }

    func getVendorHistory() async throws -> VendorHistoryResponse {
        guard let request = makeRequest("/users/me/history?groupBy=vendor") else {
            throw APIError.invalidURL
        }
        return try await perform(request)
    }

    // MARK: - Credits

    struct BalanceResponse: Codable {
        let balance: Int
    }

    func getCreditBalance() async throws -> Int {
        guard let request = makeRequest("/credits/balance") else {
            throw APIError.invalidURL
        }
        let response: BalanceResponse = try await perform(request)
        return response.balance
    }
}
