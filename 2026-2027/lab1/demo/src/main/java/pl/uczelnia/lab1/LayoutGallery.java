package pl.uczelnia.lab1;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Interaktywny pokaz oraz powtarzalne rendery tych samych komponentów Swing. */
public class LayoutGallery {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        UIManager.put("Label.font", new Font("Dialog", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Dialog", Font.PLAIN, 14));
        SwingUtilities.invokeAndWait(() -> {
            try {
                if (args.length == 2 && args[0].equals("--render")) {
                    Path output = Path.of(args[1]);
                    Files.createDirectories(output);
                    save(layouts(), 1120, 820, output.resolve("layouts.png"));
                    save(flow(), 650, 140, output.resolve("flow-wide.png"));
                    save(flow(), 250, 220, output.resolve("flow-narrow.png"));
                    JPanel root = menuDemo();
                    BufferedImage image = paint(root, 900, 430);
                    JMenuBar bar = (JMenuBar) root.getComponent(0);
                    JPopupMenu popup = bar.getMenu(0).getPopupMenu();
                    popup.setSize(popup.getPreferredSize());
                    layoutTree(popup);
                    Graphics2D g = image.createGraphics();
                    g.translate(0, bar.getHeight());
                    popup.printAll(g);
                    g.dispose();
                    ImageIO.write(image, "png", output.resolve("menu.png").toFile());
                    System.out.println("Rendered 4 PNG files in " + output.toAbsolutePath());
                } else {
                    JFrame frame = new JFrame("Swing — układy i menu");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    JTabbedPane tabs = new JTabbedPane();
                    tabs.addTab("Układy", layouts());
                    tabs.addTab("Menu i dane", menuDemo());
                    frame.add(tabs);
                    frame.setSize(1120, 850);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            } catch (Exception ex) { throw new IllegalStateException(ex); }
        });
    }

    private static JPanel flow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        for (String text : new String[]{"Otwórz", "Zapisz", "Filtruj", "Analizuj", "Eksportuj"})
            p.add(new JButton(text));
        return p;
    }

    private static JLabel tile(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(226, 238, 250));
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        return label;
    }

