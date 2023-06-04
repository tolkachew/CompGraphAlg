package laba5;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DrawingApp extends JFrame {
    private Rectangle Rectangle;
    private Line line;
    private JButton clipButton;
    private JButton createButton;
    private JButton moveButton;
    private JButton lineButton;
    private JButton clearButton;

    private enum Mode {
        DRAW, MOVE, NONE, LINE
    }

    private Mode mode;

    public DrawingApp() {
        Rectangle = null;
        line = new Line();
        mode = Mode.NONE;

        setTitle("Рисовальня");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        clipButton = new JButton("Отсечение");
        clipButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clipLine();
                repaint();
            }
        });
        clipButton.setBackground(Color.WHITE);

        createButton = new JButton("Создание прямоугольника");
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRectangle();
                mode = Mode.DRAW;
                repaint();
            }
        });
        createButton.setBackground(Color.WHITE);

        moveButton = new JButton("Перемещение");
        moveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = Mode.MOVE;
                repaint();
            }
        });
        moveButton.setBackground(Color.WHITE);

        lineButton = new JButton("Рисовать отрезок");
        lineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = Mode.LINE;
                repaint();
            }
        });
        lineButton.setBackground(Color.WHITE);

        clearButton = new JButton("Очистить поле");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearField();
                repaint();
            }
        });
        clearButton.setBackground(Color.WHITE);

        buttonPanel.add(lineButton);
        buttonPanel.add(moveButton);
        buttonPanel.add(clipButton);
        buttonPanel.add(createButton);
        buttonPanel.add(clearButton);

        JPanel drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawLine(g);
                if (mode != Mode.DRAW) {
                    drawRectangle(g);
                }
            }
        };

        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (mode == Mode.DRAW) {
                    Rectangle = new Rectangle(e.getX(), e.getY(), 0, 0);
                } else if (mode == Mode.LINE) {
                    line.setStartPoint(e.getX(), e.getY());
                }

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (mode == Mode.DRAW) {
                    if (Rectangle != null) {
                        int width = e.getX() - Rectangle.x;
                        int height = e.getY() - Rectangle.y;
                        Rectangle.width = Math.abs(width);
                        Rectangle.height = Math.abs(height);
                    }
                } else if (mode == Mode.LINE) {
                    line.setEndPoint(e.getX(), e.getY());
                }

                repaint();
            }
        });

        drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mode == Mode.DRAW) {
                    if (Rectangle != null) {
                        int width = e.getX() - Rectangle.x;
                        int height = e.getY() - Rectangle.y;
                        Rectangle.width = Math.abs(width);
                        Rectangle.height = Math.abs(height);
                    }
                } else if (mode == Mode.MOVE) {
                    if (Rectangle != null && Rectangle.x <= e.getX() && e.getX() <= Rectangle.x + Rectangle.width
                            && Rectangle.y <= e.getY() && e.getY() <= Rectangle.y + Rectangle.height) {
                        Rectangle.x = e.getX() - Rectangle.width / 2;
                        Rectangle.y = e.getY() - Rectangle.height / 2;
                    }
                } else if (mode == Mode.LINE) {
                    line.setEndPoint(e.getX(), e.getY());
                }

                repaint();
            }
        });

        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        contentPane.add(drawPanel, BorderLayout.CENTER);

        setContentPane(contentPane);
    }

    private void drawLine(Graphics g) {
        if (line.isSet()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);
            g2d.drawLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }
    }

    private void drawRectangle(Graphics g) {
        if (Rectangle != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            g2d.drawRect(Rectangle.x, Rectangle.y, Rectangle.width, Rectangle.height);
        }
    }

    private void clipLine() {
        if (line.isSet()) {
            int startX = line.getStartX();
            int startY = line.getStartY();
            int endX = line.getEndX();
            int endY = line.getEndY();

            int codeStart = calculateCode(startX, startY);
            int codeEnd = calculateCode(endX, endY);

            boolean isVisible = false;

            while (true) {
                if ((codeStart | codeEnd) == 0) {
                    isVisible = true;
                    break;
                } else if ((codeStart & codeEnd) != 0) {
                    break;
                } else {
                    int x = 0, y = 0;
                    int code = (codeStart != 0) ? codeStart : codeEnd;

                    if ((code & Rectangle.OUT_LEFT) != 0) {
                        x = Rectangle.x;
                        y = startY + (endY - startY) * (x - startX) / (endX - startX);
                    } else if ((code & Rectangle.OUT_RIGHT) != 0) {
                        x = Rectangle.x + Rectangle.width;
                        y = startY + (endY - startY) * (x - startX) / (endX - startX);
                    } else if ((code & Rectangle.OUT_BOTTOM) != 0) {
                        y = Rectangle.y + Rectangle.height;
                        x = startX + (endX - startX) * (y - startY) / (endY - startY);
                    } else if ((code & Rectangle.OUT_TOP) != 0) {
                        y = Rectangle.y;
                        x = startX + (endX - startX) * (y - startY) / (endY - startY);
                    }

                    if (code == codeStart) {
                        startX = x;
                        startY = y;
                        codeStart = calculateCode(startX, startY);
                    } else {
                        endX = x;
                        endY = y;
                        codeEnd = calculateCode(endX, endY);
                    }
                }
            }

            if (isVisible) {
                line.setStartPoint(startX, startY);
                line.setEndPoint(endX, endY);
            } else {
                line.reset();
            }
        }
    }

    private int calculateCode(int x, int y) {
        int code = Rectangle.OUTSIDE;

        if (x >= Rectangle.x && x <= Rectangle.x + Rectangle.width) {
            code = Rectangle.INSIDE;
        } else if (x < Rectangle.x) {
            code = Rectangle.OUT_LEFT;
        } else if (x > Rectangle.x + Rectangle.width) {
            code = Rectangle.OUT_RIGHT;
        }

        if (y >= Rectangle.y && y <= Rectangle.y + Rectangle.height) {
            code |= Rectangle.INSIDE;
        } else if (y < Rectangle.y) {
            code |= Rectangle.OUT_TOP;
        } else if (y > Rectangle.y + Rectangle.height) {
            code |= Rectangle.OUT_BOTTOM;
        }

        return code;
    }

    private void deleteRectangle() {
        Rectangle = null;
        repaint();
    }

    private void clearField() {
        Rectangle = null;
        line.reset();
        mode = Mode.NONE;
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DrawingApp().setVisible(true);
            }
        });
    }
}

class Line {
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public Line() {
        startX = 0;
        startY = 0;
        endX = 0;
        endY = 0;
    }

    public void setStartPoint(int x, int y) {
        startX = x;
        startY = y;
    }

    public void setEndPoint(int x, int y) {
        endX = x;
        endY = y;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public boolean isSet() {
        return startX != endX || startY != endY;
    }

    public void reset() {
        startX = 0;
        startY = 0;
        endX = 0;
        endY = 0;
    }
}

class Rectangle {
    public static final int INSIDE = 0;
    public static final int OUT_LEFT = 1;
    public static final int OUT_RIGHT = 2;
    public static final int OUT_BOTTOM = 4;
    public static final int OUT_TOP = 8;
    public static final int OUTSIDE = OUT_LEFT | OUT_RIGHT | OUT_BOTTOM | OUT_TOP;

    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}