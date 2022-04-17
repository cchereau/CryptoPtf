package gui.screenTransaction;

import global.EnumCrypto;
import global.GlobalData;
import global.fonction.FntGUI;
import ptfManagement.Portefeuille;
import ptfManagement.Transaction;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JDialogAjustement extends JDialog {

    private final JComboBox<?> cmbInstrument = new JComboBox<>(GlobalData.stocks.getStockTickers().toArray());
    private final JTextField edValueReel = new JTextField(10);
    private final JComboBox<?> cmbPtf = new JComboBox<>(GlobalData.portfeuilles.keySet().toArray());

    public JDialogAjustement(Frame owner, boolean modal) {
        super(owner, modal);

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        FntGUI.setBagContraint(gbc, 0, 0, 1, 1, 0.25f, 0.75f);
        this.add(cmbPtf, gbc);

        FntGUI.setBagContraint(gbc, 1, 0, 1, 1, 0.25f, 0.75f);
        this.add(cmbInstrument, gbc);

        FntGUI.setBagContraint(gbc, 0, 1, 1, 1, 0.25f, 0.75f);
        JLabel lblValeurReel = new JLabel("Valeur Réelle");
        this.add(lblValeurReel, gbc);

        FntGUI.setBagContraint(gbc, 1, 1, 1, 1, 0.25f, 0.75f);
        this.add(edValueReel, gbc);

        FntGUI.setBagContraint(gbc, 0, 2, 1, 1, 0.25f, 0.75f);
        JButton btnValidation = new JButton("Valider");
        this.add(btnValidation, gbc);

        FntGUI.setBagContraint(gbc, 1, 2, 1, 1, 0.25f, 0.75f);
        JButton btnClose = new JButton("Clôturer");
        this.add(btnClose, gbc);

        btnValidation.addActionListener(e -> {

            Portefeuille ptf = GlobalData.portfeuilles.get(cmbPtf.getItemAt(cmbPtf.getSelectedIndex()).toString());
            String strInstrument = cmbInstrument.getItemAt(cmbInstrument.getSelectedIndex()).toString();

            Double nbreShareReel = ptf.getPosition(strInstrument).getPositionQuantiteShare(EnumCrypto.enTypeTransaction.all);
            Double nbreShareSupposee = Double.parseDouble(edValueReel.getText());

            Transaction transaction = new Transaction();
            transaction.setDateTransation(LocalDateTime.now());
            transaction.setTransactionType(EnumCrypto.enTypeTransaction.arbitrate);
            transaction.setNbreShare(nbreShareSupposee - nbreShareReel);
            transaction.setInstrumentName(strInstrument);
            transaction.setMontant(EnumCrypto.enTypeMontant.Fees, 0d);
            transaction.setEurPrice(0d);
            transaction.setMontant(EnumCrypto.enTypeMontant.MontantWithoutFees, 0d);
            transaction.setMontant(EnumCrypto.enTypeMontant.MontantWithFees, 0d);
            transaction.setNotes("Ajustement " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));
            ptf.addTransaction(transaction);
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////


    }


}
