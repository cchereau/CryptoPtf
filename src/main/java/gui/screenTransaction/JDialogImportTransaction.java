package gui.screenTransaction;

import dataExchangeIO.CoinbaseParserPtf;
import global.EnumCrypto;
import global.GlobalData;
import global.fonction.FntGUI;
import gui.commonComponent.JPanelTable.Transaction.JPanelTableTransaction;
import gui.commonComponent.JPanelTable.Transaction.ListnerInstrument;
import ptfManagement.Portefeuille;
import ptfManagement.Position;
import ptfManagement.Transaction;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JDialogImportTransaction extends JDialog implements ListnerInstrument {

    private final JPanelTableTransaction tradeToImport = new JPanelTableTransaction();
    private final JPanelTableTransaction tradeInPtf = new JPanelTableTransaction();
    private final JComboBox<String> comboBoxPtf;
    private ArrayList<Transaction> transactions;

    public JDialogImportTransaction() {
        super();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Zones de selection des donnes
        //////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Panel chargement du fichier source
        JPanel panelManagePtf = new JPanel();
        JButton btnLoafFile = new JButton("Load");
        btnLoafFile.addActionListener(e -> {
            File workingDirectory = new File(GlobalData.configPortfeuilleDirectoryUpdate);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(workingDirectory);
            Action details = fileChooser.getActionMap().get("viewTypeDetails");
            details.actionPerformed(null);


            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                transactions = CoinbaseParserPtf.ParseFileFromCoinBase(file.getParent(), file.getName());
                tradeToImport.addTransactions(transactions);
            }
        });
        panelManagePtf.add(btnLoafFile);
        comboBoxPtf = new JComboBox<>(GlobalData.portfeuilles.descendingKeySet().toArray(new String[0]));
        panelManagePtf.add(comboBoxPtf);

        // panel de validation ou d'abandon
        JPanel panelButtonActionOnTransaction = new JPanel();
        JButton btnValidate = new JButton("Valider");
        btnValidate.addActionListener(e -> {
                    Portefeuille ptf = GlobalData.portfeuilles.get(comboBoxPtf.getItemAt(comboBoxPtf.getSelectedIndex()));
                    updateData(transactions, ptf);
                }
        );
        panelButtonActionOnTransaction.add(btnValidate);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> {
                    Portefeuille ptf = GlobalData.portfeuilles.get(Objects.requireNonNull(comboBoxPtf.getSelectedItem()).toString());
                    updateData(transactions, ptf);
                }
        );
        panelButtonActionOnTransaction.add(btnCancel);


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Zones de composition des écrans
        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        FntGUI.setBagContraint(gbc, 0, 0, 2, 1, 0.25f, 0.75f);
        this.add(panelManagePtf, gbc);

        FntGUI.setBagContraint(gbc, 0, 1, 1, 1, 0.25f, 0.75f);
        this.add(tradeToImport, gbc);

        FntGUI.setBagContraint(gbc, 0, 2, 1, 1, 0.25f, 0.75f);
        this.add(tradeInPtf, gbc);

        FntGUI.setBagContraint(gbc, 0, 3, 1, 1, 0.25f, 0.75f);
        this.add(panelButtonActionOnTransaction, gbc);


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        this.tradeToImport.setListenerLineSelected(this);

        this.setModal(true);
        this.pack();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GESTION
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void updateData(ArrayList<Transaction> inputData, Portefeuille ptfIn) {

        Portefeuille ptfVituel = new Portefeuille();
        for (Transaction transaction : inputData)
            ptfVituel.addTransaction(transaction);

        for (Position position : ptfVituel.getPositions()) {
            // Récupération de la date de debit et de la date de fin des nouvelles tranasactions
            LocalDateTime datDeb = position.getPositionTypeAssetTypeDate(EnumCrypto.enTypeAsset.Position, EnumCrypto.enTypeDate.firstDate);
            LocalDateTime datFin = position.getPositionTypeAssetTypeDate(EnumCrypto.enTypeAsset.Position, EnumCrypto.enTypeDate.lastDate);
            Position posIn = ptfIn.getPosition(position.getInstName());

            if (posIn != null) {
                // suppresion de l'ensemble des transactions entre les deux dates
                ArrayList<EnumCrypto.enTypeTransaction> typeTransactionInScope = new ArrayList<>();
                typeTransactionInScope.add(EnumCrypto.enTypeTransaction.buy);
                typeTransactionInScope.add(EnumCrypto.enTypeTransaction.sell);
                typeTransactionInScope.add(EnumCrypto.enTypeTransaction.received);
                posIn.removeTransaction(datDeb, datFin, typeTransactionInScope);
                // ajout des transaction du ptf temporaire
                posIn.addTransaction(position.getTransactions(EnumCrypto.enTypeTransaction.all));
            } else
                ptfIn.addPosition(position);
        }
    }

    @Override
    public void onLineSelected() {
        Transaction transaction = tradeToImport.getSlectedTransaction();
        // récupération du portefeuille
        Portefeuille ptf = GlobalData.portfeuilles.get(comboBoxPtf.getItemAt(comboBoxPtf.getSelectedIndex()));
        // récupération de la position
        Position position = ptf.getPosition(transaction.getInstrumentName());
        // récupération des tranasactions à la date
        ArrayList<Transaction> transactions = position.getTransactions(transaction.getTransactionType());
        Predicate<Transaction> transactionPredicate = (Transaction p) -> p.getDateTransaction().equals(transaction.getDateTransaction());
        tradeInPtf.addTransactions(transactions.stream().filter(transactionPredicate).collect(Collectors.toCollection(ArrayList<Transaction>::new)));
    }


}
