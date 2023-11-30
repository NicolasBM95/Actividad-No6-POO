import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class FriendsInterface extends JFrame {

    private JTextField nameTextField, numberTextField;
    private JTextArea resultTextArea;
    private JLabel nameLabelResult, numberLabelResult;

    public FriendsInterface() {
        setTitle("NUMERO AMIGOS");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel titleLabel = new JLabel("NUMERO AMIGOS");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));

        nameLabelResult = new JLabel("Nombre: ");
        nameTextField = new JTextField();
        nameTextField.setPreferredSize(new Dimension(150, nameTextField.getPreferredSize().height));

        numberLabelResult = new JLabel("Número: ");
        numberTextField = new JTextField();
        numberTextField.setPreferredSize(new Dimension(150, numberTextField.getPreferredSize().height));

        inputPanel.add(nameLabelResult);
        inputPanel.add(nameTextField);
        inputPanel.add(Box.createHorizontalStrut(10));
        inputPanel.add(numberLabelResult);
        inputPanel.add(numberTextField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton createButton = new JButton("Crear");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createButtonClicked();
            }
        });

        JButton readButton = new JButton("Leer");
        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readButtonClicked();
            }
        });

        JButton updateButton = new JButton("Actualizar");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateButtonClicked();
            }
        });

        JButton deleteButton = new JButton("Borrar");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteButtonClicked();
            }
        });

        buttonPanel.add(createButton);
        buttonPanel.add(readButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        add(titleLabel);
        add(inputPanel);
        add(buttonPanel);
        add(scrollPane);

        pack();
        setLocationRelativeTo(null);
    }

    private void createButtonClicked() {
        try {
            String newName = nameTextField.getText();
            long newNumber = Long.parseLong(numberTextField.getText());

            File file = new File("friendsContact.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            boolean found = false;

            while (raf.getFilePointer() < raf.length()) {
                String nameNumberString = raf.readLine();
                String[] lineSplit = nameNumberString.split("!");
                String name = lineSplit[0];

                if (name.equals(newName)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                String nameNumberString = newName + "!" + String.valueOf(newNumber) + "\n";
                raf.seek(raf.length());
                raf.writeBytes(nameNumberString);
                resultTextArea.setText("Amigo creado.");
            } else {
                resultTextArea.setText("El nombre ya existe.");
            }

            raf.close();
        } catch (IOException | NumberFormatException ex) {
            resultTextArea.setText(ex.toString());
        }
    }

    private void readButtonClicked() {
        try {
            File file = new File("friendsContact.txt");
            if (!file.exists()) {
                resultTextArea.setText("El archivo no existe.");
                return;
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rw");

            StringBuilder namesAndNumbers = new StringBuilder();
            while (raf.getFilePointer() < raf.length()) {
                String nameNumberString = raf.readLine();
                String[] lineSplit = nameNumberString.split("!");
                String name = lineSplit[0];
                long number = Long.parseLong(lineSplit[1]);
                namesAndNumbers.append("Nombre: ").append(name).append(" - Número: ").append(number).append("\n");
            }

            raf.close();
            resultTextArea.setText(namesAndNumbers.toString());
        } catch (IOException | NumberFormatException e) {
            resultTextArea.setText(e.toString());
        }
    }

    private void updateButtonClicked() {
        try {
            String newName = nameTextField.getText();
            long newNumber = Long.parseLong(numberTextField.getText());

            File file = new File("friendsContact.txt");
            if (!file.exists()) {
                resultTextArea.setText("El archivo no existe.");
                return;
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            boolean found = false;

            while (raf.getFilePointer() < raf.length()) {
                String nameNumberString = raf.readLine();
                String[] lineSplit = nameNumberString.split("!");
                String name = lineSplit[0];

                if (name.equals(newName)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                File tmpFile = new File("temp.txt");
                RandomAccessFile tmpraf = new RandomAccessFile(tmpFile, "rw");

                raf.seek(0);
                tmpraf.seek(0);

                while (raf.getFilePointer() < raf.length()) {
                    String nameNumberString = raf.readLine();
                    int index = nameNumberString.indexOf('!');
                    String name = nameNumberString.substring(0, index);

                    if (name.equals(newName)) {
                        nameNumberString = name + "!" + String.valueOf(newNumber);
                    }

                    tmpraf.writeBytes(nameNumberString);
                    tmpraf.writeBytes(System.lineSeparator());
                }

                raf.seek(0);
                tmpraf.seek(0);

                while (tmpraf.getFilePointer() < tmpraf.length()) {
                    raf.writeBytes(tmpraf.readLine());
                    raf.writeBytes(System.lineSeparator());
                }

                raf.setLength(tmpraf.length());

                tmpraf.close();
                raf.close();

                tmpFile.delete();
                resultTextArea.setText("Amigo actualizado.");
            } else {
                resultTextArea.setText("El nombre no existe.");
            }
        } catch (IOException | NumberFormatException ex) {
            resultTextArea.setText(ex.toString());
        }
    }

    private void deleteButtonClicked() {
        try {
            String newName = nameTextField.getText();

            File file = new File("friendsContact.txt");
            if (!file.exists()) {
                resultTextArea.setText("El archivo no existe.");
                return;
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            boolean found = false;

            while (raf.getFilePointer() < raf.length()) {
                String nameNumberString = raf.readLine();
                String[] lineSplit = nameNumberString.split("!");
                String name = lineSplit[0];

                if (name.equals(newName)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                File tmpFile = new File("temp.txt");
                RandomAccessFile tmpraf = new RandomAccessFile(tmpFile, "rw");

                raf.seek(0);
                tmpraf.seek(0);

                while (raf.getFilePointer() < raf.length()) {
                    String nameNumberString = raf.readLine();
                    int index = nameNumberString.indexOf('!');
                    String name = nameNumberString.substring(0, index);

                    if (name.equals(newName)) {
                        continue;
                    }

                    tmpraf.writeBytes(nameNumberString);
                    tmpraf.writeBytes(System.lineSeparator());
                }

                raf.seek(0);
                tmpraf.seek(0);

                while (tmpraf.getFilePointer() < tmpraf.length()) {
                    raf.writeBytes(tmpraf.readLine());
                    raf.writeBytes(System.lineSeparator());
                }

                raf.setLength(tmpraf.length());

                tmpraf.close();
                raf.close();

                tmpFile.delete();
                resultTextArea.setText("Amigo borrado.");
            } else {
                resultTextArea.setText("El nombre no existe.");
            }
        } catch (IOException ex) {
            resultTextArea.setText(ex.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FriendsInterface().setVisible(true);
            }
        });
    }
}




