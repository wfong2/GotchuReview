import SwiftUI
import PhotosUI

struct CameraView: View {
    @ObservedObject var viewModel: ScanViewModel
    @State private var showCamera = false
    @State private var showPhotoPicker = false
    @State private var selectedPhoto: PhotosPickerItem?

    var body: some View {
        VStack(spacing: 24) {
            Spacer()

            Image(systemName: "doc.text.viewfinder")
                .font(.system(size: 72))
                .foregroundColor(.blue)

            Text(NSLocalizedString("scan.instruction", comment: ""))
                .font(.title3)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 32)

            Text(NSLocalizedString("scan.alignInvoice", comment: ""))
                .font(.subheadline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 32)

            Spacer()

            // Capture button
            Button {
                showCamera = true
            } label: {
                Label(
                    NSLocalizedString("scan.capture", comment: ""),
                    systemImage: "camera.fill"
                )
                .font(.headline)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .cornerRadius(14)
            }
            .padding(.horizontal, 32)

            // Photo library option
            PhotosPicker(
                selection: $selectedPhoto,
                matching: .images
            ) {
                Text(NSLocalizedString("scan.fromGallery", comment: ""))
                    .font(.subheadline)
                    .foregroundColor(.blue)
            }
            .onChange(of: selectedPhoto) { _, newItem in
                guard let item = newItem else { return }
                Task {
                    if let data = try? await item.loadTransferable(type: Data.self) {
                        if let image = UIImage(data: data) {
                            viewModel.capturedImage = image
                            if let jpegData = image.jpegData(compressionQuality: 0.8) {
                                await viewModel.extractInvoice(imageData: jpegData)
                            }
                        }
                    }
                }
            }

            Spacer().frame(height: 20)
        }
        .fullScreenCover(isPresented: $showCamera) {
            ImagePickerView { image in
                viewModel.capturedImage = image
                if let jpegData = image.jpegData(compressionQuality: 0.8) {
                    Task {
                        await viewModel.extractInvoice(imageData: jpegData)
                    }
                }
            }
        }
    }
}

struct ImagePickerView: UIViewControllerRepresentable {
    let onImageCaptured: (UIImage) -> Void
    @Environment(\.dismiss) private var dismiss

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.sourceType = .camera
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        let parent: ImagePickerView

        init(_ parent: ImagePickerView) {
            self.parent = parent
        }

        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
            if let image = info[.originalImage] as? UIImage {
                parent.onImageCaptured(image)
            }
            parent.dismiss()
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            parent.dismiss()
        }
    }
}
