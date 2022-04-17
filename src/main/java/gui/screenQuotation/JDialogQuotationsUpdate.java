package gui.screenQuotation;

import com.mashape.unirest.http.exceptions.UnirestException;
import dataExchangeIO.YahooStockMarketParser;
import global.EnumCrypto.enTypeDate;
import global.EnumCrypto.enTypeQuotation;
import global.GlobalCte;
import gui.commonComponent.RenderCellule.RenderCurrencyWithColor;
import org.json.simple.parser.ParseException;
import stock.Stock;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;


public class JDialogQuotationsUpdate extends JDialog {

    private final JProgressBar progressBar;
    private final JButton btnUpdate;
    private boolean end = false;
    private boolean running = false;

    public JDialogQuotationsUpdate(Collection<Stock> stocks) {
        super();

        // récupération des valeurs qui peuvent être aut
        Predicate<Stock> prStockUpdatable = stockPrice -> !stockPrice.isUpdateAuto();
        ArrayList<Stock> stockUpdatable = new ArrayList<>(stocks);
        stockUpdatable.removeIf(prStockUpdatable);

        TableModelQuotationsUpdate tableModelStockPrice = new TableModelQuotationsUpdate();
        JTable tableau = new JTable();
        JScrollPane scrollPaneTable = new JScrollPane(tableau);
        tableau.setModel(tableModelStockPrice);
        tableau.getColumnModel().getColumn(GlobalCte.COL_QUOTATIONS_LAST_VALEUR).setCellRenderer(new RenderCurrencyWithColor(false));
        tableModelStockPrice.addAll(stockUpdatable);
        JPanel panelStock = new JPanel();
        panelStock.setBorder(BorderFactory.createTitledBorder("Liste Stock"));
        panelStock.add(scrollPaneTable);

        JPanel panelUpdatePrice = new JPanel();
        panelStock.setBorder(BorderFactory.createTitledBorder("Update price"));
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(stockUpdatable.size());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        btnUpdate = new JButton("Update");
        panelUpdatePrice.add(progressBar);
        panelUpdatePrice.add(btnUpdate);

        tableModelStockPrice.fireTableDataChanged();
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbcPositions = new GridBagConstraints();
        gbcPositions.fill = GridBagConstraints.HORIZONTAL;
        gbcPositions.weightx = gbcPositions.weighty = 1;
        gbcPositions.gridx = 0;
        gbcPositions.gridy = 0;
        this.add(panelStock, gbcPositions);
        gbcPositions.gridx = 0;
        gbcPositions.gridy = 1;
        this.add(panelUpdatePrice, gbcPositions);

        btnUpdate.addActionListener(e -> {

            if (end) {
                progressBar.setValue(0);
                end = false;
                return;
            }

            if (!running)
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        setRun();
                        int currentValue = 0;
                        // recupération de la liste des valeur qui peuvent être récupérée automatiquement


                        for (Stock stock : stockUpdatable) {
                            currentValue++;
                            try {
                                stock.addQuotations(YahooStockMarketParser.getStockHQuotation(stock, true));
                                YahooStockMarketParser.CreateJSONFile(stock);
                                Thread.sleep(100);
                            } catch (IOException | UnirestException | ParseException | InterruptedException ioException) {
                                ioException.printStackTrace();
                            }
                            progressBar.setValue(currentValue);
                            currentValue++;
                        }
                        setEnd();
                    }
                }).start();
        });
    }


    private void setRun() {
        end = false;
        running = true;
        btnUpdate.setEnabled(false);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }

    private void setEnd() {
        end = true;
        running = false;
        btnUpdate.setEnabled(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // TABLE MODEL
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static class TableModelQuotationsUpdate extends AbstractTableModel {

        private final ArrayList<Stock> stocks = new ArrayList<>();

        /**
         * Retourne le titre de la colonne et l'indice
         */
        public String getColumnName(int col) {
            return GlobalCte.entetesQuotationsUpdate[col];
        }

        /**
         * Retourne le nombre de colonnes
         */
        public int getColumnCount() {
            return GlobalCte.entetesQuotationsUpdate.length;
        }

        /**
         * Retourne le nombre de lignes
         */
        public int getRowCount() {
            return this.stocks.size();
        }

        /**
         * Retourne l'objet à l'intersection de ligne et colonne
         */
        @Override
        public Object getValueAt(int row, int col) {

            return switch (col) {
                case GlobalCte.COL_QUOTATIONS_LAST_VALEUR -> this.stocks.get(row).getQuotation(this.stocks.get(row).getPriceTypeDate(enTypeDate.lastDate), enTypeQuotation.close);
                case GlobalCte.COL_QUOTATIONS_CODE -> this.stocks.get(row).getNom();
                case GlobalCte.COL_QUOTATIONS_FIRST_DATE -> this.stocks.get(row).getPriceTypeDate(enTypeDate.firstDate);
                case GlobalCte.COL_QUOTATIONS_LAST_DATE -> this.stocks.get(row).getPriceTypeDate(enTypeDate.lastDate);
                default -> null;
            };
        }

        public void addAll(Collection<Stock> values) {
            stocks.addAll(values);
        }
    }


}


