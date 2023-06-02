package laba4;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.Random;

public class DrawingApp extends JFrame {

    private int width;
    private int height;
    private Color[] colors;
    private Color color;
    private int blockSize;
    private BufferedImage insertedImage;

    public DrawingApp() {
        setTitle("Рисовальня");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Рисовать");
        JMenuItem customItem = new JMenuItem("По выбору");
        customItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCustomDialog();
            }
        });
        JMenuItem mosaicItem = new JMenuItem("Мозайка");
        mosaicItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMosaicDialog();
            }
        });
        JMenuItem BresenhamItem = new JMenuItem("Алгоритм Брезенхэма");
        BresenhamItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showBresenhamDialog();
            }
        });
        menu.add(customItem);
        menu.add(mosaicItem);
        menu.add(BresenhamItem);
        menuBar.add(menu);

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem insertImageItem = new JMenuItem("Добавить изображение");
        insertImageItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertImage();
            }
        });
        JMenuItem saveItem = new JMenuItem("Сохранить");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });

        fileMenu.add(insertImageItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        if (insertedImage != null) {
            int x = centerX - (insertedImage.getWidth() / 2);
            int y = centerY - (insertedImage.getHeight() / 2);
            g2d.drawImage(insertedImage, x, y, null);
        } else if (colors != null && blockSize > 0) {
            Random random = new Random();

            for (int y = centerY - (height / 2); y < centerY + (height / 2); y += blockSize) {
                for (int x = centerX - (width / 2); x < centerX + (width / 2); x += blockSize) {
                    Color color = colors[random.nextInt(colors.length)];
                    g2d.setColor(color);
                    g2d.fillRect(x, y, blockSize, blockSize);
                }
            }
        } else {
            g2d.setColor(color);
            g2d.fillRect(centerX - (width / 2), centerY - (height / 2), width, height);
        }
    }

    private void showMosaicDialog() {
        JTextField widthField = new JTextField(5);
        JTextField heightField = new JTextField(5);
        JComboBox<String> blockSizeCombo = new JComboBox<>(new String[]{"2x2", "4x4", "8x8"});

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new GridLayout(2, 2));
        JButton[] colorButtons = new JButton[4];
        colors = new Color[4];
        for (int i = 0; i < colorButtons.length; i++) {
            colorButtons[i] = new JButton("Выбор цвета");
            int index = i;
            colorButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Color color = JColorChooser.showDialog(null, "Выбор цвета", colors[index]);
                    if (color != null) {
                        colors[index] = color;
                        colorButtons[index].setBackground(color);
                    }
                }
            });
            colorPanel.add(colorButtons[i]);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        panel.add(new JLabel("Ширина:"));
        panel.add(widthField);
        panel.add(new JLabel("Высота:"));
        panel.add(heightField);
        panel.add(new JLabel("Размер блока:"));
        panel.add(blockSizeCombo);
        panel.add(new JLabel("Цвета:"));
        panel.add(colorPanel);

        int result = JOptionPane.showConfirmDialog(null, panel, "Параметры мозайки", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());
                blockSize = getBlockSizeFromCombo(blockSizeCombo);
                insertedImage = null;
                repaint();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Некорректный ввод! Пожалуйста, введите числовые значения для ширины, высоты и размера блока.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showCustomDialog() {
        JTextField widthField = new JTextField(5);
        JTextField heightField = new JTextField(5);
        JButton colorButton = new JButton("Выбор цвета");
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                color = JColorChooser.showDialog(null, "Выбор цвета", color);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("Ширина:"));
        panel.add(widthField);
        panel.add(new JLabel("Высота:"));
        panel.add(heightField);
        panel.add(colorButton);

        int result = JOptionPane.showConfirmDialog(null, panel, "Пользовательские параметры", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());
                blockSize = 0;
                insertedImage = null;
                repaint();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Некорректный ввод! Пожалуйста, введите числовые значения для ширины и высоты.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getBlockSizeFromCombo(JComboBox<String> combo) {
        String selectedSize = (String) combo.getSelectedItem();
        switch (selectedSize) {
            case "2x2":
                return 2;
            case "4x4":
                return 4;
            case "8x8":
                return 8;
            default:
                return 1;
        }
    }

    private void insertImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите изображение");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(file);
                insertedImage = image;
                width = image.getWidth();
                height = image.getHeight();
                blockSize = 0;
                colors = null;
                repaint();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Не удалось загрузить изображение.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        paint(g2d);
        g2d.dispose();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int x = centerX - (width / 2);
        int y = centerY - (height / 2);
        int croppedWidth = width;
        int croppedHeight = height;

        BufferedImage croppedImage = image.getSubimage(x, y, croppedWidth, croppedHeight);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить изображение");
        fileChooser.setFileFilter(new FileNameExtensionFilter("BMP Image (*.bmp)", "bmp"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Image (*.png)", "png"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPEG Image (*.jpeg)", "jpeg"));
        fileChooser.setSelectedFile(new File("image.jpeg")); // Устанавливаем имя файла по умолчанию
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String format = getFileFormat(file);
            try {
                ImageIO.write(croppedImage, format, file);
                JOptionPane.showMessageDialog(this, "Изображение сохранено успешно!", "Успех!", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Не удалось сохранить изображение.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getFileFormat(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex + 1).toLowerCase();
        }
        // По умолчанию JPEG
        return "jpeg";
    }

    private void showBresenhamDialog() {
        JDialog dialog = new JDialog(this, "Алгоритм Брезенхэма", true);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));
        JLabel phi1Label = new JLabel("Начальный угол (φ1):");
        JTextField phi1Field = new JTextField("0");
        JLabel phi2Label = new JLabel("Конечный угол (φ2):");
        JTextField phi2Field = new JTextField("360");
        JLabel deltaPhiLabel = new JLabel("Шаг угла (Δφ):");
        JTextField deltaPhiField = new JTextField("1");
        inputPanel.add(phi1Label);
        inputPanel.add(phi1Field);
        inputPanel.add(phi2Label);
        inputPanel.add(phi2Field);
        inputPanel.add(deltaPhiLabel);
        inputPanel.add(deltaPhiField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton applyButton = new JButton("Применить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        applyButton.addActionListener(e -> {
            double phi1 = Double.parseDouble(phi1Field.getText());
            double phi2 = Double.parseDouble(phi2Field.getText());
            double deltaPhi = Double.parseDouble(deltaPhiField.getText());

            this.getGraphics().clearRect(0, 0, this.getWidth(), this.getHeight());
            drawPrimitives(phi1, phi2, deltaPhi);

            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void drawPrimitives(double phi1, double phi2, double deltaPhi) {
        double centerX = this.getWidth() / 2.0;
        double centerY = this.getHeight() / 2.0;
        double fixedRadius = Math.min(this.getWidth(), this.getHeight()) / 2.0;
        Graphics2D g2d = (Graphics2D) this.getGraphics();
        g2d.setStroke(new BasicStroke(2.0f));

        double phi = phi1;

        while (phi <= phi2) {
            double radius1 = (1 - Math.abs(Math.cos(2*phi)))/5;
            double radius2 = ((1 + Math.pow(Math.sin(2*Math.cos(4*phi)),2)))/2;
            double x1 = centerX + radius1 * Math.cos(Math.toRadians(phi)) * fixedRadius;
            double y1 = centerY - radius1 * Math.sin(Math.toRadians(phi)) * fixedRadius;
            double x2 = centerX + radius2 * Math.cos(Math.toRadians(phi)) * fixedRadius;
            double y2 = centerY - radius2 * Math.sin(Math.toRadians(phi)) * fixedRadius;

            float hue = (float) (phi / 360.0);
            float saturation = 1.0f;
            float brightness = 1.0f;
            float alpha = 0.5f;

            Color color = Color.getHSBColor(hue, saturation, brightness);
            Color transparentColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.round(alpha * 255));
            g2d.setColor(transparentColor);

            drawBresenhamLine((int) centerX, (int) centerY, (int) x1, (int) y1, g2d);
            drawBresenhamLine((int) centerX, (int) centerY, (int) x2, (int) y2, g2d);
            phi += deltaPhi;
        }

        g2d.setColor(Color.BLACK);

        drawBresenhamCircle((int) centerX, (int) centerY, (int) fixedRadius, g2d);
        drawBresenhamCircle((int) centerX, (int) centerY, (int) (fixedRadius * (1.0 / 3.0)), g2d);
    }

    private void drawBresenhamLine(int x1, int y1, int x2, int y2, Graphics2D g2d) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (x1 != x2 || y1 != y2) {
            g2d.drawLine(x1, y1, x1, y1);
            int err2 = 2 * err;

            if (err2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (err2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private void drawBresenhamCircle(int centerX, int centerY, int radius, Graphics2D g2d) {
        int x = 0;
        int y = radius;
        int delta = 1 - 2 * radius;
        int error = 0;

        while (y >= 0) {
            g2d.fillRect(centerX + x, centerY + y, 1, 1);
            g2d.fillRect(centerX + x, centerY - y, 1, 1);
            g2d.fillRect(centerX - x, centerY + y, 1, 1);
            g2d.fillRect(centerX - x, centerY - y, 1, 1);

            error = 2 * (delta + y) - 1;
            if ((delta < 0) && (error <= 0)) {
                delta += 2 * ++x + 1;
                continue;
            }
            if ((delta > 0) && (error > 0)) {
                delta -= 2 * --y + 1;
                continue;
            }
            delta += 2 * (++x - y--);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new laba4.DrawingApp();
        });
    }
}