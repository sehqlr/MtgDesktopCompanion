package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.commons.io.FileUtils;
import org.jfree.ui.ExtensionFileFilter;
import org.magic.api.interfaces.MTGScript;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.tools.UITools;

public class ScriptPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	JEditorPane editorPane;
	JTextPane resultPane;
	JComboBox<MTGScript> cboScript;
	
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
		JButton btnNewButton = new JButton(MTGConstants.PLAY_ICON);
		JButton btnSaveButton = new JButton(MTGConstants.ICON_SAVE);
		JPanel paneHaut = new JPanel();
		cboScript = UITools.createCombobox(MTGScript.class, true);

		
		setPreferredSize(new Dimension(800, 600));
		
		
		resultPane.setBackground(SystemColor.windowBorder);
		
		paneHaut.add(cboScript);
		add(paneHaut,BorderLayout.NORTH);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		paneHaut.add(btnOpen);
		paneHaut.add(btnSaveButton);
		paneHaut.add(btnNewButton);
		splitPane.setLeftComponent(new JScrollPane(editorPane));
		splitPane.setRightComponent(new JScrollPane(resultPane));
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
		add(splitPane,BorderLayout.CENTER);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				splitPane.setDividerLocation(.45);
				splitPane.setDividerLocation(.5);
				removeComponentListener(this);
			}

		});
		
		btnNewButton.addActionListener(al->{
			try {
				Object ret = ((MTGScript)cboScript.getSelectedItem()).runContent(editorPane.getText());
				
				appendToPane(String.valueOf(ret)+"\n",SystemColor.info);
			} catch (ScriptException e) {
				appendToPane(e.getMessage()+"\n",Color.RED);
			}
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
				String content;
				try {
					content = FileUtils.readFileToString(choose.getSelectedFile(), MTGConstants.DEFAULT_ENCODING);
					editorPane.setText(content);
				} catch (IOException e) {
					MTGControler.getInstance().notify(e);
				}
				
			}
		});
		
	}
	
	private void appendToPane(String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

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