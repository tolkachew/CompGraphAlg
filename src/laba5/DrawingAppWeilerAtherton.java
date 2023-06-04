package laba5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

public class DrawingAppWeilerAtherton extends JFrame {
    private JPanel canvas;
    private Mode mode;
    private List<Shape> shapes;
    private Polygon customPolygon;
    private Polygon octagon;
    private Intersection intersection;
    private boolean isCreatingShape;

    enum Mode {
        WEILER_ATHERTON
    }

    public DrawingAppWeilerAtherton() {
        setTitle("алгоритм Вейлера-Азертона для плоских фигур произвольной формы");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new JPanel() {
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            mode = Mode.WEILER_ATHERTON;
                            customPolygon = null;
                            octagon = createOctagon(e.getX(), e.getY());
                            shapes.add(octagon);
                            intersection = getIntersection();
                            repaint();
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            if (mode == Mode.WEILER_ATHERTON) {
                                if (!isCreatingShape) {
                                    customPolygon = new Polygon();
                                    customPolygon.addPoint(e.getX(), e.getY());
                                    shapes.add(customPolygon);
                                    isCreatingShape = true;
                                    intersection = getIntersection();
                                    repaint();
                                } else {
                                    customPolygon.addPoint(e.getX(), e.getY());
                                    intersection = getIntersection();
                                    repaint();
                                }
                            }
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e) && isCreatingShape) {
                            isCreatingShape = false;
                            intersection = getIntersection();
                            repaint();
                        }
                    }
                });

                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (mode == Mode.WEILER_ATHERTON && isCreatingShape && customPolygon != null) {
                            customPolygon.addPoint(e.getX(), e.getY());
                            intersection = getIntersection();
                            repaint();
                        }
                    }
                });
            }

            private Polygon createOctagon(int x, int y) {
                int radius = 120;
                Polygon octagon = new Polygon();
                for (int i = 0; i < 8; i++) {
                    double angle = 2 * Math.PI * i / 8;
                    int px = (int) (x + radius * Math.cos(angle));
                    int py = (int) (y + radius * Math.sin(angle));
                    octagon.addPoint(px, py);
                }
                return octagon;
            }

            private Intersection getIntersection() {
                if (octagon != null && customPolygon != null) {
                    Area area1 = new Area(octagon);
                    Area area2 = new Area(customPolygon);
                    area1.intersect(area2);
                    return new Intersection(area1);
                }
                return null;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Shape shape : shapes) {
                    g.setColor(Color.BLUE);
                    if (shape instanceof Polygon) {
                        g.drawPolygon((Polygon) shape);
                    }
                }
                if (customPolygon != null) {
                    g.setColor(Color.RED);
                    g.drawPolygon(customPolygon);
                }
                if (intersection != null) {
                    g.setColor(new Color(255, 0, 0, 128));
                    g.fillPolygon(intersection.getPolygon());
                }
            }
        };

        JPanel bottomPanel = new JPanel();
        JButton clearButton = new JButton("Очистить поле");
        clearButton.addActionListener(e -> {
            shapes.clear();
            customPolygon = null;
            octagon = null;
            intersection = null;
            isCreatingShape = false;
            canvas.repaint();
        });
        bottomPanel.add(clearButton);

        add(canvas, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);

        mode = Mode.WEILER_ATHERTON;
        shapes = new ArrayList<>();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DrawingAppWeilerAtherton::new);
    }

    private static class Intersection {
        private final Area area;

        public Intersection(Area area) {
            this.area = area;
        }

        public Polygon getPolygon() {
            PathIterator iterator = area.getPathIterator(null);
            int[] xPoints = new int[1000];
            int[] yPoints = new int[1000];
            int i = 0;
            while (!iterator.isDone()) {
                double[] coords = new double[6];
                int type = iterator.currentSegment(coords);
                if (type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO) {
                    xPoints[i] = (int) coords[0];
                    yPoints[i] = (int) coords[1];
                    i++;
                }
                iterator.next();
            }
            return new Polygon(xPoints, yPoints, i);
        }
    }
}