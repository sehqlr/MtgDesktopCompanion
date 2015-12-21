package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.pricers.impl.ChannelFireballPricer;
import org.magic.api.pricers.impl.EbayPricer;
import org.magic.api.pricers.impl.MagicTradersPricer;
import org.magic.api.pricers.impl.MagicVillePricer;
import org.magic.api.pricers.impl.TCGPlayerPricer;
import org.magic.api.providers.impl.MtgjsonProvider;
import org.magic.db.HsqlDAO;
import org.magic.gui.components.CollectionPanelGUI;
import org.magic.gui.components.DeckBuilderGUI;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.components.ManaPanel;
import org.magic.gui.components.RssNewsPanel;
import org.magic.gui.components.ThumbnailPanel;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.RarityRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.models.MagicPriceTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.tools.MagicPDFGenerator;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.cache.Cache;
import com.jayway.jsonpath.spi.cache.CacheProvider;

public class MagicGUI extends JFrame {

	private MagicCardDetailPanel detailCardPanel;

	private JXTable tableCards;
	private JXTable tablePrice;

	private JTextField txtMagicSearch;
	private JList<MagicEdition> listEdition;
	private JLabel lblLoading = new JLabel("");
	private JLabel lblNbcard = new JLabel("");
	private JLabel lblCard = new JLabel();
	private JComboBox<String> cboQuereableItems;
	private JButton btnSearch;
	private JPanel panneauHaut;
	private JPanel panneauCard = new JPanel();
	private RssNewsPanel panneauRss;


	static final Logger logger = LogManager.getLogger(MagicGUI.class.getName());

	private static final int INDEX_PRICES = 2;


	MagicCard selected;
	MagicPriceTableModel priceModel;
	MagicEdition selectedEdition;

	private HsqlDAO dao;
	private String defaultLanguage;

	private MagicCardsProvider provider;
	private MagicCardTableModel cardsModeltable;
	private JComboBox<MagicCardNames> cboLanguages;
	private JTextArea txtRulesArea;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmExit;
	private JMenuItem mntmExportGrid;
	private JTabbedPane tabbedCardsView;
	private JScrollPane scrollThumbnails;
	private ThumbnailPanel thumbnailPanel;
	private CmcChartPanel cmcChart;
	private ManaRepartitionPanel manaRepartitionPanel;
	private JMenuItem mntmExportAsPdf;

