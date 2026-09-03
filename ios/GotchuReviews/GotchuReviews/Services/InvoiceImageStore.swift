import UIKit

class InvoiceImageStore {
    static let shared = InvoiceImageStore()

    private let directory: URL

    private init() {
        let docs = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        directory = docs.appendingPathComponent("InvoiceImages", isDirectory: true)
        try? FileManager.default.createDirectory(at: directory, withIntermediateDirectories: true)
    }

    private func fileURL(for documentHash: String) -> URL {
        let sanitized = documentHash.replacingOccurrences(of: ":", with: "_")
        return directory.appendingPathComponent("\(sanitized).jpg")
    }

    func save(imageData: Data, documentHash: String) {
        let url = fileURL(for: documentHash)
        try? imageData.write(to: url)
    }

    func loadImage(documentHash: String) -> UIImage? {
        let url = fileURL(for: documentHash)
        guard let data = try? Data(contentsOf: url) else { return nil }
        return UIImage(data: data)
    }

    func thumbnail(documentHash: String, maxSize: CGFloat = 60) -> UIImage? {
        guard let image = loadImage(documentHash: documentHash) else { return nil }
        let scale = maxSize / max(image.size.width, image.size.height)
        if scale >= 1 { return image }
        let newSize = CGSize(width: image.size.width * scale, height: image.size.height * scale)
        let renderer = UIGraphicsImageRenderer(size: newSize)
        return renderer.image { _ in
            image.draw(in: CGRect(origin: .zero, size: newSize))
        }
    }

    func hasImage(for documentHash: String) -> Bool {
        FileManager.default.fileExists(atPath: fileURL(for: documentHash).path)
    }
}
