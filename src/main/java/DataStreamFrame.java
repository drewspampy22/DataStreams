import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.in;
import static java.nio.file.StandardOpenOption.CREATE;

public class DataStreamFrame extends JFrame {
    JPanel mainPanel;
    JPanel displayPanel;
    JPanel buttonPanel;
    JPanel searchPanel;

    JTextArea leftArea;
    JTextArea rightArea;

    JScrollPane leftPane;
    JScrollPane rightPane;

    JButton loadButton;
    JButton filterButton;
    JButton quitButton;

    JLabel label;

    JTextField searchString;

    private File selectedFile;
    private Path filePath;

    private Set set = new HashSet();

    public DataStreamFrame()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        createDisplayPanel();
        createButtonPanel();
        createSearchPanel();

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(displayPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);





        add(mainPanel);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        setSize(screenWidth / 2, screenHeight / 2);
        setLocation(screenWidth / 4, screenHeight / 4);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void createSearchPanel()
    {
        searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(1,2));

        searchString = new JTextField();
        searchString.setToolTipText("Enter a search string here");
        searchString.setBackground(new Color(203, 232, 202));

        label = new JLabel("Search String: ");
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        label.setHorizontalAlignment(JLabel.RIGHT);

        searchPanel.add(label);
        searchPanel.add(searchString);
    }

    public void createDisplayPanel()
    {
        displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayout(1,2));
        displayPanel.setBorder(new TitledBorder(new EtchedBorder(), ""));

        leftArea = new JTextArea();
        rightArea = new JTextArea();

        leftArea.setEditable(false);
        rightArea.setEditable(false);

        leftArea.setBackground(new Color(235, 234, 230));
        rightArea.setBackground(new Color(235, 234, 230));

        leftArea.setFont(new Font("Arial", Font.PLAIN, 18));
        rightArea.setFont(new Font("Arial", Font.PLAIN, 18));

        leftPane = new JScrollPane(leftArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPane = new JScrollPane(rightArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        leftPane.setToolTipText("Original file");
        rightPane.setToolTipText("Filtered File");

        displayPanel.add(leftPane);
        displayPanel.add(rightPane);
    }

    public void createButtonPanel()
    {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        buttonPanel.setBorder(new TitledBorder(new EtchedBorder(), "Data Stream"));

        loadButton = new JButton("Load");
        filterButton = new JButton("Filter");
        quitButton = new JButton("Quit");

        filterButton.setEnabled(false);
        filterButton.setBackground(new Color(235, 205, 202));

        loadButton.setFont(new Font("Arial", Font.BOLD, 24));
        filterButton.setFont(new Font("Arial", Font.BOLD, 24));
        quitButton.setFont(new Font("Arial", Font.BOLD, 24));

        loadButton.addActionListener((ActionEvent e) ->{load();});
        filterButton.addActionListener((ActionEvent e) -> {filter();});
        quitButton.addActionListener((ActionEvent e) -> {System.exit(0);});

        buttonPanel.add(loadButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(quitButton);
    }

    public void load()
    {
        JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = chooser.getSelectedFile();
            filePath = selectedFile.toPath();
        }
        filterButton.setEnabled(true);
        filterButton.setBackground(null);
        JOptionPane.showMessageDialog(mainPanel, "File Loaded", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void filter() {
        leftArea.setText("");
        rightArea.setText("");
        String wordFilter = searchString.getText();
        String rec = "";
        try (Stream<String> lines = Files.lines(Paths.get(selectedFile.getPath())))
        {
            Set<String> set = lines.filter(w -> w.contains(wordFilter)).collect(Collectors.toSet());
            set.forEach(w -> rightArea.append(w + "\n"));
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            InputStream in =
                    new BufferedInputStream(Files.newInputStream(filePath, CREATE));
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in));
            int line = 0;
            while(reader.ready())
            {
                rec = reader.readLine();
                leftArea.append(rec + "\n");
                line++;
            }
            reader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}