    private static JPanel example(String title, String caption, JComponent content) {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title), new EmptyBorder(8, 8, 8, 8)));
        p.add(new JLabel("<html>" + caption + "</html>"), BorderLayout.NORTH);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private static JPanel layouts() {
        JPanel all = new JPanel(new GridLayout(3, 2, 18, 18));
        all.setBorder(new EmptyBorder(18, 18, 18, 18));
        all.add(example("FlowLayout", "Przyciski zawijają się do następnego wiersza.", flow()));
        JPanel border = new JPanel(new BorderLayout(5, 5));
        border.add(tile("NORTH — narzędzia"), BorderLayout.NORTH);
        border.add(tile("SOUTH — status"), BorderLayout.SOUTH);
        border.add(tile("WEST"), BorderLayout.WEST);
        border.add(tile("EAST"), BorderLayout.EAST);
        border.add(tile("CENTER — reszta miejsca"), BorderLayout.CENTER);
        all.add(example("BorderLayout", "Środek rośnie wraz z oknem.", border));
        JPanel grid = new JPanel(new GridLayout(2, 3, 5, 5));
        for (int i = 1; i <= 6; i++) grid.add(new JButton("Komórka " + i));
        all.add(example("GridLayout", "Wszystkie komórki mają jednakowe rozmiary.", grid));
        JPanel bag = new JPanel(new GridBagLayout());
        String[] labels = {"Zbiór", "Okres", "Jednostka"};
        String[] values = {"Bezrobocie — dane demo", "2020–2025", "%"};
        for (int i = 0; i < labels.length; i++) {
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 5, 5, 5);
            c.gridy = i; c.gridx = 0; c.anchor = GridBagConstraints.LINE_END;
            bag.add(new JLabel(labels[i]), c);
            c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
            bag.add(new JTextField(values[i], 12), c);
        }
        all.add(example("GridBagLayout", "Pola: weightx=1, fill=HORIZONTAL.", bag));
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        for (String text : new String[]{"1. Wybierz dane", "2. Wybierz zmienne", "3. Oblicz model"}) {
            JButton button = new JButton(text);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            box.add(button); box.add(Box.createVerticalStrut(8));
        }
        all.add(example("BoxLayout.Y_AXIS", "Jeden komponent pod drugim.", box));
        CardLayout cards = new CardLayout();
        JPanel views = new JPanel(cards);
        views.add(tile("DANE — aktywna karta"), "dane");
        views.add(tile("ANALIZA — aktywna karta"), "analiza");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (String name : new String[]{"dane", "analiza"}) {
            JButton button = new JButton(name);
            button.addActionListener(e -> cards.show(views, name));
            actions.add(button);
        }
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.add(actions, BorderLayout.NORTH); card.add(views, BorderLayout.CENTER);
        all.add(example("CardLayout", "Przyciski przełączają widoki w tym samym obszarze.", card));
        return all;
    }

    private static JPanel menuDemo() {
        JPanel root = new JPanel(new BorderLayout());
        JLabel status = new JLabel("Dane syntetyczne — stopa bezrobocia w %");
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("Plik");
        for (String text : new String[]{"Nowy", "Otwórz...", "Zapisz", "Wyjdź"}) {
            JMenuItem item = new JMenuItem(text);
            item.addActionListener(e -> {
                if (text.equals("Otwórz...")) {
                    JFileChooser chooser = new JFileChooser();
                    if (chooser.showOpenDialog(root) == JFileChooser.APPROVE_OPTION)
                        status.setText("Wybrano (bez importu): " + chooser.getSelectedFile().getName());
                } else if (text.equals("Wyjdź")) {
                    Window window = SwingUtilities.getWindowAncestor(root);
                    if (window != null) window.dispose();
                } else status.setText(text + " — demonstracja zdarzenia, bez zapisu danych");
            });
            if (text.equals("Otwórz...")) item.setAccelerator(KeyStroke.getKeyStroke("control O"));
            file.add(item);
        }
        bar.add(file);
        JMenu help = new JMenu("Pomoc");
        JMenuItem about = new JMenuItem("O pokazie");
        about.addActionListener(e -> status.setText("JMenuBar → JMenu → JMenuItem → ActionListener"));
        help.add(about); bar.add(help); root.add(bar, BorderLayout.NORTH);
        Object[][] rows = {{2020, "Region A", 6.2}, {2021, "Region A", 5.8},
                {2022, "Region A", 5.1}, {2023, "Region A", 4.9}, {2024, "Region A", 4.6}};
        DefaultTableModel model = new DefaultTableModel(rows, new String[]{"Rok", "Region", "Stopa (%)"}) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int column) {
                return switch (column) { case 0 -> Integer.class; case 2 -> Double.class; default -> String.class; };
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(34); table.setAutoCreateRowSorter(true);
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.add(tile("Przegląd obserwacji ekonomicznych"), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(table);
        // Nagłówek także w renderowaniu bez podłączenia tabeli do okna.
        scroll.setColumnHeaderView(table.getTableHeader());
        content.add(scroll, BorderLayout.CENTER);
        content.add(status, BorderLayout.SOUTH);
        root.add(content, BorderLayout.CENTER);
        return root;
    }

    private static void layoutTree(Container c) {
        c.doLayout();
        for (Component child : c.getComponents())
            if (child instanceof Container nested) layoutTree(nested);
    }

    private static BufferedImage paint(JComponent component, int width, int height) {
        component.setSize(width, height); layoutTree(component);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE); g.fillRect(0, 0, width, height);
        component.printAll(g); g.dispose();
        return image;
    }

    private static void save(JComponent c, int width, int height, Path path) throws Exception {
        ImageIO.write(paint(c, width, height), "png", path.toFile());
    }
}