	private List<MagicCard> cards;
	private TypeRepartitionPanel typeRepartitionPanel;
	private JPanel globalPanel;
	private JTabbedPane tabbedPane;
	private DeckBuilderGUI deckBuilderGUI;
	private RarityRepartitionPanel rarityRepartitionPanel;
	private JPanel editionDetailPanel;
	private MagicEditionDetailPanel magicEditionDetailPanel;
	private JPanel panelEditionRight;
	private JButton btnGenerateBooster;
	private CollectionPanelGUI collectionPanelGUI;
	private JPopupMenu popupMenu = new JPopupMenu();
    private JTabbedPane tabbedCardsInfo ;
    
    
	public static void main(String[] args) {

		CacheProvider.setCache(new Cache() {
			//Not thread safe simple cache
			private Map<String, JsonPath> map = new HashMap<String, JsonPath>();

			@Override
			public JsonPath get(String key) {
				return map.get(key);
			}

			@Override
			public void put(String key, JsonPath jsonPath) {
				map.put(key, jsonPath);
			}
		});



		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
					UIManager.put("Table.alternateRowColor", Color.decode("#E1E4F2"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				MagicCardsProvider	provider = new MtgjsonProvider();
				provider.init();

				MagicGUI gui = new MagicGUI(provider);
				gui.setDefaultLanguage("English");
				gui.setVisible(true);

			}
		});

	}


	protected void setDefaultLanguage(String language) {
		defaultLanguage=language;

	}

	public void loading(boolean show,String text)
	{
		lblLoading.setText(text);
		lblLoading.setVisible(show);
	}


	public void setLookAndFeel(String lookAndFeel)
	{
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			logger.error(e.getStackTrace());
		}
		SwingUtilities.updateComponentTreeUI(this);
	}


	public void initPopupCollection() throws Exception
	{
		JMenu menuItemAdd = new JMenu("Add");

		for(MagicCollection mc : dao.getCollections())
		{
			JMenuItem adds = new JMenuItem(mc.getName());
			adds.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					String collec = ((JMenuItem)e.getSource()).getText();
					loading(true, "add cards to " + collec); 

					for (int i = 0; i < tableCards.getSelectedRowCount(); i++) { 
						MagicCard mc = (MagicCard) tableCards.getValueAt(tableCards.convertRowIndexToModel(tableCards.getSelectedRows()[i]),tableCards.convertColumnIndexToModel(0));
						try {
							dao.saveCard(mc, dao.getCollection(collec));
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(null, e1,"ERROR",JOptionPane.ERROR_MESSAGE);
						}

					}
					collectionPanelGUI.getJTree().refresh();
					loading(false, "");
				}
			});
			menuItemAdd.add(adds);
		}

		popupMenu.add(menuItemAdd);
	}

	public void initGUI() throws Exception
	{
		menuBar = new JMenuBar();
		mnFile = new JMenu("File");
		mntmExportGrid = new JMenuItem("Export as CSV");
		mntmExit = new JMenuItem("Exit");
		mntmExportAsPdf = new JMenuItem("Export as PDF");
		panneauRss = new RssNewsPanel();	

		setSize(new Dimension(1420, 900));
		setTitle("Magic Desktop Companion");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MagicGUI.class.getResource("/res/logo.gif")));
		setJMenuBar(menuBar);

		menuBar.add(mnFile);
		mnFile.add(mntmExportGrid);
		mnFile.add(mntmExportAsPdf);
		mnFile.add(mntmExit);



		JMenu jmnuLook = new JMenu("Look");
		menuBar.add(jmnuLook);
		for(LookAndFeelInfo ui : UIManager.getInstalledLookAndFeels())
		{
			final JMenuItem it = new JMenuItem(ui.getClassName());
			it.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setLookAndFeel(it.getText());
				}
			});
			jmnuLook.add(it);
		}


		DefaultRowSorter sorterPrice = new TableRowSorter<DefaultTableModel>(priceModel);
		DefaultRowSorter sorterCards = new TableRowSorter<DefaultTableModel>(cardsModeltable);

		loading(false,null);
		getContentPane().setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		globalPanel = new JPanel();
		globalPanel.setLayout(new BorderLayout());


		btnSearch = new JButton("Rechercher");
		cboQuereableItems = new JComboBox(provider.getQueryableAttributs());
		txtMagicSearch = new JTextField();
		panneauHaut = new JPanel();
		globalPanel.add(panneauHaut, BorderLayout.NORTH);

		FlowLayout flowLayout = (FlowLayout) panneauHaut.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		txtMagicSearch.setColumns(35);
		lblLoading.setIcon(new ImageIcon(MagicGUI.class.getResource("/res/load.gif")));

		panneauHaut.add(cboQuereableItems);
		panneauHaut.add(txtMagicSearch);
		panneauHaut.add(btnSearch);
		panneauHaut.add(lblLoading);
		panneauCard = new JPanel();
		globalPanel.add(panneauCard, BorderLayout.EAST);
		cboLanguages = new JComboBox<MagicCardNames>();
		JScrollPane scrollEditions = new JScrollPane();
		lblCard.setVerticalAlignment(SwingConstants.TOP);
		panneauCard.setLayout(new BorderLayout(0, 0));


		cmcChart = new CmcChartPanel();
		manaRepartitionPanel = new ManaRepartitionPanel();
		typeRepartitionPanel = new TypeRepartitionPanel();



		panneauCard.add(lblCard, BorderLayout.CENTER);
		panneauCard.add(scrollEditions, BorderLayout.SOUTH);
		listEdition = new JList<MagicEdition>();
		scrollEditions.setViewportView(listEdition);							

		listEdition.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listEdition.setModel(new DefaultListModel<MagicEdition>());
		panneauCard.add(cboLanguages, BorderLayout.NORTH);
		tablePrice = new JXTable();
		detailCardPanel = new MagicCardDetailPanel(new MagicCard());
		tabbedCardsView = new JTabbedPane(JTabbedPane.TOP);
		tabbedCardsInfo = new JTabbedPane(JTabbedPane.TOP);
		scrollThumbnails = new JScrollPane();
		thumbnailPanel = new ThumbnailPanel();

		rarityRepartitionPanel = new RarityRepartitionPanel();

		tableCards = new JXTable();
		tableCards.setColumnControlVisible(true);
		JScrollPane scrollCards = new JScrollPane();
		JScrollPane scrollPaneRules = new JScrollPane();
		JScrollPane scrollPanePrices = new JScrollPane();
		JSplitPane panneauCentral = new JSplitPane();
		globalPanel.add(panneauCentral, BorderLayout.CENTER);
		

		tablePrice.setModel(priceModel);
		tablePrice.setRowSorter(sorterPrice);



		scrollPanePrices.setViewportView(tablePrice);

		panneauCentral.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panneauCentral.setRightComponent(tabbedCardsInfo);
		panneauCentral.setLeftComponent(tabbedCardsView);


		tabbedCardsInfo.addTab("Details", null, detailCardPanel, null);

		editionDetailPanel = new JPanel();
		tabbedCardsInfo.addTab("Edition", null, editionDetailPanel, null);
		editionDetailPanel.setLayout(new BorderLayout(0, 0));

		magicEditionDetailPanel = new MagicEditionDetailPanel();
		editionDetailPanel.add(magicEditionDetailPanel, BorderLayout.CENTER);

		panelEditionRight = new JPanel();
		editionDetailPanel.add(panelEditionRight, BorderLayout.EAST);

		btnGenerateBooster = new JButton("Open a Booster");

		panelEditionRight.add(btnGenerateBooster);
		tabbedCardsInfo.addTab("Prices", null, scrollPanePrices, null);
		tabbedCardsInfo.addTab("Rules", null, scrollPaneRules, null);

		tabbedCardsView.addTab("Result", null, scrollCards, null);
		tabbedCardsView.addTab("Thumbnail", null, scrollThumbnails, null);

		txtRulesArea = new JTextArea();
		txtRulesArea.setLineWrap(true);
		txtRulesArea.setWrapStyleWord(true);
		txtRulesArea.setEditable(false);
		scrollPaneRules.setViewportView(txtRulesArea);
		scrollThumbnails.setViewportView(thumbnailPanel);
		scrollThumbnails.getVerticalScrollBar().setUnitIncrement(10);
		scrollCards.setViewportView(tableCards);



		scrollCards.setMinimumSize(new Dimension(23, 250));
		tabbedCardsInfo.setMinimumSize(new Dimension(23,200));


		tableCards.setRowHeight(ManaPanel.pix_resize);
		tableCards.setModel(cardsModeltable);
		tableCards.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());
		tableCards.setRowSorter(sorterCards);



		tabbedCardsView.addTab("Mana Curve", null, cmcChart, null);

		tabbedCardsView.addTab("Colors", null, manaRepartitionPanel, null);


		tabbedCardsView.addTab("Types", null, typeRepartitionPanel, null);
		tabbedCardsView.addTab("Rarity", null, rarityRepartitionPanel, null);
		globalPanel.add(lblNbcard, BorderLayout.SOUTH);

		deckBuilderGUI = new DeckBuilderGUI(provider);



		tabbedPane.addTab("Search", new ImageIcon(MagicGUI.class.getResource("/res/search.gif")), globalPanel, null);
		tabbedPane.addTab("Deck", new ImageIcon(MagicGUI.class.getResource("/res/book_icon.jpg")), deckBuilderGUI, null);

		collectionPanelGUI = new CollectionPanelGUI(provider,dao);
		tabbedPane.addTab("Collection", new ImageIcon(MagicGUI.class.getResource("/res/collection.png")), collectionPanelGUI, null);
		tabbedPane.addTab("News", new ImageIcon(MagicGUI.class.getResource("/res/rss.png")), panneauRss, null);

		initPopupCollection();


	}


	public void setSelectedCard(MagicCard mc)
	{
		this.selected=mc;
		updateCards();
	}

	public MagicGUI( final MagicCardsProvider provider) {

		try {
			priceModel=new MagicPriceTableModel();
			priceModel.addProvider(new EbayPricer());
			priceModel.addProvider(new MagicVillePricer());
			priceModel.addProvider(new TCGPlayerPricer());
			priceModel.addProvider(new MagicTradersPricer());
			priceModel.addProvider(new ChannelFireballPricer());

			this.provider=provider;

			dao=new HsqlDAO();
			dao.init();
			cardsModeltable = new MagicCardTableModel(provider);

			initGUI();


			btnSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					selectedEdition=null;
					if(txtMagicSearch.getText().equals(""))
						return;
					Thread tsearch = new Thread(new Runnable() {
						public void run() {
							loading(true,"searching");
							try {
								String searchName=URLEncoder.encode(txtMagicSearch.getText(),"UTF-8");
								cards = provider.searchCardByCriteria(cboQuereableItems.getSelectedItem().toString(),searchName);

								cardsModeltable.init(cards);
								cardsModeltable.fireTableDataChanged();
								thumbnailPanel.initThumbnails(cards);
								cmcChart.init(cards);
								typeRepartitionPanel.init(cards);
								manaRepartitionPanel.init(cards);
								rarityRepartitionPanel.init(cards);
								lblNbcard.setText(cardsModeltable.getRowCount()+" items");

							} catch (Exception e) {
								e.printStackTrace();
								JOptionPane.showMessageDialog(null, e.getMessage(),"ERREUR",JOptionPane.ERROR_MESSAGE);
							}
							loading(false,"");
						}
					});

					tsearch.start();



				}
			});

			btnGenerateBooster.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					if(selectedEdition==null)
						selectedEdition = selected.getEditions().get(0);

					try {
						tabbedCardsView.setSelectedIndex(1);
						thumbnailPanel.initThumbnails( provider.openBooster(selectedEdition));

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			tableCards.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent evt) {

					if(SwingUtilities.isRightMouseButton(evt))
					{
						Point point = evt.getPoint();
						//					 		int rowNumber = tableCards.rowAtPoint( point );
						//							ListSelectionModel model = tableCards.getSelectionModel();
						//							model.setSelectionInterval( rowNumber, rowNumber );
						//						

						popupMenu.show(tableCards, (int)point.getX(), (int)point.getY());
					}
					else
					{


						selected = (MagicCard)tableCards.getValueAt(tableCards.getSelectedRow(), 0);
						selectedEdition = selected.getEditions().get(0);
						updateCards();

					}
				}
			});

			listEdition.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent mev) {

					try {

						selectedEdition = listEdition.getSelectedValue();
						detailCardPanel.setMagicLogo(selectedEdition.getId(),selectedEdition.getRarity());
						magicEditionDetailPanel.setMagicEdition(selectedEdition);
						new Thread(new Runnable() {
							public void run() {
								try {
									lblCard.setIcon(new ImageIcon(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+selectedEdition.getMultiverse_id()+"&type=card")));
									loading(true,"loading edition");
									priceModel.init(selected, selectedEdition);
									priceModel.fireTableDataChanged();
									loading(false,"");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}}).start();

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			tablePrice.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent ev) {
					if(ev.getClickCount()==2 && !ev.isConsumed())
					{
						ev.consume();
						try {
							String url = tablePrice.getValueAt(tablePrice.getSelectedRow(), 4).toString();
							Desktop.getDesktop().browse(new URI(url));
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				}
			});

			cboLanguages.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					MagicCardNames selLang = (MagicCardNames)cboLanguages.getSelectedItem();
					if(selLang!=null)
					{
						defaultLanguage=selLang.getLanguage();
						logger.debug("Change default language" + defaultLanguage);
					}
				}
			});


			cboLanguages.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MagicCardNames selLang = (MagicCardNames)cboLanguages.getSelectedItem();
					try {
						if(selLang!=null)
						{
							lblCard.setIcon(new ImageIcon(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+selLang.getGathererId()+"&type=card")));
							logger.debug("loading" + selLang + " " + selLang.getGathererId());
						}



					} catch (MalformedURLException e1) {}
				}

			});


			mntmExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);

				}
			});

			mntmExportAsPdf.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					new Thread(new Runnable() {

						@Override
						public void run() {
							JFileChooser choose = new JFileChooser(".");
							choose.showSaveDialog(null);

							File f = choose.getSelectedFile();
							if(f==null)
								f=new File("temp.pdf");

							loading(false,"exporting pdf");
							MagicPDFGenerator.generatePDF(cards,f,defaultLanguage);
							loading(false,"");
							JOptionPane.showMessageDialog(null, "Export PDF Finished","Finished",JOptionPane.INFORMATION_MESSAGE);

						}
					}).start();;

				}
			});
			
			tabbedCardsInfo.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {

						if(tabbedCardsInfo.getSelectedIndex()==INDEX_PRICES)
							updatePrices();
					
				}
			});

			mntmExportGrid.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BufferedWriter bw;
					try {
						TableModel model=tableCards.getModel();
						JFileChooser jf =new JFileChooser();
						jf.showSaveDialog(null);
						File f=jf.getSelectedFile();
						FileWriter out;

						out = new FileWriter(f);
						bw=new BufferedWriter(out);
						for (int i=0;i<model.getColumnCount();i++){
							bw.write(model.getColumnName(i)+";");
						}
						bw.write("\n");
						for (int i=0;i<model.getRowCount();i++){
							for (int j=0;j<model.getColumnCount();j++){
								bw.write(model.getValueAt(i,j).toString()+";");
							}
							bw.write("\n");
						}
						bw.close();
						JOptionPane.showMessageDialog(null, "Export Finished","Finished",JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e1) {
						
						JOptionPane.showMessageDialog(null, e1,"Error",JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}

				}
			});
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}

		txtMagicSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSearch.doClick();

			}
		});


	}

	public void updatePrices() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				loading(true,"loading prices");
				priceModel.init(selected, selectedEdition);
				priceModel.fireTableDataChanged();
				loading(false,"");

			}
		}).start();
		
	}
	public void updateCards() {
		try {
			cboLanguages.removeAllItems();
			txtRulesArea.setText("");


			((DefaultListModel<MagicEdition>)listEdition.getModel()).removeAllElements();

			for(MagicEdition me : selected.getEditions())
				((DefaultListModel<MagicEdition>)listEdition.getModel()).addElement(me);

			ImageIcon icon = new ImageIcon(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+selected.getEditions().get(0).getMultiverse_id()+"&type=card"));
			lblCard.setIcon(icon);

			detailCardPanel.setMagicCard(selected,true);
			magicEditionDetailPanel.setMagicEdition(selected.getEditions().get(0));


			for(MagicRuling mr : selected.getRulings())
			{
				txtRulesArea.append(mr.toString());
				txtRulesArea.append("\n");
			}



			for(MagicCardNames mcn : selected.getForeignNames())
			{
				cboLanguages.addItem(mcn);
				if(mcn.getLanguage().startsWith(defaultLanguage))
				{
					cboLanguages.setSelectedItem(mcn);
				}
			}

			if(tabbedCardsInfo.getSelectedIndex()==INDEX_PRICES)
				updatePrices();

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}


	

}
