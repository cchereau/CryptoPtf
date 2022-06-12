package gui.screenPosition;

import global.EnumCrypto;
import global.fonction.FntGUI;
import gui.commonComponent.JPanelGraphique.JPanelStockGraph;
import gui.commonComponent.JPanelTable.Transaction.JPanelTableTransaction;
import gui.commonComponent.JPanelTable.Transaction.ListnerInstrument;
import ptfManagement.Portefeuille;
import ptfManagement.Position;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class JFramePosition extends JFrame implements ListnerInstrument {

    private final Portefeuille ptf;
    private final JPanelStockGraph panelJPanelStockGraphGraphique = new JPanelStockGraph(true);
    private final JPanelPositionSynthese panelPositionSynthese = new JPanelPositionSynthese();
    private final JPanelTableTransaction panelPostionTradeTable = new JPanelTableTransaction();
    private final JPanelPositionStrategieStrategieAction panelPositionStrategie = new JPanelPositionStrategieStrategieAction();
    private final JPanelComposition panelPositionComposition = new JPanelComposition();
    private final JTabbedPane tabbedPane = new JTabbedPane();

    private Integer index;

    public JFramePosition(Portefeuille ptf, String assetCode) {
        super();

        this.ptf = ptf;
        index = getIndexPosition(assetCode);

        JButton btnFirst = new JButton("<<");
        JButton btnPrevious = new JButton("<");
        JButton btnNext = new JButton(">");
        JButton btnLast = new JButton(">>");

        JToolBar toolBar = new JToolBar();
        toolBar.setRollover(true);
        toolBar.add(btnFirst);
        toolBar.add(btnPrevious);
        toolBar.addSeparator();
        toolBar.add(btnNext);
        toolBar.add(btnLast);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // conxtruction du panel principal
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        GridBagLayout layoutCurrent = new GridBagLayout();
        JPanel panelPositionCurrent = new JPanel();
        panelPositionCurrent.setLayout(layoutCurrent);
        GridBagConstraints gbcCurrent = new GridBagConstraints();
        gbcCurrent.fill = GridBagConstraints.BOTH;
        FntGUI.setBagContraint(gbcCurrent, 0, 0, 1, 1, 1, 0.5f);
        panelPositionCurrent.add(panelPostionTradeTable, gbcCurrent);
        FntGUI.setBagContraint(gbcCurrent, 0, 1, 1, 1, 1, 0.5f);
        panelPositionCurrent.add(panelJPanelStockGraphGraphique, gbcCurrent);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        // construction du tabPane
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        tabbedPane.add("Position", panelPositionCurrent);
        tabbedPane.add("Composition", panelPositionComposition);
        tabbedPane.add("Stratégie", panelPositionStrategie);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelPositionSynthese, tabbedPane);
        mainSplitPane.setResizeWeight(0.1f);
        mainSplitPane.setSize(new Dimension());

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        FntGUI.setBagContraint(gbc, 0, 0, 1, 5, 0.5f, 0.99f);
        this.add(mainSplitPane, gbc);
        FntGUI.setBagContraint(gbc, 0, 6, 1, 1, 0.5f, 0.01f);
        this.add(toolBar, gbc);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        panelPostionTradeTable.isNameColumnVisible(false);
        this.panelPostionTradeTable.setListenerLineSelected(this);

        // dessin de positions
        btnFirst.addActionListener(e -> {
            index = 0;
            refreshPosition(ptf.getPosition(getAssetPosition(index)));
        });

        btnPrevious.addActionListener(e -> {
            if (index > 0) index--;
            else index = 0;
            refreshPosition(ptf.getPosition(getAssetPosition(index)));
        });

        btnLast.addActionListener(e -> {
            index = ptf.getPositions().size() - 1;
            refreshPosition(ptf.getPosition(getAssetPosition(index)));
        });

        btnNext.addActionListener(e -> {
            if (index < ptf.getPositions().size() - 1) index++;
            else index = ptf.getPositions().size() - 1;
            refreshPosition(ptf.getPosition(getAssetPosition(index)));

        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(1800, 1000));
        this.pack();
        this.setLocationRelativeTo(getParent());
        this.setAlwaysOnTop(false);

        refreshPosition(ptf.getPosition(getAssetPosition(index)));

    }

    private void refreshPosition(Position position) {

        // Rafraichissement du graphique
        panelJPanelStockGraphGraphique.removeSeries();
        panelJPanelStockGraphGraphique.addSeries(position.getStockPrice());
        // refraichissement de la synthèses
        panelPositionSynthese.setPosition(position);
        // rafrichissement des trade
        panelPostionTradeTable.addTransactions(position.getTransactions(EnumCrypto.enTypeTransaction.all));

        // refriachissement de la composition
        panelPositionComposition.setPosition(ptf, position);

        // rafraichissement de la stratégie
        panelPositionStrategie.setPosition(ptf, position);


        tabbedPane.setEnabledAt(2, panelPositionStrategie.isStrategieAvailable());
        tabbedPane.setSelectedIndex(0);
        this.setTitle("Detail de la Position :" + position.getInstName());


    }

    private int getIndexPosition(String asset) {
        ArrayList<String> indexdata = new ArrayList<>(this.ptf.getPositionListInstrument());
        return indexdata.indexOf(asset);
    }

    private String getAssetPosition(Integer index) {
        ArrayList<String> indexed = new ArrayList<>(this.ptf.getPositionListInstrument());
        return indexed.get(index);
    }


    @Override
    public void onLineSelected() {
        panelJPanelStockGraphGraphique.removeSeries();

    }
}
