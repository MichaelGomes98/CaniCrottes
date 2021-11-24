import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class EditForm extends JFrame implements ActionListener {

    private boolean isConnected;
    private boolean isNew;

    private static final Integer WINDOW_WIDTH = 600;
    private static final Integer WINDOW_HEIGHT = 300;
    private static final Integer LABEL_HEIGHT = 20;
    private static final Integer LABEL_WIDTH = 80;
    private static final Integer TEXTFIELD_WIDTH = 40;
    private static final Integer BUTTON_HEIGHT = 20;
    private static final Integer BUTTON_WIDTH = 100;

    private static final String ADDRESS_LABEL = "Adresse";
    private static final String STATUS_LABEL = "Etat";
    private static final String NOTE_LABEL = "Remarques";
    private static final String NUMBER_LABEL = "Numéro";
    private static final String CLOSE_LABEL = "Fermer";
    private static final String SAVE_LABEL = "Enregistrer";
    private static final String DELETE_LABEL = "Supprimer";

    private static final String UPDATE_SUCCESS_MSG = "Caninette mise à jour";
    private static final String CREATE_SUCCESS_MSG = "Caninette crée";
    private static final String DELETE_SUCCESS_MSG = "Caninette supprimée";

    private static final String[] STATUSLIST = {"Posée", "En travaux", "Hors service"};

    private static final Dimension DIMENSION = new Dimension(LABEL_WIDTH, LABEL_HEIGHT);

    // Buttons
    private JButton saveButton, exitButton, deleteButton;

    // Edit
    private JTextField addressEditTextField, numberEditTextField, noteEditTextField;
    private JComboBox statusComboBox;

    // View
    private JLabel addressViewLabel, numberViewLabel, noteViewLabel;

    // Values
    private Integer id;
    private String status;
    private String number;
    private double longitude;
    private double latitude;

    /**
     * Adaptive form to view/edit/add a point
     *
     * @param windowTitle String
     * @param isConnected boolean
     */
    EditForm(String windowTitle, boolean isConnected, boolean isNew) {

        this.isConnected = isConnected;
        this.isNew = isNew;
        //FRAME PROPERTIES
        setTitle(windowTitle);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        //FRAME CONTENT
        // Panels
        JPanel mainPanel = new JPanel(new GridLayout(0, 1));
        add(mainPanel);

        //address
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(addressPanel);
        // Titles
        JLabel addressTitleLabel = new JLabel(ADDRESS_LABEL + " :");
        addressTitleLabel.setPreferredSize(DIMENSION);
        addressPanel.add(addressTitleLabel);

        //number
        JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(numberPanel);
        JLabel numberTitleLabel = new JLabel(NUMBER_LABEL + " :");
        numberTitleLabel.setPreferredSize(DIMENSION);
        numberPanel.add(numberTitleLabel);

        //status
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(statusPanel);
        JLabel statusTitleLabel = new JLabel(STATUS_LABEL + " :");
        statusTitleLabel.setPreferredSize(DIMENSION);
        statusPanel.add(statusTitleLabel);
        statusComboBox = new JComboBox(STATUSLIST);
        statusComboBox.setMinimumSize(DIMENSION);
        statusComboBox.addActionListener(this);
        statusPanel.add(statusComboBox);

        //note
        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(notePanel);
        JLabel noteTitleLabel = new JLabel(NOTE_LABEL + " :");
        noteTitleLabel.setPreferredSize(DIMENSION);
        notePanel.add(noteTitleLabel);

        //buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(buttonsPanel);
        exitButton = new JButton(CLOSE_LABEL);
        exitButton.addActionListener(this);
        exitButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        buttonsPanel.add(exitButton);

        // Checks whether the user is logged and adds the items
        if (isConnected) {
            //address
            addressEditTextField = new JTextField();
            addressEditTextField.setColumns(TEXTFIELD_WIDTH);
            addressPanel.add(addressEditTextField);

            //number
            numberEditTextField = new JTextField();
            numberEditTextField.setColumns(TEXTFIELD_WIDTH);
            numberPanel.add(numberEditTextField);

            //status
            statusComboBox.setEditable(true);

            //note
            noteEditTextField = new JTextField();
            noteEditTextField.setColumns(TEXTFIELD_WIDTH);
            notePanel.add(noteEditTextField);

            //buttons
            if (!isNew) {
                deleteButton = new JButton(DELETE_LABEL);
                deleteButton.addActionListener(this);
                buttonsPanel.add(deleteButton);
            }
            saveButton = new JButton(SAVE_LABEL);
            saveButton.addActionListener(this);
            buttonsPanel.add(saveButton);


        } else {
            //address
            addressViewLabel = new JLabel();
            addressPanel.add(addressViewLabel);

            //number
            numberViewLabel = new JLabel();
            numberPanel.add(numberViewLabel);

            //status
            statusComboBox.setEditable(false);
            statusComboBox.setEnabled(false);

            //note
            noteViewLabel = new JLabel();
            notePanel.add(noteViewLabel);
        }
    }

    void setId(Integer id) {
        this.id = id;
    }

    void setAddress(String address) {
        if (isConnected) {
            this.addressEditTextField.setText(address);
        } else {
            this.addressViewLabel.setText(address);
        }
    }

    void setNote(String address) {
        if (isConnected) {
            this.noteEditTextField.setText(address);
        } else {
            this.noteViewLabel.setText(address);
        }
    }

    void setStatus(String status) {
        if (isConnected) {
            this.statusComboBox.setSelectedItem(status);
        } else {
            this.statusComboBox.setSelectedItem(status);
        }
    }

    void setNumber(String number) {
        this.number = number;
        if (isConnected) {
            this.numberEditTextField.setText(number);
        } else {
            this.numberViewLabel.setText(number);
        }
    }

    void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private boolean validateFrom() {
        if (addressEditTextField.getText().equals("")) {
            return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitButton)) {
            dispose();

        } else if (e.getSource().equals(saveButton)) {

            if (validateFrom()) {
                try {
                    DaoCaninette daoCaninette = new DaoCaninette(CaniCrottes.getSqliteConnection(false));

                    if (isNew) {
                        Integer lastCaninette = daoCaninette.insertCaninette(addressEditTextField.getText(), numberEditTextField.getText(), statusComboBox.getSelectedItem().toString(), noteEditTextField.getText(), longitude, latitude);

                        if (lastCaninette >= 0) {
                            MainWindow.createCaninettePoint(daoCaninette.getCaninette(lastCaninette));
                            ToastMessage toastMessage = new ToastMessage(CREATE_SUCCESS_MSG, 3000);
                            toastMessage.setVisible(true);
                        }
                    } else {
                        if (daoCaninette.updateCaninette(id, statusComboBox.getSelectedItem().toString(), addressEditTextField.getText(), numberEditTextField.getText(), noteEditTextField.getText()) >= 0) {
                            ToastMessage toastMessage = new ToastMessage(UPDATE_SUCCESS_MSG, 3000);
                            MainWindow.refreshMap();
                            toastMessage.setVisible(true);
                        }
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                dispose();

            }
        } else if (e.getSource().equals(deleteButton)) {
            try {
                DaoCaninette daoCaninette = new DaoCaninette(CaniCrottes.getSqliteConnection(false));

                if (daoCaninette.deleteCaninette(id) > 0) {
                    ToastMessage toastMessage = new ToastMessage(DELETE_SUCCESS_MSG, 3000);
                    MainWindow.refreshMap();
                    toastMessage.setVisible(true);
                }
                dispose();

            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}
