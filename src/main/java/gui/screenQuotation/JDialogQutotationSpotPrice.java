package gui.screenQuotation;

import global.EnumCrypto.enTypeQuotation;
import ptfManagement.Position;
import stock.StockPrice;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JDialogQutotationSpotPrice extends JDialog {

    public JDialogQutotationSpotPrice(Position position) {
        super();
        this.setTitle("AJout d'un prix Spot " + position.getInstName() + " en date du " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));


        JPanel panelSpotPrice = new JPanel();
        JTextField txtPriceClose = new JTextField(10);
        JLabel lblClose = new JLabel("Close");
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> {
            StockPrice stockPrice = new StockPrice();
            stockPrice.setDate(LocalDate.now());
            stockPrice.setValue(enTypeQuotation.close, Double.valueOf(txtPriceClose.getText()));
            stockPrice.setValue(enTypeQuotation.high, 0d);
            stockPrice.setValue(enTypeQuotation.low, 0d);
            stockPrice.setValue(enTypeQuotation.open, 0d);
            stockPrice.setValue(enTypeQuotation.volume, 0d);
            position.getStockPrice().addQuotation(stockPrice, true);
            dispose();
        });
        panelSpotPrice.add(lblClose);
        panelSpotPrice.add(txtPriceClose);
        panelSpotPrice.add(btnClose);
        this.add(panelSpotPrice);
        this.pack();
    }


}
