package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map.Entry;

import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.commons.io.FileUtils;
import org.jfree.ui.ExtensionFileFilter;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.PluginEntry;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGScript;
import org.magic.api.interfaces.abstracts.AbstractCardExport.MODS;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.ThreadManager;
import org.magic.services.workers.CardExportWorker;
import org.magic.tools.Chrono;
import org.magic.tools.UITools;

public class ScriptPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private JEditorPane editorPane;
	private JTextPane resultPane;
	private JComboBox<MTGScript> cboScript;
	private JCheckBox chkShowReturn ;
	private JLabel lblInfo;
	
	@Override
	public String getTitle() {
		return "Script";
	}
	
	public ScriptPanel() {
		setLayout(new BorderLayout());
		editorPane = new JEditorPane();
		resultPane = new JTextPane();
		JSplitPane splitPane = new JSplitPane();
		JButton btnOpen = new JButton(MTGConstants.ICON_OPEN);
		JButton btnRun = new JButton(MTGConstants.PLAY_ICON);
		JButton btnSaveButton = new JButton(MTGConstants.ICON_SAVE);
		JButton btnInsertFunction = new JButton(MTGConstants.ICON_GAME_TRIGGER);
		JPanel paneHaut = new JPanel();
		lblInfo = new JLabel("Result");
		cboScript = UITools.createCombobox(MTGScript.class, true);
		chkShowReturn = new JCheckBox("Show return");
		
		setPreferredSize(new Dimension(800, 600));
		
		
		
		resultPane.setBackground(SystemColor.windowBorder);
		
		add(paneHaut,BorderLayout.NORTH);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		paneHaut.add(cboScript);
		paneHaut.add(btnOpen);
		paneHaut.add(btnSaveButton);
		paneHaut.add(btnRun);
		paneHaut.add(chkShowReturn);
		paneHaut.add(btnInsertFunction);
		
		splitPane.setLeftComponent(new JScrollPane(editorPane));
		splitPane.setRightComponent(new JScrollPane(resultPane));
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
		add(splitPane,BorderLayout.CENTER);
		add(lblInfo,BorderLayout.SOUTH);
		
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				splitPane.setDividerLocation(.45);
				splitPane.setDividerLocation(.5);
				removeComponentListener(this);
			}

		});
		
		
		btnInsertFunction.addActionListener(ae -> {
			JPopupMenu menu = new JPopupMenu();

			for (Entry<Class, PluginEntry> exp : PluginRegistry.inst().entrySet()) {
					JMenu it = new JMenu(exp.getKey().getSimpleName());
					for(String s : PluginRegistry.inst().getStringMethod(exp.getKey()))
					{
						JMenuItem it2 = new JMenuItem(s);
						it2.addActionListener(al->appendEditor("."+it2.getText()));
						
						it.add(it2);
					}
					menu.add(it);
			}
			Component b = (Component) ae.getSource();
			Point p = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(p.x, p.y + b.getHeight());
		});
		
		
		btnRun.addActionListener(al->{
			
			Chrono c = new Chrono();
			c.start();
			ThreadManager.getInstance().executeThread(()->{
				try {
					
					MTGScript scripter = (MTGScript)cboScript.getSelectedItem();
					StringWriter writer = new StringWriter();
					scripter.setOutput(writer);
					
					Object ret = scripter.runContent(editorPane.getText());
					
					appendResult(writer.toString()+"\n",SystemColor.info);
					
					if(chkShowReturn.isSelected())
						appendResult("Return :" + ret+"\n",SystemColor.activeCaptionText);
					
				} catch (ScriptException e) {
					appendResult(e.getMessage()+"\n",Color.RED);
				}
					
				lblInfo.setText("Running time : " + c.stop() +"ms");
				
			}, "executing script");
		});
		
		btnSaveButton.addActionListener(al->{
			JFileChooser choose = new JFileChooser(MTGConstants.DATA_DIR);
			choose.setFileFilter(new ExtensionFileFilter(cboScript.getSelectedItem().toString(), ((MTGScript)cboScript.getSelectedItem()).getExtension()));
			int ret = choose.showSaveDialog(this);
			if(ret==JFileChooser.APPROVE_OPTION)
			{
				try {
					FileUtils.writeStringToFile(choose.getSelectedFile(), editorPane.getText(), MTGConstants.DEFAULT_ENCODING);
				} catch (IOException e) {
					MTGControler.getInstance().notify(e);
				}
			}
			
		});
		
		btnOpen.addActionListener(al-> {
			JFileChooser choose = new JFileChooser(MTGConstants.DATA_DIR);
			
			choose.setFileFilter(new ExtensionFileFilter(cboScript.getSelectedItem().toString(), ((MTGScript)cboScript.getSelectedItem()).getExtension()));
			
			int ret = choose.showOpenDialog(this);
			if(ret==JFileChooser.APPROVE_OPTION)
			{
				try {
					editorPane.setText(FileUtils.readFileToString(choose.getSelectedFile(), MTGConstants.DEFAULT_ENCODING));
				} catch (IOException e) {
					MTGControler.getInstance().notify(e);
				}
				
			}
		});
		
	}
	
	private void appendEditor(String msg)
    {
        editorPane.replaceSelection(msg);
    }
	
	private void appendResult(String msg, Color c)
    {

		StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        //aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = resultPane.getDocument().getLength();
        resultPane.setCaretPosition(len);
        resultPane.setCharacterAttributes(aset, false);
        resultPane.replaceSelection(msg);
    }


	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_RULES;
	}
	
	
	public static void main(String[] args) {
		
		ThreadManager.getInstance().invokeLater(()->MTGUIComponent.createJDialog(new ScriptPanel(), true, false).setVisible(true));
	}
	

}
