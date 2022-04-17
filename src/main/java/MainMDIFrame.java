import com.mashape.unirest.http.exceptions.UnirestException;
import dataExchangeIO.CryptoParser;
import global.Configuration;
import global.GlobalCte;
import global.GlobalData;
import gui.screenPortfolio.JInternalFramePortfolio;
import gui.screenQuotation.JDialogQuotationHistory;
import gui.screenQuotation.JDialogQuotationsUpdate;
import gui.screenStockComposite.JDialogStockComposite;
import gui.screenStrategie.JInternalFrameStrategieStrategieAction;
import gui.screenTransaction.JDialogAjustement;
import gui.screenTransaction.JDialogImportTransaction;
import gui.screenTransaction.JDialogTrade;
import ptfManagement.Portefeuille;
import ptfManagement.Position;
import stock.Stocks;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;


public class MainMDIFrame extends JFrame {

    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainMDIFrame() {
        super("JDesktopPane / JInternalFrame sample");

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Chargement des Stocks Prices
        ///////////////////////////////////////////////////////////////////////////////////////////
        GlobalData.stocks = new Stocks();
        try {
            GlobalData.stocks = CryptoParser.ReadStockJSON();
            System.out.println("Chargement des Stock Price -> OK");

        } catch (IOException | ParseException | org.json.simple.parser.ParseException | UnirestException e) {
            System.out.println("Impossible de charger les Stock Price");
            return;
        }

        //////////////////////////////////////////////////////////////////////////////////////////////
        // Chargement du portefeuille
        /////////////////////////////////////////////////////////////////////////////////////////////
        GlobalData.portfeuilles = CryptoParser.ReadPtfJSON();
        if (GlobalData.portfeuilles.size() == 0) {
            JOptionPane.showMessageDialog(this.getContentPane(), "Aucune position dans le portefeuille");
            return;
        }

        //////////////////////////////////////////////////////////////////////////////////////////////
        // Rattachement des prix au portefeuille
        //////////////////////////////////////////////////////////////////////////////////////////////
        for (Map.Entry<String, Portefeuille> ptf : GlobalData.portfeuilles.entrySet())
            for (Position position : ptf.getValue().getPositions())
                position.setStockPrice(GlobalData.stocks.getStock(position.getInstName()));

        JInternalFramePortfolio internalFramePtf = new JInternalFramePortfolio();
        desktopPane.add(internalFramePtf);
        internalFramePtf.setVisible(true);
        try {
            internalFramePtf.setMaximizable(true); // maximize
            internalFramePtf.setIconifiable(true); // set minimize
            internalFramePtf.setClosable(true); // set closed
            internalFramePtf.setResizable(true); // set resizable
            internalFramePtf.pack();
            internalFramePtf.setMaximum(true);
            internalFramePtf.show();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);

        // creation du ménu
        this.createMenuBar();

        // Autres Décorations
        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.add(desktopPane, BorderLayout.CENTER);

        ////////////////////////////////////////////////////////////////////////////////////
        // Gestion de la fenêtre JTree des options
        ///////////////////////////////////////////////////////////////////////////////////
        contentPane.add(new JTree(), BorderLayout.WEST);
        contentPane.add(new JLabel("La barre de status"), BorderLayout.SOUTH);

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            Configuration config = new Configuration();
            config.setConfig(GlobalCte.config_FichierName, GlobalCte.config_ptfDirectoryUpdate, "C:\\Users\\chris\\Downloads\\");
            config.setConfig(GlobalCte.config_FichierName, GlobalCte.config_ptfDirectory, "C:\\Developpement\\CryptoCurrency\\CryptoPtf\\Data\\ptf\\");
            config.setConfig(GlobalCte.config_FichierName, GlobalCte.config_ptfDirectoryBackup, "C:\\Developpement\\CryptoCurrency\\CryptoPtf\\Data\\ptf\\Backup\\");
            config.setConfig(GlobalCte.config_FichierName, GlobalCte.config_ptfFileName, "CptfTransactions.json");
            config.setConfig(GlobalCte.config_FichierName, GlobalCte.config_stockPriceDirectory, "C:\\Developpement\\CryptoCurrency\\CryptoPtf\\Data\\StockPrice\\");
            config.setConfig(GlobalCte.config_FichierName, GlobalCte.config_stockPriceIndex, "INDEX_STOCK.json");
            config.setConfig(GlobalCte.config_FichierName, GlobalCte.config_stockPriceExtension, ".json");

            GlobalData.configPortefeuilleDirectory = config.getConfig(GlobalCte.config_FichierName, GlobalCte.config_ptfDirectory);
            GlobalData.configPortefeuilleJSON = config.getConfig(GlobalCte.config_FichierName, GlobalCte.config_ptfFileName);
            GlobalData.configPortfeuilleDirectoryUpdate = config.getConfig(GlobalCte.config_FichierName, GlobalCte.config_ptfDirectoryUpdate);
            GlobalData.configPortefeuilleDirectoryBackup = config.getConfig(GlobalCte.config_FichierName, GlobalCte.config_ptfDirectoryBackup);
            GlobalData.configStockPriceDirectory = config.getConfig(GlobalCte.config_FichierName, GlobalCte.config_stockPriceDirectory);
            GlobalData.configStockPriceFileIndex = config.getConfig(GlobalCte.config_FichierName, GlobalCte.config_stockPriceIndex);
            GlobalData.configStockPriceExtension = config.getConfig(GlobalCte.config_FichierName, GlobalCte.config_stockPriceExtension);


        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {


            ///////////////////////////////////////////////////////////////////////////////////////////
            // Chargement de la fenetre portefeuille
            ///////////////////////////////////////////////////////////////////////////////////////////
            try {
                UIManager.setLookAndFeel(new NimbusLookAndFeel());
                MainMDIFrame mainMDIFrame;
                mainMDIFrame = new MainMDIFrame();
                mainMDIFrame.setVisible(true);
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* Methode de construction de la barre de menu */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void createMenuBar() {

        JMenuBar menuBar = new JMenuBar();
        JMenu mnuFile = new JMenu("Investissement");
        JMenuItem mnuOpenCoinBaseFile = new JMenuItem("File From Coin Base");
        mnuFile.add(mnuOpenCoinBaseFile);
        mnuOpenCoinBaseFile.addActionListener(this::mnuPtfFileFromCoinBase);
        JMenuItem mnuSavePortfolio = new JMenuItem("Save Portfolio ...");
        mnuFile.add(mnuSavePortfolio);
        mnuSavePortfolio.addActionListener(this::mnuSavePortefeuille);
        menuBar.add(mnuFile);

        JMenu mnuEdit = new JMenu("Edit");
        JMenuItem mnuUndo = new JMenuItem("Undo");
        mnuEdit.add(mnuUndo);
        JMenuItem mnuRedo = new JMenuItem("Redo");
        mnuEdit.add(mnuRedo);
        mnuEdit.addSeparator();
        JMenuItem mnuCopy = new JMenuItem("Copy");
        mnuEdit.add(mnuCopy);
        JMenuItem mnuCut = new JMenuItem("Cut");
        mnuEdit.add(mnuCut);
        JMenuItem mnuPaste = new JMenuItem("Paste");
        mnuEdit.add(mnuPaste);
        menuBar.add(mnuEdit);

        JMenu mnuWindow = new JMenu("Window");
        JMenuItem mnuCascade = new JMenuItem("Cascade");
        mnuCascade.addActionListener(this::mnuCascadeListener);
        mnuWindow.add(mnuCascade);
        JMenuItem mnuTileHorizontaly = new JMenuItem("Tile horizontaly");
        mnuTileHorizontaly.addActionListener(this::mnuTileHorizontalyListener);
        mnuWindow.add(mnuTileHorizontaly);
        JMenuItem mnuTileVerticaly = new JMenuItem("Tile verticaly");
        mnuTileVerticaly.addActionListener(this::mnuTileVerticalyListener);
        mnuWindow.add(mnuTileVerticaly);
        JMenuItem mnuIconifyAll = new JMenuItem("Iconify all");
        mnuIconifyAll.addActionListener(this::mnuIconifyAll);
        mnuWindow.add(mnuIconifyAll);
        menuBar.add(mnuWindow);

        JMenu mnuPortefeuille = new JMenu("Portefeuille");
        JMenuItem mnuPtfManagement = new JMenuItem("TO DO Management");
        mnuPortefeuille.add(mnuPtfManagement);
        menuBar.add(mnuPortefeuille);

        JMenu mnuPosition = new JMenu("Position");
        JMenuItem mnuWinPosition = new JMenuItem("Win Position");
        mnuWinPosition.addActionListener(this::mnuWinPosition);
        mnuPosition.add(mnuWinPosition);
        menuBar.add(mnuPosition);

        JMenu mnuTransaction = new JMenu("Transactions");
        JMenuItem mnuTransactionsInsert = new JMenuItem("Nouvelle");
        mnuTransaction.add(mnuTransactionsInsert);
        mnuTransactionsInsert.addActionListener(this::mnuTransactionsInsert);
        JMenuItem mnuTransactionsAjust = new JMenuItem("Ajustement");
        mnuTransaction.add(mnuTransactionsAjust);
        mnuTransactionsAjust.addActionListener(this::mnuTransactionsAjustement);
        menuBar.add(mnuTransaction);

        JMenu mnuSimulation = new JMenu("strategie");
        JMenuItem mnuSimulationInsert = new JMenuItem("Nouvelle");
        mnuSimulation.add(mnuSimulationInsert);
        mnuSimulationInsert.addActionListener(this::mnuSimulationsInsert);
        menuBar.add(mnuSimulation);

        JMenu mnuQuotation = new JMenu("Quotations");
        JMenuItem mnuQuotationsUpdate = new JMenuItem("Update");
        mnuQuotation.add(mnuQuotationsUpdate);
        mnuQuotationsUpdate.addActionListener(this::mnuQuotationsUpdate);
        JMenuItem mnuQuotationHistory = new JMenuItem("Show History");
        mnuQuotation.add(mnuQuotationHistory);
        mnuQuotationHistory.addActionListener(this::mnuQuotationHistory);
        mnuQuotation.add(new JSeparator());
        JMenuItem mnuQuotationSimulation = new JMenuItem("Oldsynthese");
        mnuQuotationSimulation.addActionListener(this::mnuQuotationSimulation);
        mnuQuotation.add(mnuQuotationSimulation);
        mnuQuotation.add(new JSeparator());
        JMenuItem mnuQuotationMatrix = new JMenuItem("Matrice de Quotation");
        mnuQuotationMatrix.addActionListener(this::mnuQuotationMatrix);
        mnuQuotation.add(mnuQuotationMatrix);
        menuBar.add(mnuQuotation);

        JMenu mnuHelp = new JMenu("Help");
        menuBar.add(mnuHelp);

        this.setJMenuBar(menuBar);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MNU PORTEFEUILLE
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mnuPtfFileFromCoinBase(ActionEvent actionEvent) {

        JDialogImportTransaction panelImportTransaction = new JDialogImportTransaction();

        panelImportTransaction.setVisible(true);
    }


    private void mnuSavePortefeuille(ActionEvent actionEvent) {
        // sauvegarde du fichier
        CryptoParser.SavePtfJSON(GlobalData.portfeuilles);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MNU PORTEFEUILLE
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mnuWinPosition(ActionEvent actionEvent) {
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Menu Simulation
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mnuSimulationsInsert(ActionEvent actionEvent) {
        JInternalFrameStrategieStrategieAction internalFrameStrategie = new JInternalFrameStrategieStrategieAction(GlobalData.portfeuilles);
        internalFrameStrategie.setVisible(true);

    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Menu Transanctions
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mnuTransactionsInsert(ActionEvent actionEvent) {
        // chargement de la forme dialogue
        JDialogTrade dlg = new JDialogTrade(this, true);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setTitle("Gestion des Prix Positions");
        dlg.pack();
        dlg.setLocationRelativeTo(getParent());
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true);
        // libération de la dialogue forme
        dlg.dispose();
        CryptoParser.SavePtfJSON(GlobalData.portfeuilles);
    }

    private void mnuTransactionsAjustement(ActionEvent actionEvent) {
        JDialogAjustement dlg = new JDialogAjustement(this, true);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setTitle("Gestion des Prix Positions");
        dlg.pack();
        dlg.setLocationRelativeTo(getParent());
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true);
        // libération de la dialogue forme
        dlg.dispose();
        CryptoParser.SavePtfJSON(GlobalData.portfeuilles);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Menu Quotation
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mnuQuotationsUpdate(ActionEvent actionEvent) {
        // chargement de la forme dialogue
        JDialogQuotationsUpdate dlg = new JDialogQuotationsUpdate(GlobalData.stocks.getStocks());
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setTitle("Gestion des Prix Positions");
        dlg.pack();
        dlg.setLocationRelativeTo(getParent());
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true);
    }

    private void mnuQuotationHistory(ActionEvent actionEvent) {
        // chargement de la forme dialogue
        JDialogQuotationHistory dlg = new JDialogQuotationHistory(GlobalData.stocks);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setTitle("Gestion des Prix Positions");
        dlg.pack();
        dlg.setLocationRelativeTo(getParent());
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true);
    }

    private void mnuQuotationSimulation(ActionEvent actionEvent) {
    }

    private void mnuQuotationMatrix(ActionEvent actionEvent) {
        JDialogStockComposite dlg = new JDialogStockComposite();
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setTitle("Price Matrix");
        dlg.pack();
        dlg.setLocationRelativeTo(getParent());
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Menu Autres (Windows,..)
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* Permet de  cascader toutes les fenêtres internes */
    private void mnuCascadeListener(ActionEvent event) {
        JInternalFrame[] internalFrames = desktopPane.getAllFrames();
        for (int i = 0; i < internalFrames.length; i++) {
            internalFrames[i].setBounds(i * 20, i * 20, 300, 200);
        }
    }

    /* Permet d'aligner horizontalement toutes les fenêtres internes */
    private void mnuTileHorizontalyListener(ActionEvent event) {
        JInternalFrame[] internalFrames = desktopPane.getAllFrames();
        int frameWidth = desktopPane.getWidth() / internalFrames.length;
        for (int i = 0; i < internalFrames.length; i++) {
            internalFrames[i].setBounds(i * frameWidth, 0, frameWidth, desktopPane.getHeight());
        }
    }

    /* Permet d'aligner verticalement toutes les fenêtres internes */
    private void mnuTileVerticalyListener(ActionEvent event) {
        JInternalFrame[] internalFrames = desktopPane.getAllFrames();
        int frameHeight = desktopPane.getHeight() / internalFrames.length;
        for (int i = 0; i < internalFrames.length; i++) {
            internalFrames[i].setBounds(0, i * frameHeight, desktopPane.getWidth(), frameHeight);
        }
    }

    /* Permet d'iconifier toutes les fenêtres internes */
    private void mnuIconifyAll(ActionEvent event) {
        JInternalFrame[] internalFrames = desktopPane.getAllFrames();
        for (JInternalFrame internalFrame : internalFrames) {
            try {
                internalFrame.setIcon(true);
            } catch (PropertyVetoException exception) {
                exception.printStackTrace();
            }
        }
    }


}
