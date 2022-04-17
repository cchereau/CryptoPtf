package gui.screenTransaction;

import global.EnumCrypto.enTypeMontant;
import global.EnumCrypto.enTypeTransaction;
import global.GlobalData;
import global.fonction.FntGUI;
import ptfManagement.Transaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;


public class JDialogTrade extends JDialog implements ActionListener {

    private final JTextField textFieldQuantite = new JTextField();
    private final JTextField textFieldPrice = new JTextField();
    private final JFormattedTextField textFieldFees = new JFormattedTextField();
    private final JFormattedTextField textFieldMontant = new JFormattedTextField();
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    private final JLabel lblQuantite = new JLabel("Quantité");
    private final JLabel lblPrice = new JLabel("Prix");
    private final JLabel lblFees = new JLabel("Fees");
    private final JLabel lblMontant = new JLabel("Montants");
    private JComboBox<Object> comboBoxFrom;
    private JComboBox<Object> comboBoxTo;
    private JComboBox<?> comboPtf;
    private enTypeTransaction typeTransaction;
    private String ptfName;

    public JDialogTrade(Frame owner, boolean modal) {
        super(owner, modal);
        this.ptfName = "";
        initComponent(GlobalData.stocks.getStockTickers());
    }

    public JDialogTrade(boolean modal, String ptfName) {
        super();
        this.setModal(modal);
        this.ptfName = ptfName;
        initComponent(GlobalData.stocks.getStockTickers());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Gestion des évènements de validation
    ////////////////////////////////////////dblQuantity///////////////////////////////////////////////////////
    public void actionPerformed(ActionEvent evt) {

        if (evt.getActionCommand().compareTo("Close") == 0) {
            this.dispose();
        } else if (evt.getActionCommand().compareTo("Validate") == 0) // procédure de Sauvegarde de la transaction
        {
            Transaction transaction;
            String instrumentFrom = Objects.requireNonNull(comboBoxFrom.getSelectedItem()).toString();
            String instrumentTo = Objects.requireNonNull(comboBoxTo.getSelectedItem()).toString();

            double zoneSaisie1, zoneSaisie2, zoneSaisie3, zoneSaisie4;
            try {
                zoneSaisie1 = Double.parseDouble(textFieldQuantite.getText());
                zoneSaisie2 = Double.parseDouble(textFieldPrice.getText());
                zoneSaisie3 = Double.parseDouble(textFieldFees.getText());
                zoneSaisie4 = Double.parseDouble(textFieldMontant.getText());
            } catch (NumberFormatException e) {
                zoneSaisie1 = zoneSaisie2 = zoneSaisie3 = zoneSaisie4 = 0d;
            }

            switch (typeTransaction) {
                case buy -> {
                    transaction = new Transaction();
                    transaction.setDateTransation(LocalDateTime.now());
                    transaction.setInstrumentName(instrumentTo);
                    transaction.setTransactionType(enTypeTransaction.buy);
                    transaction.setNbreShare(zoneSaisie1);
                    transaction.setEurPrice(zoneSaisie2);
                    transaction.setMontant(enTypeMontant.Fees, zoneSaisie3);
                    transaction.setMontant(enTypeMontant.MontantWithoutFees, transaction.getNbreShare() * transaction.getEurPrice());
                    transaction.setMontant(enTypeMontant.MontantWithFees, transaction.getMontant(enTypeMontant.MontantWithoutFees) + transaction.getMontant(enTypeMontant.Fees));
                    transaction.setNotes("Saisie via écran le :" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));
                    // enregistrement de la transaction
                    transactions.add(transaction);
                }
                case sell -> {
                    transaction = new Transaction();
                    transaction.setDateTransation(LocalDateTime.now());
                    transaction.setInstrumentName(instrumentFrom);
                    transaction.setTransactionType(enTypeTransaction.sell);
                    transaction.setNbreShare(zoneSaisie1);
                    transaction.setEurPrice(zoneSaisie2);
                    transaction.setMontant(enTypeMontant.Fees, zoneSaisie3);
                    transaction.setMontant(enTypeMontant.MontantWithoutFees, transaction.getNbreShare() * transaction.getEurPrice());
                    transaction.setMontant(enTypeMontant.MontantWithFees, transaction.getMontant(enTypeMontant.MontantWithoutFees) + transaction.getMontant(enTypeMontant.Fees));
                    transaction.setNotes("Saisie via écran le :" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));
                    transactions.add(transaction);
                }
                case arbitrate -> {
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////
                    // c'est une vente de l'instrument From
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////
                    transaction = new Transaction();
                    transaction.setDateTransation(LocalDateTime.now());
                    transaction.setInstrumentName(instrumentFrom);
                    transaction.setTransactionType(enTypeTransaction.sell);
                    transaction.setNbreShare(zoneSaisie1);
                    transaction.setMontant(enTypeMontant.Fees, zoneSaisie3);
                    transaction.setMontant(enTypeMontant.MontantWithoutFees, zoneSaisie4 - zoneSaisie3);
                    transaction.setMontant(enTypeMontant.MontantWithFees, zoneSaisie4);
                    transaction.setEurPrice(transaction.getMontant(enTypeMontant.MontantWithoutFees) / transaction.getNbreShare());
                    transaction.setNotes("Saisie via écran le :" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));
                    transactions.add(transaction);

                    ////////////////////////////////////////////////////////////////////////////////////////////////////////
                    // Et un achat de l'instrument To
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////
                    transaction = new Transaction();
                    transaction.setDateTransation(LocalDateTime.now());
                    transaction.setInstrumentName(instrumentTo);
                    transaction.setTransactionType(enTypeTransaction.buy);
                    transaction.setNbreShare(zoneSaisie2);
                    transaction.setMontant(enTypeMontant.Fees, 0d);
                    transaction.setMontant(enTypeMontant.MontantWithoutFees, zoneSaisie4);
                    transaction.setMontant(enTypeMontant.MontantWithFees, zoneSaisie4 + transaction.getMontant(enTypeMontant.Fees));
                    transaction.setEurPrice(transaction.getMontant(enTypeMontant.MontantWithoutFees) / transaction.getNbreShare());
                    transaction.setNotes("Saisie via écran le :" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));
                    transactions.add(transaction);
                }
            }

            // récupération du porefeuille courrant
            for (Transaction trans : transactions)
                GlobalData.portfeuilles.get(ptfName).addTransaction(trans);

            this.dispose();

        } else
            System.out.println("OIther");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALISATION DE LA FENETRE
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initComponent(ArrayList<String> listIntrument) {
        JPanel panelDate = new JPanel();

        JLabel lblPtfName = new JLabel("Nom du Portfeuille");
        comboPtf = new JComboBox<>(GlobalData.portfeuilles.keySet().toArray());
        if (ptfName != null) {
            comboPtf.setSelectedItem(ptfName);
            comboPtf.setEnabled(true);
        }
        panelDate.add(lblPtfName);
        panelDate.add(comboPtf);
        JLabel lblDate = new JLabel("Date du Trade");
        panelDate.add(lblDate);

        JTextField dateTransaction = new JTextField();
        panelDate.add(dateTransaction);

        listIntrument.add(0, "EURO");
        comboBoxFrom = new JComboBox<>(listIntrument.toArray());
        comboBoxTo = new JComboBox<>(listIntrument.toArray());
        JPanel panelInstrument = new JPanel();
        JLabel lblInsrumentFrom = new JLabel("From");
        panelInstrument.add(lblInsrumentFrom);
        panelInstrument.add(comboBoxFrom);
        JLabel lblInsrumentTo = new JLabel("To");
        panelInstrument.add(lblInsrumentTo);
        panelInstrument.add(comboBoxTo);

        // PANEL Quantité
        textFieldQuantite.setColumns(10);
        textFieldPrice.setColumns(10);
        textFieldMontant.setColumns(10);
        textFieldFees.setColumns(10);

        JPanel panelQuantite = new JPanel();
        panelQuantite.add(lblQuantite);
        panelQuantite.add(textFieldQuantite);
        panelQuantite.add(lblPrice);
        panelQuantite.add(textFieldPrice);
        panelQuantite.add(lblFees);
        panelQuantite.add(textFieldFees);
        panelQuantite.add(lblMontant);
        panelQuantite.add(textFieldMontant);

        // panel de controliie
        JButton btnValidate = new JButton("Validate");
        JPanel panelCommande = new JPanel();
        panelCommande.add(btnValidate, BorderLayout.EAST);
        JButton btnClose = new JButton("Close");
        panelCommande.add(btnClose, BorderLayout.EAST);

        // ajout des controle
        comboBoxFrom.addActionListener(e -> manageTypeTransaction());
        comboBoxTo.addActionListener(e -> manageTypeTransaction());
        comboPtf.addActionListener(e -> ptfName = comboPtf.getItemAt(comboPtf.getSelectedIndex()).toString());

        btnClose.setActionCommand("Close");
        btnClose.addActionListener(this);
        btnValidate.setActionCommand("Validate");
        btnValidate.addActionListener(this);

        // Compostion du Panel
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        FntGUI.setBagContraint(gbc, 0, 0, 1, 1, 0.25f, 0.75f);
        this.add(panelDate, gbc);

        FntGUI.setBagContraint(gbc, 0, 1, 1, 1, 0.25f, 0.75f);
        this.add(panelInstrument, gbc);

        FntGUI.setBagContraint(gbc, 0, 2, 1, 1, 0.25f, 0.75f);
        this.add(panelQuantite, gbc);

        FntGUI.setBagContraint(gbc, 0, 3, 1, 1, 0.25f, 0.75f);
        this.add(panelCommande, gbc);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // DETERMINATION DU TYPE DE TRANSACTION EUR/X, X/EUR, X/Y
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void manageTypeTransaction() {
        String InstrumentFrom = (String) comboBoxFrom.getSelectedItem();
        String InstrumentTo = (String) comboBoxTo.getSelectedItem();
        //LocalDateTime dateTime = LocalDateTime.now();//dateTransaction.getModel().getValue();

        assert InstrumentFrom != null;
        if ((InstrumentFrom.compareTo("EURO") == 0) && ((InstrumentTo != null ? InstrumentTo.compareTo("EURO") : 0) != 0)) {
            lblQuantite.setText("Qte " + InstrumentTo);
            lblPrice.setText("Prix " + InstrumentTo + "/" + InstrumentFrom);
            lblFees.setText("Frais");
            textFieldQuantite.setEnabled(true);
            textFieldMontant.setEnabled(true);
            textFieldFees.setEnabled(true);
            textFieldPrice.setEnabled(true);
            typeTransaction = enTypeTransaction.buy;
        } else if ((InstrumentFrom.compareTo("EURO") != 0) && ((InstrumentTo != null ? InstrumentTo.compareTo("EURO") : 0) == 0)) {
            lblQuantite.setText("Qte " + InstrumentFrom);
            lblPrice.setText("Prix " + InstrumentFrom + "/" + InstrumentTo);
            lblFees.setText("Frais");
            textFieldQuantite.setEnabled(true);
            textFieldMontant.setEnabled(true);
            textFieldFees.setEnabled(true);
            textFieldPrice.setEnabled(true);
            typeTransaction = enTypeTransaction.sell;
        } else if ((InstrumentFrom.compareTo("EURO") != 0) && ((InstrumentTo != null ? InstrumentTo.compareTo("EURO") : 0) != 0)) {
            lblQuantite.setText("Qte " + InstrumentFrom);
            lblPrice.setText("Qte " + InstrumentTo);
            lblFees.setText("Frais");

            textFieldQuantite.setEnabled(true);
            textFieldMontant.setEnabled(true);
            textFieldFees.setEnabled(true);
            textFieldPrice.setEnabled(true);
            typeTransaction = enTypeTransaction.arbitrate;
        } else {
            lblQuantite.setText("Qte ");
            lblPrice.setText("Prix ");
            lblFees.setText("Frais");
            textFieldQuantite.setEnabled(false);
            textFieldMontant.setEnabled(false);
            textFieldFees.setEnabled(false);
            textFieldPrice.setEnabled(false);
            typeTransaction = enTypeTransaction.other;
        }
        this.pack();
    }

}



