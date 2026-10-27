import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ImageMatrixConvolutionTool extends JFrame {

    private BufferedImage originalImage;
    private BufferedImage processedImage;

    private final ImagePanel originalPanel =
            new ImagePanel("Original Image");

    private final ImagePanel processedPanel =
            new ImagePanel("Processed Image");

    private final JComboBox<String> filterBox =
            new JComboBox<>(new String[]{
                    "Identity",
                    "Blur",
                    "Sharpen",
                    "Edge Detection",
                    "Emboss",
                    "Custom Matrix"
            });

    private final JTextArea matrixArea = new JTextArea(5, 25);
    private final JLabel statusLabel =
            new JLabel("Open an image to get started.");

    private final JButton applyButton =
            new JButton("Apply Convolution");

    private static final double[][] IDENTITY = {
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
    };

    private static final double[][] BLUR = {
            {1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}
    };

    private static final double[][] SHARPEN = {
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
    };

    private static final double[][] EDGE = {
            {-1, -1, -1},
            {-1,  8, -1},
            {-1, -1, -1}
    };

    private static final double[][] EMBOSS = {
            {-2, -1, 0},
            {-1,  1, 1},
            { 0,  1, 2}
    };

    public ImageMatrixConvolutionTool() {
        setTitle("Image and Matrix Convolution Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        buildInterface();
        updateMatrixFromFilter();
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        JLabel title = new JLabel(
                "IMAGE AND MATRIX CONVOLUTION TOOL",
                SwingConstants.CENTER
        );
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 12));
        centerPanel.add(originalPanel);
        centerPanel.add(processedPanel);
        root.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton openButton = new JButton("Open Image");
        JButton saveButton = new JButton("Save PNG");

        controls.add(openButton);
        controls.add(saveButton);
        controls.add(new JLabel("Filter:"));
        controls.add(filterBox);
        controls.add(applyButton);

        bottomPanel.add(controls, BorderLayout.NORTH);

        JPanel matrixPanel = new JPanel(new BorderLayout(5, 5));
        matrixPanel.setBorder(BorderFactory.createTitledBorder(
                "3 x 3 Convolution Kernel (edit the values if needed)"
        ));

        matrixArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        matrixArea.setText("0  -1   0\n-1   5  -1\n0  -1   0");
        matrixArea.setLineWrap(false);

        matrixPanel.add(new JScrollPane(matrixArea), BorderLayout.CENTER);

        JLabel helpLabel = new JLabel(
                "<html>Enter 3 rows with 3 numbers per row. " +
                "Example: 0 -1 0 / -1 5 -1 / 0 -1 0</html>"
        );
        matrixPanel.add(helpLabel, BorderLayout.SOUTH);

        bottomPanel.add(matrixPanel, BorderLayout.CENTER);

        statusLabel.setBorder(new EmptyBorder(5, 2, 0, 2));
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        root.add(bottomPanel, BorderLayout.SOUTH);

        openButton.addActionListener(e -> openImage());
        saveButton.addActionListener(e -> saveImage());
        filterBox.addActionListener(e -> updateMatrixFromFilter());
        applyButton.addActionListener(e -> applyConvolution());
    }

    private void updateMatrixFromFilter() {
        String selected = (String) filterBox.getSelectedItem();

        double[][] kernel;

        if ("Blur".equals(selected)) {
            kernel = BLUR;
        } else if ("Sharpen".equals(selected)) {
            kernel = SHARPEN;
        } else if ("Edge Detection".equals(selected)) {
            kernel = EDGE;
        } else if ("Emboss".equals(selected)) {
            kernel = EMBOSS;
        } else {
            kernel = IDENTITY;
        }

        // Keep the current matrix when Custom Matrix is selected.
        if ("Custom Matrix".equals(selected)) {
            return;
        }

        matrixArea.setText(formatMatrix(kernel));
    }

    private String formatMatrix(double[][] matrix) {
        StringBuilder builder = new StringBuilder();

        for (double[] row : matrix) {
            for (int c = 0; c < row.length; c++) {
                builder.append(String.format(Locale.US, "%7.2f", row[c]));

                if (c < row.length - 1) {
                    builder.append(" ");
                }
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    private double[][] readKernel() {
        String[] lines = matrixArea.getText().trim().split("\\R");

        if (lines.length != 3) {
            throw new IllegalArgumentException(
                    "The kernel must contain exactly 3 rows."
            );
        }

        double[][] kernel = new double[3][3];

        for (int r = 0; r < 3; r++) {
            String[] values = lines[r].trim().split("[\\s,]+");

            if (values.length != 3) {
                throw new IllegalArgumentException(
                        "Each kernel row must contain exactly 3 numbers."
                );
            }

            for (int c = 0; c < 3; c++) {
                kernel[r][c] = Double.parseDouble(values[c]);

                if (!Double.isFinite(kernel[r][c])) {
                    throw new IllegalArgumentException(
                            "Kernel values must be finite numbers."
                    );
                }
            }
        }

        return kernel;
    }

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select an image");

        int choice = chooser.showOpenDialog(this);

        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File file = chooser.getSelectedFile();
            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                throw new IOException(
                        "The selected file is not a supported image."
                );
            }

            originalImage = toArgb(image);
            processedImage = null;

            originalPanel.setImage(originalImage);
            processedPanel.setImage(null);

            statusLabel.setText(
                    "Loaded: " + file.getName()
                    + " | Size: " + image.getWidth()
                    + " x " + image.getHeight()
            );

        } catch (IOException ex) {
            showError("Could not open image: " + ex.getMessage());
        }
    }

    private void saveImage() {
        if (processedImage == null) {
            showError("Apply a convolution filter before saving.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save processed image");

        chooser.setSelectedFile(new File("convolved-image.png"));

        int choice = chooser.showSaveDialog(this);

        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".png")) {
            file = new File(file.getAbsolutePath() + ".png");
        }

        try {
            ImageIO.write(processedImage, "png", file);
            statusLabel.setText("Saved image: " + file.getAbsolutePath());
        } catch (IOException ex) {
            showError("Could not save image: " + ex.getMessage());
        }
    }

    private void applyConvolution() {
        if (originalImage == null) {
            showError("Please open an image first.");
            return;
        }

        final double[][] kernel;

        try {
            kernel = readKernel();
        } catch (IllegalArgumentException ex) {
            showError("Invalid kernel: " + ex.getMessage());
            return;
        }

        final BufferedImage input = originalImage;

        applyButton.setEnabled(false);
        statusLabel.setText("Processing image...");

        SwingWorker<BufferedImage, Void> worker =
                new SwingWorker<BufferedImage, Void>() {

            @Override
            protected BufferedImage doInBackground() {
                return convolve(input, kernel);
            }

            @Override
            protected void done() {
                try {
                    processedImage = get();
                    processedPanel.setImage(processedImage);

                    statusLabel.setText(
                            "Convolution completed successfully. "
                            + "Image size: "
                            + processedImage.getWidth() + " x "
                            + processedImage.getHeight()
                    );

                } catch (Exception ex) {
                    showError("Processing failed: " + ex.getMessage());
                } finally {
                    applyButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private static BufferedImage toArgb(BufferedImage source) {
        BufferedImage copy = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D graphics = copy.createGraphics();
        graphics.drawImage(source, 0, 0, null);
        graphics.dispose();

        return copy;
    }

    private static BufferedImage convolve(
            BufferedImage input, double[][] kernel) {

        int width = input.getWidth();
        int height = input.getHeight();

        BufferedImage output = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB
        );

        double kernelSum = 0.0;

        for (double[] row : kernel) {
            for (double value : row) {
                kernelSum += value;
            }
        }

        // A zero-sum kernel is used directly for edge detection.
        double divisor = Math.abs(kernelSum) < 1e-10
                ? 1.0 : kernelSum;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double red = 0;
                double green = 0;
                double blue = 0;

                for (int ky = 0; ky < 3; ky++) {
                    for (int kx = 0; kx < 3; kx++) {

                        int sourceX = clamp(x + kx - 1, 0, width - 1);
                        int sourceY = clamp(y + ky - 1, 0, height - 1);

                        int pixel = input.getRGB(sourceX, sourceY);
                        double weight = kernel[ky][kx];

                        red += ((pixel >> 16) & 0xFF) * weight;
                        green += ((pixel >> 8) & 0xFF) * weight;
                        blue += (pixel & 0xFF) * weight;
                    }
                }

                red /= divisor;
                green /= divisor;
                blue /= divisor;

                int alpha = (input.getRGB(x, y) >>> 24) & 0xFF;

                int result =
                        (alpha << 24)
                        | (clamp((int) Math.round(red), 0, 255) << 16)
                        | (clamp((int) Math.round(green), 0, 255) << 8)
                        | clamp((int) Math.round(blue), 0, 255);

                output.setRGB(x, y, result);
            }
        }

        return output;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Image Convolution Tool",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static class ImagePanel extends JPanel {

        private BufferedImage image;
        private final String placeholder;

        ImagePanel(String placeholder) {
            this.placeholder = placeholder;
            setBackground(new Color(245, 245, 245));
            setBorder(BorderFactory.createTitledBorder(placeholder));
        }

        void setImage(BufferedImage image) {
            this.image = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image == null) {
                g.setColor(Color.GRAY);
                g.setFont(new Font("SansSerif", Font.PLAIN, 16));

                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(placeholder)) / 2;
                int y = getHeight() / 2;

                g.drawString(placeholder, x, y);
                return;
            }

            int panelWidth = getWidth() - 20;
            int panelHeight = getHeight() - 40;

            if (panelWidth <= 0 || panelHeight <= 0) {
                return;
            }

            double scale = Math.min(
                    (double) panelWidth / image.getWidth(),
                    (double) panelHeight / image.getHeight()
            );

            int drawWidth = Math.max(
                    1, (int) (image.getWidth() * scale)
            );
            int drawHeight = Math.max(
                    1, (int) (image.getHeight() * scale)
            );

            int x = (getWidth() - drawWidth) / 2;
            int y = 25 + (panelHeight - drawHeight) / 2;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );

            g2.drawImage(
                    image, x, y, drawWidth, drawHeight, null
            );
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
                // Use the default Swing look and feel.
            }

            new ImageMatrixConvolutionTool().setVisible(true);
        });
    }
}