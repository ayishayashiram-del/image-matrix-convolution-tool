🖼️ Image and Matrix Convolution Tool

A Java-based desktop application that applies image-processing filters using 3 × 3 convolution matrices (kernels). The application allows users to load images, apply predefined filters, customize kernel values, preview the results, and save processed images as PNG files.

📌 Project Overview

Image convolution is a fundamental technique in digital image processing. It applies a mathematical kernel to the pixels of an image to produce effects such as blurring, sharpening, edge detection, and embossing.

This project demonstrates how convolution works using Java and provides a graphical user interface for interactive image processing.

✨ Features

- Image Upload: Load images from your computer.
- Image Preview: View the original and processed images side by side.
- Blur Filter: Smooths image details.
- Sharpen Filter: Enhances edges and image details.
- Edge Detection: Highlights boundaries and transitions.
- Emboss Filter: Creates a raised or engraved visual effect.
- Identity Filter: Preserves the original pixel values.
- Custom Convolution Matrix: Enter your own 3 × 3 kernel values.
- Background Processing: Uses "SwingWorker" to keep the interface responsive during processing.
- PNG Export: Save the processed image to your computer.

🛠️ Technologies Used

- Programming Language: Java
- GUI Framework: Java Swing
- Image Processing: Java AWT and "BufferedImage"
- Image File Handling: "ImageIO"
- Development Environment: Visual Studio Code
- Version Control: Git and GitHub

📂 Project Structure

ImageMatrixConvolutionTool/
│
├── ImageMatrixConvolutionTool.java
└── README.md

⚙️ Requirements

- Java Development Kit (JDK) 17 or newer
- Visual Studio Code or another Java-compatible editor
- A computer that supports Java desktop GUI applications

Note: Online Java compilers that run in headless environments may not support the Swing graphical interface.

🚀 How to Run

1. Check Java installation

Open the terminal and execute:

java -version
javac -version

Both commands should display the installed Java version.

2. Compile the program

Open the terminal in the project folder and execute:

javac ImageMatrixConvolutionTool.java

3. Run the application

java ImageMatrixConvolutionTool

The application window should open on your desktop.

🧮 Convolution Kernels

The application includes the following predefined kernels.

1. Identity Kernel

0  0  0
0  1  0
0  0  0

Preserves the original image's pixel values.

2. Blur Kernel

1  2  1
2  4  2
1  2  1

Smooths the image using a weighted average of neighboring pixels.

3. Sharpen Kernel

 0  -1   0
-1   5  -1
 0  -1   0

Enhances image details and emphasizes edges.

4. Edge Detection Kernel

-1  -1  -1
-1   8  -1
-1  -1  -1

Highlights areas with strong intensity changes.

5. Emboss Kernel

-2  -1   0
-1   1   1
 0   1   2

Produces an embossed appearance by emphasizing directional intensity changes.

🔬 Working Principle

1. The user selects an image from the computer.
2. The image is loaded into a "BufferedImage" object.
3. The user selects a predefined filter or enters a custom 3 × 3 kernel.
4. The convolution algorithm examines each pixel and its neighboring pixels.
5. Kernel coefficients are multiplied by the corresponding pixel-channel values.
6. The results are normalized where appropriate and clamped to the valid 0–255 intensity range.
7. The processed pixels are stored in a new image.
8. The output is displayed and can be saved as a PNG file.

📐 Mathematical Representation

For an image (I) and a convolution kernel (K), the output pixel is calculated as:

[
O(x,y)=\sum_{i=-1}^{1}\sum_{j=-1}^{1}
K(i,j),I(x+i,y+j)
]

For kernels whose coefficient sum is nonzero, this implementation divides the accumulated channel values by the sum of the kernel coefficients. Zero-sum kernels are applied without that normalization.

The implementation uses edge-pixel replication for pixels near the image boundary.

🎯 Applications

- Digital image processing
- Computer vision fundamentals
- Image enhancement
- Edge and feature detection
- Image filtering and smoothing
- Learning matrix operations in practical applications

🔮 Future Enhancements

- Support for larger convolution kernels such as 5 × 5 and 7 × 7
- Grayscale conversion and thresholding
- Undo and redo operations
- Adjustable filter strength
- Image comparison and processing statistics
- Support for batch image processing
- Parallel processing for improved performance on large images

👩‍💻 Author

Ayisha yashira M

Electronics and Communication Engineering (ECE) Student

